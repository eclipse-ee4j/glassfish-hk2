/*
 * Copyright (c) 2007, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.jvnet.hk2.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marker annotation indicating that an instance variable or method marked with {@link javax.inject.Inject}
 * is not required to be present at run-time. If the service is not present, there will be no error and 
 * injection will not be performed.
 * 
 *  <p>
 * Example:<br>
 * <pre> 
 *      &#064;Inject
 *      &#064;Optional  
 *      MyContract myOptionalService;
 * </pre>
 *
 * @author Mason Taube
 *
 */
@Documented
@Retention(RUNTIME)
@Target({METHOD, FIELD, PARAMETER})
public @interface Optional {

}
