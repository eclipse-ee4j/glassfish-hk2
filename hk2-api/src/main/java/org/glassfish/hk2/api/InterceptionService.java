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

package org.glassfish.hk2.api;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.jvnet.hk2.annotations.Contract;

/**
 * This service is implemented in order to configure
 * interceptors on methods or constructors provided by
 * hk2 services.  All implementations must be in the 
 * {@link javax.inject.Singleton} scope.  Only services
 * that are created by HK2 are candidates for interception.
 * In particular services created by the provide method of
 * the {@link Factory} interface can not be intercepted.  
 * <p>
 * An implementation of InterceptionService must be in the Singleton scope.
 * Implementations of InterceptionService will be instantiated as soon as
 * they are added to HK2 in order to avoid deadlocks and circular references.
 * Therefore it is recommended that implementations of InterceptionService
 * make liberal use of {@link javax.inject.Provider} or {@link IterableProvider}
 * when injecting dependent services so that these services are not instantiated
 * when the InterceptionService is created
 * 
 * @author jwells
 */
@Contract
public interface InterceptionService {
    /**
     * If the returned filter returns true then the methods
     * of the service will be passed to {@link #getMethodInterceptors(Method)}
     * to determine if a method should be intercepted and the
     * constructor of the service will be passed to
     * {@link #getConstructorInterceptors(Constructor)} to
     * determine if the constructor should be intercepted
     * 
     * @return The filter that will be applied to a descriptor
     * to determine if it is to be intercepted.  Should not
     * return null
     */
    public Filter getDescriptorFilter();
    
    /**
     * Each non-final method of a service that passes the
     * {@link #getDescriptorFilter} method will be passed
     * to this method to determine if it will intercepted
     * 
     * @param method A non-final method that may
     * be intercepted
     * @return if null (or an empty list) then this method should
     * NOT be intercepted.  Otherwise the list of interceptors to
     * apply to this method
     */
    public List<MethodInterceptor> getMethodInterceptors(Method method);
    
    /**
     * The single chosen constructor of a service that passes the
     * {@link #getDescriptorFilter} method will be passed
     * to this method to determine if it will intercepted
     * 
     * @param constructor A constructor that may
     * be intercepted
     * @return if null (or an empty list) then this constructor should
     * NOT be intercepted.  Otherwise the list of interceptors to
     * apply to this method
     */
    public List<ConstructorInterceptor> getConstructorInterceptors(Constructor<?> constructor);

}
