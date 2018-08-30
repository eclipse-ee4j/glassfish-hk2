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

package org.glassfish.hk2.tests.locator.immediate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.glassfish.hk2.api.Immediate;

/**
 * @author jwells
 *
 */
@Immediate
public class WaitableImmediateService {
    private static final Object lock = new Object();
    private static int numCreations = 0;
    private static int numDeletions = 0;
    
    @PostConstruct
    private void postConstruct() {
        synchronized (lock) {
            numCreations++;
            lock.notifyAll();
        }
        
    }
    
    @PreDestroy
    private void preDestroy() {
        synchronized (lock) {
            numDeletions++;
            lock.notifyAll();
        }
        
    }
    
    public static int waitForCreationsGreaterThanZero(long waitTime) throws InterruptedException {
        synchronized (lock) {
            while ((numCreations <= 0) && (waitTime > 0L)) {
                long currentTime = System.currentTimeMillis();
                lock.wait(waitTime);
                long elapsedTime = System.currentTimeMillis() - currentTime;
                waitTime -= elapsedTime;
            }
            
            return numCreations;
        }
    }
    
    public static int getNumDeletions() {
        synchronized (lock) {
            return numDeletions;
        }
    }
    
    public static int getNumCreations() {
        synchronized (lock) {
            return numCreations;
        }
    }
    
    public static int waitForDeletionsGreaterThanZero(long waitTime) throws InterruptedException {
        synchronized (lock) {
            while ((numDeletions <= 0) && (waitTime > 0L)) {
                long currentTime = System.currentTimeMillis();
                lock.wait(waitTime);
                long elapsedTime = System.currentTimeMillis() - currentTime;
                waitTime -= elapsedTime;
            }
            
            return numDeletions;
        }
    }
    
    public static void clear() {
        synchronized (lock) {
            numCreations = 0;
            numDeletions = 0;
        }
    }

}
