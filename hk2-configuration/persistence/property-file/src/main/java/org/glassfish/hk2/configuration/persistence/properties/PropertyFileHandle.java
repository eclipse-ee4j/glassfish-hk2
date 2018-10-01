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

package org.glassfish.hk2.configuration.persistence.properties;

import java.io.IOException;
import java.util.Properties;

/**
 * This handle is used to read property files and put the values into the
 * HK2 configuration hub.  The readFile method can be called multiple times
 * if the file should be read again because the instances or property values
 * may have changed
 * 
 * @author jwells
 *
 */
public interface PropertyFileHandle {
    /**
     * Reads the file associated with this handle and will do the following:
     * <UL>
     * <LI>Add any type found not previously added by this handle</LI>
     * <LI>Add any instance found not previously added by this handle</LI>
     * <LI>Modify any property that has changed value</LI>
     * <LI>Remove any instance no longer seen in the file but that had previously been added</LI>
     * <UL>
     * In particular this method will NOT remove a type that was previously added but
     * which has no more instances (other files may be contributing to the same type).
     * After reaching the end of the input stream this method will close it
     * 
     * @param properties The properties object to inspect.  May not be null
     */
    public void readProperties(Properties properties);
    
    /**
     * Returns the specific type associated with this handle
     * 
     * @return The specific type this handle is updating.  May
     * return null if this is a multi-type handle
     */
    public String getSpecificType();
    
    /**
     * Returns the default type name if the type cannot
     * be determined from the key of the property.  Will
     * return null if getSpecificType is not null
     * 
     * @return The default type name if the type cannot
     * be determined, or null if this handle has a
     * specific type
     */
    public String getDefaultType();
    
    /**
     * Gets the default instance name that will be given
     * to instances whose name cannot otherwise be determined
     * 
     * @return The default instance name.  Will not return
     * null
     */
    public String getDefaultInstanceName();
    
    /**
     * Will remove any instances added by this handle from
     * the hub, and make it such that this handle can no longer
     * be used
     */
    public void dispose();

}
