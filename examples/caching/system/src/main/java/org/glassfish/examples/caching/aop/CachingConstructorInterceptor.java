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

package org.glassfish.examples.caching.aop;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.ConstructorInvocation;

/**
 * This is an interceptor that will implement caching
 * of constructor results.
 * <p>
 * This interceptor does not import any hk2 API and thus
 * is a pure AOP Alliance method interceptor that might be
 * used in any software that enabled AOP Alliance interceptors
 * 
 * @author jwells
 *
 */
public class CachingConstructorInterceptor implements ConstructorInterceptor {
    private final HashMap<CacheKey, Object> cache = new HashMap<CacheKey, Object>();

    /* (non-Javadoc)
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    @Override
    public Object construct(ConstructorInvocation invocation) throws Throwable {
        Object args[] = invocation.getArguments();
        if (args.length != 1) {
            return invocation.proceed();
        }
        
        Object arg = args[0];
        if (arg == null) {
            return invocation.proceed();
        }
        
        Constructor<?> c = invocation.getConstructor();
        CacheKey key = new CacheKey(c.getDeclaringClass().getName(), "<init>", arg);
        
        if (!cache.containsKey(key)) {
            Object retVal = invocation.proceed();
            
            cache.put(key, retVal);
            
            return retVal;
        }
        
        // Found it in the cache!  Do not call the method
        return cache.get(key);
    }
}
