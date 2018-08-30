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

package org.glassfish.hk2.extras.interception;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.jvnet.hk2.annotations.Contract;

/**
 * This service can be used to modify, add or remove interceptors
 * to the set of interceptors that will be called on a Method
 * or Constructor in the default implementation of the
 * interception service.  If there are multiple implementations
 * of this service then they will be called in the natural
 * hk2 ordering of services with the result of the method being
 * fed into the next service.
 * 
 * @author jwells
 *
 */
@Contract
public interface InterceptorOrderingService {
    
    /**
     * This method is called for each method that may be intercepted by the default
     * interception service.  The incoming list is not modifiable.  If this method
     * returns null then the original list (the list passed in) will be used as-is.
     * If this method does NOT return null then the list returned will be the list
     * that will be used to intercept the given method.  This means that the interceptors
     * can be removed (if an empty list is returned) or modified.  Modifications can
     * include changes of order, additions and/or removals of the interceptors
     * passed into the method.  If this method throws an exception the exception
     * will be ignored and the interceptor list passed in will be used.
     * <p>
     * If the implementation would like to return MethodInterceptors that are not hk2
     * services it is recommended that they use {@link BuilderHelper#createConstantServiceHandle(Object)}
     * to create ServiceHandles representing their MethodInterceptors.
     *  
     * @param method The method that is to be intercepted
     * @param currentList The list that will be used to intercept the method if this
     * service returns null
     * @return A non-null list of interceptors to use when intercepting this method.  The
     * returned list must be ordered.  If this method returns null then the list passed
     * in will be used
     */
    public List<ServiceHandle<MethodInterceptor>> modifyMethodInterceptors(Method method, List<ServiceHandle<MethodInterceptor>> currentList);
    
    /**
     * This method is called for each constructor that may be intercepted by the default
     * interception service.  The incoming list is not modifiable.  If this method
     * returns null then the original list (the list passed in) will be used as-is.
     * If this method does NOT return null then the list returned will be the list
     * that will be used to intercept the given constructor.  This means that the interceptors
     * can be removed (if an empty list is returned) or modified.  Modifications can
     * include changes of order, additions and/or removals of the interceptors
     * passed into the method.  If this method throws an exception the exception
     * will be ignored and the interceptor list passed in will be used.
     * <p>
     * If the implementation would like to return ConstructorInterceptors that are not hk2
     * services it is recommended that they use {@link BuilderHelper#createConstantServiceHandle(Object)}
     * to create ServiceHandles representing their ConstructorInterceptors.
     *  
     * @param constructor The constructor that is to be intercepted
     * @param currentList The list that will be used to intercept the constructor if this
     * service returns null
     * @return A non-null list of interceptors to use when intercepting this constructor.  The
     * returned list must be ordered.  If this method returns null then the list passed
     * in will be used
     */
    public List<ServiceHandle<ConstructorInterceptor>> modifyConstructorInterceptors(Constructor<?> constructor, List<ServiceHandle<ConstructorInterceptor>> currentList);

}
