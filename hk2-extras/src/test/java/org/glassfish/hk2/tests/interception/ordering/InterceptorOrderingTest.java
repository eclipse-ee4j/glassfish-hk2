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

import java.util.List;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.extras.internal.Utilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class InterceptorOrderingTest {
    /**
     * Tests that we can reverse the natural ordering
     * of interceptors
     */
    @Test // @org.junit.Ignore
    public void testReverseOrdering() {
        ServiceLocator locator = Utilities.getUniqueLocator(AService.class,
                ConstructorInterceptorOne.class,
                ConstructorInterceptorTwo.class,
                ConstructorInterceptorThree.class,
                Recorder.class,
                MethodInterceptorOne.class,
                MethodInterceptorTwo.class,
                MethodInterceptorThree.class,
                Reverser.class);
        
        AService aService = locator.getService(AService.class);
        aService.callMe();
        
        Recorder recorder = locator.getService(Recorder.class);
        
        // Three constructor interceptors, three method interceptors
        List<Object> interceptors = recorder.get();
        
        Assert.assertEquals(6, interceptors.size());
        
        // The order should be REVERSED (3-2-1)
        Assert.assertEquals(ConstructorInterceptorThree.class, interceptors.get(0).getClass());
        Assert.assertEquals(ConstructorInterceptorTwo.class, interceptors.get(1).getClass());
        Assert.assertEquals(ConstructorInterceptorOne.class, interceptors.get(2).getClass());
        
        Assert.assertEquals(MethodInterceptorThree.class, interceptors.get(3).getClass());
        Assert.assertEquals(MethodInterceptorTwo.class, interceptors.get(4).getClass());
        Assert.assertEquals(MethodInterceptorOne.class, interceptors.get(5).getClass());
        
    }
    
    /**
     * Has multiple ordering services, one of which returns the original
     * list, one of which is the reverser and one of which adds services
     * to the start and end of the list (and runs last due to low rank)
     */
    @Test // @org.junit.Ignore
    public void testOrderingServiceChain() {
        ServiceLocator locator = Utilities.getUniqueLocator(AService.class,
                ConstructorInterceptorOne.class,
                ConstructorInterceptorTwo.class,
                ConstructorInterceptorThree.class,
                Recorder.class,
                MethodInterceptorOne.class,
                MethodInterceptorTwo.class,
                MethodInterceptorThree.class,
                AddToBeginningAndEndOrderer.class,
                Reverser.class,
                DoNothingOrderer.class);
        
        AService aService = locator.getService(AService.class);
        aService.callMe();
        
        Recorder recorder = locator.getService(Recorder.class);
        
        // Three constructor interceptors, three method interceptors
        List<Object> interceptors = recorder.get();
        
        Assert.assertEquals(10, interceptors.size());
        
        // Zero should be first (and NOT re-ordered)
        Assert.assertEquals(NonServiceConstructorInterceptorZero.class, interceptors.get(0).getClass());
        
        // The order should be REVERSED (3-2-1)
        Assert.assertEquals(ConstructorInterceptorThree.class, interceptors.get(1).getClass());
        Assert.assertEquals(ConstructorInterceptorTwo.class, interceptors.get(2).getClass());
        Assert.assertEquals(ConstructorInterceptorOne.class, interceptors.get(3).getClass());
        
        // Inifinity should be last (and NOT re-ordered)
        Assert.assertEquals(NonServiceConstructorInterceptorInfinity.class, interceptors.get(4).getClass());
        
        // Zero should be first (and NOT re-ordered)
        Assert.assertEquals(NonServiceMethodInterceptorZero.class, interceptors.get(5).getClass());
        
        // The order should be REVERSED (3-2-1)
        Assert.assertEquals(MethodInterceptorThree.class, interceptors.get(6).getClass());
        Assert.assertEquals(MethodInterceptorTwo.class, interceptors.get(7).getClass());
        Assert.assertEquals(MethodInterceptorOne.class, interceptors.get(8).getClass());
        
        // Infinity should be last (and NOT re-ordered)
        Assert.assertEquals(NonServiceMethodInterceptorInfinity.class, interceptors.get(9).getClass());
    }
    
    /**
     * Tests that we can reverse the natural ordering
     * of interceptors
     */
    @Test // @org.junit.Ignore
    public void testReverseOrderingWithThrowingOrderer() {
        ServiceLocator locator = Utilities.getUniqueLocator(AService.class,
                ConstructorInterceptorOne.class,
                ConstructorInterceptorTwo.class,
                ConstructorInterceptorThree.class,
                Recorder.class,
                MethodInterceptorOne.class,
                MethodInterceptorTwo.class,
                MethodInterceptorThree.class,
                Reverser.class,
                ThrowingOrderer.class);
        
        AService aService = locator.getService(AService.class);
        aService.callMe();
        
        Recorder recorder = locator.getService(Recorder.class);
        
        // Three constructor interceptors, three method interceptors
        List<Object> interceptors = recorder.get();
        
        Assert.assertEquals(6, interceptors.size());
        
        // The order should be REVERSED (3-2-1)
        Assert.assertEquals(ConstructorInterceptorThree.class, interceptors.get(0).getClass());
        Assert.assertEquals(ConstructorInterceptorTwo.class, interceptors.get(1).getClass());
        Assert.assertEquals(ConstructorInterceptorOne.class, interceptors.get(2).getClass());
        
        Assert.assertEquals(MethodInterceptorThree.class, interceptors.get(3).getClass());
        Assert.assertEquals(MethodInterceptorTwo.class, interceptors.get(4).getClass());
        Assert.assertEquals(MethodInterceptorOne.class, interceptors.get(5).getClass());
        
    }

}
