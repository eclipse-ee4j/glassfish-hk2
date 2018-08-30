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
 * A set of locator inhabitant files to search for in the classpath.  This
 * annotation must be placed on a class that extends {@link HK2Runner}
 *
 * @author jwells
 *
 */
@Documented
@Inherited
@Retention(RUNTIME)
@Target( TYPE )
public @interface InhabitantFiles {
    public static final String DEFAULT_INHABITANT_PATH = "META-INF/hk2-locator/default";
    
    /**
     * The name of inhabitant files to find in the classpath of the test.
     * If this value is an empty set then no inhabitant files will be loaded.
     * The default for this is to search META-INF/hk2-locator/default
     * 
     * @return inhabitant file names to find in the classpath for loading
     * into testLocator
     */
    public String[] value() default { DEFAULT_INHABITANT_PATH };

}
