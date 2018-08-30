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
 * The list of packages (in &quot;.&quot; format, i.e. &quot;com.acme.test.services&quot;)
 * that we should scan through the classpath for in order to find services.  This
 * annotation must be placed on a class that extends {@link HK2Runner}
 * 
 * @author jwells
 *
 */
@Documented
@Inherited
@Retention(RUNTIME)
@Target( TYPE )
public @interface Packages {
    /**
     * This special value indicates the same package as the package
     * in which the test itself resides
     */
    public final static String THIS_PACKAGE = "${THIS}";
    
    /**
     * Returns the set of packages (with dot separator)
     * that should be scanned for hk2 services.  The
     * default value for this is to scan the package in
     * which the test class itself resides
     * 
     * @return The set of packages to scan for services
     */
    public String[] value() default { THIS_PACKAGE };
}
