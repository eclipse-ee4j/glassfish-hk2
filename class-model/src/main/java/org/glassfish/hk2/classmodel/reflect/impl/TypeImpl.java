/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2023 Payara Foundation and/or its affiliates. All rights reserved.
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

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.net.URI;

/**
 * Implementation of the Type abstraction.
 *
 * @author Jerome Dochez
 */
public class TypeImpl extends AnnotatedElementImpl implements Type {

    private final TypeProxy<Type> sink;
    private final List<MethodModel> methods = new ArrayList<MethodModel>();
    private final Set<URI> definingURIs= new HashSet<URI>();


    public TypeImpl(String name, TypeProxy<Type> sink) {
        super(name);
        this.sink = sink;
    }

    @Override
    public Collection<URI> getDefiningURIs() {
        return Collections.unmodifiableSet(definingURIs);
    }

    synchronized void addDefiningURI(URI uri) {
        definingURIs.add(uri);
    }

    @Override
    public boolean wasDefinedIn(Collection<URI> uris) {
        for (URI uri : uris) {
            if (definingURIs.contains(uri)) {
                return true;
            }
        }
        return false;
    }

    synchronized void addMethod(MethodModelImpl m) {
        methods.add(m);
    }

    @Override
    public Collection<MethodModel> getMethods() {
        return Collections.unmodifiableList(methods);
    }

    TypeProxy<Type> getProxy() {
        return sink;
    }

    @Override
    public Collection<Member> getReferences() {
        return sink.getRefs();
    }

    @Override
    protected void print(StringBuffer sb) {
        super.print(sb);    //To change body of overridden methods use File | Settings | File Templates.
        sb.append(", subclasses=[");
        for (AnnotatedElement cm : sink.getSubTypeRefs()) {
            sb.append(" ").append(cm.getName());
        }
        sb.append("]");
    }
}
