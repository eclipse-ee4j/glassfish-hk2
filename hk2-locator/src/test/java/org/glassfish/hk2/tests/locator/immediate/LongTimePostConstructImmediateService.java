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

package org.glassfish.hk2.tests.locator.immediate;

import javax.annotation.PostConstruct;

import org.glassfish.hk2.api.Immediate;

/**
 * @author jwells
 *
 */
@Immediate
public class LongTimePostConstructImmediateService {
    private static final Object sLock = new Object();
    private static boolean released = false;
    
    private static final Object rLock = new Object();
    private static boolean running = false;
    
    @PostConstruct
    private void postConstruct() {
        synchronized(rLock) {
            running = true;
            rLock.notifyAll();
        }
        
        synchronized(sLock) {
            while (!released) {
                try {
                    sLock.wait();
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    
    public static void waitUntilRunning() throws InterruptedException {
        synchronized (rLock) {
            while (!running) {
                rLock.wait();
            }
        }
    }
    
    public static void release() {
        synchronized (sLock) {
            released = true;
            sLock.notifyAll();
        }
    }

}
