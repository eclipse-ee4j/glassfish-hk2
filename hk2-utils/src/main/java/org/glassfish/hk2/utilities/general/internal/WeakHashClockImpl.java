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
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.glassfish.hk2.utilities.cache.CacheKeyFilter;
import org.glassfish.hk2.utilities.general.WeakHashClock;

/**
 * Implementation of WeakHashClock as needed by the CAR algorithm
 * 
 * @author jwells
 *
 */
public class WeakHashClockImpl<K,V> implements WeakHashClock<K,V> {
    private final boolean isWeak;
    private final ConcurrentHashMap<K, DoubleNode<K,V>> byKeyNotWeak;
    private final WeakHashMap<K, DoubleNode<K, V>> byKey;
    
    private final ReferenceQueue<? super K> myQueue = new ReferenceQueue<K>();
    
    private DoubleNode<K, V> head;
    private DoubleNode<K, V> tail;
    private DoubleNode<K, V> dot;
    
    public WeakHashClockImpl(boolean isWeak) {
        this.isWeak = isWeak;
        if (isWeak) {
            byKey = new WeakHashMap<K, DoubleNode<K, V>>();
            byKeyNotWeak = null;
        }
        else {
            byKeyNotWeak = new ConcurrentHashMap<K, DoubleNode<K,V>>();
            byKey = null;
        }
    }
    
    private DoubleNode<K,V> addBeforeDot(K key, V value) {
        DoubleNode<K, V> toAdd = new DoubleNode<K,V>(key,value, myQueue);
        
        if (dot == null) {
            // List is empty
            head = toAdd;
            tail = toAdd;
            dot = toAdd;
            return toAdd;
        }
        
        if (dot.getPrevious() == null) {
            // Dot is currently at the head
            dot.setPrevious(toAdd);
            
            toAdd.setNext(dot);
            head = toAdd;
            return toAdd;
        }
        
        // Otherwise just add it.  Note it will NEVER be added as
        // the tail because it is always being added before something
        toAdd.setNext(dot);
        toAdd.setPrevious(dot.getPrevious());
        
        dot.getPrevious().setNext(toAdd);
        dot.setPrevious(toAdd);
        
        return toAdd;
    }
    
    private void removeFromDLL(DoubleNode<K,V> removeMe) {
        if (removeMe.getPrevious() != null) {
            removeMe.getPrevious().setNext(removeMe.getNext());
        }
        if (removeMe.getNext() != null) {
            removeMe.getNext().setPrevious(removeMe.getPrevious());
        }
        
        if (removeMe == head) {
            head = removeMe.getNext();
        }
        if (removeMe == tail) {
            tail = removeMe.getPrevious();
        }
        
        if (removeMe == dot) {
            dot = removeMe.getNext();
            if (dot == null) dot = head;
        }
        
        removeMe.setNext(null);
        removeMe.setPrevious(null);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.general.WeakHashClock#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public void put(final K key, final V value) {
        if (key == null || value == null) throw new IllegalArgumentException("key " + key + " or value " + value + " is null");
        
        synchronized (this) {
            if (isWeak) {
                removeStale();
            }
            
            DoubleNode<K,V> addMe = addBeforeDot(key, value);
        
            if (isWeak) {
                byKey.put(key, addMe);
            }
            else {
                byKeyNotWeak.put(key, addMe);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.general.WeakHashClock#get(java.lang.Object)
     */
    @Override
    public V get(K key) {
        if (key == null) return null;
        
        DoubleNode<K,V> node;
        if (isWeak) {
            synchronized (this) {
                removeStale();
        
                node = byKey.get(key);
            }
        }
        else {
            node = byKeyNotWeak.get(key);
        }
        
        if (node == null) return null;
        
        return node.getValue();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.general.WeakHashClock#remove(java.lang.Object)
     */
    @Override
    public V remove(K key) {
        if (key == null) return null;
        
        synchronized (this) {
            DoubleNode<K,V> node;
            if (isWeak) {
                removeStale();
                
                node = byKey.remove(key);
            }
            else {
                node = byKeyNotWeak.remove(key);
            }
                
            if (node == null) return null;
            
            removeFromDLL(node);
            
            return node.getValue();
        }
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.general.WeakHashClock#releaseMatching(org.glassfish.hk2.utilities.cache.CacheKeyFilter)
     */
    @Override
    public synchronized void releaseMatching(CacheKeyFilter<K> filter) {
        if (filter == null) return;
        
        if (isWeak) {
            removeStale();
        }
        
        LinkedList<K> removeMe = new LinkedList<K>();
        DoubleNode<K,V> current = head;
        while (current != null) {
            K key = current.getWeakKey().get();
            if (key != null && filter.matches(key)) {
                removeMe.add(key);
            }
            
            current = current.getNext();
        }
        
        for (K removeKey : removeMe) {
            remove(removeKey);
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.general.WeakHashClock#size()
     */
    @Override
    public int size() {
        if (isWeak) {
            synchronized (this) {
                removeStale();
        
                return byKey.size();
            }
        }
        
        return byKeyNotWeak.size();
    }
    
    private DoubleNode<K,V> moveDot() {
        if (dot == null) return null;
        
        DoubleNode<K,V> returnSource = dot;
        dot = returnSource.getNext();
        if (dot == null) dot = head;
        
        return returnSource;
    }
    
    private DoubleNode<K,V> moveDotNoWeak() {
        DoubleNode<K,V> original = moveDot();
        DoubleNode<K,V> retVal = original;
        if (retVal == null) return null;
        
        K key;
        while ((key = retVal.getWeakKey().get()) == null) {
            retVal = moveDot();
            if (retVal == null) return null;
            if (retVal == original) return null; // All the way around
        }
        
        retVal.setHardenedKey(key);
        
        return retVal;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.general.WeakHashClock#next()
     */
    @Override
    public synchronized Entry<K, V> next() {
        DoubleNode<K,V> hardenedNode = moveDotNoWeak();
        if (hardenedNode == null) return null;
        try {
            final K key = hardenedNode.getHardenedKey();
            final V value = hardenedNode.getValue();
            
            return new Map.Entry<K,V>() {

                @Override
                public K getKey() {
                    return key;
                }

                @Override
                public V getValue() {
                    return value;
                }

                @Override
                public V setValue(V value) {
                    throw new AssertionError("not implemented");
                }
                
            };
        }
        finally {
            hardenedNode.setHardenedKey(null);
            
            removeStale();
        }
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.general.WeakHashClock#clear()
     */
    @Override
    public synchronized void clear() {
        if (isWeak) {
            byKey.clear();
        }
        else {
            byKeyNotWeak.clear();
        }
        
        head = tail = dot = null;
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.general.WeakHashClock#clearStaleReferences()
     */
    @Override
    public synchronized void clearStaleReferences() {
        removeStale();
    }
    
    private void removeStale() {
        boolean goOn = false;
        while (myQueue.poll() != null) {
            goOn = true;
        }
        
        if (!goOn) return;
        
        DoubleNode<K,V> current = head;
        while (current != null) {
            DoubleNode<K,V> next = current.getNext();
            
            if (current.getWeakKey().get() == null) {
                removeFromDLL(current);
            }
            
            current = next;
        }
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.general.WeakHashClock#hasWeakKeys()
     */
    @Override
    public boolean hasWeakKeys() {
        return isWeak;
    }
    
    @Override
    public synchronized String toString() {
        StringBuffer sb = new StringBuffer("WeakHashClockImpl({");
        
        boolean first = true;
        DoubleNode<K,V> current = dot;
        if (current != null) {
            do {
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
                if (current == null) current = head;
            } while (current != dot);
        }
        
        sb.append("}," + System.identityHashCode(this) + ")");
              
        return sb.toString();
    }
}
