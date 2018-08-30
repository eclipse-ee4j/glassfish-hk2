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

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.junit.Assert;

/**
 * This service has no explicit dependencies, but DOES call
 * getService from the postConstruct, and so it has a
 * hidden dependency on a service that is blocking...
 * 
 * @author jwells
 *
 */
@Singleton
public class SingletonWithSneakyDependency {
    @Inject
    private ServiceLocator locator;
    
    private final static Object lock = new Object();
    private static boolean initialized = false;
    
    private static boolean useServiceHandle = false;
    
    public static void setUseServiceHandle(boolean paramUseServiceHandle) {
        useServiceHandle = paramUseServiceHandle;
        synchronized (lock) {
            initialized = false;
        }
    }
    
    @SuppressWarnings("unused")
    @PostConstruct
    private void postConstruct() {
        // This sleep makes sure the BlockingService
        // gets going on the other thread prior to
        // getting invoked
        try {
            Thread.sleep(250L);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        if (!useServiceHandle) {
            try {
                locator.getService(BlockingService.class);
            }
            catch (Throwable th) {
                th.printStackTrace();
                Assert.fail("Should not have reached here, not rethrowing original exception");
            }
        }
        else {
            try {
                ServiceHandle<BlockingService> handle = locator.getServiceHandle(BlockingService.class);
                
                handle.getService();
            }
            catch (Throwable th) {
                th.printStackTrace();
                Assert.fail("Should not have reached here, not rethrowing original exception (service handle path)");
            }
        }
        
        synchronized (lock) {
            initialized = true;
            lock.notifyAll();
        }
        
    }
    
    public static boolean isInitialized(long totalWait) throws InterruptedException {
        synchronized (lock) {
            while (totalWait > 0 && !initialized) {
                long elapsedTime = System.currentTimeMillis();
                lock.wait(totalWait);
                elapsedTime = System.currentTimeMillis() - elapsedTime;
                
                totalWait -= elapsedTime;
            }
            
            return initialized;
        }
    }

}
