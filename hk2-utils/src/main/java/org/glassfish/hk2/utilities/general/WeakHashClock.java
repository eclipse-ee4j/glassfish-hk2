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

package org.glassfish.hk2.utilities.general;

import java.util.Map;

import org.glassfish.hk2.utilities.cache.CacheKeyFilter;

/**
 * This is a clock (if non-empty the next verb will always return a new value
 * in a cycle) that can also get values in O(1) complexity.  This HashClock
 * also has Weak key references, so if the key becomes unavailable it
 * will not be retrievable from the get operation and the next operation
 * will remove it from the clock
 * 
 * @author jwells
 *
 */
public interface WeakHashClock<K,V> {
    /**
     * Adds the given pair to the clock.  It will
     * be placed at the current tail of the clock
     * 
     * @param key Must not be null
     * @param value May not be null
     */
    public void put(K key, V value);
    
    /**
     * Gets the given key, returning null
     * if not found
     * 
     * @param key The key to search for, may not be null
     * @return The value found, or null if not found
     */
    public V get(K key);
    
    /**
     * Removes the given key from the clock, if found
     * 
     * @param key The key to remove, may not be null
     * @return The value removed if found, or null if not found
     */
    public V remove(K key);
    
    /**
     * Releases all key/value pairs that match the filter
     * 
     * @param filter A non-null filter that can be used
     * to delete every key/value pair that matches the filter
     */
    public void releaseMatching(CacheKeyFilter<K> filter);
    
    /**
     * Returns the number of elements currently
     * in the clock.  References that have gone
     * away because they were weakly referenced
     * will not be counted in the size
     * 
     * @return The number of entries currently
     * in the clock
     */
    public int size();
    
    /**
     * Returns the next key/value pair in the clock,
     * or null if the clock has no members.  This
     * will advance the head and tail of the clock
     * to the next element.  If the WeakReference
     * for the returned element is null then this
     * element will also have been removed from
     * the clock by this operation
     * 
     * @return The next key/value pair in the 
     */
    public Map.Entry<K, V> next();
    
    /**
     * Sets the clock size back to zero, no entries
     */
    public void clear();
    
    /**
     * Causes stale references to be cleared from the data
     * structures.  Since this is a weak clock the references
     * can go away at any time, which happens whenever
     * any operation has been performed.  However, it may be
     * the case that no operation will be performed for a while
     * and so this method is provided to have a no-op operation
     * to call in order to clear out any stale references
     */
    public void clearStaleReferences();
    
    /**
     * Tells if this WeakHashClock has Weak keys
     * @return true if this map has weak keys, true otherwise
     */
    public boolean hasWeakKeys();
}
