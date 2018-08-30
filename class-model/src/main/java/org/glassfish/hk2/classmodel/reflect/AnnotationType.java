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
import java.util.Map;

/**
 * Represents an annotation
 *
 * @author Jerome Dochez
 */
public interface AnnotationType extends InterfaceModel {

    /**
     * Returns an unmodifiable collection of annotated element with
     * this annotation
     *
     * @return collection of elements annotated with this annotation
     */
    Collection<AnnotatedElement> allAnnotatedTypes();
    
    /**
     * Returns an unmodifiable collection of annotation default values.
     *
     * @return collection of default value elements of this annotation
     */
    Map<String, Object> getDefaultValues();

}
