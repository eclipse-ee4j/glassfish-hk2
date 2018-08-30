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

import org.glassfish.hk2.utilities.cache.CacheKeyFilter;

/**
 * @author jwells
 *
 */
public interface WeakHashLRU<K> {
    /**
     * Adds the given key to the LRU.  It will
     * be placed at the MRU of the LRU.  If this
     * key already exists in the LRU it will
     * be moved to the MRU
     * 
     * @param key Must not be null
     */
    public void add(K key);
    
    /**
     * Tells if the given key is in the LRU
     * 
     * @param key The key to search for, may not be null
     * @return true if found, false otherwise
     */
    public boolean contains(K key);
    
    /**
     * Removes the given key from the LRU, if found
     * 
     * @param key The key to remove, may not be null
     * @return true if removed, false otherwise
     */
    public boolean remove(K key);
    
    /**
     * Releases all keys that match the filter
     * 
     * @param filter A non-null filter that can be used
     * to delete every key that matches the filter
     */
    public void releaseMatching(CacheKeyFilter<K> filter);
    
    /**
     * Returns the number of elements currently
     * in the clock.  References that have gone
     * away because they were weakly referenced
     * will not be counted in the size
     * 
     * @return The number of entries currently
     * in the LRU
     */
    public int size();
    
    /**
     * Removes the key that was Least
     * Recently Used
     * 
     * @return The key that was removed, or
     * null if the list is empty
     */
    public K remove();
    
    /**
     * Removes all entries from this LRU
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

}
