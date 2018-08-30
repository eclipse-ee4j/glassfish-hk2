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

import java.util.HashMap;
import java.util.Map;

/**
 * This bean configures the PropertyFileService itself.  An implementation
 * of this bean can be added directly to the Hub, or the utility method
 * {@link PropertyFileService#addPropertyFileBean(PropertyFileBean)} can
 * be used to add this bean to the Hub
 * 
 * @author jwells
 *
 */
public class PropertyFileBean {
    /** The name of the type under which this bean should be placed */
    public final static String TYPE_NAME = "PropertyFileServiceBean";
    
    /** The name of the single instance of this bean */
    public final static String INSTANCE_NAME = "DEFAULT";
    
    private final HashMap<String, Class<?>> mapping = new HashMap<String, Class<?>>();
    
    /**
     * A null constructor for creating an empty PropertyFileBean
     */
    public PropertyFileBean() {
    }
    
    /**
     * This method will create a deep copy of the passed in PropertyFileBean
     * 
     * @param copyMe The non-null bean to copy
     */
    public PropertyFileBean(PropertyFileBean copyMe) {
        mapping.putAll(copyMe.getTypeMapping());
    }
    
    /**
     * Gets the mapping from type name to bean class
     * 
     * @return A copy of the type name to bean class mapping
     */
    public Map<String, Class<?>> getTypeMapping() {
        synchronized (mapping) {
            return new HashMap<String, Class<?>>(mapping);
        }
    }
    
    /**
     * Adds a type mapping to the set of type mappings
     * 
     * @param typeName The name of the type.  May not be null
     * @param beanClass The bean class to which this type should be mapped.
     * May not be null
     */
    public void addTypeMapping(String typeName, Class<?> beanClass) {
        synchronized (mapping) {
            mapping.put(typeName, beanClass);
        }
    }
    
    /**
     * Removes the type mapping with the given name
     * 
     * @param typeName removes the type mapping of the given name.  May
     * not be null
     * @return The class associated with the type name, or null if there
     * was no type mapping with the given name
     */
    public Class<?> removeTypeMapping(String typeName) {
        synchronized (mapping) {
            return mapping.remove(typeName);
        }
    }
    
    /**
     * Gets the type mapping with the given name
     * 
     * @param typeName the type mapping to search for.  May
     * not be null
     * @return The class associated with the type name, or null if there
     * was no type mapping with the given name
     */
    public Class<?> getTypeMapping(String typeName) {
        synchronized (mapping) {
            return mapping.get(typeName);
        }
    }

}
