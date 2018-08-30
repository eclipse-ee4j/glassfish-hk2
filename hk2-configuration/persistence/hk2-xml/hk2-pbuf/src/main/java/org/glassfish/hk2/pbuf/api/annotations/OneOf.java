/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.pbuf.api.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * This annotation is placed on consecutive (by XmlType propOrder) fields
 * in order to indicate that they are part of the same oneOf.  The oneOfs
 * must all have matching names for fields that should be in the same oneOf.
 * There can be multiple oneOfs with different names.  The same name cannot
 * appear twice except on consecutive fields.
 * 
 * @author jwells
 *
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface OneOf {
    /**
     * The name of the oneof for which this field should
     * be part of
     * 
     * @return The name of the oneof for which this field is part of
     */
    public String value();

}
