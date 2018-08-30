/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.examples.caching.hk2;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation should be put on methods that
 * take one input parameter and return a value.  It
 * can also be put on constructors that take one input
 * parameter.  Methods marked with this annotation will
 * not be called with the same input, instead returning
 * the previously returned value.  Constructors marked
 * with this annotation will not be called with the same
 * input, instead returning the object returned given the
 * same input
 *  
 * @author jwells
 */
@Retention(RUNTIME)
@Target( { METHOD, CONSTRUCTOR })
public @interface Cache {
}
