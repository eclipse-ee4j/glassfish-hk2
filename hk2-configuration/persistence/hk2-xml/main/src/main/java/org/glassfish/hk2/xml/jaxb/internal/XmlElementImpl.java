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

package org.glassfish.hk2.xml.jaxb.internal;

import javax.xml.bind.annotation.XmlElement;

import org.glassfish.hk2.api.AnnotationLiteral;
import org.glassfish.hk2.xml.internal.Utilities;

/**
 * @author jwells
 *
 */
public class XmlElementImpl extends AnnotationLiteral<XmlElement> implements XmlElement {
    private static final long serialVersionUID = -5015658933035011114L;
    
    private final String name;
    private final boolean nillable;
    private final boolean required;
    private final String namespace;
    private final String defaultValue;
    private final String typeByName;
    
    public XmlElementImpl(String name, boolean nillable, boolean required, String namespace, String defaultValue, String typeByName) {
        this.name = name;
        this.nillable = nillable;
        this.required = required;
        this.namespace = namespace;
        this.defaultValue = defaultValue;
        this.typeByName = typeByName;
    }

    /* (non-Javadoc)
     * @see javax.xml.bind.annotation.XmlElement#name()
     */
    @Override
    public String name() {
        return name;
    }

    /* (non-Javadoc)
     * @see javax.xml.bind.annotation.XmlElement#nillable()
     */
    @Override
    public boolean nillable() {
        return nillable;
    }

    /* (non-Javadoc)
     * @see javax.xml.bind.annotation.XmlElement#required()
     */
    @Override
    public boolean required() {
        return required;
    }

    /* (non-Javadoc)
     * @see javax.xml.bind.annotation.XmlElement#namespace()
     */
    @Override
    public String namespace() {
        return namespace;
    }

    /* (non-Javadoc)
     * @see javax.xml.bind.annotation.XmlElement#defaultValue()
     */
    @Override
    public String defaultValue() {
        return defaultValue;
    }

    /* (non-Javadoc)
     * @see javax.xml.bind.annotation.XmlElement#type()
     */
    @Override
    public Class type() {
        return XmlElement.DEFAULT.class;
    }
    
    public String getTypeByName() {
        return typeByName;
    }
    
    @Override
    public String toString() {
        return "@XmlElementImpl(name=" + name +
                ",nillable=" + nillable +
                ",required=" + required +
                ",namespace=" + namespace +
                ",defaultValue=" + Utilities.safeString(defaultValue) +
                ",typeByName=" + typeByName +
                ",sid=" + System.identityHashCode(this) + ")";
    }

}
