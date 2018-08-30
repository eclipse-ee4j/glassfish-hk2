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
import java.util.List;

/**
 * This represents a single change made to a {@link BeanDatabase}
 * 
 * @author jwells
 *
 */
public interface Change {
    /**
     * Gets the category of change this Change object
     * represents
     * 
     * @return <UL>
     * <LI>REMOVE_TYPE</LI>
     * <LI>ADD_TYPE</LI>
     * <LI>ADD_INSTANCE</LI>
     * <LI>REMOVE_INSTANCE</LI>
     * <LI>MODIFY_INSTANCE</LI>
     * </UL>
     */
    public ChangeCategory getChangeCategory();
    
    /**
     * Gets the type of the change for all change categories.  In
     * the case of ADD_TYPE the value returned will include all
     * instances added, but there will also be an ADD_INSTANCE
     * change sent for each instance of this type that was added.
     * In the case of REMOVE_TYPE the value return will include
     * all instances still in the type at the time of removal, but
     * there will also be a REMOVE_INSTANCE change sent for each
     * instance that was in the type at the time of type removal 
     * 
     * @return The type of the change.  Will not be null
     */
    public Type getChangeType();
    
    /**
     * Returns the key of the instance that was removed, added or modified
     * for the categories ADD_INSTANCE, REMOVE_INSTANCE and MODIFY_INSTANCE
     * 
     * @return The key of the instance that was added, removed or modified.
     * Returns null for change category REMOVE_TYPE or ADD_TYPE
     */
    public String getInstanceKey();
    
    /**
     * Returns the value of the instance that was removed, added or modified
     * for the categories ADD_INSTANCE, REMOVE_INSTANCE and MODIFY_INSTANCE.
     * In the MODIFY_INSTANCE case this will return the new Instance value
     * 
     * @return The value of the instance that was added, removed or modified.
     * Returns null for change category REMOVE_TYPE or ADD_TYPE
     */
    public Instance getInstanceValue();
    
    /**
     * Returns the original Instance for the category MODIFY_INSTANCE
     * 
     * @return The original Instance for this key if the category is
     * MODIFY_INSTANCE.  Returns null for all other category of change
     */
    public Instance getOriginalInstanceValue();
    
    /**
     * Returns a list of properties that were changed if the change category
     * is MODIFY_INSTANCE.
     * 
     * @return A non-null and non-empty list of modified properties that were
     * changed in the instance for change category MODIFY_INSTANCE.  Returns
     * null for all other change categories
     */
    public List<PropertyChangeEvent> getModifiedProperties();
    
    public enum ChangeCategory {
        /** A type was removed */
        REMOVE_TYPE,
        
        /** A type was added */
        ADD_TYPE,
        
        /** An instance of a type was added */
        ADD_INSTANCE,
        
        /** An instance of a type was removed */
        REMOVE_INSTANCE,
        
        /** An instance of a type was modified */
        MODIFY_INSTANCE
        
    }

}
