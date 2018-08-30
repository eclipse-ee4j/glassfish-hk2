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

import java.util.Collections;
import java.util.List;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InstanceLifecycleEventType;
import org.glassfish.hk2.api.InstantiationService;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.general.ThreadSpecificObject;
import org.glassfish.hk2.utilities.reflection.Pretty;

/**
 * @author jwells
 * @param <T> The thing this factory is producing
 */
public class FactoryCreator<T> implements Creator<T> {
    private final static Object MAP_VALUE = new Object();
    private final ConcurrentHashMap<ThreadSpecificObject<ActiveDescriptor<?>>, Object> cycleFinder =
            new ConcurrentHashMap<ThreadSpecificObject<ActiveDescriptor<?>>, Object>();
    
    private final ServiceLocator locator;
    private final ActiveDescriptor<?> factoryDescriptor;
    private final InstantiationServiceImpl instantiationService;
    
    /* package */ FactoryCreator(ServiceLocator locator, ActiveDescriptor<?> factoryDescriptor) {
        this.locator = locator;
        this.factoryDescriptor = factoryDescriptor;
        
        if (!factoryDescriptor.isReified()) {
            factoryDescriptor = locator.reifyDescriptor(factoryDescriptor);
        }
        
        InstantiationServiceImpl found = null;
        for (Injectee factoryInjectee : factoryDescriptor.getInjectees()) {
            if (InstantiationService.class.equals(factoryInjectee.getRequiredType())) {
                found = locator.getService(InstantiationServiceImpl.class);
                break;
            }
        }
        
        // Will ONLY be non-null if the factory has injected the InstantiationService
        instantiationService = found;
    }

    /* (non-Javadoc)
     * @see org.jvnet.hk2.internal.Creator#getInjectees()
     */
    @Override
    public List<Injectee> getInjectees() {
        return Collections.emptyList();
    }
    
    @SuppressWarnings("unchecked")
    private ServiceHandle<Factory<T>> getFactoryHandle() {
        try {
            return (ServiceHandle<Factory<T>>) locator.getServiceHandle(factoryDescriptor);
        }
        catch (Throwable th) {
            throw new MultiException(th);
        }
    }

    /* (non-Javadoc)
     * @see org.jvnet.hk2.internal.Creator#create(org.glassfish.hk2.api.ServiceHandle)
     */
    @Override
    public T create(ServiceHandle<?> root, SystemDescriptor<?> eventThrower) throws MultiException {
        ServiceHandle<Factory<T>> handle = getFactoryHandle();
        
        eventThrower.invokeInstanceListeners(new InstanceLifecycleEventImpl(
                InstanceLifecycleEventType.PRE_PRODUCTION, null, eventThrower));
        
        ThreadSpecificObject<ActiveDescriptor<?>> tso = new ThreadSpecificObject<ActiveDescriptor<?>>(handle.getActiveDescriptor());
        
        if (cycleFinder.containsKey(tso)) {
            HashSet<String> impls = new HashSet<String>();
            for (ThreadSpecificObject<ActiveDescriptor<?>> candidate : cycleFinder.keySet()) {
                if (candidate.getThreadIdentifier() != tso.getThreadIdentifier()) continue;
                impls.add(candidate.getIncomingObject().getImplementation());
            }
            
            throw new AssertionError("A cycle was detected involving these Factory implementations: " + Pretty.collection(impls));
        }
        
        cycleFinder.put(tso, MAP_VALUE);
        Factory<T> retValFactory;
        try {
            retValFactory = handle.getService();
        }
        finally {
            cycleFinder.remove(tso);
        }
        
        if (instantiationService != null) {
            Injectee parentInjectee = null;
            if (root != null && (root instanceof ServiceHandleImpl)) {
                parentInjectee = ((ServiceHandleImpl<?>) root).getOriginalRequest();
            }
            
            // Even if it is null
            instantiationService.pushInjecteeParent(parentInjectee);
        }
        
        T retVal;
        try {
            retVal = retValFactory.provide();
        }
        finally {
            if (instantiationService != null) {
                instantiationService.popInjecteeParent();
            }
        }
        
        eventThrower.invokeInstanceListeners(new InstanceLifecycleEventImpl(
                InstanceLifecycleEventType.POST_PRODUCTION, retVal, eventThrower));
        
        return retVal;
    }

    /* (non-Javadoc)
     * @see org.jvnet.hk2.internal.Creator#dispose(java.lang.Object, org.glassfish.hk2.api.ServiceHandle)
     */
    @Override
    public void dispose(T instance) {
        try {
            ServiceHandle<Factory<T>> handle = getFactoryHandle();
            
            Factory<T> factory = handle.getService();
            
            factory.dispose(instance);
        }
        catch (Throwable th) {
            if (th instanceof MultiException) {
                throw (MultiException) th;
            }
            
            throw new MultiException(th);
        }
    }
    
    public String toString() {
        return "FactoryCreator(" + locator + "," + factoryDescriptor + "," + System.identityHashCode(this) + ")";
    }
}
