/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.xml.internal.alt;

import javax.xml.namespace.QName;

import org.glassfish.hk2.xml.internal.Format;
import org.glassfish.hk2.xml.internal.MethodType;

public interface MethodInformationI {

    /**
     * @return the originalMethod
     */
    public AltMethod getOriginalMethod();

    /**
     * @return the methodType
     */
    public MethodType getMethodType();

    /**
     * @return the getterSetterType
     */
    public AltClass getGetterSetterType();

    /**
     * @return the representedProperty
     */
    public QName getRepresentedProperty();

    /**
     * @return the defaultValue
     */
    public String getDefaultValue();
    
    /**
     * @return The wrapper tag or null if there is none
     */
    public String getWrapperTag();

    /**
     * @return the baseChildType
     */
    public AltClass getBaseChildType();

    /**
     * @return true if this is a method that returns a key
     */
    public boolean isKey();

    /**
     * @return true if the method is for a List
     */
    public boolean isList();

    /**
     * @return true if the method is for an array
     */
    public boolean isArray();

    /**
     * @return true if this is a reference method
     */
    public boolean isReference();
    
    /**
     * @return true if this is a required field
     */
    public boolean isRequired();

    /**
     * @return The decapitilized version of the property name
     */
    public String getDecapitalizedMethodProperty();

    /**
     * @return Attribute, Element or Value
     */
    public Format getFormat();
    
    /**
     * Returns the parameterized type of the
     * list, or null if this is not a list or
     * the type of the list is unknown
     * 
     * @return The fully qualified class name
     * of the lists parameterized type
     */
    public AltClass getListParameterizedType();
    
    /**
     * Returns information about the type adapter for
     * this method.  Returns null if there is no
     * adapter
     * 
     * @return The adapter information on this method
     * or null if there is none
     */
    public AdapterInformation getAdapterInformation();
    
    /**
     * The name of the method upon which the annotation
     * was found.  May be useful in add-on parsers
     * that need to find other annotations on the method
     * 
     * @return The name of the original method on the interface
     */
    public String getOriginalMethodName();

}
