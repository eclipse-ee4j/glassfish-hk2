/*
 * Copyright (c) 2010, 2020 Oracle and/or its affiliates. All rights reserved.
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

import java.util.List;

/**
 * Model to represent a method declaration
 */
public interface MethodModel extends Member, AnnotatedElement {

    /**
     * Returns the class or interface model this method belongs to.
     * @return the class of this method
     */
    ExtensibleType<?> getDeclaringType();

    /**
     * Returns the method signature.
     * @return this method signature
     */
    String getSignature();

    /**
     * Returns the method return type
     * @return the method's return type
     */
    ParameterizedType getReturnType();

    /**
     * Returns the parameter types as string
     * @return the parameter types
     */
    String[] getArgumentTypes();

    /**
     * @return the checked exception types, or an empty array if the method doesn't
     *         declare any thrown exceptions
     */
    String[] getExceptionTypes();

    /**
     * Returns the list of parameter
     *
     * @return the list of parameter
     */
    List<Parameter> getParameters();

    /**
     * Return the parameter by index
     *
     * @param index
     * @return the parameter by index
     */
    Parameter getParameter(int index);
}
