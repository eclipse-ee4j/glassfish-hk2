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

package org.glassfish.examples.events.threaded.tests;

import javax.inject.Singleton;

import org.glassfish.hk2.api.messaging.MessageReceiver;
import org.glassfish.hk2.api.messaging.SubscribeTo;
import org.jvnet.hk2.annotations.Service;

/**
 * This service receives an event and records the thread-id of
 * the thread upon which it was called
 * 
 * @author jwells
 *
 */
@Service @Singleton @MessageReceiver
public class EventSubscriberService {
    private final Object lock = new Object();
    private Long eventThreadId = null;
    
    /**
     * This is the method that should get called on a different thread
     * from the one the publisher used
     * 
     * @param event The event that was raised
     */
    @SuppressWarnings("unused")
    private void eventSubscriber(@SubscribeTo Event event) {
        synchronized (lock) {
            eventThreadId = Thread.currentThread().getId();
            
            lock.notifyAll();
        }
        
    }
    
    /**
     * Returns the thread-id upon which the event was raised
     * 
     * @return the thread-id of the thread on which the event was raised
     * @throws InterruptedException If the thread gets interrupted
     */
    public long getEventThread() throws InterruptedException {
        synchronized (lock) {
            while (eventThreadId == null) {
                lock.wait();
            }
            
            return eventThreadId;
        }
    }

}
