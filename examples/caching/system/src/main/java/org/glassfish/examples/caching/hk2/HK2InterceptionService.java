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

package org.glassfish.examples.caching.hk2;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.examples.caching.aop.CachingConstructorInterceptor;
import org.glassfish.examples.caching.aop.CachingMethodInterceptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.jvnet.hk2.annotations.Service;

/**
 * This is the HK2 interception service that will be used
 * to determine if a method or constructor should be intercepted
 * 
 * @author jwells
 *
 */
@Service
public class HK2InterceptionService implements InterceptionService {
    private final static MethodInterceptor METHOD_INTERCEPTOR = new CachingMethodInterceptor();
    private final static ConstructorInterceptor CONSTRUCTOR_INTERCEPTOR = new CachingConstructorInterceptor();
    
    private final static List<MethodInterceptor> METHOD_LIST = Collections.singletonList(METHOD_INTERCEPTOR);
    private final static List<ConstructorInterceptor> CONSTRUCTOR_LIST = Collections.singletonList(CONSTRUCTOR_INTERCEPTOR);

    /**
     * Interception could happen on any class, so we can
     * easily return the all filter here
     */
    @Override
    public Filter getDescriptorFilter() {
        return BuilderHelper.allFilter();
    }

    /**
     * Returns the method interceptors if Cache is on the method
     * @param method The method under investigation
     * @return If Cache is on the method will return the caching interceptor list,
     * otherwise returns null
     */
    @Override
    public List<MethodInterceptor> getMethodInterceptors(Method method) {
        if (method.isAnnotationPresent(Cache.class)) {
            return METHOD_LIST;
        }
        
        return null;
    }

    /**
     * Returns the consturctor interceptors if Cache is on the constructor
     * @param constructor The constructor under investigation
     * @return If Cache is on the constructor will return the caching interceptor list,
     * otherwise returns null
     */
    @Override
    public List<ConstructorInterceptor> getConstructorInterceptors(
            Constructor<?> constructor) {
        if (constructor.isAnnotationPresent(Cache.class)) {
            return CONSTRUCTOR_LIST;
        }
        
        return null;
    }

}
