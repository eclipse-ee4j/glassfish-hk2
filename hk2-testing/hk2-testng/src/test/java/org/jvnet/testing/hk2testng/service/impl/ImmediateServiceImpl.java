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

package org.jvnet.testing.hk2testng.service.impl;

import org.glassfish.hk2.api.Immediate;

/**
 *
 * @author jwells
 */
@Immediate
public class ImmediateServiceImpl {
    private final static Object sLock = new Object();
    private static boolean started = false;
    
    private ImmediateServiceImpl() {
        synchronized (sLock) {
            started = true;
            sLock.notifyAll();
        }
    }
    
    /**
     * Waits for this service to start, which should not take long as long as the
     * immediate scope is available
     * 
     * @param waitTimeMillis The amount of time to wait for this service
     * @return true if the service was started
     * @throws InterruptedException if the thread was interrupted
     */
    public static boolean waitForStart(long waitTimeMillis) throws InterruptedException {
        synchronized (sLock) {
            long currentTime;
            long elapsedTime;
            
            while (!started && (waitTimeMillis > 0)) {
                currentTime = System.currentTimeMillis();
                sLock.wait(waitTimeMillis);
                
                elapsedTime = System.currentTimeMillis() - currentTime;
                
                waitTimeMillis -= elapsedTime;
            }
            
            return started;
        }
    }
}
