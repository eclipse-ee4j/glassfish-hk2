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

package org.glassfish.hk2.runlevel.tests.blocking2;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.runlevel.RunLevelController;
import org.glassfish.hk2.runlevel.RunLevelFuture;
import org.glassfish.hk2.runlevel.tests.utilities.Utilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class Blocking2Test {
    /**
     * Tests that a WouldBlockException does not leak out into a getService call
     * 
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Test
    public void testIndirectBlockingDependency() throws InterruptedException, ExecutionException {
        ServiceLocator locator = Utilities.getServiceLocator(
                BlockingService.class,
                SingletonWithSneakyDependency.class,
                RunLevelServiceWithHiddenDependency.class,
                ExtraService.class);
        
        BlockingService.stop();
        SingletonWithSneakyDependency.setUseServiceHandle(false);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        
        controller.setMaximumUseableThreads(2);
        
        RunLevelFuture future = controller.proceedToAsync(1);
        try {
            future.get(1, TimeUnit.SECONDS);
            Assert.fail("Should not have succeeded, the blocking service is still blocking");
        }
        catch (TimeoutException te) {
            // success
        }
        Assert.assertFalse(future.isDone());
        
        Assert.assertFalse(SingletonWithSneakyDependency.isInitialized(0));
        
        BlockingService.go();
        
        Assert.assertTrue(SingletonWithSneakyDependency.isInitialized(5 * 1000));
        
        future.get();
        Assert.assertTrue(future.isDone());
    }
    
    /**
     * Tests that a WouldBlockException does not leak out into a getService call
     * from a ServiceHandle
     * 
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Test
    public void testIndirectBlockingDependencyWithServiceHandle() throws InterruptedException, ExecutionException {
        ServiceLocator locator = Utilities.getServiceLocator(
                BlockingService.class,
                SingletonWithSneakyDependency.class,
                RunLevelServiceWithHiddenDependency.class,
                ExtraService.class);
        
        BlockingService.stop();
        SingletonWithSneakyDependency.setUseServiceHandle(true);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        
        controller.setMaximumUseableThreads(2);
        
        RunLevelFuture future = controller.proceedToAsync(1);
        try {
            future.get(1, TimeUnit.SECONDS);
            Assert.fail("Should not have succeeded, the blocking service is still blocking");
        }
        catch (TimeoutException te) {
            // success
        }
        Assert.assertFalse(future.isDone());
        
        Assert.assertFalse(SingletonWithSneakyDependency.isInitialized(0));
        
        BlockingService.go();
        
        Assert.assertTrue(SingletonWithSneakyDependency.isInitialized(5 * 1000));
        
        future.get();
        Assert.assertTrue(future.isDone());
    }

}
