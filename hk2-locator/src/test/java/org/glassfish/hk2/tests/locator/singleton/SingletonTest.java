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

package org.glassfish.hk2.tests.locator.singleton;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.junit.Test;

/**
 * 
 * @author jwells
 */
public class SingletonTest {
    private final static String TEST_NAME = "SingletonTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new SingletonModule());
    
    private final static String TEST_NAME2 = "SingletonTest2";
    private final static ServiceLocator locator2 = LocatorHelper.create(TEST_NAME2, new SingletonModule2());
    
    private final static int NUM_THREADS = 20;
    private final Object lock = new Object();
    private int threadsRun = 0;
    
    /**
     * The 100ms sleep in Single means that this test will fail nearly
     * every time if not protected by a single lock
     * 
     * @throws InterruptedException
     */
    @Test
    public void testOnlyOne() throws InterruptedException {
        List<Thread> serviceHandles = new LinkedList<Thread>();
        for (int lcv = 0; lcv < NUM_THREADS; lcv++) {
            ActiveDescriptor<?> ad = locator.getBestDescriptor(
                    BuilderHelper.createContractFilter(ConstructorCounterService.class.getName()));
            ServiceHandle<?> handle = locator.getServiceHandle(ad);
            Assert.assertNotNull(handle);
            
            serviceHandles.add(new Thread(new MyWorker(handle)));
        }
        
        for (Thread t : serviceHandles) {
            t.start();
        }
        
        synchronized (lock) {
            while (threadsRun < NUM_THREADS) {
                lock.wait();
            }
            
            Assert.assertEquals(threadsRun, NUM_THREADS);
        }
        
        Assert.assertEquals(ConstructorCounterService.getNumTimesInitialized(), 1);
    }
    
    /**
     * This tests that when a locator is shut down it will pre-destroy
     * singletons
     */
    @Test
    public void testLocatorShutDown() {
        Triple triple = locator2.getService(Triple.class);
        Assert.assertFalse(triple.getWasPreDestroyed());
        
        Double myDouble = locator2.getService(Double.class);
        Assert.assertFalse(myDouble.getWasPreDestroyed());
        
        // Shut down the locator
        ServiceLocatorFactory.getInstance().destroy(TEST_NAME2);
        
        Assert.assertTrue(myDouble.getWasPreDestroyed());
        Assert.assertTrue(triple.getWasPreDestroyed());
    }
    
    private class MyWorker implements Runnable {
        private final ServiceHandle<?> handle;
        
        private MyWorker(ServiceHandle<?> handle) {
            this.handle = handle;
        }

        @Override
        public void run() {
            handle.getService();
            
            synchronized (lock) {
                threadsRun++;
                if (threadsRun >= NUM_THREADS) {
                    lock.notify();
                }
            }
        }
        
    }

}
