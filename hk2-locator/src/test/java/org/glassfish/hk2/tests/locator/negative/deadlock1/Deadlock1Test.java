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

package org.glassfish.hk2.tests.locator.negative.deadlock1;

import junit.framework.Assert;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This test is inspired by a deadlock that happened when a lock in
 * SingletonContext was held by two threads creating a Singleton
 * service.
 * <p>
 * This test attempts to recreate the deadlock by having a
 * Singleton service that is both created by itself and then
 * destroyed and which is also created because it is injected
 * into another service (which is per-lookup).
 * 
 * @author jwells
 *
 */
public class Deadlock1Test {
    private final static String TEST_NAME = "Deadlock1Test";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new Deadlock1Module());
    
    private final static long HEAVY_ITERATIONS = 1000;
    private final static long LIGHT_ITERATIONS = 100000;
    private final static int RUNNER1_THREADS = 1;
    private final static int RUNNER2_THREADS = 1;
    private final static int ALL_THREADS = RUNNER1_THREADS + RUNNER2_THREADS;
    
    private final Object lock = new Object();
    private boolean go = false;
    
    private final Object threadCountLock = new Object();
    private int threadCount = 0;
    
    @Test
    public void testDeadlock1() throws InterruptedException {
        for (int lcv = 0; lcv < RUNNER1_THREADS; lcv++) {
            Thread thread = new Thread(new Runnable1());
            thread.start();
        }
        
        for (int lcv = 0; lcv < RUNNER2_THREADS; lcv++) {
            Thread thread = new Thread(new Runnable2());
            thread.start();
        }
        
        synchronized (threadCountLock) {
            while (threadCount < ALL_THREADS) {
                threadCountLock.wait();
            }
        }
        
        synchronized (lock) {
            go = true;
            lock.notifyAll();
        }
        
        synchronized (threadCountLock) {
            long totalWaitTime = 5 * 60 * 1000;  // Give it five minutes
            
            while (totalWaitTime > 0 && threadCount > 0) {
                long startTime = System.currentTimeMillis();
                threadCountLock.wait(totalWaitTime);
                long elapsedTime = System.currentTimeMillis() - startTime;
                
                totalWaitTime -= elapsedTime;
            }
            
            Assert.assertTrue(totalWaitTime > 0);
            Assert.assertTrue(threadCount <= 0);
        }
        
    }
    
    /**
     * This thread just creates and destroys SimpleSingleton for the requested number of iterations
     * 
     * @author jwells
     *
     */
    private class Runnable1 implements Runnable {

        @Override
        public void run() {
            ActiveDescriptor<?> simpleSingletonDescriptor = locator.getBestDescriptor(
                    BuilderHelper.createContractFilter(SimpleSingleton.class.getName()));
            DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
            
            synchronized (threadCountLock) {
                threadCount++;
                threadCountLock.notify();
            }
            
            synchronized (lock) {
                while (!go) {
                    try {
                        lock.wait();
                    }
                    catch (InterruptedException e) {
                        throw new AssertionError(e);
                    }
                }
                
            }
            
            for (long lcv = 0; lcv < HEAVY_ITERATIONS; lcv++) {
                // Adding and removing the complex1Descriptor foils caching
                DynamicConfiguration config = dcs.createDynamicConfiguration();
                ActiveDescriptor<?> complex1Descriptor = config.addActiveDescriptor(ComplexService1.class);
                config.commit();
                
                locator.getService(ComplexService1.class);
                
                locator.getServiceHandle(simpleSingletonDescriptor).destroy();
                
                // This clears the hk2 cache, so we don't get foiled by caching
                ServiceLocatorUtilities.removeOneDescriptor(locator, complex1Descriptor);
            }
            
            synchronized (threadCountLock) {
                threadCount--;
                threadCountLock.notify();
            }
            
        }
        
    }
    
    /**
     * This thread looks up via contract and via getAllServices
     * (a different path through ServiceLocator)
     * 
     * @author jwells
     *
     */
    private class Runnable2 implements Runnable {

        @Override
        public void run() {
            
            synchronized (threadCountLock) {
                threadCount++;
                threadCountLock.notify();
            }
            
            synchronized (lock) {
                while (!go) {
                    try {
                        lock.wait();
                    }
                    catch (InterruptedException e) {
                        throw new AssertionError(e);
                    }
                }
                
            }
            
            for (long lcv = 0; lcv < LIGHT_ITERATIONS; lcv++) {
                locator.getAllServices(SimpleContract.class);
            }
            
            synchronized (threadCountLock) {
                threadCount--;
                threadCountLock.notify();
            }
            
        }
        
    }

}
