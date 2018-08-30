/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.classmodel.reflect.impl;

import org.glassfish.hk2.classmodel.reflect.ClassModel;
import org.glassfish.hk2.classmodel.reflect.ExtensibleType;
import org.glassfish.hk2.classmodel.reflect.FieldModel;

/**
 * Implementation of a field model
 */
public class FieldModelImpl extends AnnotatedElementImpl implements FieldModel {

    final TypeProxy type;
    private final ExtensibleType declaringType;

    public FieldModelImpl(String name, TypeProxy type, ExtensibleType declaringType) {
        super(name);
        this.type = type;
        this.declaringType = declaringType;
    }

    @Override
    public Type getMemberType() {
        return Type.FIELD;
    }

    @Override
    public ExtensibleType getDeclaringType() {
        return declaringType;
    }

    @Override
    public ExtensibleType getType() {
        return (ExtensibleType) type.get();
    }

    @Override
    protected void print(StringBuffer sb) {
        super.print(sb);
        sb.append(", type =").append(type.getName());
    }
}
