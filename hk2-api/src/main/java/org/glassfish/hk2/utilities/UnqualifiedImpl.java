/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.utilities;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import org.glassfish.hk2.api.AnnotationLiteral;
import org.glassfish.hk2.api.Unqualified;

/**
 * This is an implementation of {@link Unqualified}.  It is here
 * as a convenience for creating instances of this annotation
 * where necessary
 * 
 * @author jwells
 *
 */
public class UnqualifiedImpl extends AnnotationLiteral<Unqualified> implements Unqualified {
    private static final long serialVersionUID = 7982327982416740739L;
    
    private final Class<? extends Annotation>[] value;
    
    /**
     * Makes a copy of the annotation classes values and initializes
     * this {@link Unqualified} annotation with those values
     * 
     * @param value A list of qualifiers that must NOT be on
     * injection point.  A zero-length list indicates that 
     * no qualifier must be present on the matching service
     */
    // @SafeVarargs
    public UnqualifiedImpl(Class<? extends Annotation>... value) {
        this.value = Arrays.copyOf(value, value.length);
    }
    
    /**
     * The set of annotations that must not be associated with
     * the service being injected
     * 
     * @return All annotations that must not be on the injected
     * service.  An empty list indicates that NO annotations must
     * be on the injected service
     */
    @Override
    public Class<? extends Annotation>[] value() {
        return Arrays.copyOf(value, value.length);
    }
    
    @Override
    public String toString() {
        return "UnqualifiedImpl(" + Arrays.toString(value) + "," + System.identityHashCode(this) + ")";
    }
}
