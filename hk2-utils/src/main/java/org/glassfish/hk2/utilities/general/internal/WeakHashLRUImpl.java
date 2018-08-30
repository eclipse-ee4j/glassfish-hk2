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

package org.glassfish.hk2.utilities.general.internal;

import java.lang.ref.ReferenceQueue;
import java.util.LinkedList;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.glassfish.hk2.utilities.cache.CacheKeyFilter;
import org.glassfish.hk2.utilities.general.WeakHashLRU;

/**
 * An implementation of the WeakHashLRU as needed by the CAR algorithm
 * 
 * @author jwells
 *
 */
public class WeakHashLRUImpl<K> implements WeakHashLRU<K> {
    private final static Object VALUE = new Object();
    
    private final boolean isWeak;
    private final WeakHashMap<K, DoubleNode<K, Object>> byKey;
    private final ConcurrentHashMap<K, DoubleNode<K, Object>> byKeyNotWeak;
    
    private final ReferenceQueue<? super K> myQueue = new ReferenceQueue<K>();
    
    private DoubleNode<K, Object> mru;
    private DoubleNode<K, Object> lru;
    
    public WeakHashLRUImpl(boolean isWeak) {
        this.isWeak = isWeak;
        if (isWeak) {
            byKey = new WeakHashMap<K, DoubleNode<K, Object>>();
            byKeyNotWeak = null;
        }
        else {
            byKey = null;
            byKeyNotWeak = new ConcurrentHashMap<K, DoubleNode<K, Object>>();
        }
    }
    
    private DoubleNode<K,Object> addToHead(K key) {
        DoubleNode<K, Object> added = new DoubleNode<K,Object>(key, VALUE, myQueue);
        
        if (mru == null) {
            mru = added;
            lru = added;
            return added;
        }
        
        added.setNext(mru);
        
        mru.setPrevious(added);
        mru = added;
        
        return added;
    }
    
    private K remove(DoubleNode<K, Object> removeMe) {
        K retVal = removeMe.getWeakKey().get();
        
        if (removeMe.getNext() != null) {
            removeMe.getNext().setPrevious(removeMe.getPrevious());
        }
        if (removeMe.getPrevious() != null) {
            removeMe.getPrevious().setNext(removeMe.getNext());
        }
        
        if (removeMe == mru) {
            mru = removeMe.getNext();
        }
        if (removeMe == lru) {
            lru = removeMe.getPrevious();
        }
        
        removeMe.setNext(null);
        removeMe.setPrevious(null);
        
        return retVal;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.general.WeakHashLRU#add(java.lang.Object)
     */
    @Override
    public synchronized void add(K key) {
        if (key == null) {
            throw new IllegalArgumentException("key may not be null");
        }
        
        DoubleNode<K, Object> existing;
        if (isWeak) {
            clearStale();
            
            existing = byKey.get(key);
        }
        else {
            existing = byKeyNotWeak.get(key);
        }
        
        if (existing != null) {
            remove(existing);
        }
        
        DoubleNode<K, Object> added = addToHead(key);
        
        if (isWeak) {
            byKey.put(key, added);
        }
        else {
            byKeyNotWeak.put(key, added);
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.general.WeakHashLRU#contains(java.lang.Object)
     */
    @Override
    public boolean contains(K key) {
        if (isWeak) {
            synchronized (this) {
                clearStale();
        
                return byKey.containsKey(key);
            }
        }
        
        return byKeyNotWeak.containsKey(key);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.general.WeakHashLRU#remove(java.lang.Object)
     */
    @Override
    public synchronized boolean remove(K key) {
        if (isWeak) {
            clearStale();
        }
        
        return removeNoClear(key);
    }
    
    private boolean removeNoClear(K key) {
        if (key == null) return false;
        
        DoubleNode<K, Object> removeMe;
        if (isWeak) {
            removeMe = byKey.remove(key);
        }
        else {
            removeMe = byKeyNotWeak.remove(key);
        }
        if (removeMe == null) return false;
        
        remove(removeMe);
        
        return true;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.general.WeakHashLRU#size()
     */
    @Override
    public int size() {
        if (isWeak) {
            synchronized (this) {
                clearStale();
        
                return byKey.size();
            }
        }
        
        return byKeyNotWeak.size();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.general.WeakHashLRU#remove()
     */
    @Override
    public synchronized K remove() {
        try {
            if (lru == null) return null;
        
            DoubleNode<K, Object> current = lru;
            while (current != null) {
                DoubleNode<K, Object> previous = current.getPrevious();
            
                K retVal = current.getWeakKey().get();
            
                if (retVal != null) {
                    removeNoClear(retVal);
                
                    return retVal;
                }
                else {
                    remove(current);
                }
            
                current = previous;
            }
        
            return null;
        }
        finally {
            clearStale();
        }
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.general.WeakHashLRU#releaseMatching(org.glassfish.hk2.utilities.cache.CacheKeyFilter)
     */
    @Override
    public synchronized void releaseMatching(CacheKeyFilter<K> filter) {
        if (filter == null) return;
        if (isWeak) {
            clearStale();
        }
        
        LinkedList<K> removeMe = new LinkedList<K>();
        DoubleNode<K, Object> current = mru;
        while (current != null) {
            K key = current.getWeakKey().get();
            if (key != null && filter.matches(key)) {
                removeMe.add(key);
            }
            
            current = current.getNext();
        }
        
        for (K removeKey : removeMe) {
            removeNoClear(removeKey);
        }
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.general.WeakHashLRU#clear()
     */
    @Override
    public synchronized void clear() {
        if (isWeak) {
            clearStale();
            
            byKey.clear();
        }
        else {
            byKeyNotWeak.clear();
        }
        
        mru = null;
        lru = null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.general.WeakHashLRU#clearStaleReferences()
     */
    @Override
    public synchronized void clearStaleReferences() {
        clearStale();
    }
    
    private void clearStale() {
        boolean goOn = false;
        while (myQueue.poll() != null) {
            goOn = true;
        }
        
        if (!goOn) return;
        
        DoubleNode<K, Object> current;
        
        current = mru;
        while (current != null) {
            DoubleNode<K, Object> next = current.getNext();
            
            if (current.getWeakKey().get() == null) {
                remove(current);
            }
            
            current = next;
        }
    }
    
    @Override
    public synchronized String toString() {
        StringBuffer sb = new StringBuffer("WeakHashLRUImpl({");
        
        boolean first = true;
        DoubleNode<K,Object> current = mru;
        while (current != null) {
            K key = current.getWeakKey().get();
            String keyString = (key == null) ? "null" : key.toString();
            
            if (first) {
                first = false;
                
                sb.append(keyString);
            }
            else {
                sb.append("," + keyString);
            }
            
            current = current.getNext();
        }
        
        sb.append("}," + System.identityHashCode(this) + ")");
              
        return sb.toString();
    }
}
