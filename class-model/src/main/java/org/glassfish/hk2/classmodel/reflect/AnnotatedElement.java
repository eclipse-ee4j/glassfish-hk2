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

package org.glassfish.hk2.classmodel.reflect;


import java.util.Collection;

/**
 * An annotated element is a java declaration that can be annotated. Such
 * declaration are usually types (like classes or interfaces), fields of
 * a class, or methods of a class.
 *
 * An annotated element is defined by its name and the set of annotations
 * present on the declaration.
 *
 * @author Jerome Dochez
 */
public interface AnnotatedElement {

    /**
     * Annotated element have a name, which vary depending on the actual
     * subclass type. For instance, a class annotated element's name is the
     * class name as obtained from {@link Class#getName()}
     *
     * @return the annotated element name
     */
    public String getName();

    /**
     * Construct and return a short description name that can be used to
     * display the instance value
     *
     * @return a short description
     */
    public String shortDesc();

    /**
     * Returns a unmodifiable set of annotations that are present on this
     * annotated element.
     *
     * @return the collection of annotations
     */
    Collection<AnnotationModel> getAnnotations();

    /**
     * Returns an annotation model if the type is annotated with the
     * passed annotation name
     * @param name the annotation name
     * @return the annotation model or null if the type is not annotated with
     * this annotation type of the passed name.
     */
    public AnnotationModel getAnnotation(String name);
}
