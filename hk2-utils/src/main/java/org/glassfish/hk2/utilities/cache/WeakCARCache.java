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

package org.glassfish.hk2.utilities.cache;

/**
 * A cache that uses the CAR algorithm to remove entries.
 * <p>
 * As a quick review, the CAR algorithm maintains four lists:<OL>
 * <LI>t1 - A clock of recently used keys with values</LI>
 * <LI>t2 - A clock of frequently used keys with values</LI>
 * <LI>b1 - A LRU of keys removed from t1 with no values</LI>
 * <LI>b2 - A LRU of keys removed from t2 with no values</LI>
 * </OL>
 * The sum of entries in t1 and t2 will never be higher than maxSize (c).
 * The sum of entries in all four lists will never be higher than 2c.
 * There is an adaptive parameter p which is the target size of the
 * t1 list which gets modified when a key is found on either the b1 or
 * b2 list.  This adaptive parameter essentially allows the algorithm
 * to adapt to the pattern of keys, whether there be more frequently used
 * keys or there be a pattern of keys used quickly but not again.
 * 
 * 
 * @author jwells
 *
 */
public interface WeakCARCache<K,V> {
    /**
     * The method used to get or add values to this cache
     * 
     * @param key The key to add to the cache.  If the value
     * is not found, then the computable will be called to
     * get the value.  May not be null
     * 
     * @return The calculated return value.  May not be null
     */
    public V compute(K key);
    
    /**
     * Returns the current number of keys in the cache.  Note
     * that the number of keys can be up to 2x the maximum size
     * of the cache
     * 
     * @return The current number of key entries in the cache
     */
    public int getKeySize();
    
    /**
     * Returns the current number of values in the cache.  Note
     * that the number of values can be up the maximum size
     * of the cache
     * 
     * @return The current number of value entries in the cache
     */
    public int getValueSize();
    
    /**
     * Returns the number of items in the T1 clock
     * 
     * @return The current number of items in the T1 clock
     */
    public int getT1Size();
    
    /**
     * Returns the number of items in the T2 clock
     * 
     * @return The current number of items in the T2 clock
     */
    public int getT2Size();
    
    /**
     * Returns the number of items in the B1 LRU
     * 
     * @return The current number of items in the B1 LRU
     */
    public int getB1Size();
    
    /**
     * Returns the number of items in the B2 LRU
     * 
     * @return The current number of items in the B2 LRU
     */
    public int getB2Size();
    
    
    /**
     * Clears the current cache, making the current size zero
     */
    public void clear();
    
    /**
     * Gets the current maximum size of the cache (the maximum
     * number of values that will be kept by the cache).  Note
     * that the number of keys kept will be 2x, where x is the
     * maximum size of the cache (see CAR algorithm which keeps
     * a key history)
     * 
     * @return The maximum size of the cache
     */
    public int getMaxSize();
    
    /**
     * The computable associated with this cache
     * 
     * @return The computable associated with this cache
     */
    public Computable<K,V> getComputable();
    
    /**
     * Used to remove a single key and value from the cache (if
     * the value is available)
     * @param key The key to remove. May not be null
     * @return true if a key was found and removed
     */
    public boolean remove(K key);
    
    /**
     * Releases all key/value pairs that match the filter
     * 
     * @param filter A non-null filter that can be used
     * to delete every key/value pair that matches the filter
     */
    public void releaseMatching(CacheKeyFilter<K> filter);
    
    /**
     * Causes stale references to be cleared from the data
     * structures.  Since this is a weak cache the references
     * can go away at any time, which happens whenever
     * any operation has been performed.  However, it may be
     * the case that no operation will be performed for a while
     * and so this method is provided to have a no-op operation
     * to call in order to clear out any stale references
     */
    public void clearStaleReferences();
    
    /**
     * Returns the value of p from the CAR algorithm, which
     * is the target size of the t1 clock
     * 
     * @return The current value of P
     */
    public int getP();
    
    /**
     * Returns a string that will contain all the elements of the four lists
     * 
     * @return A String containing the values of T1, T2, B1 and B2
     */
    public String dumpAllLists();
    
    /**
     * Returns the hit rate from the last time clear was called
     * @return The Hit rate from the last time clear was called 
     * or 0 if there is no data
     */
    public double getHitRate();

}
