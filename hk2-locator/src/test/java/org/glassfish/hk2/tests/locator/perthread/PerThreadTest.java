/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.locator.perthread;

import java.util.HashSet;

import junit.framework.Assert;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.PerThreadScopeModule;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Before;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class PerThreadTest {
    private final static String TEST_NAME = "PerThradTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new PerThreadModule());
    private final static int NUM_LOOKUPS = 10000;
    private final static int NUM_SHIRT_THREADS = 10;
    
    private final Object lock = new Object();
    private int numFinished = 0;
    private int shirtThreadsDone = 0;
    
    @Before
    public void before() {
        ServiceLocatorUtilities.enablePerThreadScope(locator);
        
        // Doing this twice ensures the idempotence of this call
        ServiceLocatorUtilities.enablePerThreadScope(locator);
    }
    
    /**
     * Tests we get different values per thread
     * 
     * @throws InterruptedException
     */
    @Test // @org.junit.Ignore
    public void testPerThread() throws InterruptedException {
        synchronized (lock) {
            numFinished = 0;
        }
        
        StoreRunner runner1 = new StoreRunner(locator);
        StoreRunner runner2 = new StoreRunner(locator);
        StoreRunner runner3 = new StoreRunner(locator);
        
        Thread thread1 = new Thread(runner1);
        Thread thread2 = new Thread(runner2);
        Thread thread3 = new Thread(runner3);
        
        thread1.start();
        thread2.start();
        thread3.start();
        
        synchronized (lock) {
            while (numFinished < 3) {
                lock.wait();
            }
        }
        
        ClothingStore store1 = runner1.store;
        ClothingStore store2 = runner2.store;
        ClothingStore store3 = runner3.store;
        
        Pants pants1 = store1.check();
        Pants pants2 = store2.check();
        Pants pants3 = store3.check();
        
        Assert.assertNotSame(pants1, pants2);
        Assert.assertNotSame(pants1, pants3);
        Assert.assertNotSame(pants2, pants3);
    }
    
    /**
     * Tests we get the same value perThread on multiple lookups
     * 
     * @throws InterruptedException
     */
    @Test // @org.junit.Ignore
    public void testSameValuePerThread() throws InterruptedException {
        ServiceLocator locator = LocatorHelper.create();
        ServiceLocatorUtilities.enablePerThreadScope(locator);
        ServiceLocatorUtilities.addClasses(locator, ShirtFactory.class);
        
        HashSet<Shirt> collector = new HashSet<Shirt>();
        
        Thread threads[] = new Thread[NUM_SHIRT_THREADS];
        for (int lcv = 0; lcv < NUM_SHIRT_THREADS; lcv++) {
            ShirtRunner runner = new ShirtRunner(locator, collector);
            
            threads[lcv] = new Thread(runner);
        }
        
        for (int lcv = 0; lcv < NUM_SHIRT_THREADS; lcv++) {
            threads[lcv].start();
        }
        
        synchronized (lock) {
            while (shirtThreadsDone < NUM_SHIRT_THREADS) {
                lock.wait();
            }
        }
        
        Assert.assertEquals(NUM_SHIRT_THREADS, collector.size());
    }
    
    /**
     * Tests we get different values per thread
     * 
     * @throws InterruptedException
     */
    @Test // @org.junit.Ignore
    public void testPerThreadWithModule() throws InterruptedException {
        synchronized (lock) {
            numFinished = 0;
        }
        
        ServiceLocator locator = ServiceLocatorUtilities.bind(new PerThreadScopeModule());
        ServiceLocatorUtilities.addClasses(locator, ClothingStore.class, Pants.class);
        
        StoreRunner runner1 = new StoreRunner(locator);
        StoreRunner runner2 = new StoreRunner(locator);
        StoreRunner runner3 = new StoreRunner(locator);
        
        Thread thread1 = new Thread(runner1);
        Thread thread2 = new Thread(runner2);
        Thread thread3 = new Thread(runner3);
        
        thread1.start();
        thread2.start();
        thread3.start();
        
        synchronized (lock) {
            while (numFinished < 3) {
                lock.wait();
            }
        }
        
        ClothingStore store1 = runner1.store;
        ClothingStore store2 = runner2.store;
        ClothingStore store3 = runner3.store;
        
        Pants pants1 = store1.check();
        Pants pants2 = store2.check();
        Pants pants3 = store3.check();
        
        Assert.assertNotSame(pants1, pants2);
        Assert.assertNotSame(pants1, pants3);
        Assert.assertNotSame(pants2, pants3);
    }
    
    private final static int NUM_MANY_THREADS = 100;
    
    /**
     * Tests a single extra thread but with a large number of children
     * service locators.  This test exhibits a memory leak in the
     * PerThreadContext since the children descriptors never leave
     * the map of the PerThreadContext
     * 
     * @throws InterruptedException
     */
    @Test // @org.junit.Ignore
    public void testManyChildLocatorsOneThread() throws InterruptedException {
        synchronized (lock) {
            numFinished = 0;
        }
        
        ServiceLocator locator = LocatorHelper.create();
        ServiceLocatorUtilities.bind(locator, new PerThreadScopeModule());
        
        Worker worker = new Worker();
        Thread t = new Thread(worker);
        t.start();
        
        try {
            for (int lcv = 0; lcv < NUM_MANY_THREADS; lcv++) {
                ServiceLocator child = LocatorHelper.create(locator);
                
                ServiceLocatorUtilities.enablePerThreadScope(child);
            
                ServiceLocatorUtilities.addClasses(child, Pants.class);
            
                worker.doJob(child);
            }
        }
        finally {
            worker.shutdown();
        }
        
    }
    
    /**
     * Tests a large number of threads
     * 
     * @throws InterruptedException
     */
    @Test // @org.junit.Ignore
    public void testManyThreads() throws InterruptedException {
        final ServiceLocator locator = LocatorHelper.create();
        ServiceLocatorUtilities.enablePerThreadScope(locator);
        ServiceLocatorUtilities.addClasses(locator, Pants.class);
        
        synchronized (lock) {
            numFinished = 0;
        }
        
        for (int lcv =  0; lcv < NUM_MANY_THREADS; lcv++) {
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    locator.getService(Pants.class);
                    synchronized (lock) {
                        numFinished++;
                        if (numFinished >= NUM_MANY_THREADS) {
                            lock.notify();
                        }
                    }
                }
            });
            
            thread.start();
        }
        
        synchronized (lock) {
            long totalWait = 20 * 1000;
            
            while (numFinished < NUM_MANY_THREADS && totalWait > 0) {
                long elapsedTime = System.currentTimeMillis();
                lock.wait();
                elapsedTime = System.currentTimeMillis() - elapsedTime;
                totalWait -= elapsedTime;
            }
            
            Assert.assertTrue(numFinished >= NUM_MANY_THREADS);
        }
    }
    
    public class StoreRunner implements Runnable {
        private final ServiceLocator locator;
        private ClothingStore store;
        
        private StoreRunner(ServiceLocator locator) {
            this.locator = locator;
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            store = locator.getService(ClothingStore.class);
            
            synchronized (lock) {
                numFinished++;
                lock.notify();
            }
        }
        
    }
    
    public class ShirtRunner implements Runnable {
        private final ServiceLocator locator;
        private final HashSet<Shirt> collector;
        
        private ShirtRunner(ServiceLocator locator, HashSet<Shirt> collector) {
            this.locator = locator;
            this.collector = collector;
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            for (int lcv = 0; lcv < NUM_LOOKUPS; lcv++) {
                Shirt shirt = locator.getService(Shirt.class);
                synchronized (collector) {
                    collector.add(shirt);
                }
            }
            
            synchronized (lock) {
                shirtThreadsDone++;
                lock.notify();
            }
        }
        
    }
    
    public static class Worker implements Runnable {
        private ServiceLocator nextJob;
        private boolean daylightCome = false;  // I want to go home
        private final Object lock = new Object();
        
        private void doJob(ServiceLocator job) {
            synchronized (lock) {
                while (nextJob != null) {
                    try {
                        lock.wait();
                    }
                    catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                
                nextJob = job;
                lock.notifyAll();
            }
        }
        
        private void shutdown() {
            synchronized (lock) {
                daylightCome = true;
                lock.notifyAll();
            }
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            for (;;) {
                ServiceLocator currentJob = null;
                synchronized (lock) {
                    while (nextJob == null && !daylightCome) {
                        try {
                            lock.wait();
                        }
                        catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    
                    if (daylightCome) return;
                    
                    if (nextJob == null) continue;
                    
                    currentJob = nextJob;
                }
                
                // Now do the job
                Pants currentResult = currentJob.getService(Pants.class);
                Assert.assertNotNull(currentResult);
                
                synchronized (lock) {
                    nextJob = null;
                    lock.notifyAll();
                }
                
            }
            
        }
        
    }

}
