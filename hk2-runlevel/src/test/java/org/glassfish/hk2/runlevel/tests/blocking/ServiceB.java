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

package org.glassfish.hk2.runlevel.tests.blocking;

import javax.annotation.PostConstruct;

import org.glassfish.hk2.api.Rank;
import org.glassfish.hk2.runlevel.RunLevel;

/**
 * This service will wait forever until ServiceC allows it to go.
 * <p>
 * The idea is that this service blocks in postConstruct and hence
 * both ServiceA which depends on ServiceB and ServiceB itself
 * would cause a block.  Both ServiceA and ServiceB are of higher
 * rank than ServiceC, and hence the two threads in the controller
 * will originally try to farm out starting ServiceA and ServiceB.
 * <p>
 * One of them will report a blocking condition, and hence that
 * thread will move on to ServiceC.
 * @author jwells
 *
 */
@RunLevel(5)
@Rank(100)
public class ServiceB {
    private final static Object lock = new Object();
    private static boolean go = false;
    
    @SuppressWarnings("unused")
    @PostConstruct
    private void postConstruct() {
        synchronized (lock) {
            while (!go) {
                try {
                    lock.wait();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        
    }
    
    /* package */ static void goAhead() {
        synchronized (lock) {
            go = true;
            lock.notifyAll();
        }
        
    }

}
