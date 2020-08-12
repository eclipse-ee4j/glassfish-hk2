/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
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
 *
 * @author gaurav.gupta@payara.fish
 */
public interface ParameterizedType {

    /**
     * Returns the parameter type
     *
     * @return parameter type
     */
    public Type getType();

    /**
     * Returns the parameter type name
     *
     * @return parameter type name
     */
    String getTypeName();

    /**
     * Returns the formal type name
     *
     * @return the formal type name
     */
    String getFormalType();

    /**
     * @return the true value for formal type parameters and false value for
     * parameterized type with actual type arguments.
     */
    boolean isFormalType();

    /**
     *
     * @return true if type is array
     */
    boolean isArray();

    /**
     *
     * @return the list of parameterized subtype
     */
    List<ParameterizedType> getParameterizedTypes();

}
