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

package org.glassfish.hk2.utilities.test;

import junit.framework.Assert;

import org.glassfish.hk2.utilities.cache.CacheEntry;
import org.glassfish.hk2.utilities.cache.CacheKeyFilter;
import org.glassfish.hk2.utilities.cache.LRUCache;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class CacheTest {
    
    /**
     * The most basic test, add to and get from cache
     */
    @Test
    public void testCanGetFromCache() {
        LRUCache<Integer, Integer> cache = LRUCache.createCache(3);
        
        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(3, 3);
        
        Assert.assertSame(1, cache.get(1));
        Assert.assertSame(2, cache.get(2));
        Assert.assertSame(3, cache.get(3));
    }
    
    /**
     * If not in cache, returns null
     */
    @Test
    public void testIfNotThereReturnsNull() {
        LRUCache<Integer, Integer> cache = LRUCache.createCache(3);
        
        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(3, 3);
        
        Assert.assertNull(cache.get(4));
    }
    
    /**
     * If cache is size three, only three values should be stored.  And the one removed
     * should be the last one
     */
    @Test
    public void testLastIsRemoved() {
        LRUCache<Integer, Integer> cache = LRUCache.createCache(3);
        
        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(3, 3);
        cache.put(4, 4);
        
        // 1 is least recently used, so it should be gone
        Assert.assertNull(cache.get(1));
        
        // Also check that the others ARE there
        Assert.assertSame(4, cache.get(4));
        Assert.assertSame(2, cache.get(2));
        Assert.assertSame(3, cache.get(3));
    }
    
    /**
     * Ensure that get changes the LRU order of the list
     */
    @Test
    public void testGetChangesOrder() {
        LRUCache<Integer, Integer> cache = LRUCache.createCache(3);
        
        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(3, 3);
        
        // 1 is last now, but if I get it, it should move to the front, and
        // 2 would be put out on the next add
        Assert.assertSame(1, cache.get(1));
        
        cache.put(4, 4);
        
        // 2 was least recently used, so it should be gone
        Assert.assertNull(cache.get(2));
        
        // Also check that the others ARE there
        Assert.assertSame(4, cache.get(4));
        Assert.assertSame(1, cache.get(1));
        Assert.assertSame(3, cache.get(3));
    }
    
    /**
     * Tests that an entry in the middle of a larger cache can be properly moved
     */
    @Test
    public void testMakeChangesToLargerCache() {
        LRUCache<Integer, Integer> cache = LRUCache.createCache(5);
        
        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(3, 3);
        cache.put(4, 4);
        cache.put(5, 5);
        
        // Three would be in the middle of the queue
        Assert.assertSame(3, cache.get(3));
        
        cache.put(6, 6);
        
        // 1 was least recently used, should be gone
        Assert.assertNull(cache.get(1));
        
        // Also check that the others ARE there
        Assert.assertSame(4, cache.get(4));
        Assert.assertSame(2, cache.get(2));
        Assert.assertSame(3, cache.get(3));
        Assert.assertSame(5, cache.get(5));
    }
    
    /**
     * The most basic test, add to and get from cache
     */
    @Test
    public void testReleaseCache() {
        LRUCache<Integer, Integer> cache = LRUCache.createCache(3);
        
        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(3, 3);
        
        cache.releaseCache();
        
        Assert.assertNull(cache.get(1));
        Assert.assertNull(cache.get(2));
        Assert.assertNull(cache.get(3));
    }
    
    /**
     * Tests that removing directly from the cache using the CacheEntry works
     */
    @Test
    public void testRemoveFirstFromCache() {
        LRUCache<Integer, Integer> cache = LRUCache.createCache(3);
        
        CacheEntry entry = cache.put(1, 1);
        cache.put(2, 2);
        cache.put(3, 3);
        
        entry.removeFromCache();
        
        Assert.assertNull(cache.get(1));
        Assert.assertNotNull(cache.get(2));
        Assert.assertNotNull(cache.get(3));
    }
    
    /**
     * Tests that removing directly from the cache using the CacheEntry works
     */
    @Test
    public void testRemoveMiddleFromCache() {
        LRUCache<Integer, Integer> cache = LRUCache.createCache(3);
        
        cache.put(1, 1);
        CacheEntry entry = cache.put(2, 2);
        cache.put(3, 3);
        
        entry.removeFromCache();
        
        Assert.assertNotNull(cache.get(1));
        Assert.assertNull(cache.get(2));
        Assert.assertNotNull(cache.get(3));
    }
    
    /**
     * Tests that removing directly from the cache using the CacheEntry works
     */
    @Test
    public void testRemoveLastFromCache() {
        LRUCache<Integer, Integer> cache = LRUCache.createCache(3);
        
        cache.put(1, 1);
        cache.put(2, 2);
        CacheEntry entry = cache.put(3, 3);
        
        entry.removeFromCache();
        
        Assert.assertNotNull(cache.get(1));
        Assert.assertNotNull(cache.get(2));
        Assert.assertNull(cache.get(3));
    }
    
    /**
     * Tests changing entries around
     */
    @Test
    public void testChangeEntries() {
        LRUCache<Integer, Integer> cache = LRUCache.createCache(3);
        
        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(3, 3);
        
        cache.put(1, 4);
        cache.put(2, 5);
        cache.put(3, 6);
        
        Assert.assertEquals(new Integer(4), cache.get(1));
        Assert.assertEquals(new Integer(5), cache.get(2));
        Assert.assertEquals(new Integer(6), cache.get(3));
    }
    
    /**
     * This was an error case found in ServiceLocatorImpl
     */
    @Test
    public void testRemoveMovedEntry() {
        LRUCache<Integer, Integer> cache = LRUCache.createCache(3);
        
        cache.put(1, 1);
        CacheEntry entry = cache.put(2, 2);
        cache.put(3, 3);
        
        Assert.assertEquals(new Integer(2), cache.get(2));  // Moves 2
        
        entry.removeFromCache();
        
        Assert.assertNull(cache.get(2));
    }
    
    /**
     * This removes multiple entries from the cache based
     * on a filter
     */
    @Test
    public void testRemoveEvenEntries() {
        LRUCache<Integer, Integer> cache = LRUCache.createCache(20);
        
        for (int i = 0; i < 10; i++) {
            cache.put(i, i);
        }
        
        cache.releaseMatching(new CacheKeyFilter<Integer>() {

            @Override
            public boolean matches(Integer key) {
                if ((key % 2) == 0) return true;
                
                return false;
            }
            
        });
        
        for (int i = 0; i < 10; i++) {
            if ((i % 2) == 0) {
                Assert.assertNull(cache.get(i));
            }
            else {
                Assert.assertSame(cache.get(i), i);
            }
        }
    }

}
