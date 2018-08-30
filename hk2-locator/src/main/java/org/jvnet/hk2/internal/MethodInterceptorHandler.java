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

package org.jvnet.hk2.internal;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.glassfish.hk2.api.AOPProxyCtl;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.HK2Invocation;
import org.glassfish.hk2.utilities.reflection.Logger;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;

import javassist.util.proxy.MethodHandler;

/**
 * This is the handler that runs the aopalliance method interception
 * 
 * @author jwells
 *
 */
public class MethodInterceptorHandler implements MethodHandler {
    private final static boolean DEBUG_INTERCEPTION = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
        @Override
        public Boolean run() {
            return Boolean.parseBoolean(
                System.getProperty("org.jvnet.hk2.properties.tracing.interceptors", "false"));
        }
            
    });
    
    private final ServiceLocatorImpl locator;
    private final Map<Method, List<MethodInterceptor>> interceptorLists;
    private final ActiveDescriptor<?> underlyingDescriptor;
    
    /* package */ MethodInterceptorHandler(ServiceLocatorImpl locator,
            ActiveDescriptor<?> underlyingDescriptor,
            Map<Method, List<MethodInterceptor>> interceptorLists) {
        this.locator = locator;
        this.interceptorLists = interceptorLists;
        this.underlyingDescriptor = underlyingDescriptor;
    }

    /* (non-Javadoc)
     * @see javassist.util.proxy.MethodHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args)
            throws Throwable {
        if (thisMethod.getName().equals(AOPProxyCtl.UNDERLYING_METHOD_NAME)) {
            return underlyingDescriptor;
        }
        
        List<MethodInterceptor> interceptors = interceptorLists.get(thisMethod);
        if (interceptors == null || interceptors.isEmpty()) {
            return ReflectionHelper.invoke(self, proceed, args, locator.getNeutralContextClassLoader());
        }
        
        if (!(interceptors instanceof RandomAccess)) {
            // Make sure we are indexable
            interceptors = new ArrayList<MethodInterceptor>(interceptors);
        }
        
        MethodInterceptor nextInterceptor = interceptors.get(0);
        
        long aggregateInterceptionTime = 0L;
        if (DEBUG_INTERCEPTION) {
            aggregateInterceptionTime = System.currentTimeMillis();
            Logger.getLogger().debug("Invoking interceptor " + nextInterceptor.getClass().getName() +
                    " index 0 in stack of " + interceptors.size() + " of method " + thisMethod);
        }
        
        try {
            return nextInterceptor.invoke(new MethodInvocationImpl(args,
                thisMethod, self, interceptors, 0, proceed, null));
        }
        finally {
            if (DEBUG_INTERCEPTION) {
                aggregateInterceptionTime = System.currentTimeMillis() - aggregateInterceptionTime;
                Logger.getLogger().debug("Interceptor " + nextInterceptor.getClass().getName() +
                        " index 0 took an aggregate of " + aggregateInterceptionTime + " milliseconds");
            }
        }
    }
    
    private class MethodInvocationImpl implements MethodInvocation, HK2Invocation {
        private final Object[] arguments;  // Live!
        private final Method method;
        private final Object myself;
        private final List<MethodInterceptor> interceptors;
        private final int index;
        private final Method proceed;
        private HashMap<String, Object> userData;
        
        private MethodInvocationImpl(Object[] arguments,
                Method method,
                Object myself,
                List<MethodInterceptor> interceptors,
                int index,
                Method proceed,
                HashMap<String, Object> userData) {
            this.arguments = arguments;
            this.method = method;
            this.myself = myself;
            this.interceptors = interceptors;
            this.index = index;
            this.proceed = proceed;
            this.userData = userData;
        }

        @Override
        public Object[] getArguments() {
            return arguments;
        }

        @Override
        public AccessibleObject getStaticPart() {
            return method;
        }

        @Override
        public Object getThis() {
            return myself;
        }

        @Override
        public Method getMethod() {
            return method;
        }
        
        @Override
        public Object proceed() throws Throwable {
            int newIndex = index + 1;
            if (newIndex >= interceptors.size()) {
                long methodTime = 0L;
                if (DEBUG_INTERCEPTION) {
                    methodTime = System.currentTimeMillis();
                }
                try {
                    // Call the actual method
                    return ReflectionHelper.invoke(myself, proceed, arguments, locator.getNeutralContextClassLoader());
                }
                finally {
                    if (DEBUG_INTERCEPTION) {
                        methodTime = System.currentTimeMillis() - methodTime;
                        
                        Logger.getLogger().debug("Time to call actual intercepted method " + method + " is " + methodTime + " milliseconds");
                    }
                }
            }
            
            // Invoke the next interceptor
            MethodInterceptor nextInterceptor = interceptors.get(newIndex);
            
            long aggregateInterceptionTime = 0L;
            if (DEBUG_INTERCEPTION) {
                aggregateInterceptionTime = System.currentTimeMillis();
                Logger.getLogger().debug("Invoking interceptor " + nextInterceptor.getClass().getName() +
                        " index " + newIndex + " in stack of " + interceptors.size() +
                        " of method " + method);
            }
            
            try {
                return nextInterceptor.invoke(new MethodInvocationImpl(arguments,
                    method, myself, interceptors, newIndex, proceed, userData));
            }
            finally {
                if (DEBUG_INTERCEPTION) {
                    aggregateInterceptionTime = System.currentTimeMillis() - aggregateInterceptionTime;
                    Logger.getLogger().debug("Interceptor " + nextInterceptor.getClass().getName() +
                            " index " + newIndex +
                            " took an aggregate of " + aggregateInterceptionTime + " milliseconds");
                }
            }
        }

        /* (non-Javadoc)
         * @see org.glassfish.hk2.api.HK2Invocation#setUserData(java.lang.String, java.lang.Object)
         */
        @Override
        public void setUserData(String key, Object data) {
            if (key == null) throw new IllegalArgumentException();
            
            if (userData == null) userData = new HashMap<String, Object>();
            
            if (data == null) {
                userData.remove(key);
            }
            else {
                userData.put(key, data);
            }
        }

        /* (non-Javadoc)
         * @see org.glassfish.hk2.api.HK2Invocation#getUserData(java.lang.String)
         */
        @Override
        public Object getUserData(String key) {
            if (key == null) throw new IllegalArgumentException();
            
            if (userData == null) return null;
            return userData.get(key);
        }
        
    }

}
