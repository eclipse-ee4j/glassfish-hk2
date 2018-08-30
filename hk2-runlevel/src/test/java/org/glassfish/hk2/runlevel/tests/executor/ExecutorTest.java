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

package org.glassfish.hk2.runlevel.tests.executor;

import java.util.concurrent.Executor;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.runlevel.RunLevelController;
import org.glassfish.hk2.runlevel.tests.utilities.Utilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class ExecutorTest {
    @Test
    public void testCustomExecutor() {
        ServiceLocator locator = Utilities.getServiceLocator(
                ThreadNameRecorderService.class);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        
        controller.proceedTo(5);
        
        ThreadNameRecorderService service = locator.getService(ThreadNameRecorderService.class);
        Assert.assertFalse(ExecutorImpl.THREAD_NAME.equals(service.getThreadName()));
        
        controller.proceedTo(4);
        
        controller.setExecutor(new ExecutorImpl());
        
        controller.proceedTo(5);
        
        service = locator.getService(ThreadNameRecorderService.class);
        Assert.assertTrue(ExecutorImpl.THREAD_NAME.equals(service.getThreadName()));
        
        controller.proceedTo(4);
        
        controller.setExecutor(null);
        
        controller.proceedTo(5);
        
        service = locator.getService(ThreadNameRecorderService.class);
        Assert.assertFalse(ExecutorImpl.THREAD_NAME.equals(service.getThreadName()));
    }
    
    @Test
    public void testGetExecutor() {
        ServiceLocator locator = Utilities.getServiceLocator(
                ThreadNameRecorderService.class);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        
        Executor defaultExecutor = controller.getExecutor();
        Assert.assertNotNull(defaultExecutor);
        
        Executor myExecutor = new ExecutorImpl();
        
        controller.setExecutor(myExecutor);
        
        Assert.assertEquals(myExecutor, controller.getExecutor());
        
        Executor secondExecutor = new ExecutorImpl();
        
        controller.setExecutor(secondExecutor);
        
        Assert.assertEquals(secondExecutor, controller.getExecutor());
        
        controller.setExecutor(null);
        
        Assert.assertEquals(defaultExecutor, controller.getExecutor());
    }
    
    public static class ExecutorImpl implements Executor {
        public final static String THREAD_NAME = "TestThread";

        @Override
        public void execute(Runnable command) {
            Thread t = new Thread(command);
            t.setDaemon(true);
            t.setName(THREAD_NAME);
            
            t.start();
        }
        
    }

}
