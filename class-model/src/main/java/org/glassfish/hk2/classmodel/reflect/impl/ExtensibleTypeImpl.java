/*
 * Copyright (c) 2010, 2024 Oracle and/or its affiliates. All rights reserved.
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
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementation of an extensible type (Class or Interface)
 */
public abstract class ExtensibleTypeImpl<T extends ExtensibleType> extends TypeImpl implements ExtensibleType<T> {

    private final ReentrantLock lock = new ReentrantLock();
    protected TypeProxy<?> parent;
    private final List<FieldModel> staticFields = new ArrayList<>();
    private final List<TypeProxy<InterfaceModel>> implementedIntf = new ArrayList<>();
    private final List<ParameterizedInterfaceModel> implementedParameterizedIntf = new ArrayList<>();
    private Map<String, ParameterizedInterfaceModel> formalTypeParameters;
    
    public ExtensibleTypeImpl(String name, TypeProxy<Type> sink, TypeProxy parent) {
        super(name, sink);
        this.parent =  parent;
    }

    @Override
    public Map<String, ParameterizedInterfaceModel> getFormalTypeParameters() {
        return formalTypeParameters;
    }

    public void setFormalTypeParameters(Map<String, ParameterizedInterfaceModel> typeParameters) {
        this.formalTypeParameters = typeParameters;
    }

    @Override
    public T getParent() {
        if (parent != null) {
            return (T) parent.get();
        } else {
            return null;
        }
    }

    @Override
    public boolean isInstanceOf(String name) {
        if (name == null) {
            throw new IllegalArgumentException(name);
        }
        if (this.getParent() != null) {
            if (name.equals(this.getParent().getName())) {
                return true;
            } else {
                return ((T) this.getParent()).isInstanceOf(name);
            }
        }
        return false;
    }

    @Override
    public String getSimpleName() {
        String simpleName = getName();
        simpleName = simpleName.substring(simpleName.lastIndexOf('.') + 1);
        simpleName = simpleName.substring(simpleName.lastIndexOf('$') + 1);
        return simpleName;
    }

    public TypeProxy<?> setParent(final TypeProxy<?> parent) {
        lock.lock();
        try {
            if (null == this.parent) { 
              this.parent = parent;
            }
            return this.parent;
        } finally {
            lock.unlock();
        }
    }

    void isImplementing(TypeProxy<InterfaceModel> intf) {
        try {
            lock.lock();
            implementedIntf.add(intf);
        } finally {
            lock.unlock();
        }
    }

    void isImplementing(ParameterizedInterfaceModelImpl pim) {
        try {
            lock.lock();
            if (pim.getRawInterface() instanceof InterfaceModel) {
                implementedIntf.add((TypeProxy<InterfaceModel>) pim.getRawInterfaceProxy());
            }
            implementedParameterizedIntf.add(pim);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Collection<InterfaceModel> getInterfaces() {
        return TypeProxy.adapter(Collections.unmodifiableCollection(implementedIntf));
    }

    @Override
    public Collection<ParameterizedInterfaceModel> getParameterizedInterfaces() {
        return Collections.unmodifiableCollection(implementedParameterizedIntf);
    }

    @Override
    public ParameterizedInterfaceModel getParameterizedInterface(ExtensibleType type) {
        for (ParameterizedInterfaceModel interfaceModel : implementedParameterizedIntf) {
            if (interfaceModel.getRawInterface() == type) {
                return interfaceModel;
            }
        }
        return null;
    }

    @Override
    public Collection<T> subTypes() {
        List<T> subTypes = new ArrayList<T>();
        for (Type t : getProxy().getSubTypeRefs()) {
            subTypes.add((T) t);
        }
        return subTypes;
    }

    @Override
    public Collection<T> allSubTypes() {
        Collection<T> allTypes = subTypes();
        for (T child : subTypes()) {
            allTypes.addAll(child.allSubTypes());
        }
        return allTypes;
    }

    void addStaticField(FieldModel field) {
        try {
            lock.lock();
            staticFields.add(field);
        } finally {
            lock.unlock();
        }
    }

    void addField(FieldModel field) {
        throw new RuntimeException("Cannot add a field to a non classmodel type");
    }

    @Override
    public Collection<FieldModel> getStaticFields() {
        return Collections.unmodifiableCollection(staticFields);
    }

    /**
     * prints a meaningful string
     * @param sb the string buffer to write to.
     */
    @Override
    protected void print(StringBuffer sb) {
        super.print(sb);
        sb.append(", parent=").append(parent==null?"null":parent.getName());
        sb.append(", interfaces=[");
        for (TypeProxy<InterfaceModel> im : implementedIntf) {
            sb.append(" ").append(im.getName());
        }
        sb.append("]");
        
    }
}
