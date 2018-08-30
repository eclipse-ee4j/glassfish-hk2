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

package org.jvnet.hk2.external.runtime;

import org.jvnet.hk2.annotations.Contract;

/**
 * Runtime information about the ServiceLocator.
 * The ServiceLocatorRuntimeBean is specific to
 * this implementation of the HK2 API.  Further,
 * none of the values or operations on this bean
 * are guaranteed to be meaningful in the next
 * version of HK2, which may have used different
 * algorithms
 * 
 * @author jwells
 *
 */
@Contract
public interface ServiceLocatorRuntimeBean {
    /**
     * Returns the total number of descriptors
     * in this ServiceLocator.  Does not include
     * parent services
     * 
     * @return The number of services in this
     * ServiceLocator (does not include services
     * in the parent locator)
     */
    public int getNumberOfDescriptors();
    
    /**
     * Returns the current total number of children
     * attached to this ServiceLocator
     * 
     * @return The current number of children locators
     * attached to this ServiceLocator
     */
    public int getNumberOfChildren();
    
    /**
     * Returns the current size of the HK2 service
     * cache.  The service cache is used to optimize
     * frequent service lookups and injections
     * 
     * @return The current size of the HK2 service
     * cache
     */
    public int getServiceCacheSize();
    
    /**
     * Returns the maximum number of entries allowed
     * in the HK2 service cache.  The service cache is
     * used to optimize frequent service lookups and
     * injections
     * 
     * @return The maximum number of entries allowed
     * in the HK2 service cache
     */
    public int getServiceCacheMaximumSize();
    
    /**
     * Clears all entries from the HK2 service cache.
     * The service cache is used to optimize frequent
     * service lookups and injections.  Calling this
     * method may free up memory but will cause
     * degraded injection and lookup performance
     * until the cache can be built back up
     */
    public void clearServiceCache();
    
    /**
     * Returns the current size of the HK2 reflection
     * cache.  The reflection cache is used to minimize
     * the amount of reflection done by HK2
     * 
     * @return The current size of the HK2 reflection
     * cache
     */
    public int getReflectionCacheSize();
    
    /**
     * Clears all entries from the HK2 reflection
     * cache. The reflection cache is used to minimize
     * the amount of reflection done by HK2.  Calling this
     * method may free up memory but will cause
     * degraded service creation performance
     * until the cache can be built back up
     */
    public void clearReflectionCache();

}
