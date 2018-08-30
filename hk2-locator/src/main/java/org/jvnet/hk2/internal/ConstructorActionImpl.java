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

package org.jvnet.hk2.internal;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.ProxyFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.hk2.api.AOPProxyCtl;
import org.glassfish.hk2.utilities.reflection.Logger;

/**
 * @author jwells
 *
 */
final class ConstructorActionImpl<T> implements ConstructorAction {
    private final static Class<?> ADDED_INTERFACES[] = { AOPProxyCtl.class };
    private final static MethodFilter METHOD_FILTER = new MethodFilter() {

        @Override
        public boolean isHandled(Method method) {
            // We do not allow interception of finalize
            if (method.getName().equals("finalize")) return false;
            
            return true;
        }
        
    };
    
    /**
     * 
     */
    private final ClazzCreator<T> clazzCreator;
    
    /**
     * 
     */
    private final Map<Method, List<MethodInterceptor>> methodInterceptors;

    /**
     * @param methodInterceptors
     * @param clazzCreator TODO
     */
    ConstructorActionImpl(
            ClazzCreator<T> clazzCreator, Map<Method, List<MethodInterceptor>> methodInterceptors) {
        this.clazzCreator = clazzCreator;
        this.methodInterceptors = methodInterceptors;
    }

    @Override
    public Object makeMe(final Constructor<?> c, final Object[] args, final boolean neutralCCL)
            throws Throwable {
        final MethodInterceptorHandler methodInterceptor = new MethodInterceptorHandler(
                clazzCreator.getServiceLocator(),
                clazzCreator.getUnderlyingDescriptor(),
                methodInterceptors);
            
        final ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setSuperclass(clazzCreator.getImplClass());
        proxyFactory.setFilter(METHOD_FILTER);
        proxyFactory.setInterfaces(ADDED_INTERFACES);
        
        return AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {

            @Override
            public Object run() throws Exception {
                ClassLoader currentCCL = null;
                if (neutralCCL) {
                    currentCCL = Thread.currentThread().getContextClassLoader();
                }
          
                try {
                  return proxyFactory.create(c.getParameterTypes(), args, methodInterceptor);
                }
                catch (InvocationTargetException ite) {
                    Throwable targetException = ite.getTargetException();
                    Logger.getLogger().debug(c.getDeclaringClass().getName(), c.getName(), targetException);
                    if (targetException instanceof Exception) {
                        throw (Exception) targetException;
                    }
                    throw new RuntimeException(targetException);
                }
                finally {
                    if (neutralCCL) {
                        Thread.currentThread().setContextClassLoader(currentCCL);
                    }
                }
            }
                
        });
    }
}
