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

package org.glassfish.hk2.tests.locator.interception1;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.utilities.BuilderHelper;

/**
 * This one adds three interceptors, but the second one does not move
 * call proceed
 * 
 * @author jwells
 *
 */
@Singleton
public class MiddleInterceptorNoProceedService implements InterceptionService {

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InterceptionService#getDescriptorFilter()
     */
    @Override
    public Filter getDescriptorFilter() {
        return BuilderHelper.allFilter();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InterceptionService#getMethodInterceptors(java.lang.reflect.Method)
     */
    @Override
    public List<MethodInterceptor> getMethodInterceptors(Method method) {
        if (method.getName().equals("recordInput")) {
            ArrayList<MethodInterceptor> interceptors = new ArrayList<MethodInterceptor>();
            
            interceptors.add(new FirstInterceptor());
            interceptors.add(new SecondInterceptor());
            interceptors.add(new ThirdInterceptor());
            
            return interceptors;
        }
        
        return null;
    }
    
    private static class FirstInterceptor implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation arg0) throws Throwable {
            return arg0.proceed();
        }
        
    }
    
    /**
     * This one does not proceed
     * 
     * @author jwells
     *
     */
    private static class SecondInterceptor implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation arg0) throws Throwable {
            // On purpose!
            return null;
        }
        
    }
    
    /**
     * This always throws
     * 
     * @author jwells
     *
     */
    private static class ThirdInterceptor implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation arg0) throws Throwable {
            throw new AssertionError("Should not get called");
        }
        
    }

    @Override
    public List<ConstructorInterceptor> getConstructorInterceptors(
            Constructor<?> constructor) {
        return null;
    }

}
