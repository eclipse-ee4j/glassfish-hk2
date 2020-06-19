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

import org.glassfish.hk2.classmodel.reflect.MethodModel;
import org.glassfish.hk2.classmodel.reflect.Parameter;
import org.glassfish.hk2.classmodel.reflect.Type;

/**
 *
 * @author gaurav.gupta@payara.fish
 */
public class ParameterImpl extends AnnotatedElementImpl implements Parameter {

    private final MethodModel methodModel;

    private final TypeProxy<?> type;

    private final int index;

    public ParameterImpl(int index, String name, TypeProxy<?> type, MethodModel methodModel) {
        super(name);
        this.index = index;
        this.type = type;
        this.methodModel = methodModel;
    }

    @Override
    public MethodModel getMethod() {
        return methodModel;
    }

    @Override
    public Type getType() {
        return type.get();
    }

    @Override
    protected void print(StringBuffer sb) {
        super.print(sb);
        sb.append(", type =").append(type.getName());
    }

    @Override
    public int getIndex() {
        return index;
    }
}
