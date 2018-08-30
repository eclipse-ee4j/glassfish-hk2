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

import java.lang.reflect.Proxy;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ProxyCtl;
import org.glassfish.hk2.api.ServiceLocator;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

/**
 * Utilities around proxying
 * 
 * @author jwells
 *
 */
public class ProxyUtilities {
    private final static Object proxyCreationLock = new Object();
    private final HashMap<ClassLoader, DelegatingClassLoader> superClassToDelegator = new HashMap<ClassLoader, DelegatingClassLoader>();
    
    /**
     * We put the anchor as the value even though we don't use it in order to
     * make it easier to catch memory leaks here.  See MemoryTest
     * 
     * @param superclass
     * @param interfaces
     * @param callback
     * @param useJDKProxy
     * @param anchor This is put into the WeakMap to make sure that IF this
     * map should leak that it will leak big, making it easier to detect
     * @return
     */
    private <T> T secureCreate(final Class<?> superclass,
            final Class<?>[] interfaces,
            final MethodHandler callback,
            boolean useJDKProxy,
            ServiceLocator anchor) {

        /* construct the classloader where the generated proxy will be created --
         * this classloader must have visibility into the javaassist classloader as well as
         * the superclass' classloader
         */
        
        final ClassLoader loader = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {

            @Override
            public ClassLoader run() {
                ClassLoader retVal = superclass.getClassLoader();
                if (retVal == null) {
                    try {
                        retVal = ClassLoader.getSystemClassLoader();
                    }
                    catch (SecurityException se) {
                        throw new IllegalStateException(
                                "Insufficient privilege to get system classloader while looking for classloader of " +
                                superclass.getName(), se);
                    }
                }
                if (retVal == null) {
                    throw new IllegalStateException("Could not find system classloader or classloader of " + superclass.getName());
                }
                
                return retVal;
            }
            
        });
        
        DelegatingClassLoader initDelegatingLoader;
        synchronized (superClassToDelegator) {
            initDelegatingLoader = superClassToDelegator.get(loader);
            if (initDelegatingLoader == null) {
                initDelegatingLoader = AccessController.doPrivileged(new PrivilegedAction<DelegatingClassLoader>() {

                    @Override
                    public DelegatingClassLoader run() {
                        return new DelegatingClassLoader(
                                loader,
                                ProxyFactory.class.getClassLoader(),
                                ProxyCtl.class.getClassLoader());
                    }
                    
                });
                
                superClassToDelegator.put(loader, initDelegatingLoader);
            }
        }
        
        final DelegatingClassLoader delegatingLoader = initDelegatingLoader;

        if (useJDKProxy) {
            return AccessController.doPrivileged(new PrivilegedAction<T>() {

                @SuppressWarnings("unchecked")
                @Override
                public T run() {
                    return (T) Proxy.newProxyInstance(delegatingLoader, interfaces,
                            new MethodInterceptorInvocationHandler(callback));
                }

            });

        }

        return AccessController.doPrivileged(new PrivilegedAction<T>() {

            @SuppressWarnings("unchecked")
            @Override
            public T run() {
                synchronized (proxyCreationLock) {
                    ProxyFactory.ClassLoaderProvider originalProvider = ProxyFactory.classLoaderProvider;
                    ProxyFactory.classLoaderProvider = new ProxyFactory.ClassLoaderProvider() {
                        
                        @Override
                        public ClassLoader get(ProxyFactory arg0) {
                            return delegatingLoader;
                        }
                    };
                    
                    try {
                        ProxyFactory proxyFactory = new ProxyFactory();
                        proxyFactory.setInterfaces(interfaces);
                        proxyFactory.setSuperclass(superclass);

                        Class<?> proxyClass = proxyFactory.createClass();

                        try {
                            T proxy = (T) proxyClass.newInstance();

                            ((ProxyObject) proxy).setHandler(callback);

                            return proxy;
                        } catch (Exception e1) {
                            throw new RuntimeException(e1);
                        }
                    }
                    finally {
                        ProxyFactory.classLoaderProvider = originalProvider;
                    }
                }
            }

        });

    }
    
    @SuppressWarnings("unchecked")
    public <T> T generateProxy(Class<?> requestedClass,
            ServiceLocatorImpl locator,
            ActiveDescriptor<T> root,
            ServiceHandleImpl<T> handle,
            Injectee injectee) {
        boolean isInterface = (requestedClass == null) ? false : requestedClass.isInterface() ;

        final Class<?> proxyClass;
        Class<?> iFaces[];
        if (isInterface) {
            proxyClass = requestedClass;
            iFaces = new Class<?>[2];
            iFaces[0] = proxyClass;
            iFaces[1] = ProxyCtl.class;
        }
        else {
            proxyClass = Utilities.getFactoryAwareImplementationClass(root);

            iFaces = Utilities.getInterfacesForProxy(root.getContractTypes());
        }

        T proxy;
        try {
            proxy = (T) secureCreate(proxyClass,
                iFaces,
                new MethodInterceptorImpl(locator, root, handle, injectee),
                isInterface, locator);
        }
        catch (Throwable th) {
            Exception addMe = new IllegalArgumentException("While attempting to create a Proxy for " + proxyClass.getName() +
                    " in scope " + root.getScope() + " an error occured while creating the proxy");

            if (th instanceof MultiException) {
                MultiException me = (MultiException) th;

                me.addError(addMe);

                throw me;
            }

            MultiException me = new MultiException(th);
            me.addError(addMe);
            throw me;
        }

        return proxy;
    }
    
    public void releaseCache() {
        synchronized (superClassToDelegator) {
            superClassToDelegator.clear();
        }
    }
}
