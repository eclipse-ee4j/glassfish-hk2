/*
 * Copyright (c) 2012, 2024 Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ListIterator;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This object contains a list of values.  The list is not always sorted, but will
 * always be returned sorted.
 * 
 * All of the methods on here must be called with lock held.
 * 
 * @author jwells
 *
 */
public class IndexedListData {
    private final ReentrantLock lock = new ReentrantLock();
    private final ArrayList<SystemDescriptor<?>> unsortedList = new ArrayList<SystemDescriptor<?>>();
    private volatile boolean sorted = true;
    
    public Collection<SystemDescriptor<?>> getSortedList() {
        if (sorted) return unsortedList;
        
        lock.lock();
        try {
            if (sorted) return unsortedList;
        
            if (unsortedList.size() <= 1) {
                sorted = true;
                return unsortedList;
            }
            
            Collections.sort(unsortedList, ServiceLocatorImpl.DESCRIPTOR_COMPARATOR);
        
            sorted = true;
            return unsortedList;
        } finally {
            lock.unlock();
        }
    }
    
    public void addDescriptor(SystemDescriptor<?> descriptor) {
        lock.lock();
        try {
            unsortedList.add(descriptor);
            
            if (unsortedList.size() > 1) {
                sorted = false;
            }
            else {
                sorted = true;
            }
            
            descriptor.addList(this);
        } finally {
            lock.unlock();
        }
    }
    
    public void removeDescriptor(SystemDescriptor<?> descriptor) {
        lock.lock();
        try {
            ListIterator<SystemDescriptor<?>> iterator = unsortedList.listIterator();
            while (iterator.hasNext()) {
                SystemDescriptor<?> candidate = iterator.next();
                if (ServiceLocatorImpl.DESCRIPTOR_COMPARATOR.compare(descriptor, candidate) == 0) {
                    iterator.remove();
                    break;
                }
            }
            
            if (unsortedList.size() > 1) {
                sorted = false;
            }
            else {
                sorted = true;
            }
            
            descriptor.removeList(this);
        } finally {
            lock.unlock();
        }
    }
    
    public boolean isEmpty() {
        lock.lock();
        try {
            return unsortedList.isEmpty();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Called by a SystemDescriptor when its ranking has changed
     */
    public void unSort() {
        lock.lock();
        try {
            if (unsortedList.size() > 1) {
                sorted = false;
            }
        } finally {
            lock.unlock();
        }
    }
    
    public void clear() {
        lock.lock();
        try {
            for (SystemDescriptor<?> descriptor : unsortedList) {
                descriptor.removeList(this);
            }
            
            unsortedList.clear();
        } finally {
            lock.unlock();
        }
    }
    
    public int size() {
        lock.lock();
        try {
            return unsortedList.size();
        } finally {
            lock.unlock();
        }
    }
}
