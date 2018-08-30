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

package org.glassfish.hk2.api;

/**
 * This cache can be used in some circumstances when there can be only one
 * of a service.  This is useful and can avoid an expensive lookup in certain
 * context implementations
 * 
 * @author jwells
 * @param <T> The type of service stored and returned from this cache
 *
 */
public interface SingleCache<T> {
    /**
     * This can be used for scopes that will only every be created once.
     * The returned value must have been set previously with setCache.
     * If this is called when isCacheSet is false will result in a
     * RuntimeException
     * 
     * @return A value cached with this ActiveDescriptor
     */
    public T getCache();
    
    /**
     * Returns true if this cache has been set
     * 
     * @return true if there is a currently cached value, false
     * otherwise
     */
    public boolean isCacheSet();
    
    /**
     * Sets the value into the cache
     * 
     * @param cacheMe A single value that can be cached in this
     * active descriptor
     */
    public void setCache(T cacheMe);
    
    /**
     * Removes the cached value and makes it such
     * that this cache has not been set
     */
    public void releaseCache();

}
