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

package org.glassfish.hk2.api;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates that a method should be customized.  Any method marked
 * with this annotation will be sent to the class level customizer
 * rather than the normal "add/lookup/remove" format that it would
 * normally be mapped to.  This is useful if there is some sort of
 * special processing that needs to be done, or if a method on an
 * interface looks like one of the special method types but in fact
 * is not.  It is an error for this to be placed on a method where
 * the class does not have a {@link Customizer} that will be detected
 * only at runtime
 * <p>
 * This annotation is for use with the hk2-xml configuration
 * system
 * 
 * @author jwells
 *
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface Customize {

}
