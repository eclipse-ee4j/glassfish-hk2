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

package org.glassfish.hk2.internal;

import java.lang.annotation.Annotation;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;

import javax.inject.Singleton;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.PerThread;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.Visibility;
import org.glassfish.hk2.utilities.general.Hk2ThreadLocal;
import org.glassfish.hk2.utilities.reflection.Logger;

/**
 * @author jwells
 */
@Singleton @Visibility(DescriptorVisibility.LOCAL)
public class PerThreadContext implements Context<PerThread> {
    private final static boolean LOG_THREAD_DESTRUCTION = AccessController.<Boolean>doPrivileged(new PrivilegedAction<Boolean>() {

        @Override
        public Boolean run() {
            return Boolean.parseBoolean(System.getProperty("org.hk2.debug.perthreadcontext.log", "false"));
        }
        
    });
    
    private final Hk2ThreadLocal<PerContextThreadWrapper> threadMap =
            new Hk2ThreadLocal<PerContextThreadWrapper>() {
        public PerContextThreadWrapper initialValue() {
            return new PerContextThreadWrapper();
        }
    };

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#getScope()
     */
    @Override
    public Class<? extends Annotation> getScope() {
        return PerThread.class;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#findOrCreate(org.glassfish.hk2.api.ActiveDescriptor, org.glassfish.hk2.api.ServiceHandle)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <U> U findOrCreate(ActiveDescriptor<U> activeDescriptor,
            ServiceHandle<?> root) {
        U retVal = (U) threadMap.get().get(activeDescriptor);
        if (retVal == null) {
            retVal = activeDescriptor.create(root);
            threadMap.get().put(activeDescriptor, retVal);
        }
        
        return retVal;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#find(org.glassfish.hk2.api.ActiveDescriptor)
     */
    @Override
    public boolean containsKey(ActiveDescriptor<?> descriptor) {
        return threadMap.get().has(descriptor);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#isActive()
     */
    @Override
    public boolean isActive() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#supportsNullCreation()
     */
    @Override
    public boolean supportsNullCreation() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#supportsNullCreation()
     */
    @Override
    public void shutdown() {
        threadMap.removeAll();
    }

    @Override
    public void destroyOne(ActiveDescriptor<?> descriptor) {
        // per-thread instances live for the life of the thread,
        // so we will ignore any request to destroy a descriptor
        
    }
    
    private static class PerContextThreadWrapper {
        private final HashMap<ActiveDescriptor<?>, Object> instances =
                new HashMap<ActiveDescriptor<?>, Object>();
        private final long id = Thread.currentThread().getId();
        
        public boolean has(ActiveDescriptor<?> d) {
            return instances.containsKey(d);
        }
        
        public Object get(ActiveDescriptor<?> d) {
            return instances.get(d);
        }
        
        public void put(ActiveDescriptor<?> d, Object v) {
            instances.put(d, v);
        }
        
        @Override
        public void finalize() throws Throwable {
            instances.clear();
            
            if (LOG_THREAD_DESTRUCTION) {
                Logger.getLogger().debug("Removing PerThreadContext data for thread " + id);
            }
        }
        
    }
}
