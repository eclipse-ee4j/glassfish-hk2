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

package org.glassfish.hk2.xml.internal;

import javax.xml.namespace.QName;

import org.glassfish.hk2.utilities.general.GeneralUtilities;
import org.glassfish.hk2.xml.api.XmlService;

/**
 * @author jwells
 *
 */
public class QNameUtilities {
    
    /**
     * Returns the namespace after accounting for null or empty strings
     * 
     * @param namespace The possibly null namespace
     * @return The non-null namespace
     */
    public static final String fixNamespace(String namespace) {
        if (namespace == null || namespace.isEmpty() || namespace.trim().isEmpty()) {
            return XmlService.DEFAULT_NAMESPACE;
        }
        
        return namespace;
    }
    
    public static QName createQName(String namespace, String localPart) {
        return createQName(namespace, localPart, null);
    }
    
    /**
     * Creates a QName taking into account the DEFAULT_NAMESPACE field
     * from JAXB
     * 
     * @param namespace The possibly null namespace
     * @param localPart The not-null localPart
     * @param defaultNamespace The default namespace if known, or null if not known
     * @return
     */
    public static QName createQName(String namespace, String localPart, String defaultNamespace) {
        if (localPart == null) return null;
        
        if ((namespace == null) || namespace.isEmpty() || namespace.trim().isEmpty() ||
                XmlService.DEFAULT_NAMESPACE.equals(namespace) ||
                ((defaultNamespace != null) && GeneralUtilities.safeEquals(namespace, defaultNamespace))) {
            return new QName(localPart);
        }
        
        return new QName(namespace, localPart);
    }
    
    /**
     * Returns the namespace, but if the namespace is null or
     * empty will return
     * {@link XmlService#DEFAULT_NAMESPACE} instead
     * 
     * @param qName qName to find the namespace of or null
     * @return null if qName is null or the String for the namespace if not null
     */
    public static String getNamespace(QName qName) {
        return getNamespace(qName, null);
    }
    
    /**
     * Returns the namespace, but if the namespace is null or
     * empty will return
     * {@link XmlService#DEFAULT_NAMESPACE} instead
     * 
     * @param qName qName to find the namespace of or null
     * @param defaultNamespace The default namespace if known, or null if not known
     * @return null if qName is null or the String for the namespace if not null
     */
    public static String getNamespace(QName qName, String defaultNamespace) {
        if (qName == null) return null;
        
        String namespace = qName.getNamespaceURI();
        if ((namespace == null) || namespace.isEmpty() || namespace.trim().isEmpty() ||
                ((defaultNamespace != null) && GeneralUtilities.safeEquals(defaultNamespace, namespace))) {
            return XmlService.DEFAULT_NAMESPACE;
        }
        
        return namespace;
        
    }

}
