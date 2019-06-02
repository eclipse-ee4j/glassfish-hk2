/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.configuration.internal;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.Visibility;
import org.glassfish.hk2.configuration.api.ConfiguredBy;

/**
 * @author jwells
 *
 */
@Singleton
@Visibility(DescriptorVisibility.LOCAL)
public class ConfiguredByContext implements Context<ConfiguredBy> {

    private final static ThreadLocal<ActiveDescriptor<?>> workingOn = ThreadLocal.withInitial(() -> null);

    private final Object lock = new Object();
    private final Map<ActiveDescriptor<?>, Object> db = new HashMap<>();

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#getScope()
     */
    @Override
    public Class<? extends Annotation> getScope() {
        return ConfiguredBy.class;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#findOrCreate(org.glassfish.hk2.api.ActiveDescriptor, org.glassfish.hk2.api.ServiceHandle)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <U> U findOrCreate(ActiveDescriptor<U> activeDescriptor,
            ServiceHandle<?> root) {
        ActiveDescriptor<U> previousValue = (ActiveDescriptor<U>) workingOn.get();
        workingOn.set(activeDescriptor);
        try {
            return internalFindOrCreate(activeDescriptor, root);
        }
        finally {
            workingOn.set(previousValue);
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#findOrCreate(org.glassfish.hk2.api.ActiveDescriptor, org.glassfish.hk2.api.ServiceHandle)
     */
    @SuppressWarnings("unchecked")
    private <U> U internalFindOrCreate(ActiveDescriptor<U> activeDescriptor,
            ServiceHandle<?> root) {
        synchronized (lock) {
            U retVal = (U) db.get(activeDescriptor);
            if (retVal != null) return retVal;

            if (activeDescriptor.getName() == null) {
                throw new MultiException(new IllegalStateException("ConfiguredBy services without names are templates and cannot be created directly"));
            }

            retVal = activeDescriptor.create(root);
            db.put(activeDescriptor, retVal);

            return retVal;
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#containsKey(org.glassfish.hk2.api.ActiveDescriptor)
     */
    @Override
    public boolean containsKey(ActiveDescriptor<?> descriptor) {
        synchronized (lock) {
            return db.containsKey(descriptor);
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#destroyOne(org.glassfish.hk2.api.ActiveDescriptor)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void destroyOne(ActiveDescriptor<?> descriptor) {
        synchronized (lock) {
            Object destroyMe = db.remove(descriptor);
            if (destroyMe == null) return;

            ((ActiveDescriptor<Object>) descriptor).dispose(destroyMe);
        }

    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#supportsNullCreation()
     */
    @Override
    public boolean supportsNullCreation() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#isActive()
     */
    @Override
    public boolean isActive() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#shutdown()
     */
    @Override
    public void shutdown() {
        synchronized (lock) {
            Set<ActiveDescriptor<?>> activeDescriptors = new HashSet<>(db.keySet());
            for (ActiveDescriptor<?> killMe : activeDescriptors) {
                destroyOne(killMe);
            }
        }

    }

    /* package */ ActiveDescriptor<?> getWorkingOn() {
        return workingOn.get();
    }

    /* package */ Object findOnly(ActiveDescriptor<?> descriptor) {
        synchronized (lock) {
            return db.get(descriptor);
        }
    }

}
