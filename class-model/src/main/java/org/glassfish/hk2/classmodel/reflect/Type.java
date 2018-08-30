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
import java.net.URI;

/**
 * A type defines java type which can be an interface or a class.
 *
 * @author Jerome Dochez
 */
public interface Type extends AnnotatedElement {

    /**
     * Returns an unmodifiable collection of methods that are declared
     * in this type.
     *
     * @return methods declared on this type
     */
    Collection<MethodModel> getMethods();

    /**
     * Returns a unmodifiable collection of this type references. A
     * reference can be a field declaration in a type which type is this
     * instance or it can be a method declaration which return type is
     * this type
     *
     * @return references on this type
     */
    Collection<Member> getReferences();

    /**
     * Returns the defining URIs
     * @return a collection of URIs in which the type was defined
     */
    Collection<URI> getDefiningURIs();

    /**
     * Determine if this type was defined in one of the passed URI or not
     *
     * @param uris collection of URI to check if this type was defined in them.
     * @return true if this type as defined in one the passed URI
     */
    boolean wasDefinedIn(Collection<URI> uris);
}
