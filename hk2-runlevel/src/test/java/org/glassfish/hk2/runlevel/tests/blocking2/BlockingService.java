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

import org.glassfish.hk2.runlevel.RunLevel;

/**
 * @author jwells
 *
 */
@RunLevel(1)
public class BlockingService {
    private static final Object lock = new Object();
    private static boolean go = false;
    
    public static void stop() {
        synchronized (lock) {
            go = false;
        }
    }
    
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
    
    public static void go() {
        synchronized (lock) {
            go = true;
            lock.notifyAll();
        }
    }

}
