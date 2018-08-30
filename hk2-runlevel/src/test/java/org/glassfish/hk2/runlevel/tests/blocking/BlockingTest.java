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

package org.glassfish.hk2.runlevel.tests.blocking;

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
public class BlockingTest {
    @Test
    public void testBasicBlocking() throws InterruptedException, ExecutionException, TimeoutException {
        ServiceLocator locator = Utilities.getServiceLocator(
                ServiceA.class, ServiceB.class, ServiceC.class);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        controller.setMaximumUseableThreads(2);  // Must only be two threads in this controller
        
        RunLevelFuture future = controller.proceedToAsync(5);
        future.get(5, TimeUnit.SECONDS);
        Assert.assertTrue(future.isDone());
        
        Assert.assertNotNull(locator.getService(ServiceA.class));
        Assert.assertNotNull(locator.getService(ServiceB.class));
        Assert.assertNotNull(locator.getService(ServiceC.class));
    }

}
