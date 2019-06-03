/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.locator.multithreaded1;

import java.util.List;

import javax.inject.Singleton;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.IndexedFilter;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class MultiThreaded1Test {
    private final static int NUM_THREADS = 20;
    private final static int NUM_ITERATIONS = 10000;
    
    private final static IndexedFilter FILTER = new IndexedFilter() {
        @Override
        public boolean matches(Descriptor d) {
            return d.getQualifiers().contains(QualifierA.class.getName());
        }

        @Override
        public String getAdvertisedContract() {
            return ContractA.class.getName();
        }

        @Override
        public String getName() {
            return null;
        }
        
    };
    
    private ServiceLocator getLocator() {
        return LocatorHelper.getServiceLocator(
                Singleton1.class,
                Singleton2.class,
                Singleton3.class,
                Singleton4.class,
                Singleton5.class,
                Singleton6.class,
                Singleton7.class,
                Singleton8.class,
                Singleton9.class,
                Singleton10.class,
                Singleton11.class,
                Singleton12.class,
                Singleton13.class,
                Singleton14.class,
                Singleton15.class,
                Singleton16.class,
                Singleton17.class,
                Singleton18.class,
                Singleton19.class,
                Singleton20.class);
        
    }
    
    /**
     * Many threads looking up all of many services
     * using a Filter with no name but an interface
     * contract and looking for a specific qualifier
     */
    @Test // @org.junit.Ignore
    public void testManyThreadsGettingALotOfServices() throws Throwable {
        ServiceLocator locator = getLocator();
        
        Thread threads[] = new Thread[NUM_THREADS];
        Runner runners[] = new Runner[NUM_THREADS];
        
        for (int lcv = 0; lcv < NUM_THREADS; lcv++) {
            runners[lcv] = new Runner(FILTER, NUM_ITERATIONS, locator, true);
            threads[lcv] = new Thread(runners[lcv]);
        }
        
        for (int lcv = 0; lcv < NUM_THREADS; lcv++) {
            threads[lcv].start();
        }
        
        for (int lcv = 0; lcv < NUM_THREADS; lcv++) {
            runners[lcv].isDone(20 * 1000);
        }
        
    }
    
    /**
     * Many threads looking up all of many services
     * using a Filter with no name but an interface
     * contract and looking for a specific qualifier.
     * This version of the test dynamically adds and
     * removes services during the run
     */
    @Test // @org.junit.Ignore
    public void testManyThreadsGettingALotOfServicesWithAddsAndRemoves() throws Throwable {
        ServiceLocator locator = getLocator();
        
        Thread threads[] = new Thread[NUM_THREADS];
        Runner runners[] = new Runner[NUM_THREADS];
        
        for (int lcv = 0; lcv < NUM_THREADS; lcv++) {
            runners[lcv] = new Runner(FILTER, NUM_ITERATIONS, locator, false);
            threads[lcv] = new Thread(runners[lcv]);
        }
        
        for (int lcv = 0; lcv < NUM_THREADS; lcv++) {
            threads[lcv].start();
        }
        
        for (int lcv = 0; lcv < NUM_THREADS; lcv++) {
            runners[lcv].isDone(60 * 1000);
        }
        
    }
    
    private static class Runner implements Runnable {
        private final Object lock = new Object();
        private final Filter filter;
        private final int iterations;
        private final ServiceLocator locator;
        // If true swap ranks, if false do adds/removes
        private final boolean ranks;
        
        private Throwable exception;
        private boolean done = false;
        
        private Runner(Filter filter, int iterations, ServiceLocator locator, boolean ranks) {
            this.filter = filter;
            this.iterations = iterations;
            this.locator = locator;
            this.ranks = ranks;
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            try {
                internalRun();
                synchronized (lock) {
                    done = true;
                    lock.notifyAll();
                }
            }
            catch (Throwable re) {
                synchronized (lock) {
                    exception = re;
                    done = true;
                    lock.notifyAll();
                }
            }
        }
        
        private void internalRun() throws Throwable {
            boolean oneOrNegativeOne = false;
            
            ActiveDescriptor<?> added = null;
            for (int lcv = 0; lcv < iterations; lcv++) {
                List<ServiceHandle<?>> allInterceptors = locator.getAllServiceHandles(filter);
                if (!ranks) {
                    Assert.assertTrue(allInterceptors.size() >= 20);
                }
                else {
                    Assert.assertEquals(20, allInterceptors.size());
                }
                
                if (ranks) {
                    if ((lcv % 5) == 0) {
                        if (oneOrNegativeOne) {
                            allInterceptors.get(0).getActiveDescriptor().setRanking(1);
                            oneOrNegativeOne = false;
                        }
                        else {
                            allInterceptors.get(0).getActiveDescriptor().setRanking(-1);
                            oneOrNegativeOne = true;
                        }
                    
                    }
                }
                else {
                    if ((lcv % 5) == 0) {
                        if (added == null) {
                            added = ServiceLocatorUtilities.addClasses(locator, SingletonExtra.class).get(0);
                        }
                        else {
                            ServiceLocatorUtilities.removeOneDescriptor(locator, added);
                            added = null;
                        }
                    }
                    
                }
            }
            
        }
        
        private void isDone(long timeout) throws Throwable {
            synchronized (lock) {
                while (!done && timeout > 0) {
                    long elapsedTime = System.currentTimeMillis();
                    
                    lock.wait(timeout);
                    
                    elapsedTime = System.currentTimeMillis() - elapsedTime;
                    timeout -= elapsedTime;
                }
                
                Assert.assertTrue(done);
                if (exception != null) {
                    throw exception;
                }
            }
            
        }
        
    }
    
    @Singleton @QualifierA
    private static class Singleton1 implements ContractA {
    }
    @Singleton @QualifierA
    private static class Singleton2 implements ContractA {
    }
    @Singleton @QualifierA
    private static class Singleton3 implements ContractA {
    }
    @Singleton @QualifierA
    private static class Singleton4 implements ContractA {
    }
    @Singleton @QualifierA
    private static class Singleton5 implements ContractA {
    }
    @Singleton @QualifierA
    private static class Singleton6 implements ContractA {
    }
    @Singleton @QualifierA
    private static class Singleton7 implements ContractA {
    }
    @Singleton @QualifierA
    private static class Singleton8 implements ContractA {
    }
    @Singleton @QualifierA
    private static class Singleton9 implements ContractA {
    }
    @Singleton @QualifierA
    private static class Singleton10 implements ContractA {
    }
    @Singleton @QualifierA
    private static class Singleton11 implements ContractA {
    }
    @Singleton @QualifierA
    private static class Singleton12 implements ContractA {
    }
    @Singleton @QualifierA
    private static class Singleton13 implements ContractA {
    }
    @Singleton @QualifierA
    private static class Singleton14 implements ContractA {
    }
    @Singleton @QualifierA
    private static class Singleton15 implements ContractA {
    }
    @Singleton @QualifierA
    private static class Singleton16 implements ContractA {
    }
    @Singleton @QualifierA
    private static class Singleton17 implements ContractA {
    }
    @Singleton @QualifierA
    private static class Singleton18 implements ContractA {
    }
    @Singleton @QualifierA
    private static class Singleton19 implements ContractA {
    }
    @Singleton @QualifierA
    private static class Singleton20 implements ContractA {
    }
    
    @Singleton @QualifierA
    private static class SingletonExtra implements ContractA {
    }

}
