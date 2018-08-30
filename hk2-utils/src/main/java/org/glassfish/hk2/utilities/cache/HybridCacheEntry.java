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
 * Represents a single hybrid cache entry.
 * The entry can avoid being cached, see {@link #dropMe()} for details.
 *
 * @author Jakub Podlesak (jakub.podlesak at oracle.com)
 */
public interface HybridCacheEntry<V> extends CacheEntry {

    /**
     * Getter for this cache entry internal value.
     *
     * @return Internal value.
     */
    public V getValue();

    /**
     * Tell the cache if this entry should be dropped
     * as opposed to being kept in the cache.
     *
     * @return true if the entry should not be cached.
     */
    public boolean dropMe();
}
