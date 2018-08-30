/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package org.glassfish.hk2.utilities;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.Immediate;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.Visibility;
import org.glassfish.hk2.internal.HandleAndService;
import org.glassfish.hk2.internal.ImmediateLocalLocatorFilter;

/**
 * This is the {@link Context} implementation for the {@link Immediate}
 * scope
 * 
 * @author jwells
 *
 */
@Singleton @Visibility(DescriptorVisibility.LOCAL)
public class ImmediateContext implements Context<Immediate>{
    private final HashMap<ActiveDescriptor<?>, HandleAndService> currentImmediateServices = new HashMap<ActiveDescriptor<?>, HandleAndService>();
    private final HashMap<ActiveDescriptor<?>, Long> creating = new HashMap<ActiveDescriptor<?>, Long>();
    
    private final ServiceLocator locator;
    private final Filter validationFilter;
    
    @Inject
    private ImmediateContext(ServiceLocator locator) {
        this.locator = locator;
        validationFilter = new ImmediateLocalLocatorFilter(locator.getLocatorId());
    }
    
    @Override
    public Class<? extends Annotation> getScope() {
        return Immediate.class;
    }

    /**
     * @param activeDescriptor The descriptor to create
     * @param root The root handle
     * @return The service
     */
    @Override
    @SuppressWarnings("unchecked")
    public <U> U findOrCreate(ActiveDescriptor<U> activeDescriptor,
            ServiceHandle<?> root) {
        U retVal = null;
        
        synchronized (this) {
            HandleAndService has = currentImmediateServices.get(activeDescriptor);
            if (has != null) {
                return (U) has.getService();
            }
            
            while (creating.containsKey(activeDescriptor)) {
                long alreadyCreatingThread = creating.get(activeDescriptor);
                if (alreadyCreatingThread == Thread.currentThread().getId()) {
                    throw new MultiException(new IllegalStateException(
                            "A circular dependency involving Immediate service " + activeDescriptor.getImplementation() +
                            " was found.  Full descriptor is " + activeDescriptor));
                }
                
                try {
                    this.wait();
                }
                catch (InterruptedException ie) {
                    throw new MultiException(ie);
                }
                
            }
            
            has = currentImmediateServices.get(activeDescriptor);
            if (has != null) {
                return (U) has.getService();
            }
            
            creating.put(activeDescriptor, Thread.currentThread().getId());
        }
        
        try {
            retVal = activeDescriptor.create(root);
        }
        finally {
            synchronized (this) {
                ServiceHandle<?> discoveredRoot = null;
                if (root != null) {
                    if (root.getActiveDescriptor().equals(activeDescriptor)) {
                        discoveredRoot = root;
                    }
                }
                
                if (retVal != null) {
                    currentImmediateServices.put(activeDescriptor, new HandleAndService(discoveredRoot, retVal));
                }
                
                creating.remove(activeDescriptor);
                this.notifyAll();
            }
            
        }
        
        return retVal;
    }

    /**
     * @param descriptor The descriptor to find
     * @return true if this service has been created
     */
    @Override
    public synchronized boolean containsKey(ActiveDescriptor<?> descriptor) {
        return currentImmediateServices.containsKey(descriptor);
    }

    @Override
    public void destroyOne(ActiveDescriptor<?> descriptor) {
        destroyOne(descriptor, null);
    }
    
    /**
     * Destroys a single descriptor
     * 
     * @param descriptor The descriptor to destroy
     * @param errorHandlers The handlers for exceptions (if null will get from service locator)
     */
    @SuppressWarnings("unchecked")
    private void destroyOne(ActiveDescriptor<?> descriptor, List<ImmediateErrorHandler> errorHandlers) {
        if (errorHandlers == null) {
            errorHandlers = locator.getAllServices(ImmediateErrorHandler.class);
        }
        
        synchronized (this) {
            HandleAndService has = currentImmediateServices.remove(descriptor);
            Object instance = has.getService();
        
            try {
                ((ActiveDescriptor<Object>) descriptor).dispose(instance);
            }
            catch (Throwable th) {
                for (ImmediateErrorHandler ieh : errorHandlers) {
                    try {
                        ieh.preDestroyFailed(descriptor, th);
                    }
                    catch (Throwable th2) {
                        // ignore
                    }
                }
            }
            
        }
        
    }

    @Override
    public boolean supportsNullCreation() {
        return false;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    /**
     * For when the server shuts down
     */
    @Override
    public void shutdown() {
        List<ImmediateErrorHandler> errorHandlers = locator.getAllServices(ImmediateErrorHandler.class);
        
        synchronized (this) {
            for (Map.Entry<ActiveDescriptor<?>, HandleAndService> entry :
                new HashSet<Map.Entry<ActiveDescriptor<?>, HandleAndService>>(currentImmediateServices.entrySet())) {
                HandleAndService has = entry.getValue();
                
                ServiceHandle<?> handle = has.getHandle();
                
                if (handle != null) {
                    handle.destroy();
                }
                else {
                    destroyOne(entry.getKey(), errorHandlers);
                }
                
            }
            
            
        }
    }
    
    private List<ActiveDescriptor<?>> getImmediateServices() {
        List<ActiveDescriptor<?>> inScopeAndInThisLocator;
        try {
            inScopeAndInThisLocator = locator.getDescriptors(validationFilter);
        }
        catch (IllegalStateException ise) {
            // locator has been shutdown
            inScopeAndInThisLocator = Collections.emptyList();
        }
        
        return inScopeAndInThisLocator;
    }
    
    public Filter getValidationFilter() {
        return validationFilter;
    }
    
    public void doWork() {
        List<ActiveDescriptor<?>> inScopeAndInThisLocator = getImmediateServices();
        
        List<ImmediateErrorHandler> errorHandlers;
        try {
            errorHandlers = locator.getAllServices(ImmediateErrorHandler.class);
        }
        catch (IllegalStateException ise) {
            // Locator has been shut down
            return;
        }
        
        LinkedHashSet<ActiveDescriptor<?>> oldSet;
        LinkedHashSet<ActiveDescriptor<?>> newFullSet = new LinkedHashSet<ActiveDescriptor<?>>(inScopeAndInThisLocator);
        LinkedHashSet<ActiveDescriptor<?>> addMe = new LinkedHashSet<ActiveDescriptor<?>>();
        
        synchronized (this) {
            // First thing to do is wait until all the things in-flight have gone
            while (creating.size() > 0) {
                try {
                    this.wait();
                }
                catch (InterruptedException ie) {
                    throw new RuntimeException(ie);
                }
            }
            
            oldSet = new LinkedHashSet<ActiveDescriptor<?>>(currentImmediateServices.keySet());
            
            for (ActiveDescriptor<?> ad : inScopeAndInThisLocator) {
                if (!oldSet.contains(ad)) {
                  addMe.add(ad);
                }
            }
                    
            oldSet.removeAll(newFullSet);
            
            for (ActiveDescriptor<?> gone : oldSet) {
                HandleAndService has = currentImmediateServices.get(gone);
                ServiceHandle<?> handle = has.getHandle();
                
                if (handle != null) {
                    handle.destroy();
                }
                else {
                    destroyOne(gone, errorHandlers);
                }
            }
        }
        
        for (ActiveDescriptor<?> ad : addMe) {
            // Create demand
            try {
                locator.getServiceHandle(ad).getService();
            }
            catch (Throwable th) {
                for (ImmediateErrorHandler ieh : errorHandlers) {
                    try {
                        ieh.postConstructFailed(ad, th);
                    }
                    catch (Throwable th2) {
                        // ignore
                    }
                }
                
            }
            
        }
    }

}
