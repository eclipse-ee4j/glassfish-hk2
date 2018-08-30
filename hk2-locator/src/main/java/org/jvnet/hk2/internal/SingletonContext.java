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

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import javax.inject.Singleton;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.ContextualInput;
import org.glassfish.hk2.utilities.cache.Cache;
import org.glassfish.hk2.utilities.cache.Computable;
import org.glassfish.hk2.utilities.reflection.Logger;

/**
 * @author jwells
 *
 */
@Singleton
public class SingletonContext implements Context<Singleton> {
    private int generationNumber = Integer.MIN_VALUE;
    private final ServiceLocatorImpl locator;

    private final Cache<ContextualInput<Object>, Object> valueCache =
            new Cache<ContextualInput<Object>, Object>(new Computable<ContextualInput<Object>, Object>() {

        @Override
        public Object compute(ContextualInput<Object> a) {

            final ActiveDescriptor<Object> activeDescriptor = a.getDescriptor();

            final Object cachedVal = activeDescriptor.getCache();
            if (cachedVal != null) {
                return cachedVal;
            }

            final Object createdVal = activeDescriptor.create(a.getRoot());
            activeDescriptor.setCache(createdVal);
            if (activeDescriptor instanceof SystemDescriptor) {
                ((SystemDescriptor<?>) activeDescriptor).setSingletonGeneration(generationNumber++);
            }

            return createdVal;
        }
    }, new Cache.CycleHandler<ContextualInput<Object>>(){

        @Override
        public void handleCycle(ContextualInput<Object> key) {
            throw new MultiException(new IllegalStateException(
                            "A circular dependency involving Singleton service " + key.getDescriptor().getImplementation() +
                            " was found.  Full descriptor is " + key.getDescriptor()));
        }
    });

    /* package */ SingletonContext(ServiceLocatorImpl impl) {
        locator = impl;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#getScope()
     */
    @Override
    public Class<? extends Annotation> getScope() {
        return Singleton.class;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#findOrCreate(org.glassfish.hk2.api.ActiveDescriptor, org.glassfish.hk2.api.ServiceHandle)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T findOrCreate(ActiveDescriptor<T> activeDescriptor,
            ServiceHandle<?> root) {

        try {
            return (T)valueCache.compute(new ContextualInput<Object>((ActiveDescriptor<Object>) activeDescriptor, root));
        } catch (Throwable th) {
            if (th instanceof MultiException) {
                throw (MultiException) th;
            }
            throw new MultiException(th);
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#find(org.glassfish.hk2.api.Descriptor)
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean containsKey(ActiveDescriptor<?> descriptor) {
        return valueCache.containsKey(new ContextualInput<Object>((ActiveDescriptor<Object>) descriptor, null));
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
    @SuppressWarnings("unchecked")
    @Override
    public void shutdown() {
        List<ActiveDescriptor<?>> all = locator.getDescriptors(BuilderHelper.allFilter());

        long myLocatorId = locator.getLocatorId();

        TreeSet<SystemDescriptor<Object>> singlesOnly = new TreeSet<SystemDescriptor<Object>>(
                new GenerationComparator());
        for (ActiveDescriptor<?> one : all) {
            if (one.getScope() == null || !one.getScope().equals(Singleton.class.getName())) continue;

            synchronized (this) {
                if (one.getCache() == null) continue;
            }

            if (one.getLocatorId() == null || one.getLocatorId().longValue() != myLocatorId) continue;

            SystemDescriptor<Object> oneAsObject = (SystemDescriptor<Object>) one;

            singlesOnly.add(oneAsObject);
        }

        for (SystemDescriptor<Object> one : singlesOnly) {
            destroyOne(one);
        }
    }

    /**
     * Release one system descriptor
     *
     * @param one The descriptor to release (may not be null).  Further, the cache MUST be set
     */
    @SuppressWarnings("unchecked")
    @Override
    public void destroyOne(ActiveDescriptor<?> one) {
        Object value;
        valueCache.remove(new ContextualInput<Object>((ActiveDescriptor<Object>) one, null));
        value = one.getCache();
        one.releaseCache();

        if (value == null) return;

        try {
            ((ActiveDescriptor<Object>) one).dispose(value);
        }
        catch (Throwable th) {
            Logger.getLogger().debug("SingletonContext", "releaseOne", th);
        }

    }

    private static class GenerationComparator implements Comparator<SystemDescriptor<Object>>, Serializable {

        /**
         * For serialization
         */
        private static final long serialVersionUID = -6931828935035131179L;

        @Override
        public int compare(SystemDescriptor<Object> o1,
                SystemDescriptor<Object> o2) {
            if (o1.getSingletonGeneration() > o2.getSingletonGeneration()) {
                return -1;
            }
            if (o1.getSingletonGeneration() == o2.getSingletonGeneration()) {
                return 0;
            }

            return 1;
        }

    }
}
