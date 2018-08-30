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

package org.jvnet.hk2.testing.junit.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.jvnet.hk2.testing.junit.HK2Runner; // for javadoc only

/**
 * A set of classes that should be analyzed as services, whether they declare
 * &#64;Service or not.  This annotation must be placed on a class that
 * extends {@link HK2Runner}
 * 
 * @author jwells
 *
 */
@Documented
@Inherited
@Retention(RUNTIME)
@Target( TYPE )
public @interface Classes {
    /**
     * A list of classes that should be added to
     * testLocator as services
     * 
     * @return Classes to treat as HK2 services
     */
    public Class<?>[] value();
}
