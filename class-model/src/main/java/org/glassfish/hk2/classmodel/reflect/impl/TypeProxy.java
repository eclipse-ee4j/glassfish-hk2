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

import java.util.*;

/**
 * Proxy for types, used in place until the type can be properly instantiated.
 * Proxy type also holds all incoming reference to the type.
 *
 * @author Jerome Dochez
 */
public class TypeProxy<T extends Type> {

    private T value = null;
    private volatile boolean visited = false;
    private final String name;
    private final Notifier<T> notifier;
    private final List<Member> fieldRefs = new ArrayList<Member>();
    private final List<Type> subTypeRefs = new ArrayList<Type>();
    private final List<ClassModel> implementations = new ArrayList<ClassModel>();


    /**
     * Creates a new type proxy, this ctor is package private as many
     * other activities must be performed when a new proxy type is created.
     *
     * @param notifier notification handle to notify receiver the proxied
     * type has been resolved
     * @param name type name
     */
    TypeProxy(Notifier<T> notifier, String name) {
        this.notifier = notifier;
        this.name = name;
    }
    
    @Override
    public String toString() {
      return "TypeProxy:" + name;
    }

    public void set(T  value) {
        this.value = value;
        if (notifier!=null) {
            notifier.valueSet(value);
        }

    }

    public T get() {
        return value;
    }

    public String getName() {
        if (value!=null) return value.getName();
        return name;
    }
    
    public interface Notifier<T> {
        public void valueSet(T value);
    }

    public synchronized void addFieldRef(FieldModel field) {
        fieldRefs.add(field);
    }

    public List<Member> getRefs() {
        return Collections.unmodifiableList(fieldRefs);
    }

    public synchronized void addSubTypeRef(Type subType) {
        subTypeRefs.add(subType);
    }

    public List<Type> getSubTypeRefs() {
        return Collections.unmodifiableList(subTypeRefs);
    }

    public synchronized void addImplementation(ClassModel classModel) {
        implementations.add(classModel);
    }

    public List<ClassModel> getImplementations() {
        return Collections.unmodifiableList(implementations);
    }

    public static <U extends Type> Collection<U> adapter(final Collection<TypeProxy<U>> source) {
        return new AbstractCollection<U>() {

            @Override
            public Iterator<U> iterator() {
                final Iterator<TypeProxy<U>> itr = source.iterator();
                return new Iterator<U>() {
                    @Override
                    public boolean hasNext() {
                        return itr.hasNext();
                    }

                    @Override
                    public U next() {
                        TypeProxy<U> next = itr.next();
                        if (next!=null) {
                            return next.get();
                        } else {
                            return null;
                        }
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }

            @Override
            public int size() {
                return source.size();
            }
        };
    }

    public void visited() {
        visited=true;
    }

    public boolean isVisited() {
        return visited;
    }
}
