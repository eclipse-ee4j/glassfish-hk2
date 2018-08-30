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

import javax.xml.bind.annotation.XmlRootElement;

import org.glassfish.hk2.api.AnnotationLiteral;

/**
 * @author jwells
 *
 */
public class XmlRootElementImpl extends AnnotationLiteral<XmlRootElement> implements XmlRootElement {
    private static final long serialVersionUID = -4244154751522096417L;
    
    private final String namespace;
    private final String name;
    
    public XmlRootElementImpl(String namespace, String name) {
        this.namespace = namespace;
        this.name = name;
    }

    /* (non-Javadoc)
     * @see javax.xml.bind.annotation.XmlRootElement#namespace()
     */
    @Override
    public String namespace() {
        return namespace;
    }

    /* (non-Javadoc)
     * @see javax.xml.bind.annotation.XmlRootElement#name()
     */
    @Override
    public String name() {
        return name;
    }

    

}
