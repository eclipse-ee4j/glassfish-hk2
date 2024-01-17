/*
 * Copyright (c) 2015, 2024 Oracle and/or its affiliates. All rights reserved.
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

package org.jvnet.hk2.internal;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InstantiationData;
import org.glassfish.hk2.api.InstantiationService;
import org.glassfish.hk2.api.Visibility;

/**
 * @author jwells
 *
 */
@Visibility(DescriptorVisibility.LOCAL)
public class InstantiationServiceImpl implements InstantiationService {
    private final ReentrantLock lock = new ReentrantLock();
    private final HashMap<Long, LinkedList<Injectee>> injecteeStack = new HashMap<Long, LinkedList<Injectee>>();

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InstantiationService#getInstantiationData()
     */
    @Override
    public InstantiationData getInstantiationData() {
        try {
            lock.lock();
            long tid = Thread.currentThread().getId();
            
            LinkedList<Injectee> threadStack = injecteeStack.get(tid);
            if (threadStack == null) return null;
            if (threadStack.isEmpty()) return null;
            
            final Injectee head = threadStack.getLast();
            
            return new InstantiationData() {
    
                @Override
                public Injectee getParentInjectee() {
                    return head;
                }
                
                @Override
                public String toString() {
                    return "InstantiationData(" + head + "," + System.identityHashCode(this) + ")";
                }
                
            };
        } finally {
            lock.unlock();
        }
   
    }
    
    public void pushInjecteeParent(Injectee injectee) {
        try {
            lock.lock();
            long tid = Thread.currentThread().getId();
            
            LinkedList<Injectee> threadStack = injecteeStack.get(tid);
            if (threadStack == null) {
                threadStack = new LinkedList<Injectee>();
                injecteeStack.put(tid, threadStack);
            }
            
            threadStack.addLast(injectee);
        } finally {
            lock.unlock();
        }
    }
    
    public void popInjecteeParent() {
        try {
            lock.lock();
            long tid = Thread.currentThread().getId();
            
            LinkedList<Injectee> threadStack = injecteeStack.get(tid);
            if (threadStack == null) return;
            
            threadStack.removeLast();
            
            if (threadStack.isEmpty()) {
                // prevents memory leaks for long dead threads
                injecteeStack.remove(tid);
            }
        } finally {
            lock.unlock();
        }
    }
    
    @Override
    public String toString() {
        return "InstantiationServiceImpl(" + injecteeStack.keySet() + "," + System.identityHashCode(this) + ")";
    }

}
