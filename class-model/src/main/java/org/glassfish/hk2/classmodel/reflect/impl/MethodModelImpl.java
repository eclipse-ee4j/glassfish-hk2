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

import org.glassfish.hk2.classmodel.reflect.*;

import java.util.List;

/**
 * Implementation of a method model
 */
public class MethodModelImpl extends AnnotatedElementImpl implements MethodModel {

    private List<Parameter> parameters;
    private ParameterizedType returnType;
    final ExtensibleType<?> owner;
    private final String signature;

    public MethodModelImpl(String name, ExtensibleType owner, String signature) {
        super(name);
        this.owner = owner;
        this.signature = signature;
    }

    @Override
    public Type getMemberType() {
        return Type.METHOD;
    }

    @Override
    public ExtensibleType<?> getDeclaringType() {
        return owner;
    }

    @Override
    public String getSignature() {
        return signature;
    }

    @Override
    public ParameterizedType getReturnType() {
        return returnType;
    }

    public void setReturnType(ParameterizedType returnType) {
        this.returnType = returnType;
    }

    @Override
    public String[] getArgumentTypes() {
        String[] stringTypes;
        if (parameters != null) {
            stringTypes = new String[parameters.size()];
            for (int i = 0; i < parameters.size(); i++) {
                stringTypes[i] = parameters.get(i).getTypeName();
            }
        } else {
            stringTypes = new String[0];
        }
        return stringTypes;
    }

    @Override
    public List<Parameter> getParameters() {
        return parameters;
    }

    @Override
    public Parameter getParameter(int index) {
        if (parameters != null) {
            return parameters.get(index);
        }
        return null;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

}
