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

package org.glassfish.hk2.utilities.cache.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.glassfish.hk2.utilities.cache.CacheEntry;
import org.glassfish.hk2.utilities.cache.CacheKeyFilter;
import org.glassfish.hk2.utilities.cache.LRUCache;

/**
 * LRU Cache implementation that relies on entries that keep
 * last hit (get/put) timestamp in order to be able to remove least recently
 * accessed items when running out of cache capacity.
 * Item order is not being maintained during regular cache usage (mainly reads).
 * This makes pruning operation expensive in exchange
 * for making reads quite cheap in a multi-threaded environment.
 *
 * @author Jakub Podlesak (jakub.podlesak at oracle.com)
 * @param <K> The key of the cache
 * @param <V> The values in the cache
 */
public class LRUCacheCheapRead<K,V> extends LRUCache<K,V> {

    final Object prunningLock = new Object();

    final int maxCacheSize;
    Map<K,CacheEntryImpl<K, V>> cache = new ConcurrentHashMap<K, CacheEntryImpl<K,V>>();

    /**
     * Create new cache with given maximum capacity.
     *
     * @param maxCacheSize Maximum number of items to keep.
     */
    public LRUCacheCheapRead(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
    }

    @Override
    public V get(K key) {
        final CacheEntryImpl<K, V> entry = cache.get(key);
        return entry != null ? entry.hit().value : null;
    }

    @Override
    public CacheEntry put(K key, V value) {
        CacheEntryImpl<K, V> entry = new CacheEntryImpl<K, V>(key, value, this);
        synchronized (prunningLock) {
            if (cache.size() + 1 > maxCacheSize) {
                removeLRUItem();
            }
            cache.put(key, entry);
            return entry;
        }
    }

    @Override
    public void releaseCache() {
        cache.clear();
    }

    @Override
    public int getMaxCacheSize() {
        return maxCacheSize;
    }

    @Override
    public void releaseMatching(CacheKeyFilter<K> filter) {
        if (filter == null) return;

        for (Map.Entry<K, CacheEntryImpl<K,V>> entry : (new HashMap<K, CacheEntryImpl<K,V>>(cache)).entrySet()) {
            if (filter.matches(entry.getKey())) {
                entry.getValue().removeFromCache();
            }
        }

    }

    /**
     * Remove least recently used item form the cache.
     * No checks are done here. The method just tries to remove the least recently used
     * cache item. An exception will be thrown if the cache is empty.
     */
    private void removeLRUItem() {
        final Collection<CacheEntryImpl<K, V>> values = cache.values();
        Collections.min(values, COMPARATOR).removeFromCache();
    }

    private static final CacheEntryImplComparator COMPARATOR = new CacheEntryImplComparator();

    private static class CacheEntryImplComparator implements Comparator<CacheEntryImpl<?,?>> {

        @Override
        public int compare(CacheEntryImpl<?,?> first, CacheEntryImpl<?,?> second) {
            final long diff = first.lastHit - second.lastHit;
            return diff > 0 ? 1 : diff == 0 ? 0 : -1;
        }

    }

    private static class CacheEntryImpl<K,V> implements CacheEntry {

        final K key;
        final V value;
        final LRUCacheCheapRead<K, V> parent;
        long lastHit;

        public CacheEntryImpl(K k, V v, LRUCacheCheapRead<K,V> cache) {
            this.parent = cache;
            this.key = k;
            this.value = v;
            this.lastHit = System.nanoTime();
        }

        @Override
        public void removeFromCache() {
            parent.cache.remove(key);
        }

        public CacheEntryImpl<K,V> hit() {
            this.lastHit = System.nanoTime();
            return this;
        }
    }


}
