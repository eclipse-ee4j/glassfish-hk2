/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.api;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * An injection point can be annotated with &#064;Unqualified if
 * it should only be injected with services that have no qualifiers
 * at all
 * 
 * @author jwells
 *
 */
@Documented
@Retention(RUNTIME)
@Target({FIELD, PARAMETER})
public @interface Unqualified {
    /**
     * The list of qualifiers that must NOT be present on the service.  If this list is empty
     * then there must be NO qualifiers at all on the service returned.  If this list is
     * not empty then the service must not have ANY of the listed qualifiers (with any values).
     *
     * @return The set of qualifiers that the service must NOT have
     */
    Class<? extends Annotation>[] value() default {};
}
