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

import org.glassfish.hk2.classmodel.reflect.Type;
import org.glassfish.hk2.classmodel.reflect.Types;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * contains all the parsed types references.
 * @author Jerome Dochez
 */
public class TypesCtr implements Types {
    
    @Override
    public Type getBy(String name) {
        for (Map<String, TypeProxy<Type>> map : storage.values()) {
            TypeProxy proxy = map.get(name);
            if (proxy!=null) {
                return proxy.get();
            }
        }
        return null;
    }

    @Override
    public <T extends Type> T getBy(Class<T> type, String name) {
        Type t = getBy(name);
        try {
            return type.cast(t);
        } catch (ClassCastException e) {
            return null;
        }
    }

    public TypeProxy<Type> getHolder(String name) {
        if (name.equals("java.lang.Object")) return null;
        // we look first in our storage pools.
        for (Map<String, TypeProxy<Type>> map : storage.values()) {
            TypeProxy<Type> proxy = map.get(name);
            if (proxy!=null) {
                return proxy;
            }
        }
        // ok let's look in our unknown storage pool.
        if (unknownTypesStorage.containsKey(name)) {
            return unknownTypesStorage.get(name);
        }
        // ok we don't have and since we don't know its type
        // let's put it in the unknown storage pool.
        TypeProxy<Type> typeProxy = new TypeProxy<Type>(null, name);
        TypeProxy<Type> old = unknownTypesStorage.putIfAbsent(name, typeProxy);
        if (old==null) {
            nonVisited.push(typeProxy);
            return typeProxy;
        }
        return old;
    }

    public <T extends Type> TypeProxy<Type> getHolder(String name, Class<T> type) {
        if (name.equals("java.lang.Object")) return null;
        ConcurrentMap<String, TypeProxy<Type>> typeStorage = storage.get(type);
        if (typeStorage==null) {
            typeStorage = new ConcurrentHashMap<String, TypeProxy<Type>>();
            ConcurrentMap<String, TypeProxy<Type>> old = storage.putIfAbsent(type, typeStorage);
            if (old!=null) {
                // some other thread got to set that type storage before us, let's use it
                typeStorage=old;
            }
        }
        TypeProxy<Type> typeProxy = typeStorage.get(name);
        if (typeProxy ==null) {
            // in our unknown type pool ? 
            TypeProxy<Type> tmp = unknownTypesStorage.get(name);
            // in our unknown type pool ?
            if (tmp!=null) {
                synchronized (unknownTypesStorage) {
                    typeProxy = unknownTypesStorage.remove(name);
                    if (typeProxy == null) { 
                        typeProxy = tmp; 
                     }
                }
                if (typeProxy!=null) {
                    TypeProxy<Type> old = typeStorage.putIfAbsent(name, typeProxy);
                    if (old!=null) {
                        typeProxy = old;
                    }
                }
            } else {
                typeProxy = new TypeProxy<Type>(null, name);
                TypeProxy<Type> old = typeStorage.putIfAbsent(name, typeProxy);
                if (old==null) {
                    nonVisited.push(typeProxy);
                } else {
                    typeProxy=old;
                }
            }
        }
        return typeProxy;
    }

    public interface ProxyTask {
        public void on(TypeProxy<?> proxy);
    }

    /**
     * Runs a task on each non visited types parsing discovered.
     *
     * @param proxyTask the task to run on each non visited type.
     */
    public void onNotVisitedEntries(ProxyTask proxyTask) {
        while(!nonVisited.isEmpty()) {
            TypeProxy proxy = nonVisited.pop();
            if (!proxy.isVisited()) {
                proxyTask.on(proxy);
            }
        }
    }

    public void clearNonVisitedEntries() {
        nonVisited.clear();
        unknownTypesStorage.clear();
    }

    @Override
    public Collection<Type> getAllTypes() {
        List<Type> allTypes = new ArrayList<Type>();
        for (Map<String, TypeProxy<Type>> map : storage.values()) {
            for (TypeProxy typeProxy : map.values()) {
                if (typeProxy.get()!=null) {
                    allTypes.add(typeProxy.get());
                }
            }
        }
        return allTypes;
    }

    /**
     * Storage indexed by TYPE : interface | class | annotation and then by name.
     */
    private final ConcurrentMap<Class, ConcurrentMap<String, TypeProxy<Type>>> storage=
            new ConcurrentHashMap<Class, ConcurrentMap<String, TypeProxy<Type>>>();

    /**
     * Map of encountered types which we don't know if it is an interface, class or annotation
     */
    private final ConcurrentMap<String, TypeProxy<Type>> unknownTypesStorage = new ConcurrentHashMap<String, TypeProxy<Type>>();
    /**
     * Stack on type proxy as they have been instantiated in FILO order.
     */
    private final Stack<TypeProxy> nonVisited = new Stack<TypeProxy>();
}
