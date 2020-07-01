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

import java.util.ArrayList;
import java.util.List;
import org.glassfish.hk2.classmodel.reflect.ParameterizedType;
import org.glassfish.hk2.classmodel.reflect.Type;

/**
 *
 * @author gaurav.gupta@payara.fish
 */
public class ParameterizedTypeImpl implements ParameterizedType {

    private TypeProxy<?> typeProxy;

    private final List<ParameterizedType> genericTypes = new ArrayList<>();

    private org.objectweb.asm.Type type;

    public ParameterizedTypeImpl() {
    }

    public ParameterizedTypeImpl(TypeProxy<?> type) {
        this.typeProxy = type;
    }

    @Override
    public Type getType() {
        if (typeProxy != null) {
            return typeProxy.get();
        }
        return null;
    }

    @Override
    public String getTypeName() {
        if (typeProxy != null) {
            return typeProxy.getName();
        } else {
            return type.getClassName();
        }
    }

    public TypeProxy<?> getTypeProxy() {
        return typeProxy;
    }

    public void setTypeProxy(TypeProxy<?> typeProxy) {
        this.typeProxy = typeProxy;
    }

    public void setType(org.objectweb.asm.Type type) {
        this.type = type;
    }

    @Override
    public List<ParameterizedType> getGenericTypes() {
        return genericTypes;
    }

    @Override
    public boolean isArray() {
        return type != null
                && type.getSort() == org.objectweb.asm.Type.ARRAY;
    }

}
