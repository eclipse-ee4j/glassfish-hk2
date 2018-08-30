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

package org.glassfish.hk2.tests.locator.interception2;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Singleton;

import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.ConstructorInvocation;
import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class ConstructorInterceptorTest {
    /**
     * Tests that an interceptor is called on a very basic service
     */
    @Test
    public void testBasicConstructorInterception() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(SimpleService.class,
                RecordingInterceptorService.class,
                RecordingInterceptor.class);
        
        locator.getService(SimpleService.class);
        
        RecordingInterceptor recorder = locator.getService(RecordingInterceptor.class);
        
        List<Constructor<?>> cCalled = recorder.getConstructorsCalled();
        Assert.assertEquals(1, cCalled.size());
        
    }
    
    /**
     * Ensures that all of the interceptors in a chain get called
     */
    @Test
    public void testMultipleConstructorInterceptors() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(SimpleService.class,
                ThreeInterceptionService.class);
        
        ThreeInterceptionService tis = locator.getService(ThreeInterceptionService.class);
        
        locator.getService(SimpleService.class);
        
        List<ConstructorInterceptor> interceptors = tis.getConstructorInterceptors(null);
        
        InterceptorOne i1 = (InterceptorOne) interceptors.get(0);
        InterceptorTwo i2 = (InterceptorTwo) interceptors.get(1);
        InterceptorThree i3 = (InterceptorThree) interceptors.get(2);
        
        Assert.assertEquals(1, i1.wasCalled());
        Assert.assertEquals(1, i2.wasCalled());
        Assert.assertEquals(1, i3.wasCalled());
    }
    
    private static class InterceptorOne implements ConstructorInterceptor {
        private int called;

        @Override
        public Object construct(ConstructorInvocation invocation) throws Throwable {
            called++;
            return invocation.proceed();
        }
        
        private int wasCalled() { return called; }
        
    }
    
    private static class InterceptorTwo implements ConstructorInterceptor {
        private int called;

        @Override
        public Object construct(ConstructorInvocation invocation) throws Throwable {
            called++;
            return invocation.proceed();
        }
        
        private int wasCalled() { return called; }
        
    }
    
    private static class InterceptorThree implements ConstructorInterceptor {
        private int called;

        @Override
        public Object construct(ConstructorInvocation invocation) throws Throwable {
            called++;
            return invocation.proceed();
        }
        
        private int wasCalled() { return called; }
    }
    
    @Singleton
    private static class ThreeInterceptionService implements InterceptionService {
        private final LinkedList<ConstructorInterceptor> interceptors;
        
        private ThreeInterceptionService() {
            interceptors = new LinkedList<ConstructorInterceptor>();
            
            interceptors.add(new InterceptorOne());
            interceptors.add(new InterceptorTwo());
            interceptors.add(new InterceptorThree());
        }

        @Override
        public Filter getDescriptorFilter() {
            return BuilderHelper.createContractFilter(SimpleService.class.getName());
        }

        @Override
        public List<MethodInterceptor> getMethodInterceptors(Method method) {
            return null;
        }

        @Override
        public List<ConstructorInterceptor> getConstructorInterceptors(
                Constructor<?> constructor) {
            return interceptors;
        }
        
    }
}
