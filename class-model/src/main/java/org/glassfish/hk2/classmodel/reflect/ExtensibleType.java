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
 * An extensible type is a type that can be subclassed like an interface
 * or a class.
 *
 * @param <T> parent type which is always the same as the child type
 * (classes extends classes, interfaces extends interfaces...)
 *  
 * @author Jerome Dochez
 */
public interface ExtensibleType<T extends ExtensibleType> extends Type {

    /**
     * Return the parent type instance. If there are more than one parent
     * with the same FQCN within the various URI we parsed, we return the
     * one defined within the same URI (if it exists). If there is more
     * than one parsed metadata with the same FQCN and none of them are
     * defined within the same URI as this type, then null is returned.
     *
     * @return the parent type instance or null
     */
    T getParent();

    /**
     * Returns the child subtypes of this type. A child subtype is a
     * type which parent is this type.
     *
     * @return the immediate subtypes
     */
    Collection<T> subTypes();

    /**
     * Returns all the children subtypes (including grand children) of
     * this type. 
     *
     * @return all the children
     */
    Collection<T> allSubTypes();

    /**
     * Returns an unmodifiable list of interfaces implemented or extended by
     * this type.
     *
     * @return collection of implemented or extended interfaces
     */
    Collection<InterfaceModel> getInterfaces();

    Collection<ParameterizedInterfaceModel> getParameterizedInterfaces();

    /**
     * Returns an unmodifiable list of static fields defined by this type
     *
     * @reutrn collection of defined static fields
     */
    Collection<FieldModel> getStaticFields();
}
