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

package org.glassfish.hk2.runlevel.tests.cancel;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.runlevel.RunLevelController;
import org.glassfish.hk2.runlevel.RunLevelController.ThreadingPolicy;
import org.glassfish.hk2.runlevel.RunLevelFuture;
import org.glassfish.hk2.runlevel.tests.utilities.Utilities;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class CancelTest {
    /**
     * Tests that forced cancel will work even if services are blocking
     * @throws TimeoutException 
     * @throws ExecutionException 
     * @throws InterruptedException 
     */
    @Test
    public void testForcedCancel() throws InterruptedException, ExecutionException, TimeoutException {
        ServiceLocator locator = Utilities.getServiceLocator(BlockingService.class);
        
        BlockingService.clear();
        
        RunLevelController rlc = locator.getService(RunLevelController.class);
        rlc.setThreadingPolicy(ThreadingPolicy.FULLY_THREADED);
        rlc.setCancelTimeoutMilliseconds(500);
        
        Assert.assertEquals(500L, rlc.getCancelTimeoutMilliseconds());
        
        try {
            rlc.proceedToAsync(5);
        
            RunLevelFuture future = rlc.getCurrentProceeding();
            Assert.assertNotNull(future);
            
            // Makes sure the postConstruct of the BlockingService has been invoked
            BlockingService.waitForPostConstruct();
            
            Assert.assertFalse(future.isDone());
        
            // This should force the issue
            future.cancel(true);
        
            future.get(10, TimeUnit.SECONDS);
        
            Assert.assertTrue(future.isDone());
        }
        finally {
            // Don't hold onto this thread forever
            BlockingService.go();
        }
        
        // Note that after the blocking service has been told to go, it should
        // now be possible to go back up to five.  This sleep gives time to
        // let the BlockingService complete
        Thread.sleep(100);
        
        Assert.assertEquals(4, rlc.getCurrentRunLevel());
        
        // A hard-cancelled service should NOT be pre-destroyed
        Assert.assertFalse(BlockingService.getPreDestroyCalled());
        
        // Now reset blocking service, but also let it go to ensure
        // that since it did clear out it CAN be started again
        
        BlockingService.clear();
        BlockingService.go();
        
        // Should work
        rlc.proceedTo(5);
        
        Assert.assertTrue(BlockingService.getPostConstructCalled());
    }
    
    /**
     * This test has one thread where the DependsOnBlockingService depends
     * on the BlockingService which will sit around until we tell it to go.
     * We do a non-force cancel and then let the BlockingService go.  The
     * DependsOnBlockingService should NOT be postConstructed because the
     * cancel should have cancelled it
     * @throws InterruptedException 
     * @throws TimeoutException 
     * @throws ExecutionException 
     */
    @Test
    public void testThingsDependingOnServicesCaughtByCancelAreNotCreated() throws InterruptedException, ExecutionException, TimeoutException {
        // NOTE: DependsOnBlockingService MUST come first so that it'll be
        // on the stack in the one thread
        ServiceLocator locator = Utilities.getServiceLocator(DependsOnBlockingService.class,
                BlockingService.class);
        
        BlockingService.clear();
        DependsOnBlockingService.clear();
        
        try {
            RunLevelController rlc = locator.getService(RunLevelController.class);
            rlc.setThreadingPolicy(ThreadingPolicy.FULLY_THREADED);
            rlc.setMaximumUseableThreads(1);
            
            rlc.proceedToAsync(5);
            
            RunLevelFuture future = rlc.getCurrentProceeding();
            Assert.assertNotNull(future);
            
            // Makes sure the postConstruct of the BlockingService has been invoked
            BlockingService.waitForPostConstruct();
            
            Assert.assertFalse(future.isDone());
        
            // Do NOT force the issue, let the thing complete
            future.cancel(false);
            
            BlockingService.go();
            
            // The DependsOnMeBlocking service should NOT be started/initialized
            
            future.get(5, TimeUnit.SECONDS);
            
            Assert.assertTrue(future.isDone());
            
            Assert.assertFalse(DependsOnBlockingService.getPostConstructCalled());
            
            Assert.assertTrue(BlockingService.getPreDestroyCalled());
            
        }
        finally {
            BlockingService.go();
            
        }
        
    }
    
    /**
     * Tests that when going down that a blocked PreDestroy
     * can be cancelled with a cancel call
     * @throws ExecutionException 
     * @throws InterruptedException 
     * @throws TimeoutException 
     */
    @Test
    public void testDownCancelWithBlockedService() throws InterruptedException, ExecutionException, TimeoutException {
        ServiceLocator locator = Utilities.getServiceLocator(BlockingPreDestroyService.class,
                CountingDestructionService.class,
                CountingDestructionService2.class);
        
        BlockingPreDestroyService.clear();
        CountingDestructionService.clear();
        CountingDestructionService2.clear();
        
        try {
            RunLevelController rlc = locator.getService(RunLevelController.class);
            rlc.setThreadingPolicy(ThreadingPolicy.FULLY_THREADED);
            rlc.setMaximumUseableThreads(1);
            rlc.setCancelTimeoutMilliseconds(1000);
            
            RunLevelFuture future = rlc.proceedToAsync(5);
            future.get();
            
            // Going down, but one service will block
            future = rlc.proceedToAsync(0);
            
            Thread.sleep(100);
            
            Assert.assertFalse(future.isDone());
            
            rlc.cancel();
            
            future.get(5, TimeUnit.SECONDS);
            
            BlockingPreDestroyService.go();
            
            Assert.assertEquals(1, CountingDestructionService.getDestructionCount());
            Assert.assertEquals(1, CountingDestructionService2.getDestructionCount());
            
            // The cancel would have made it stop at 4
            Assert.assertEquals(4, rlc.getCurrentRunLevel());
        }
        finally {
            BlockingPreDestroyService.go();
        }
        
    }
    
    /**
     * Test two blocking services.  The first service only blocks for a little,
     * but it will set the timer going.  We need to make sure that it'll still
     * work if a *second* service blocks forever
     * 
     * @throws TimeoutException 
     * @throws ExecutionException 
     * @throws InterruptedException 
     */
    @Test
    public void testDownCancelWithTwoBlockedServices() throws InterruptedException, ExecutionException, TimeoutException {
        ServiceLocator locator = Utilities.getServiceLocator(BlockingPreDestroyService.class,
                CountingDestructionService.class,
                CountingDestructionService2.class,
                BlockingPreDestroyService2.class);
        
        BlockingPreDestroyService.clear();
        BlockingPreDestroyService2.clear();
        CountingDestructionService.clear();
        CountingDestructionService2.clear();
        
        try {
            RunLevelController rlc = locator.getService(RunLevelController.class);
            rlc.setThreadingPolicy(ThreadingPolicy.FULLY_THREADED);
            rlc.setMaximumUseableThreads(1);
            rlc.setCancelTimeoutMilliseconds(1000);
            
            RunLevelFuture future = rlc.proceedToAsync(5);
            future.get();
            
            // Going down, but the first service will block (BlockingPreDestroyService2)
            future = rlc.proceedToAsync(0);
            
            BlockingPreDestroyService2.waitUntilInPreDestroy();
            
            Assert.assertFalse(future.isDone());
            
            rlc.cancel();
            
            BlockingPreDestroyService2.go();
            
            future.get(5, TimeUnit.SECONDS);
            
            BlockingPreDestroyService.go();
            
            Assert.assertEquals(1, CountingDestructionService.getDestructionCount());
            Assert.assertEquals(1, CountingDestructionService2.getDestructionCount());
            
            // The cancel would have made it stop at 4
            Assert.assertEquals(4, rlc.getCurrentRunLevel());
        }
        finally {
            BlockingPreDestroyService.go();
            BlockingPreDestroyService2.go();
        }
        
    }
    
    /**
     * Tests that a blocked cancelled downward service can
     * be cancelled, waited for and continued downward on
     * a different thread
     * @throws Throwable Lots of things can fail
     */
    @Test
    public void testRestartStalledDownOnDifferentThread() throws Throwable {
        ServiceLocator locator = Utilities.getServiceLocator(BlockingPreDestroyService.class,
                CountingDestructionService.class,
                LevelThreeService.class);
        
        CountingDestructionService.clear();
        BlockingPreDestroyService.clear();
        
        try {
            RunLevelController rlc = locator.getService(RunLevelController.class);
            rlc.setThreadingPolicy(ThreadingPolicy.FULLY_THREADED);
            rlc.setMaximumUseableThreads(1);
            rlc.setCancelTimeoutMilliseconds(1000);
            
            rlc.proceedTo(5);
            
            LevelThreeService lts = locator.getService(LevelThreeService.class);
            Assert.assertFalse(lts.isShutdown());
            
            RunLevelFuture future = rlc.proceedToAsync(0);
            
            // Now start thread that will truly go down
            GoesTheRestOfTheWayDown downer = new GoesTheRestOfTheWayDown(rlc);
            Thread th = new Thread(downer);
            th.start();
            
            future.get(20, TimeUnit.SECONDS);
            
            downer.waitForCompletion();
            
            Assert.assertTrue(lts.isShutdown());
            
            Assert.assertEquals(0, rlc.getCurrentRunLevel());
            
        }
        finally {
            BlockingPreDestroyService.go();
        }
        
    }
    
    private static class GoesTheRestOfTheWayDown implements Runnable {
        private final RunLevelController rlc;
        private final Object waiterLock = new Object();
        private boolean done = false;
        private Throwable exception = null;
        
        private GoesTheRestOfTheWayDown(RunLevelController rlc) {
            this.rlc = rlc;
        }
        
        private void internalRun() throws Throwable {
            RunLevelFuture currentProceeding = rlc.getCurrentProceeding();
            Assert.assertNotNull(currentProceeding);
            
            currentProceeding.cancel(false);
            
            currentProceeding.get(20, TimeUnit.SECONDS);
            
            // OK, the rest of the way down is done from *this* thread
            
            rlc.proceedTo(0);
        }

        @Override
        public void run() {
            try {
                internalRun();
                synchronized (waiterLock) {
                    done = true;
                    waiterLock.notify();
                }
            }
            catch (Throwable th) {
                synchronized (waiterLock) {
                    exception = th;
                    waiterLock.notify();
                }
            }
            
        }
        
        private void waitForCompletion() throws Throwable {
            synchronized (waiterLock) {
                while((exception == null) && !done) {
                    waiterLock.wait();
                }
                
                if (exception != null) throw exception;
            }
        }
    }

}
