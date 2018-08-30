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

/**
 * Information about the XmlElement annotation
 * 
 * @author jwells
 *
 */
public class XmlElementData {
    private final String namespace;
    private final String name;
    private final String alias;
    private final String defaultValue;
    private final Format format;
    private final String type;
    private final boolean isTypeInterface;
    private final String xmlWrapperTag;
    private final boolean required;
    private final String originalMethodName;
    
    XmlElementData(String namespace,
            String name,
            String alias,
            String defaultValue,
            Format format,
            String type,
            boolean isTypeInterface,
            String xmlWrapperTag,
            boolean required,
            String originalMethodName) {
        this.namespace = namespace;
        this.name = name;
        this.alias = alias;
        this.defaultValue = defaultValue;
        this.format = format;
        this.type = type;
        this.isTypeInterface = isTypeInterface;
        this.xmlWrapperTag = xmlWrapperTag;
        this.required = required;
        this.originalMethodName = originalMethodName;
    }
    
    public String getNamespace() { return namespace; }
    public String getName() { return name; }
    public String getAlias() { return alias; }
    public String getDefaultValue() { return defaultValue; }
    public Format getFormat() { return format; }
    public String getType() { return type; }
    public boolean isTypeInterface() { return isTypeInterface; }
    public String getXmlWrapperTag() { return xmlWrapperTag; }
    public boolean isRequired() { return required; }
    public String getOriginalMethodName() { return originalMethodName; }
    
    @Override
    public String toString() {
        return "XmlElementData(" + namespace +
                "," + name +
                "," + alias +
                "," + Utilities.safeString(defaultValue) +
                "," + format +
                "," + type +
                "," + isTypeInterface +
                "," + xmlWrapperTag +
                "," + required +
                "," + originalMethodName +
                "," + System.identityHashCode(this) + ")";
    }
}
