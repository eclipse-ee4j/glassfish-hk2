/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.utilities.cache;

import org.glassfish.hk2.utilities.cache.internal.LRUCacheCheapRead;

/**
 * A cache that contains a certain number of entries, and whose oldest accessed
 * entries are removed when removal is necessary.
 *
 * @author jwells
 * @param <K> The key type for this cache
 * @param <V> The value type for this cache
 *
 */
public abstract class LRUCache<K,V> {

    /**
     * Creates a cache with the given maximum cache size
     *
     * @param maxCacheSize The maximum number of entries in the cache, must be greater than 2
     * @return An LRUCache that can be used to quickly retrieve objects
     */
    public static <K,V> LRUCache<K,V> createCache(int maxCacheSize) {
        return new LRUCacheCheapRead<K,V>(maxCacheSize);
    }

    /**
     * Returns the value associated with the given key.  If there is no
     * value, returns null
     *
     * @param key Must be a non-null key, appropriate for use as the key to a hash map
     * @return The value associated with the key, or null if there is no such value
     */
    public abstract V get(K key);

    /**
     * Adds the given key and value pair into the cache
     *
     * @param key Must be a non-null key, appropriate for use as the key to a hash map
     * @param value Must be a non-null value
     * @return A cache entry that can be used to remove this entry from the cache.  Will not return null
     */
    public abstract CacheEntry put(K key, V value);

    /**
     * Clears all entries in the cache, for use when a known event makes the cache incorrect
     */
    public abstract void releaseCache();

    /**
     * Returns the maximum number of entries that will be stored in this cache
     *
     * @return The maximum number of entries that will be stored in this cache
     */
    public abstract int getMaxCacheSize();
    
    /**
     * This method will remove all cache entries for which this filter
     * matches
     * 
     * @param filter Entries in the cache that match this filter will
     * be removed from the cache.  If filter is null nothing
     * will be removed from the cache
     */
    public abstract void releaseMatching(CacheKeyFilter<K> filter);
}
