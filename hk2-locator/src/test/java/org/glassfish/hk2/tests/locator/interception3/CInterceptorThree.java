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

package org.glassfish.hk2.tests.locator.interception3;

import junit.framework.Assert;

import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.ConstructorInvocation;
import org.glassfish.hk2.api.HK2Invocation;

/**
 * @author jwells
 *
 */
public class CInterceptorThree implements ConstructorInterceptor {

    /* (non-Javadoc)
     * @see org.aopalliance.intercept.ConstructorInterceptor#construct(org.aopalliance.intercept.ConstructorInvocation)
     */
    @Override
    public Object construct(ConstructorInvocation arg0) throws Throwable {
        HK2Invocation invocation = (HK2Invocation) arg0;
        
        Assert.assertEquals(1, ((CounterService) invocation.getUserData(HK2InvocationTest.COUNTER_1)).getAndIncrement());
        Assert.assertEquals(0, ((CounterService) invocation.getUserData(HK2InvocationTest.COUNTER_2)).getAndIncrement());
        Assert.assertNull(invocation.getUserData(HK2InvocationTest.COUNTER_3));
        Assert.assertNull(invocation.getUserData(HK2InvocationTest.COUNTER_4));
        
        invocation.setUserData(HK2InvocationTest.COUNTER_4, new CounterService());
        
        Object retVal = arg0.proceed();
        
        // Added by three on up, incremented by three on the way down
        Assert.assertEquals(0, ((CounterService) invocation.getUserData(HK2InvocationTest.COUNTER_4)).getAndIncrement());
        
        // Add three on the way down
        invocation.setUserData(HK2InvocationTest.COUNTER_3, new CounterService());
        
        // Incremented by three on up and down
        Assert.assertEquals(1, ((CounterService) invocation.getUserData(HK2InvocationTest.COUNTER_2)).getAndIncrement());
        
        // Incremented up and down by two and three
        Assert.assertEquals(2, ((CounterService) invocation.getUserData(HK2InvocationTest.COUNTER_1)).getAndIncrement());
        
        return retVal;
    }

}
