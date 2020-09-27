/*
 * Copyright (c) 2010, 2020 Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList;
import java.util.List;
import org.glassfish.hk2.classmodel.reflect.ExtensibleType;
import org.glassfish.hk2.classmodel.reflect.FieldModel;
import org.objectweb.asm.Opcodes;
import org.glassfish.hk2.classmodel.reflect.ParameterizedType;

/**
 * Implementation of a field model
 */
public class FieldModelImpl extends AnnotatedElementImpl implements FieldModel {

    private final ExtensibleType declaringType;

    private TypeProxy typeProxy;

    private org.objectweb.asm.Type type;

    private String formalType;

    private int access;

    private final List<ParameterizedType> parameterizedTypes = new ArrayList<>();

    public FieldModelImpl(String name, TypeProxy typeProxy, ExtensibleType declaringType) {
        super(name);
        this.typeProxy = typeProxy;
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
    public String getDeclaringTypeName() {
        return typeProxy.getName();
    }

    @Override
    public ExtensibleType getType() {
        if (typeProxy != null) {
            return (ExtensibleType) typeProxy.get();
        }
        return null;
    }

    @Override
    public String getTypeName() {
        if (typeProxy != null) {
            return typeProxy.getName();
        } else if (type != null) {
            return type.getClassName();
        } else {
            return null;
        }
    }

    @Override
    public String getFormalType() {
        return formalType;
    }

    public void setFormalType(String formalType) {
        this.formalType = formalType;
    }

    public TypeProxy<?> getTypeProxy() {
        return typeProxy;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    public void setTypeProxy(TypeProxy typeProxy) {
        this.typeProxy = typeProxy;
    }

    public void setType(org.objectweb.asm.Type type) {
        this.type = type;
    }

    @Override
    protected void print(StringBuffer sb) {
        super.print(sb);
        sb.append(", type =").append(typeProxy.getName());
    }

    @Override
    public List<ParameterizedType> getParameterizedTypes() {
        return parameterizedTypes;
    }

    @Override
    public boolean isFormalType() {
        return formalType != null;
    }

    @Override
    public boolean isTransient() {
        return (Opcodes.ACC_TRANSIENT & access) == Opcodes.ACC_TRANSIENT;
    }

    @Override
    public boolean isArray() {
        return type != null
                && type.getSort() == org.objectweb.asm.Type.ARRAY;
    }
}
