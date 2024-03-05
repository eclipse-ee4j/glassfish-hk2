/*
 * Copyright (c) 2020, 2024 Oracle and/or its affiliates. All rights reserved.
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.glassfish.hk2.classmodel.reflect.*;

/**
 *
 * @author gaurav.gupta@payara.fish
 */
public class EnumTypeImpl extends ExtensibleTypeImpl<EnumType> implements EnumType {

    private final ReentrantLock lock = new ReentrantLock();
    final List<FieldModel> fields = new ArrayList<>();

    public EnumTypeImpl(String name, TypeProxy<Type> sink, TypeProxy parent) {
        super(name, sink, parent);
    }

    @Override
    void addField(FieldModel field) {
        lock.lock();
        try {
            fields.add(field);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Collection<FieldModel> getFields() {
        return Collections.unmodifiableCollection(fields);
    }

}
