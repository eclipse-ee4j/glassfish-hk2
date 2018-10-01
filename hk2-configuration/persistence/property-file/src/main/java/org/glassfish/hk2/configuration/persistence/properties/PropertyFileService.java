/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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

import org.jvnet.hk2.annotations.Contract;

/**
 * This service will read a java property file and add its contents
 * to the HK2 configuration hub.
 * <p>
 * The property file can have structured keys.  If the property
 * key has a dot in it (.) then the string before the dot is
 * considered to be the name of the type.  If there are two dots
 * then the name of the instance is the string following the first
 * dot and before the second dot.  Anything after the second dot
 * is the name of the property.
 * <p>
 * A specific type name can be given.  If a specific type name is provided then
 * the name prior to the first dot is considered the name of the instance, and
 * everything after the first dot is the name of the property
 * <p>
 * A default name for instances of the type can be given.  Any instance
 * name provided in the property file will override the default
 * instance name.  The default default instance name is "DEFAULT"
 * 
 * @author jwells
 *
 */
@Contract
public interface PropertyFileService {
    /** The default name for an instance if the instance name cannot be determined */
    public final static String DEFAULT_INSTANCE_NAME = "DEFAULT";
    
    /** The default name for a type if the type name cannot be determined */
    public final static String DEFAULT_TYPE_NAME = "DEFAULT_TYPE";
    
    /**
     * Creates a PropertyFileHandle for reading an HK2 property file
     * that has a specific type name.  This is generally used for
     * property files that provide a specific set of instances
     * for a single type.  The default instance name will be set
     * to the defaultInstanceName given
     * 
     * @param specificTypeName The non-null, non-empty string specific type
     * name.  All instances created or modified with this PropertyFileHandle
     * will be in this type
     * @param defaultInstanceName The default name to give to instances of
     * this type if the instance name cannot be determined.  If null or
     * the empty string then the default default instance of DEFAULT will
     * be used
     * @return A non-null PropertyFileHandle that can be used to read the
     * property file
     */
    public PropertyFileHandle createPropertyHandleOfSpecificType(String specificTypeName,
            String defaultInstanceName);
    
    /**
     * Creates a PropertyFileHandle for reading an HK2 property file
     * that has a specific type name.  This is generally used for
     * property files that provide a specific set of instances
     * for a single type
     * 
     * @param specificTypeName The non-null, non-empty string specific type
     * name.  All instances created or modified with this PropertyFileHandle
     * will be in this type
     * @return A non-null PropertyFileHandle that can be used to read the
     * property file
     */
    public PropertyFileHandle createPropertyHandleOfSpecificType(String specificTypeName);
    
    /**
     * Creates a PropertyFileHandle for reading an HK2 property file.  This is
     * used for property files that provide instances of multiple types within the
     * same property file
     *
     * @param defaultTypeName The default type name that will be used
     * if a type name could not be determined.  If null or empty the
     * default type name of DEFAULT_TYPE will be used
     * @param defaultInstanceName The default name to give to instances of
     * this type if the instance name cannot be determined.  If null or
     * the empty string then the default default instance of DEFAULT will
     * be used
     * @return A non-null PropertyFileHandle that can be used to read the
     * property file
     */
    public PropertyFileHandle createPropertyHandleOfAnyType(
            String defaultTypeName,
            String defaultInstanceName);
    
    /**
     * Creates a PropertyFileHandle for reading an HK2 property file.  This is
     * used for property files that provide instances of multiple types within the
     * same property file.  Will use the default type name of DEFAULT_TYPE for
     * types that cannot be determined and the default instance name of DEFAULT
     * for instance names that cannot be determined
     *
     * @return A non-null PropertyFileHandle that can be used to read the
     * property file
     */
    public PropertyFileHandle createPropertyHandleOfAnyType();
    
    /**
     * This is a utility method that will add the given {@link PropertyFileBean}
     * to the Hub for use in configuring this service
     * 
     * @param propertyFileBean The non-null property file bean that should
     * either be added to the Hub or be used to modify the existing
     * bean in the hub
     */
    public void addPropertyFileBean(PropertyFileBean propertyFileBean);
    
    /**
     * This is a utility method that will remove the {@link PropertyFileBean}
     * from the Hub if it is present
     */
    public void removePropertyFileBean();
}
