/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.configuration.tests.threaded;

import java.util.HashMap;

import javax.inject.Inject;

import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.configuration.api.ConfigurationUtilities;
import org.glassfish.hk2.configuration.hub.api.Hub;
import org.glassfish.hk2.configuration.hub.api.WriteableBeanDatabase;
import org.glassfish.hk2.configuration.hub.api.WriteableType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.jvnet.hk2.testing.junit.HK2Runner;

/**
 * @author jwells
 *
 */
public class ConfiguredThreadedTest extends HK2Runner {
    /* package */ final static String THREADED_TYPE = "ThreadedTest1";
    /* package */ final static String ANOTHER_THREADED_TYPE = "AnotherThreadedTest1";
    
    /* package */ final static String NAME_KEY = "name";
    
    private final static int NUM_RUNS = 10000;
    private final static int NUM_THREADS = 10;
    
    @Inject
    private Hub hub;
    
    @Before
    public void before() {
        super.before();
        
        ConfigurationUtilities.enableConfigurationSystem(testLocator);
    }
    
    private void addNamedBean(String typeName, String name) {
        for (;;) {
            WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
        
            WriteableType wt = wbd.findOrAddWriteableType(typeName);
        
            HashMap<String, Object> namedBean = new HashMap<String, Object>();
            namedBean.put(NAME_KEY, name);
        
            wt.addInstance(name, namedBean);
        
            try {
                wbd.commit();
                return;
            }
            catch (IllegalStateException ise) {
                // lost race
            }
        }
    }
    
    private void removeType(String typeName) {
        for (;;) {
            WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
        
            wbd.removeType(typeName);
        
            try {
                wbd.commit();
                return;
            }
            catch (IllegalStateException ise) {
                // lost race
            }
        }
    }
    
    private void removeNamedBean(String typeName, String name) {
        for (;;) {
            WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
        
            WriteableType wt = wbd.findOrAddWriteableType(typeName);
        
            wt.removeInstance(name);
        
            try {
                wbd.commit();
                return;
            }
            catch (IllegalStateException ise) {
                // Lost race
            }
        }
    }
    
    /**
     * Tests that many threads banging against the context will work
     * 
     * @throws Throwable 
     */
    @Test @Ignore
    public void testThreadedConfiguredService() throws Throwable {
        Thread threads[] = new Thread[NUM_THREADS];
        Runner runners[] = new Runner[NUM_THREADS];
        
        for (int lcv = 0; lcv < NUM_THREADS; lcv++) {
            runners[lcv] = new Runner("Name_" + lcv);
            threads[lcv] = new Thread(runners[lcv]);
        }
        
        try {
            addNamedBean(ANOTHER_THREADED_TYPE, "");
            
            for (int lcv = 0; lcv < NUM_THREADS; lcv++) {
                threads[lcv].start();
            }
        
        
            for (int lcv = 0; lcv < NUM_THREADS; lcv++) {
                runners[lcv].waitForCompletion(20 * 1000);
            }
        }
        finally {
            removeType(THREADED_TYPE);
            removeType(ANOTHER_THREADED_TYPE);
        }
        
    }
    
    private class Runner implements Runnable {
        private final String myName;
        private final Object lock = new Object();
        
        private boolean done = false;
        private Throwable error;
        
        private Runner(String name) {
            myName = name;
        }
        
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            try {
                internalRun();
            }
            catch (Throwable th) {
                synchronized (lock) {
                    error = th;
                    done = true;
                    lock.notify();
                    return;
                }
            }
            
            synchronized (lock) {
                done = true;
                lock.notify();
            }
        }
        
        private void internalRun() throws Throwable {
            for (int lcv = 0; lcv < NUM_RUNS; lcv++) {
                Assert.assertNull(testLocator.getService(ConfiguredService.class, myName));
                
                addNamedBean(THREADED_TYPE, myName);
                
                ConfiguredService cs = testLocator.getService(ConfiguredService.class, myName);
                Assert.assertNotNull(cs);
                
                Assert.assertEquals(cs.getName(), myName);
                
                Assert.assertTrue(cs.runOthersDown() > 0);
                
                removeNamedBean(THREADED_TYPE, myName);
                
                Assert.assertNull(testLocator.getService(ConfiguredService.class, myName));
                
                // Adds nastiness, this service will often need to be
                // recreated on the stack now
                ServiceHandle<AnotherConfiguredService> handle =
                        testLocator.getServiceHandle(AnotherConfiguredService.class);
                handle.destroy();
            }
        }
        
        private void waitForCompletion(long waitTime) throws Throwable {
            synchronized (lock) {
                while (waitTime > 0L && !done) {
                    long elapsedTime = System.currentTimeMillis();
                    
                    lock.wait(waitTime);
                    
                    elapsedTime = System.currentTimeMillis() - elapsedTime;
                    
                    waitTime -= elapsedTime;
                }
                
                if (!done) {
                    Assert.fail("Did not complete in the allotted time");
                }
                
                if (error != null) {
                    throw error;
                }
            }
        }
        
    }

}
