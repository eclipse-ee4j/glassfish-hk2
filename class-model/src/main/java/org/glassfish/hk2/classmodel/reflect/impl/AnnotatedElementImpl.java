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
 * Implementation of an annotated element
 *
 * @author Jerome Dochez
 */
public class AnnotatedElementImpl implements AnnotatedElement {
    
    private final String name;

    private final List<AnnotationModel> annotations = new ArrayList<AnnotationModel>();

    private boolean isApplicationClass;

    public AnnotatedElementImpl(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    synchronized void addAnnotation(AnnotationModel annotation) {
        annotations.add(annotation);
    }

    @Override
    public Collection<AnnotationModel> getAnnotations() {
        return Collections.unmodifiableList(annotations);
    }

    @Override
    public AnnotationModel getAnnotation(String name) {
        for (AnnotationModel am : annotations) {
            if (am.getType().getName().equals(name)) {
                return am;
            }
        }
        return null;
    }

    public boolean isApplicationClass() {
        return isApplicationClass;
    }

    public void setApplicationClass(boolean applicationClass) {
        isApplicationClass = applicationClass;
    }

    @Override
    public String toString() {

        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getSimpleName()).append("{");
        print(sb);
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String shortDesc() {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getSimpleName()).append("{");
        sb.append(getName());
        sb.append("}");
        return sb.toString();
    }

    protected void print(StringBuffer sb) {
        sb.append("name='").append(name).append("\', annotations=");
        sb.append("[");
        for (AnnotationModel am : this.getAnnotations()) {
            sb.append(" ").append(am.getType().getName());
        }
        sb.append("]");
    }
}
