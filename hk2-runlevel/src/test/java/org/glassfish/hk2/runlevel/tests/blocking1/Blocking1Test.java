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

package org.glassfish.hk2.runlevel.tests.blocking1;

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
public class Blocking1Test {
    /**
     * This test has too many services that would block.  There
     * are three services that would block, but only two threads.
     * We want to make sure the system does not thrash around in
     * this scenario and just eventually gives up and blocks.  This
     * is tested by having a per-lookup service that all the services
     * have a dependency which counts.  It should only get created
     * a certain number of times.<OL>
     * <LI>for the service that actually blocks (BlockingService)</LI>
     * <LI>for DependingService(1 or 2) that will not block</LI>
     * <LI>for DependingService(2 or 1) that will not block</LI>
     * <LI>for DependingService(1 or 2) that WILL block this time</LI>
     * </OL>
     * @throws TimeoutException 
     * @throws ExecutionException 
     * @throws InterruptedException 
     */
    @Test
    public void testTooManyBlockers() throws InterruptedException, ExecutionException, TimeoutException {
        ServiceLocator locator = Utilities.getServiceLocator(
                BlockingService.class,
                DependingService1.class,
                DependingService2.class,
                CountingDependency.class);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        controller.setMaximumUseableThreads(2);
        
        RunLevelFuture future = controller.proceedToAsync(5);
        try {
            future.get(1, TimeUnit.SECONDS);
            Assert.fail("Should not have succeeded, the blocking service is still blocking");
        }
        catch (TimeoutException te) {
            // success
        }
        Assert.assertFalse(future.isDone());
        
        Assert.assertTrue("Expecting fewer than 4 creations, but got " + CountingDependency.getCount(),
                CountingDependency.getCount() <= 4);
        
        BlockingService.go();
        
        future.get();
        Assert.assertTrue(future.isDone());
    }

}
