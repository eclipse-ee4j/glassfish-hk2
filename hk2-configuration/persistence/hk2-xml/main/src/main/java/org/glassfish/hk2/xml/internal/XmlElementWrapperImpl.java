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

import javax.xml.bind.annotation.XmlElementWrapper;

import org.glassfish.hk2.api.AnnotationLiteral;

/**
 * @author jwells
 *
 */
public class XmlElementWrapperImpl extends AnnotationLiteral<XmlElementWrapper> implements XmlElementWrapper {
    private static final long serialVersionUID = 3661729772479049681L;
    
    private final String name;
    private final String namespace;
    private final boolean nillable;
    private final boolean required;
    
    public XmlElementWrapperImpl(String name, String namespace, boolean nillable, boolean required) {
        this.name = name;
        this.namespace = namespace;
        this.nillable = nillable;
        this.required = required;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String namespace() {
        return namespace;
    }

    @Override
    public boolean nillable() {
        return nillable;
    }

    @Override
    public boolean required() {
        return required;
    }
    
    @Override
    public String toString() {
        return "@XmlElemntWrapperImpl(" + name + "," + namespace + "," + nillable + "," + required + "," + System.identityHashCode(this) + ")";
    }

}
