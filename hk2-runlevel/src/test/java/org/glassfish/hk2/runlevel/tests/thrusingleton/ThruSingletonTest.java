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

package org.glassfish.hk2.runlevel.tests.thrusingleton;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.runlevel.RunLevelController;
import org.glassfish.hk2.runlevel.RunLevelController.ThreadingPolicy;
import org.glassfish.hk2.runlevel.RunLevelFuture;
import org.glassfish.hk2.runlevel.tests.utilities.Utilities;
import org.junit.Assert;
import org.junit.Test;


/**
 * @author jwells
 *
 */
public class ThruSingletonTest {
    /**
     * Tests that run-level services may get started through other contexts, such
     * as singleton context (even if there is a blocking condition)
     * @throws InterruptedException
     * @throws TimeoutException 
     * @throws ExecutionException 
     */
    @Test
    public void testRunLevelServiceStartedThroughSingletonAndBlocking() throws InterruptedException, ExecutionException, TimeoutException {
        ServiceLocator locator = Utilities.getServiceLocator(
                BlockingService.class,
                HighPriorityServiceOne.class,
                HighPriorityServiceTwo.class,
                LowPriorityForceService.class,
                SingletonServiceA.class);
        
        BlockingService.reset();
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        
        controller.setThreadingPolicy(ThreadingPolicy.FULLY_THREADED);
        controller.setMaximumUseableThreads(2);
        
        RunLevelFuture future = controller.proceedToAsync(5);
        
        Thread.sleep(100);
        
        Assert.assertFalse(future.isDone());
        
        BlockingService.go();
        
        future.get(1, TimeUnit.HOURS);
        
        Assert.assertTrue(future.isDone());
        
        Assert.assertEquals(5, controller.getCurrentRunLevel());   
    }
    
    /**
     * Tests that run-level services may get started through other contexts, such
     * as singleton context (even if there is a blocking condition)
     * @throws InterruptedException
     * @throws TimeoutException 
     * @throws ExecutionException 
     */
    @Test
    public void testRunLevelServiceStartedThroughPerLookupAndBlocking() throws InterruptedException, ExecutionException, TimeoutException {
        ServiceLocator locator = Utilities.getServiceLocator(
                BlockingService.class,
                HighPriorityServiceOne.class,
                HighPriorityServiceTwo.class,
                LowPriorityForceService.class,
                PerLookupService.class);
        BlockingService.reset();
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        
        controller.setThreadingPolicy(ThreadingPolicy.FULLY_THREADED);
        controller.setMaximumUseableThreads(2);
        
        RunLevelFuture future = controller.proceedToAsync(5);
        
        Thread.sleep(100);
        
        Assert.assertFalse(future.isDone());
        
        BlockingService.go();
        
        future.get(1, TimeUnit.HOURS);
        
        Assert.assertTrue(future.isDone());
        
        Assert.assertEquals(5, controller.getCurrentRunLevel());   
    }

}
