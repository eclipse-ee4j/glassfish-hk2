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

import javax.xml.namespace.QName;

import org.glassfish.hk2.xml.internal.alt.AdapterInformation;
import org.glassfish.hk2.xml.internal.alt.AltClass;
import org.glassfish.hk2.xml.internal.alt.AltMethod;
import org.glassfish.hk2.xml.internal.alt.MethodInformationI;

/**
 * Information needed for proxy from a method
 * 
 * @author jwells
 *
 */
public class MethodInformation implements MethodInformationI {
    /** The actual method */
    private final AltMethod originalMethod;
    
    /** The type of method, GETTER, SETTER et al */
    private final MethodType methodType;
    
    /**
     * If this is a getter or setter, the type of thing being set,
     * which might be a List or array, it is the true thing the
     * getter or setter of the method is setting
     */
    private final AltClass getterSetterType;
    
    /**
     * The original variable name if this is a getter or setter
     * before being translated to the representedProperty by the
     * XmlElement or XmlAttribute annotation
     */
    private final String decapitalizedMethodProperty;
    
    /** The xml tag for this method */
    private final QName representedProperty;
    
    /** The default value specified for this method */
    private final String defaultValue;
    
    /**
     * This is the type of thing being set.  For example
     * if this method is returning a List or Array
     * it'll be the first parameterized Type of the List
     * and if it an Array it'll be the Component type
     * of the Array
     */
    private final AltClass baseChildType;
    
    /**
     * True if this is a key property
     */
    private final boolean key;
    
    /**
     * True if this is a List child method
     */
    private final boolean isList;
    
    /**
     * True if this is an array child method
     */
    private final boolean isArray;
    
    /**
     * True if this is a setter or getter for a reference
     */
    private final boolean isReference;
    
    /**
     * The format of the data
     */
    private final Format format;
    
    /**
     * The parameterized type of the list if known
     */
    private final AltClass listParameterizedType;
    
    /**
     * The XmlWrapper tag or null
     */
    private final String xmlWrapperTag;
    
    private final AdapterInformation adapterInfo;
    
    private final boolean required;
    
    private final String originalMethodName;
    
    public MethodInformation(AltMethod originalMethod,
            MethodType methodType,
            String decapitalizedMethodProperty,
            QName representedProperty,
            String defaultValue,
            AltClass baseChildType,
            AltClass gsType,
            boolean key,
            boolean isList,
            boolean isArray,
            boolean isReference,
            Format format,
            AltClass listParameterizedType,
            String xmlWrapperTag,
            AdapterInformation adapterInfo,
            boolean required,
            String originalMethodName) {
        this.originalMethod = originalMethod;
        this.methodType = methodType;
        this.decapitalizedMethodProperty = decapitalizedMethodProperty;
        this.representedProperty = representedProperty;
        this.defaultValue = defaultValue;
        this.baseChildType = baseChildType;
        this.getterSetterType = gsType;
        this.key = key;
        this.isList = isList;
        this.isArray = isArray;
        this.isReference = isReference;
        this.format = format;
        this.listParameterizedType = listParameterizedType;
        this.xmlWrapperTag = xmlWrapperTag;
        this.adapterInfo = adapterInfo;
        this.required = required;
        this.originalMethodName = originalMethodName;
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.MethodInformationI#getOriginalMethod()
     */
    @Override
    public AltMethod getOriginalMethod() {
        return originalMethod;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.MethodInformationI#getMethodType()
     */
    @Override
    public MethodType getMethodType() {
        return methodType;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.MethodInformationI#getGetterSetterType()
     */
    @Override
    public AltClass getGetterSetterType() {
        return getterSetterType;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.MethodInformationI#getRepresentedProperty()
     */
    @Override
    public QName getRepresentedProperty() {
        return representedProperty;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.MethodInformationI#getDefaultValue()
     */
    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.MethodInformationI#getBaseChildType()
     */
    @Override
    public AltClass getBaseChildType() {
        return baseChildType;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.MethodInformationI#isKey()
     */
    @Override
    public boolean isKey() {
        return key;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.MethodInformationI#isList()
     */
    @Override
    public boolean isList() {
        return isList;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.MethodInformationI#isArray()
     */
    @Override
    public boolean isArray() {
        return isArray;
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.MethodInformationI#isReference()
     */
    @Override
    public boolean isReference() {
        return isReference;
    }
    
    @Override
    public boolean isRequired() {
        return required;
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.MethodInformationI#getDecapitalizedMethodProperty()
     */
    @Override
    public String getDecapitalizedMethodProperty() {
        return decapitalizedMethodProperty;
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.MethodInformationI#isElement()
     */
    @Override
    public Format getFormat() {
        return format;
    }
    
    @Override
    public AltClass getListParameterizedType() {
        return listParameterizedType;
    }
    
    @Override
    public String getWrapperTag() {
        return xmlWrapperTag;
    }
    
    @Override
    public AdapterInformation getAdapterInformation() {
        return adapterInfo;
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.MethodInformationI#getOriginalMethodName()
     */
    @Override
    public String getOriginalMethodName() {
        return originalMethodName;
    }
    
    @Override
    public String toString() {
        return "MethodInformation(name=" + originalMethod.getName() + "," +
          "type=" + methodType + "," +
          "getterType=" + getterSetterType + "," +
          "decapitalizedMethodProperty=" + decapitalizedMethodProperty + "," +
          "representedProperty=" + representedProperty + "," +
          "defaultValue=" + ((Generator.JAXB_DEFAULT_DEFAULT.equals(defaultValue)) ? "" : defaultValue) + "," +
          "baseChildType=" + baseChildType + "," +
          "key=" + key + "," +
          "isList=" + isList + "," +
          "isArray=" + isArray + "," +
          "isReference=" + isReference + "," +
          "format=" + format + "," +
          "listParameterizedType=" + listParameterizedType + "," +
          "xmlWrapperTag=" + xmlWrapperTag + "," +
          "adapterInfo=" + adapterInfo + "," +
          "required=" + required + "," +
          "originalMethodName=" + originalMethodName + "," +
          System.identityHashCode(this) + ")";
    }

    
}
