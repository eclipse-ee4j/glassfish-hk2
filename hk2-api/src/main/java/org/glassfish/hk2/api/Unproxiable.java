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

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation must go on a scope annotation in order to indicate
 * that no services from this scope may be proxied.
 * <p>
 * Any descriptor that returns true from {@link Descriptor#isProxiable()} but whose
 * scope is Unproxiable will cause an exception when the {@link Descriptor} is
 * reified.
 * <p>
 * A scope must not be marked with both {@link Proxiable} and {@link Unproxiable}
 * <p>
 * The {@link PerLookup} scope is Unproxiable because every method invocation on a
 * {@link PerLookup} object would cause a new instance to be created
 * 
 * @author jwells
 *
 */
@Documented
@Retention(RUNTIME)
@Target( { ANNOTATION_TYPE })
public @interface Unproxiable {

}
