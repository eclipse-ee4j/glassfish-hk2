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
import org.glassfish.hk2.classmodel.reflect.util.ParsingConfig;
import org.objectweb.asm.Opcodes;

import java.net.URI;

/**
 * Results of a parsing activity, all java resources are inventoried in three
 * main categories : classes, interfaces and annotations with cross references
 *
 * @author Jerome Dochez
 */
public class TypesImpl implements TypeBuilder {

    final URI definingURI;
    final TypesCtr types;
    
    public TypesImpl(TypesCtr types, URI definingURI) {
        this.definingURI = definingURI;
        this.types = types;
    }

    public Class<? extends Type> getType(int access) {
        if ((access & Opcodes.ACC_ANNOTATION)==Opcodes.ACC_ANNOTATION) {
           return AnnotationType.class;
        } else
        if ((access & Opcodes.ACC_INTERFACE)==Opcodes.ACC_INTERFACE) {
            return InterfaceModel.class;
        } else {
            return ClassModel.class;
        }

    }

    public TypeImpl getType(int access, String name, TypeProxy parent) {
        Class<? extends Type> requestedType = getType(access);

        TypeProxy<Type> typeProxy = types.getHolder(name, requestedType);
        synchronized(typeProxy) {
            final Type type = typeProxy.get();
            TypeImpl result;
            if (null == type) {
                if ((access & Opcodes.ACC_ANNOTATION)==Opcodes.ACC_ANNOTATION) {
                   result = new AnnotationTypeImpl(name, typeProxy);
                } else
                if ((access & Opcodes.ACC_INTERFACE)==Opcodes.ACC_INTERFACE) {
                    result = new InterfaceModelImpl(name, typeProxy, parent);
                } else {
                    result =  new ClassModelImpl(name, typeProxy, parent);
                }
                typeProxy.set(result);
                return result;
            } else {
                TypeImpl impl = (TypeImpl)type;
                if (ExtensibleTypeImpl.class.isInstance(impl)) {
                    // ensure we have the parent right
                    ((ExtensibleTypeImpl<?>)impl).setParent(parent);
                }
                return impl;
            }
        }
    }

    public FieldModelImpl getFieldModel(String name, TypeProxy type, ExtensibleType declaringType) {
        return new FieldModelImpl(name, type, declaringType);
    }

    @Override
    public TypeProxy getHolder(String name) {
        return types.getHolder(name);
    }

    @Override
    public <T extends Type> TypeProxy<T> getHolder(String name, Class<T> type) {
        return (TypeProxy<T>) types.getHolder(name, type);
    }
}
