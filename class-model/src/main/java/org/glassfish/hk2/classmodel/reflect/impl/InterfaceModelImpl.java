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

import java.util.*;

/**
 * Implementation of an interface model
 */
public class InterfaceModelImpl extends ExtensibleTypeImpl<InterfaceModel> implements InterfaceModel {

    public InterfaceModelImpl(String name, TypeProxy<Type> sink, TypeProxy parent) {
        super(name, sink, parent);
    }

    @Override
    public Collection<ClassModel> implementations() {
        return getProxy().getImplementations();
    }

    @Override
    public Collection<ClassModel> allImplementations() {
        Collection<ClassModel> result = new HashSet<ClassModel>();
        result.addAll(getProxy().getImplementations());
        for (ClassModel cm : getProxy().getImplementations()) {
            result.addAll(cm.allSubTypes());
        }
        return Collections.unmodifiableCollection(result);
    }

    @Override
    protected void print(StringBuffer sb) {
        super.print(sb);
        sb.append(", implementors=[");
        for (ClassModel cm : getProxy().getImplementations()) {
            sb.append(" ").append(cm.getName());
        }
        sb.append("]");
    }
}
