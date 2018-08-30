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

package org.glassfish.hk2.api.messaging;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation is put onto one parameter of a method to indicate that
 * this method should be called whenever a Topic sends a message.  Any
 * class that is to receive topic messages must have the
 * {@link MessageReceiver} qualifier on it as well as use this annotation
 * to mark the specific method to be injected into
 * <p>
 * There may be only one parameter of the method annotated with this
 * annotation.  All of the other parameters of the method are normal
 * injection points
 * <p>
 * 
 * @author jwells
 */
@Documented
@Retention(RUNTIME)
@Target({PARAMETER})
public @interface SubscribeTo {

}
