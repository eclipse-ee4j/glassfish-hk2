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

import java.beans.PropertyChangeEvent;

/**
 * @author jwells
 *
 */
public interface WriteableType extends Type {
    /**
     * Adds the instance with the given key to the type
     * 
     * @param key A non-null name for this bean
     * @param bean The non-null bean to add
     * @return The instance that was created
     */
    public Instance addInstance(String key, Object bean);
    
    /**
     * Adds the instance with the given key to the type
     * 
     * @param key A non-null name for this bean
     * @param bean The non-null bean to add
     * @param metadata Possibly null metadata to be associated with this bean
     * @return The instance that was created
     */
    public Instance addInstance(String key, Object bean, Object metadata);
    
    /**
     * Removes the instance with the given key from the type
     * @param key A non-null name for this bean
     * @return The possibly null bean that was removed.  If null
     * then no bean was found with the given name
     */
    public Instance removeInstance(String key);
    
    /**
     * Modifies the instance with the given key
     * 
     * @param key A non-null name or key for the bean to modify
     * @param newBean The new bean to use with this key
     * @param changes The full set of changes from the previous version.  If this
     * is a zero-length array then the system will attempt to automatically determine
     * the changes made to this type and will generate the list of PropertyChangeEvent
     * to be associated with this modification
     * @return If changes has length greater than zero then this simply returns changes.
     * If changes is zero length then this will return the set of changes automatically
     * determined by the system
     */
    public PropertyChangeEvent[] modifyInstance(String key, Object newBean, PropertyChangeEvent... changes);

}
