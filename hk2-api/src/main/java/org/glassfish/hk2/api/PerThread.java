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

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Scope;

/**
 * PerThread is a scope that operates like {@link javax.inject.Singleton} scope, except on a per-thread basis.  The lifecycle of the
 * service is determined by the thread it is on.  On a single thread only one of the service will be created, but a new
 * service will be created for each thread.
 * <p>
 * The PerThread scope is not automatically handled by a new ServiceLocator.  In order to enable the PerThread scope
 * the user can either add an implementation of {@link Context} which handles it or it can use the
 * {@link org.glassfish.hk2.utilities.ServiceLocatorUtilities#enablePerThreadScope} method.
 * 
 * @author jwells
 *
 */
@Documented
@Retention(RUNTIME)
@Scope
@Target( { TYPE, METHOD })
public @interface PerThread {

}
