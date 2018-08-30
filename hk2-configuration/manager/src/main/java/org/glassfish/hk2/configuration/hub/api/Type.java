/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.configuration.hub.api;

import java.util.Map;

/**
 * A type contains (possibly) multiple instances of
 * a configuration bean
 * 
 * @author jwells
 *
 */
public interface Type {
    /**
     * A unique identifier for this type
     * 
     * @return A unique identifier for this type (may not return null)
     */
    public String getName();
    
    /**
     * Returns a read-only map of the instances that are associated with this type
     * 
     * @return A read-only and possibly empty map of instances associated
     * with this type
     */
    public Map<String, Instance> getInstances();
    
    /**
     * Gets the instance associated with this key, or null if there is none
     * 
     * @param key The non-null key for the instance
     * @return The resulting instance or null if there is none
     */
    public Instance getInstance(String key);
    
    /**
     * Gets information about this type.  Can be
     * used to describe the type in some useful way
     * 
     * @return The possibly null metadata associated
     * with this type
     */
    public Object getMetadata();
    
    /**
     * Sets an object containing information about this
     * type.  Can be used to describe the type in
     * some useful way
     * 
     * @param metadata The possibly null metadata
     * to be associated with this type
     */
    public void setMetadata(Object metadata);
}
