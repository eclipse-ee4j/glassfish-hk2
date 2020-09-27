/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayDeque;
import org.glassfish.hk2.classmodel.reflect.FieldModel;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureVisitor;
import org.glassfish.hk2.classmodel.reflect.ParameterizedType;

/**
 * Signature visitor to visit field and respective generic types
 *
 * @author gaurav.gupta@payara.fish
 */
public class FieldSignatureVisitorImpl extends SignatureVisitor {

    private final TypeBuilder typeBuilder;
    private final ArrayDeque<ParameterizedType> parentType = new ArrayDeque<>();

    public FieldSignatureVisitorImpl(TypeBuilder typeBuilder, FieldModel fieldModel) {
        super(Opcodes.ASM7);

        this.typeBuilder = typeBuilder;
        parentType.add(fieldModel);
    }

    @Override
    public void visitTypeVariable(String typeVariable) {
        if (!parentType.isEmpty()) {
            ParameterizedType current = parentType.peekLast();
            if (current instanceof FieldModelImpl
                    && ((FieldModelImpl) current).getTypeProxy() == null
                    && ((FieldModelImpl) current).getFormalType() == null) {
                ((FieldModelImpl) current).setFormalType(typeVariable);
            } else {
                ParameterizedTypeImpl parameterizedType = new ParameterizedTypeImpl(typeVariable);
                current.getParameterizedTypes().add(parameterizedType);
            }
        }
    }

    @Override
    public void visitClassType(String name) {
        String className = org.objectweb.asm.Type.getObjectType(name).getClassName();
        TypeProxy typeProxy = typeBuilder.getHolder(className);
        if (typeProxy != null) {
            if (!parentType.isEmpty()) {
                ParameterizedType current = parentType.peekLast();
                if (current instanceof FieldModelImpl
                        && ((FieldModelImpl) current).getTypeProxy() == null) {
                    ((FieldModelImpl) current).setTypeProxy(typeProxy);
                } else {
                    ParameterizedTypeImpl parameterizedType = new ParameterizedTypeImpl(typeProxy);
                    current.getParameterizedTypes().add(parameterizedType);
                    parentType.add(parameterizedType);
                }
            }
        }
    }

    @Override
    public void visitEnd() {
        parentType.pollLast();
    }

}
