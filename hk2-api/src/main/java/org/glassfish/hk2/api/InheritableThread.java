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

package org.glassfish.hk2.api;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import javax.inject.Scope;

/**
 * InheritableThread is a scope that operates like
 * {@link org.glassfish.hk2.api.PerThread} scope, except with the caveat that
 * InheritableThread scoped services provide inheritance of values from parent
 * thread to child thread. The lifecycle of the service is determined by the
 * thread it is on. On a single thread only one of the service will be created
 * and the service will be inherited by its child threads, but a new service
 * will be created for each new non-child threads.
 * <p>
 * The InheritableThread scope is not automatically handled by a new
 * ServiceLocator. In order to enable the InheritableThread scope * the user can
 * either add an implementation of {@link Context} which handles it or it can
 * use the
 * {@link org.glassfish.hk2.utilities.ServiceLocatorUtilities#enableInheritableThreadScope}
 * method.
 *
 * @author jwells
 *
 */
@Documented
@Retention(RUNTIME)
@Scope
@Target({TYPE, METHOD})
public @interface InheritableThread {

}
