/*
 * Copyright (c) 2025 Contributors to Eclipse Foundation.
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2023 Payara Foundation and/or its affiliates. All rights reserved.
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
import org.glassfish.hk2.utilities.CleanerFactory;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;

import jakarta.inject.Singleton;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.InheritableThread;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.Visibility;
import org.glassfish.hk2.utilities.reflection.Logger;

/**
 * @author jwells
 */
@Singleton @Visibility(DescriptorVisibility.LOCAL)
public class InheritableThreadContext implements Context<InheritableThread> {
    private final static boolean LOG_THREAD_DESTRUCTION = AccessController.<Boolean>doPrivileged(new PrivilegedAction<Boolean>() {

        @Override
        public Boolean run() {
            return Boolean.parseBoolean(System.getProperty("org.hk2.debug.inheritablethreadcontext.log", "false"));
        }

    });

    private InheritableThreadLocal<InheritableContextThreadWrapper> threadMap
            = new InheritableThreadLocal<InheritableContextThreadWrapper>() {
                public InheritableContextThreadWrapper initialValue() {
                    return new InheritableContextThreadWrapper();
        }
    };

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#getScope()
     */
    @Override
    public Class<? extends Annotation> getScope() {
        return InheritableThread.class;
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
        threadMap = null;
    }

    @Override
    public void destroyOne(ActiveDescriptor<?> descriptor) {
        // per-thread instances live for the life of the thread,
        // so we will ignore any request to destroy a descriptor

    }

    private static class InheritableContextThreadWrapper {
        
        private final CleanableContext context = new CleanableContext();

        public InheritableContextThreadWrapper() {
            registerStopEvent();
        }

        public boolean has(ActiveDescriptor<?> descriptor) {
            return context.has(descriptor);
        }

        public Object get(ActiveDescriptor<?> descriptor) {
            return context.get(descriptor);
        }

        public void put(ActiveDescriptor<?> descriptor, Object value) {
            context.put(descriptor, value);
        }

        public final void registerStopEvent() {
            CleanerFactory.create().register(this, context);
        }
    }

    private static final class CleanableContext implements Runnable {

        private final Map<ActiveDescriptor<?>, Object> instances = new HashMap<>();
        private final long id = Thread.currentThread().getId();

        public boolean has(ActiveDescriptor<?> descriptor) {
            return instances.containsKey(descriptor);
        }

        public Object get(ActiveDescriptor<?> descriptor) {
            return instances.get(descriptor);
        }

        public void put(ActiveDescriptor<?> descriptor, Object value) {
            instances.put(descriptor, value);
        }

        @Override
        public void run() {
            instances.clear();

            if (LOG_THREAD_DESTRUCTION) {
                Logger.getLogger().debug("Removing PerThreadContext data for thread " + id);
            }
        }
    }
}
