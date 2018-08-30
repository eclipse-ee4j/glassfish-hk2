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

import org.glassfish.hk2.utilities.cache.internal.WeakCARCacheImpl;

/**
 * Utilities for creating caches
 * 
 * @author jwells
 *
 */
public class CacheUtilities {
    /**
     * Returns a WEAKCarCache with the given computable and the given maximum value size of the cache.
     * The Cache returned will have weak keys, so that when the key becomes only weakly reachable it
     * will be removed from the cache.  However, values will only be removed from the Cache when an operation
     * is performed on the cache or the method {@link WeakCARCache#clearStaleReferences()} is called
     * 
     * @param computable The computable that is used to get the V from the given K
     * @param maxSize The maximumSize of the cache
     * @param isWeak if true this will keep weak keyes, if false the keys will
     * be hard and will not go away even if they do not exist anywhere else
     * but this cache
     * @return A WeakCARCache that is empty
     */
    public static <K,V> WeakCARCache<K,V> createWeakCARCache(Computable<K,V> computable, int maxSize, boolean isWeak) {
        return new WeakCARCacheImpl<K,V>(computable, maxSize, isWeak);
    }

}
