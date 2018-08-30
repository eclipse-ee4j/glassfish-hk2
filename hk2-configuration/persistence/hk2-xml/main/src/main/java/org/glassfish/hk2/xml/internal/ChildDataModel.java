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

package org.glassfish.hk2.xml.internal;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.glassfish.hk2.utilities.general.GeneralUtilities;

/**
 * This represents a child that is not a bean, as in a value like a string or a date
 * or an integer
 * 
 * @author jwells
 *
 */
public class ChildDataModel implements Serializable {
    private static final long serialVersionUID = 208423310453044595L;
    private final static Map<String, Class<?>> TYPE_MAP = new HashMap<String, Class<?>>();
    
    static {
        TYPE_MAP.put("char", char.class);
        TYPE_MAP.put("byte", byte.class);
        TYPE_MAP.put("short", short.class);
        TYPE_MAP.put("int", int.class);
        TYPE_MAP.put("float", float.class);
        TYPE_MAP.put("long", long.class);
        TYPE_MAP.put("double", double.class);
        TYPE_MAP.put("boolean", boolean.class);
    };
    
    private final Object lock = new Object();
    
    /** Set at compile time, the type of the thing */
    private String childType;
    private String defaultAsString;
    private boolean isReference;
    private Format format;
    private String childListType;
    private AliasType aliasType;
    private String aliasOf;
    private boolean required;
    private String originalMethodName;
    
    private ClassLoader myLoader;
    private Class<?> childTypeAsClass;
    private Class<?> childListTypeAsClass;
    
    public ChildDataModel() {
    }
    
    public ChildDataModel(String childType,
            String childListType,
            String defaultAsString,
            boolean isReference,
            Format format,
            AliasType aliasType,
            String aliasOf,
            boolean required,
            String originalMethodName) {
        this.childType = childType;
        this.defaultAsString = defaultAsString;
        this.isReference = isReference;
        this.format = format;
        this.childListType = childListType;
        this.aliasType = aliasType;
        this.aliasOf = aliasOf;
        this.required = required;
        this.originalMethodName = originalMethodName;
    }
    
    public String getChildType() {
        return childType;
    }
    
    public String getChildListType() {
        return childListType;
    }
    
    public String getDefaultAsString() {
        return defaultAsString;
    }
    
    public boolean isReference() {
        return isReference;
    }
    
    public Format getFormat() {
        return format;
    }
    
    public AliasType getAliasType() {
        return aliasType;
    }
    
    public String getXmlAlias() {
        return aliasOf;
    }
    
    public void setLoader(ClassLoader myLoader) {
        synchronized (lock) {
            this.myLoader = myLoader;
        }
    }
    
    public Class<?> getChildTypeAsClass() {
        synchronized (lock) {
            if (childTypeAsClass != null) return childTypeAsClass;
            
            childTypeAsClass = TYPE_MAP.get(childType);
            if (childTypeAsClass != null) return childTypeAsClass;
            
            childTypeAsClass = GeneralUtilities.loadClass(myLoader, childType);
            
            return childTypeAsClass;
        }
        
    }
    
    public Class<?> getChildListTypeAsClass() {
        synchronized (lock) {
            if (childListType == null) return null;
            if (childListTypeAsClass != null) return childListTypeAsClass;
            
            childListTypeAsClass = TYPE_MAP.get(childListType);
            if (childListTypeAsClass != null) return childListTypeAsClass;
            
            childListTypeAsClass = GeneralUtilities.loadClass(myLoader, childListType);
            
            return childListTypeAsClass;
        }
        
    }
    
    public boolean isRequired() {
        return required;
    }
    
    public String getOriginalMethodName() {
        return originalMethodName;
    }
    
    @Override
    public String toString() {
        return "ChildDataModel(" + childType +
                "," + defaultAsString +
                "," + isReference +
                "," + childListType +
                "," + aliasType +
                "," + format +
                "," + required +
                "," + originalMethodName +
                "," + System.identityHashCode(this) + ")";
    }

}
