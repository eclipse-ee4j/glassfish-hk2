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

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureVisitor;

import java.util.*;
import org.glassfish.hk2.classmodel.reflect.MethodModel;
import org.glassfish.hk2.classmodel.reflect.Parameter;
import org.glassfish.hk2.classmodel.reflect.ParameterizedType;

/**
 * Signature visitor to visit method parameters, return type and respective
 * generic types
 *
 * @author gaurav.gupta@payara.fish
 */
public class MethodSignatureVisitorImpl extends SignatureVisitor {

    private final TypeBuilder typeBuilder;
    private final MethodModel methodModel;

    private final List<Parameter> parameters = new ArrayList<>();
    private final ParameterizedType returnType = new ParameterizedTypeImpl();
    private final ArrayDeque<ParameterizedType> parentType = new ArrayDeque<>();

    public MethodSignatureVisitorImpl(TypeBuilder typeBuilder, MethodModel methodModel) {
        super(Opcodes.ASM7);

        this.typeBuilder = typeBuilder;
        this.methodModel = methodModel;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public ParameterizedType getReturnType() {
        return returnType;
    }

    @Override
    public SignatureVisitor visitParameterType() {
        ParameterImpl parameter = new ParameterImpl(parameters.size(), null, methodModel);
        parameters.add(parameter);
        parentType.add(parameter);
        return this;
    }

    @Override
    public SignatureVisitor visitReturnType() {
        parentType.add(returnType);
        return this;
    }

    @Override
    public void visitTypeVariable(String typeVariable) {
        if (!parentType.isEmpty()) {
            ParameterizedType current = parentType.peekLast();
            if (current instanceof ParameterImpl
                    && ((ParameterImpl) current).getTypeProxy() == null
                    && ((ParameterImpl) current).getFormalType() == null) {
                ((ParameterImpl) current).setFormalType(typeVariable);
            } else if (current instanceof ParameterizedTypeImpl
                    && ((ParameterizedTypeImpl) current).getTypeProxy() == null
                    && ((ParameterizedTypeImpl) current).getFormalType() == null) {
                ((ParameterizedTypeImpl) current).setFormalType(typeVariable);
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
                if (current instanceof ParameterImpl
                        && ((ParameterImpl) current).getTypeProxy() == null) {
                    ((ParameterImpl) current).setTypeProxy(typeProxy);
                } else if (current instanceof ParameterizedTypeImpl
                        && ((ParameterizedTypeImpl) current).getTypeProxy() == null) {
                    ((ParameterizedTypeImpl) current).setTypeProxy(typeProxy);
                } else {
                    ParameterizedTypeImpl parameterizedType = new ParameterizedTypeImpl(typeProxy);
                    current.getParameterizedTypes().add(parameterizedType);
                    parentType.add(parameterizedType);
                }
            }
        }
    }

    @Override
    public SignatureVisitor visitTypeArgument(char c) {
        return this;
    }

    @Override
    public void visitEnd() {
        parentType.pollLast();
    }
}
