/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
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

import org.glassfish.hk2.xml.jaxb.internal.BaseHK2JAXBBean;

public class UnresolvedReference {
    private final String type;
    private final String xmlID;
    private final String propertyNamespace;
    private final String propertyName;
    private final BaseHK2JAXBBean unfinished;
    
    UnresolvedReference(String type, String xmlID, String propertyNamespace, String propertyName, BaseHK2JAXBBean unfinished) {
        this.type = type;
        this.xmlID = xmlID;
        this.propertyNamespace = propertyNamespace;
        this.propertyName = propertyName;
        this.unfinished = unfinished;
    }
    
    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the xmlID
     */
    public String getXmlID() {
        return xmlID;
    }
    
    public String getPropertyNamespace() {
        return propertyNamespace;
    }

    /**
     * @return the propertyName
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * @return the unfinished
     */
    public BaseHK2JAXBBean getUnfinished() {
        return unfinished;
    }
    
    @Override
    public String toString() {
        return "UnresolvedReference(" + type + ","
                   + xmlID + ","
                   + propertyNamespace + ","
                   + propertyName + ","
                   + unfinished + ","
                   + System.identityHashCode(this) + ")";
    }
    
}
