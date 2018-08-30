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

package org.glassfish.hk2.utilities.cache;

/**
 * This is used by the cache to remove a series of entries that
 * match this filter
 * 
 * @author jwells
 * @param <K> The key type for this filter
 *
 */
public interface CacheKeyFilter<K> {
    
    /**
     * Returns true if the key matches the filter
     * 
     * @param key The key from the cache to filter
     * @return true if the key matches, false otherwise
     */
    public boolean matches(K key);

}
