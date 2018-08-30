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

package org.glassfish.hk2.api;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Scope;

/**
 * Immediate is a scope that operates like {@link javax.inject.Singleton} scope, except that instances are created as soon as their
 * corresponding {@link Descriptor}s are added.  When the corresponding {@link Descriptor} is removed from the 
 * locator the Immediate scope service is destroyed.  In particular Immediate scope services are not destroyed if
 * the {@link ServiceHandle#destroy()} method is called.  Care should be taken with the services injected into
 * an immediate service, as they also become virtual immediate services
 * <p>
 * The Immediate scope is not automatically handled by a new ServiceLocator.  In order to enable the Immediate scope
 * the user can either add an implementation of {@link Context} which handles it or it can use the
 * {@link org.glassfish.hk2.utilities.ServiceLocatorUtilities#enableImmediateScope(ServiceLocator)} method.
 * 
 * @author jwells
 *
 */
@Documented
@Retention(RUNTIME)
@Scope
@Target( { TYPE, METHOD })
public @interface Immediate {

}
