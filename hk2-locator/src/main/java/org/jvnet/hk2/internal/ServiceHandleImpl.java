/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.jvnet.hk2.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;

/**
 * This handle does the underlying work of getting the service.  Only
 * at the time that the getService call is made is the service gotten
 * from the context.  Once a service has been gotten, it is not looked
 * up again.
 * 
 * @author jwells
 * @param <T> The type of service to create
 *
 */
public class ServiceHandleImpl<T> implements ServiceHandle<T> {
    private ActiveDescriptor<T> root;
    private final ServiceLocatorImpl locator;
    private final LinkedList<Injectee> injectees = new LinkedList<Injectee>();
    private final Object lock = new Object();
    
    private boolean serviceDestroyed = false;
    private boolean serviceSet = false;
    private T service;
    private Object serviceData;
    
    private final LinkedList<ServiceHandleImpl<?>> subHandles = new LinkedList<ServiceHandleImpl<?>>();
    
    /* package */ ServiceHandleImpl(ServiceLocatorImpl locator, ActiveDescriptor<T> root, Injectee injectee) {
        this.root = root;
        this.locator = locator;
        if (injectee != null) {
            injectees.add(injectee);
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceHandle#getService()
     */
    @Override
    public T getService() {
        return getService(this);
    }
    
    private Injectee getLastInjectee() {
        synchronized (lock) {
            return (injectees.isEmpty()) ? null : injectees.getLast() ;
        }
    }
    
    /* package */ T getService(ServiceHandle<T> handle) {
        if (root instanceof Closeable) {
            Closeable closeable = (Closeable) root;
            if (closeable.isClosed()) {
                throw new IllegalStateException("This service has been unbound: " + root);
            }
        }
        
        synchronized (lock) {
            if (serviceDestroyed) throw new IllegalStateException("Service has been disposed");
            
            if (serviceSet) return service;
            
            Injectee injectee = getLastInjectee();
            
            Class<?> requiredClass = (injectee == null) ? null : ReflectionHelper.getRawClass(injectee.getRequiredType());
            
            service = Utilities.createService(root, injectee, locator, handle, requiredClass);
            
            serviceSet = true;
        
            return service;
        }
        
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceHandle#getActiveDescriptor()
     */
    @Override
    public ActiveDescriptor<T> getActiveDescriptor() {
        return root;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceHandle#isActive()
     */
    @Override
    public boolean isActive() {
        // No lock needed, nothing changes state
        if (serviceDestroyed) return false;
        if (serviceSet) return true;
        
        try {
            Context<?> context = locator.resolveContext(root.getScopeAnnotation());
            return context.containsKey(root);
        }
        catch (IllegalStateException ise) {
            return false;
        }
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceHandle#destroy()
     */
    @Override
    public void close() {
        boolean localServiceSet;
        boolean serviceActive;
        
        if (!root.isReified()) return;
        
        List<ServiceHandleImpl<?>> localSubHandles;
        synchronized (lock) {
            serviceActive = isActive();
            
            if (serviceDestroyed) return;
            serviceDestroyed = true;
            
            localServiceSet = serviceSet;
            
            localSubHandles = new ArrayList<ServiceHandleImpl<?>>(subHandles);
            subHandles.clear();
        }
        
        if (root.getScopeAnnotation().equals(PerLookup.class)) {
            if (localServiceSet) {
                // Otherwise it is the scope responsible for the lifecycle
                root.dispose(service);
            }
        }
        else if (serviceActive) {
            Context<?> context;
            try {
                context = locator.resolveContext(root.getScopeAnnotation());
            }
            catch (Throwable th) {
                return;
            }
            
            context.destroyOne(root);
        }
        
        for (ServiceHandleImpl<?> subHandle : localSubHandles) {
            subHandle.destroy();
        }
        

    }
    
    @Override
    public void setServiceData(Object serviceData) {
        synchronized (lock) {
            this.serviceData = serviceData;
        }
        
    }

    @Override
    public Object getServiceData() {
        synchronized (lock) {
            return serviceData;
        }
    }
    
    @Override
    public List<ServiceHandle<?>> getSubHandles() {
        synchronized (lock) {
            return new ArrayList<ServiceHandle<?>>(subHandles);
        }
    }
    
    public void pushInjectee(Injectee push) {
        synchronized (lock) {
            injectees.add(push);
        }
    }
    
    public void popInjectee() {
        synchronized (lock) {
            injectees.removeLast();
        }
    }
    
    /**
     * Add a sub handle to this for proper destruction
     * 
     * @param subHandle A handle to add for proper destruction
     */
    public void addSubHandle(ServiceHandleImpl<?> subHandle) {
        synchronized (lock) {
            subHandles.add(subHandle);
        }
    }
    
    public Injectee getOriginalRequest() {
        Injectee injectee = getLastInjectee();
        return injectee;
    }
    
    public String toString() {
        return "ServiceHandle(" + root + "," + System.identityHashCode(this) + ")"; 
    }

    
}
