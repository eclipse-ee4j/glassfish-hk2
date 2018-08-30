/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.configuration.api;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * A method marked with this annotation will be invoked prior
 * to dynamic changes being applied to a service.  The method
 * must either take no arguments or single argument that is
 * a {@link java.util.List} (of type {@link java.beans.PropertyChangeEvent}).
 * The {@link java.util.List} parameter will be filled in with the
 * set of dynamic changes being done to this service.  If the method
 * returns a boolean, and that boolean is false then the set of changes
 * will NOT be given to the fields and methods with dynamic properties.
 * Any exception thrown by this method will be ignored.  The method
 * may have any visibility, including private, package and protected.
 * 
 * @author jwells
 *
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface PreDynamicChange {

}
