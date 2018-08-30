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

package org.jvnet.testing.hk2testng;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.glassfish.hk2.utilities.Binder;

/**
 * This annotation specifies what HK2 service locator and binders should be used
 * to instantiate and inject the test class it is annotated with.
 *
 * @author saden
 */
@Documented
@Inherited
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(TYPE)
public @interface HK2 {

    /**
     * The name of the service locator that will be used.
     *
     * @return the name of the service locator
     */
    String value() default "hk2-testng-locator";

    /**
     * Create a service locator and populate it with services defined in
     * "META-INF/hk2-locator/default" inhabitant files found in the classpath.
     *
     * @return true if the classpath should be scanned for inhabitant files.
     */
    boolean populate() default true;

    /**
     * A list of binders that should be loaded.
     *
     * @return a list of binders classes
     */
    Class<? extends Binder>[] binders() default {};

    /**
     * If true then the PerThread scope will be enabled
     * in the associated service locator
     *
     * @return true if PerThread scope should be enabled
     */
    boolean enablePerThread() default true;

    /**
     * If true then the InheritableThread scope will be enabled in the
     * associated service locator
     *
     * @return true if InheritableThread scope should be enabled
     */
    boolean enableInheritableThread() default true;

    /**
     * If true then the Immediate scope will be enabled
     * in the associated service locator
     *
     * @return true if Immediate scope should be enabled
     */
    boolean enableImmediate() default true;

    /**
     * If true then the lookup exceptions will be thrown
     * back to the caller
     *
     * @return true if lookup exceptions should be thrown
     * back to the caller
     */
    boolean enableLookupExceptions() default true;

    /**
     * If true then events will be enabled
     *
     * @return true if events should be enabled
     */
    boolean enableEvents() default true;

}
