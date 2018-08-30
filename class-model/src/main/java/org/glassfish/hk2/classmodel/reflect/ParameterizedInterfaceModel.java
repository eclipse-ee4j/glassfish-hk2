/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
 * Representation of a parameterized type
 * @author Jerome Dochez
 */
public interface ParameterizedInterfaceModel {

    /**
     * Returns the name where parameter types are enclosed in < >
     * comma separated, like declaration in source java files.
     *
     * @return a declaration for this type
     */
    String getName();

    /**
     * Returns the raw interface for this parameterized type
     *
     * @return the interface model instance
     */
    InterfaceModel getRawInterface();

    /**
     * Returns the type parameters in order.
     *
     * @return the type parameters in order.
     */
    Collection<ParameterizedInterfaceModel> getParametizedTypes();
}
