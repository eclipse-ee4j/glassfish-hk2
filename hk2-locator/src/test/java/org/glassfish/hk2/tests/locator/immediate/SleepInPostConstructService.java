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

package org.glassfish.hk2.tests.locator.immediate;

import javax.annotation.PostConstruct;

import org.glassfish.hk2.api.Immediate;

/**
 * @author jwells
 *
 */
@Immediate
public class SleepInPostConstructService {
    private final static Object sLock = new Object();
    private static boolean inPostConstruct = false;
    private static int numCreations;
    
    public static boolean waitForPostConstruct(long waitTime) throws InterruptedException {
        synchronized (sLock) {
            while (waitTime > 0 && !inPostConstruct) {
                long elapsedTime = System.currentTimeMillis();
                sLock.wait(waitTime);
                elapsedTime = System.currentTimeMillis() - elapsedTime;
                waitTime -= elapsedTime;
            }
            
            return inPostConstruct;
        }
        
    }

    private SleepInPostConstructService() {
        synchronized (sLock) {
            numCreations++;
        }
    }
    
    public int getNumCreations() {
        synchronized (sLock) {
            return numCreations;
        }
    }
    
    @PostConstruct
    private void postConstruct() {
        synchronized (sLock) {
            inPostConstruct = true;
            sLock.notify();
        }
        
        // Now sleep in here, to have a good chance of
        // the test thread to get into the proper place
        // when looking for this service
        try {
            Thread.sleep(1 * 1000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        
    }
    

}
