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

package org.glassfish.hk2.tests.interception.ordering;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Singleton;

import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.extras.interception.InterceptorOrderingService;

/**
 * @author jwells
 *
 */
@Singleton
public class Reverser implements InterceptorOrderingService {

    /* (non-Javadoc)
     * @see org.glassfish.hk2.extras.interception.InterceptorOrderingService#modifyMethodInterceptors(java.lang.reflect.Method, java.util.List)
     */
    @Override
    public List<ServiceHandle<MethodInterceptor>> modifyMethodInterceptors(Method method,
            List<ServiceHandle<MethodInterceptor>> currentList) {
        
        LinkedList<ServiceHandle<MethodInterceptor>> retVal = new LinkedList<ServiceHandle<MethodInterceptor>>();
        
        for (ServiceHandle<MethodInterceptor> mi : currentList) {
            retVal.addFirst(mi);
            
        }
        
        return retVal;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.extras.interception.InterceptorOrderingService#modifyConstructorInterceptors(java.lang.reflect.Constructor, java.util.List)
     */
    @Override
    public List<ServiceHandle<ConstructorInterceptor>> modifyConstructorInterceptors(
            Constructor<?> constructor, List<ServiceHandle<ConstructorInterceptor>> currentList) {
        LinkedList<ServiceHandle<ConstructorInterceptor>> retVal = new LinkedList<ServiceHandle<ConstructorInterceptor>>();
        
        for (ServiceHandle<ConstructorInterceptor> ci : currentList) {
            retVal.addFirst(ci);
            
        }
        
        return retVal;
    }

}
