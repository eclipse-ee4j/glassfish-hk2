/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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

import org.glassfish.hk2.classmodel.reflect.InterfaceModel;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureVisitor;

import java.util.*;

/**
 * Signature visitor to visit parameterized declarations
 *
 * @author Jerome Dochez
 */
public class SignatureVisitorImpl extends SignatureVisitor {

    private final TypeBuilder typeBuilder;
    private final Stack<ParameterizedInterfaceModelImpl> stack = new Stack<ParameterizedInterfaceModelImpl>();
    private final Map<String, ParameterizedInterfaceModelImpl> formalTypes = new HashMap<String, ParameterizedInterfaceModelImpl>();
    private final Stack<String> formalTypesNames = new Stack<String>();
    private final List<ParameterizedInterfaceModelImpl> parameterizedIntf = new ArrayList<ParameterizedInterfaceModelImpl>();

    public SignatureVisitorImpl(TypeBuilder typeBuilder) {
        super(Opcodes.ASM7);
        
        this.typeBuilder = typeBuilder;
    }

    Collection<ParameterizedInterfaceModelImpl> getImplementedInterfaces() {
        return Collections.unmodifiableCollection(parameterizedIntf);
    }


    @Override
    public void visitFormalTypeParameter(String s) {
        formalTypesNames.push(s);
    }

    @Override
    public SignatureVisitor visitClassBound() {
        return this;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SignatureVisitor visitInterfaceBound() {
        return this;
    }

    @Override
    public SignatureVisitor visitSuperclass() {
        return this;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SignatureVisitor visitInterface() {
        return this;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SignatureVisitor visitParameterType() {
        return this;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SignatureVisitor visitReturnType() {
        return this;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SignatureVisitor visitExceptionType() {
        return this;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visitBaseType(char c) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visitTypeVariable(String s) {
        if (formalTypes.containsKey(s)) {
            String interfaceName = formalTypes.get(s).getName();
            TypeProxy<InterfaceModel> interfaceTypeProxy = typeBuilder.getHolder(
                    interfaceName, InterfaceModel.class);
            if (interfaceTypeProxy!=null) {
                ParameterizedInterfaceModelImpl childParameterized = new ParameterizedInterfaceModelImpl(interfaceTypeProxy);
                if (!stack.empty()) {
                    stack.peek().addParameterizedType(childParameterized);
                }
            }
        }
    }

    @Override
    public SignatureVisitor visitArrayType() {
        return this;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visitClassType(String s) {
        String interfaceName = org.objectweb.asm.Type.getObjectType(s).getClassName();
        TypeProxy<InterfaceModel> interfaceTypeProxy = typeBuilder.getHolder(interfaceName, InterfaceModel.class);
        if (interfaceTypeProxy!=null) {
            ParameterizedInterfaceModelImpl childParameterized = new ParameterizedInterfaceModelImpl(interfaceTypeProxy);
            if (!s.equals("java/lang/Object")) {
                if (formalTypesNames.empty()) {
                    if (!stack.empty()) {
                        stack.peek().addParameterizedType(childParameterized);
                    }
                } else {
                    formalTypes.put(formalTypesNames.pop(), childParameterized);
                }
            }
            stack.push(childParameterized);
        }
    }

    @Override
    public void visitInnerClassType(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visitTypeArgument() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SignatureVisitor visitTypeArgument(char c) {
        return this;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visitEnd() {
        if (stack.empty()) return;
        ParameterizedInterfaceModelImpl lastElement = stack.pop();
        if (stack.isEmpty()) {
            parameterizedIntf.add(lastElement);
        }
    }
}
