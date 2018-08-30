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

package org.jvnet.hk2.testing.junit.internal;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.JustInTimeInjectionResolver;

/**
 * @author jwells
 *
 */
@Singleton
public class JustInTimeInjectionResolverImpl implements
        JustInTimeInjectionResolver {

    private final Collection<?> excludes;
    
    @Inject
    private DynamicConfigurationService dcs;

    public JustInTimeInjectionResolverImpl() {
        this(Collections.emptySet());
    }

    public JustInTimeInjectionResolverImpl(final Collection<?> excludes) {
        super();
        if (excludes == null) {
            this.excludes = Collections.emptySet();
        } else {
            this.excludes = excludes;
        }
    }

    /**
     * Returns {@code true} if the supplied {@link Injectee}
     * represents a service lookup rather than a true injection point.
     *
     * @param failedInjectionPoint the {@link Injectee} to test; may
     * be {@code null} in which case {@code true} will be returned
     *
     * @return {@code true} if the supplied {@link Injectee}
     * represents a service lookup rather than a true injection point
     */
    protected boolean isLookup(final Injectee failedInjectionPoint) {
        return failedInjectionPoint == null || failedInjectionPoint.getParent() == null;
    }
  
    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.JustInTimeInjectionResolver#justInTimeResolution(org.glassfish.hk2.api.Injectee)
     */
    @Override
    public boolean justInTimeResolution(Injectee failedInjectionPoint) {
        boolean returnValue = false;
        if (failedInjectionPoint != null && !isLookup(failedInjectionPoint)) {
            
            Type needType = failedInjectionPoint.getRequiredType();
            
            Class<?> needClass = null;
            if (needType instanceof Class) {
                needClass = (Class<?>) needType;
            }
            else if (needType instanceof ParameterizedType) {
                Type rawType = ((ParameterizedType) needType).getRawType();
                if (rawType instanceof Class) {
                    needClass = (Class<?>) rawType;
                }
            }
            
            if (needClass == null || needClass.isInterface() || (excludes != null && excludes.contains(needClass.getName()))) {
                return false;
            }
            
            DynamicConfiguration config = dcs.createDynamicConfiguration();
            
            try {
                final ActiveDescriptor<?> ad = config.addActiveDescriptor(needClass);
                config.commit();
                returnValue = true;
            }
            catch (Throwable th) {
                returnValue = false;
            }
        }
        return returnValue;
    }

}
