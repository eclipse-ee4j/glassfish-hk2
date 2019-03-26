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

import org.glassfish.hk2.classmodel.reflect.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a method model
 */
public class MethodModelImpl extends AnnotatedElementImpl implements MethodModel {

    final List<Parameter> parameters = new ArrayList<Parameter>();
    final ExtensibleType<?> owner;
    final String signature;

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
    public String getReturnType() {
        return org.objectweb.asm.Type.getReturnType(signature).getClassName();
    }

    @Override
    public String[] getArgumentTypes() {
        org.objectweb.asm.Type[] types = org.objectweb.asm.Type.getArgumentTypes(signature);
        String[] stringTypes = new String[types.length];
        for (int i=0;i<types.length;i++) {
            stringTypes[i] = types[i].getClassName();
        }
        return stringTypes;
    }
}
