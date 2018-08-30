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

/**
 * Model that represent the field of a class
 *
 * @author Jerome Dochez
 */
public interface FieldModel extends Member, AnnotatedElement {

    /**
     * Returns the declared type of the field
     * 
     * @return the field type
     */
    public ExtensibleType getType();

    /**
     * Returns the declaring type of this field, which is a class.
     *
     * @return the field declaring class.
     */
    public ExtensibleType getDeclaringType();
}
