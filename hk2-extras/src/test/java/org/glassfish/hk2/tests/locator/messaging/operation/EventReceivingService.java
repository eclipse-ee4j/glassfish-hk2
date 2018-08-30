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

package org.glassfish.hk2.tests.locator.messaging.operation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.glassfish.hk2.api.messaging.MessageReceiver;
import org.glassfish.hk2.api.messaging.SubscribeTo;

/**
 * @author jwells
 *
 */
@EventReceivingOperation @MessageReceiver
public class EventReceivingService {
    private final static Map<Integer, List<Integer>> EVENT_MAP = new HashMap<Integer, List<Integer>>();
    private final static AtomicInteger ID_GENERATOR = new AtomicInteger();
    
    private final int id;
    
    public EventReceivingService() {
        id = ID_GENERATOR.getAndIncrement();
    }
    
    public int doOperation() {
        return id;
    }
    
    public void subscriber(@SubscribeTo int eventId) {
        synchronized (EventReceivingService.class) {
            List<Integer> events = EVENT_MAP.get(id);
            if (events == null) {
                events = new LinkedList<Integer>();
                EVENT_MAP.put(id, events);
            }
            
            events.add(eventId);
        }
    }
    
    public static Map<Integer, List<Integer>> getEventMap() {
        synchronized (EventReceivingService.class) {
            HashMap<Integer, List<Integer>> retVal = new HashMap<Integer, List<Integer>>();
            
            for (Map.Entry<Integer, List<Integer>> entry : EVENT_MAP.entrySet()) {
                List<Integer> valueCopy = new LinkedList<Integer>(entry.getValue());
                
                retVal.put(entry.getKey(), valueCopy);
            }
            
            return retVal;
        }
        
    }

}
