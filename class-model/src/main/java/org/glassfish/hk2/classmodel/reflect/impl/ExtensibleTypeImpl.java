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
import java.util.logging.Logger;

/**
 * Implementation of an extensible type (Class or Interface)
 */
public abstract class ExtensibleTypeImpl<T extends ExtensibleType> extends TypeImpl implements ExtensibleType<T> {

    private TypeProxy<?> parent;
    private final List<FieldModel> staticFields = new ArrayList<FieldModel> ();
    private final List<TypeProxy<InterfaceModel>> implementedIntf = new ArrayList<TypeProxy<InterfaceModel>>();
    private final List<ParameterizedInterfaceModel> implementedParameterizedIntf =
            new ArrayList<ParameterizedInterfaceModel>();
    
    public ExtensibleTypeImpl(String name, TypeProxy<Type> sink, TypeProxy parent) {
        super(name, sink);
        this.parent =  parent;
    }

    public T getParent() {
        if (parent!=null) {
            return (T) parent.get();
        } else {
            return null;
        }
    }
    
    public synchronized TypeProxy<?> setParent(final TypeProxy<?> parent) {
        if (null == this.parent) { 
          this.parent = parent;
        }
        return this.parent;
    }

    synchronized void isImplementing(TypeProxy<InterfaceModel> intf) {
        implementedIntf.add(intf);
    }

    synchronized void isImplementing(ParameterizedInterfaceModelImpl pim) {
        implementedIntf.add(pim.rawInterface);
        implementedParameterizedIntf.add(pim);
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

    synchronized void addStaticField(FieldModel field) {
        staticFields.add(field);
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
