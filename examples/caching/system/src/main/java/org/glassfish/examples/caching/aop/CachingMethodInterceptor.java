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

import java.lang.reflect.Method;
import java.util.HashMap;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * This is an interceptor that will implement caching
 * of method results.
 * <p>
 * This interceptor does not import any hk2 API and thus
 * is a pure AOP Alliance method interceptor that might be
 * used in any software that enabled AOP Alliance interceptors
 * 
 * @author jwells
 *
 */
public class CachingMethodInterceptor implements MethodInterceptor {
    private final HashMap<CacheKey, Object> cache = new HashMap<CacheKey, Object>();

    /* (non-Javadoc)
     * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object args[] = invocation.getArguments();
        if (args.length != 1) {
            return invocation.proceed();
        }
        
        Object arg = args[0];
        if (arg == null) {
            return invocation.proceed();
        }
        
        Method m = invocation.getMethod();
        CacheKey key = new CacheKey(m.getDeclaringClass().getName(), m.getName(), arg);
        
        if (!cache.containsKey(key)) {
            Object retVal = invocation.proceed();
            
            cache.put(key, retVal);
            
            return retVal;
        }
        
        // Found it in the cache!  Do not call the method
        return cache.get(key);
    }
}
