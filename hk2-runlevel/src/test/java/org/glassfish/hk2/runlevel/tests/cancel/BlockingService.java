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

package org.glassfish.hk2.runlevel.tests.cancel;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.glassfish.hk2.runlevel.RunLevel;

/**
 * The postConstruct method blocks until the test tells it to go
 * (which may be never)
 * @author jwells
 *
 */
@RunLevel(5)
public class BlockingService {
    private final static Object lock = new Object();
    private static boolean go = false;
    
    private final static Object postConstructLock = new Object();
    private static boolean postConstructCalled = false;
    
    private static boolean preDestroyCalled = false;
    
    /**
     * Will block until test tells it to go
     */
    @PostConstruct
    public void postConstruct() {
        synchronized (postConstructLock) {
            postConstructCalled = true;
            postConstructLock.notifyAll();
        }
        
        synchronized (lock) {
            while (!go) {
                try {
                   lock.wait();
                }
                catch (InterruptedException ie) {
                    throw new RuntimeException(ie);
                }
            }
        }
        
    }
    
    /**
     * Ensures that if this came up, it also went down
     */
    @PreDestroy
    public void preDestroy() {
        preDestroyCalled = true;
    }
    
    public static boolean getPreDestroyCalled() {
        return preDestroyCalled;
    }
    
    /**
     * Done so the test can be sure the postConstruct has been called
     * 
     * @throws InterruptedException
     */
    public static void waitForPostConstruct() throws InterruptedException {
        synchronized (postConstructLock) {
            while (!postConstructCalled) {
                postConstructLock.wait();
            }
        }
    }
    
    public static boolean getPostConstructCalled() {
        synchronized (postConstructLock) {
            return postConstructCalled;
        }
    }
    
    /**
     * Tells service to go ahead
     */
    public static void go() {
        synchronized (lock) {
            go = true;
            lock.notifyAll();
        }
    }
    
    /**
     * Tells service to stop in the postConstruct
     */
    public static void clear() {
        synchronized (lock) {
            go = false;
        }
        
        synchronized (postConstructLock) {
            postConstructCalled = false;
        }
        
        preDestroyCalled = false;
    }
}
