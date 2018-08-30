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

package org.glassfish.hk2.classmodel.reflect.util;

import java.util.Set;

/**
 * Filters the parsing activity to only deeply parse classes that are either
 * annotated with an annotation returned by {@link #getAnnotationsOfInterest}
 * or implements/subclass a type returned by {@link #getTypesOfInterest}.
 *
 * A class identified to be deeply parsed will contain all the metadata about
 * its members like fields, methods as well as annotations on those.
 *
 * @author Jerome Dochez
 */
public interface ParsingConfig {

    /**
     * Returns a list of annotations that should trigger an exhaustive visit
     * of the annotated type.
     *
     * @return list of annotations that triggers an exhaustive scanning of the
     * annotated type
     */
    Set<String> getAnnotationsOfInterest();

    /**
     * Returns a list of types (classes or interfaces) that a type must either
     * subclass or implement to trigger an exhaustive scanning
     *
     * @return list of types that will trigger an exhaustive scanning.
     */
    Set<String> getTypesOfInterest();

    /**
     * Returns true if unannotated fields and methods should be part of the
     * model returned.
     *
     * @return true if unannotated fields and methods will be accessible from
     * the returned {@link org.glassfish.hk2.classmodel.reflect.Types} model.
     */
    boolean modelUnAnnotatedMembers();
}
