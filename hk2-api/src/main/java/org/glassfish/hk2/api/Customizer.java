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

package org.glassfish.hk2.api;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Gives the type (and optional name) of a customizer service
 * to use when an unknown method on a bean interface is
 * encountered.  Customizers are found in the hk2 service
 * registry
 * <p>
 * This annotation is for use with the hk2-xml configuration
 * system
 * 
 * @author jwells
 *
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Customizer {
    /**
     * The class of the customizer to lookup for
     * this bean
     * 
     * @return the class of the customizer for this bean
     */
    public Class<?>[] value();
    
    /**
     * The name of the customizer to lookup for
     * this bean
     * 
     * @return the name of the customizer for this bean
     */
    public String[] name() default {};
    
    /**
     * If true then if a bean method is not mirrored in
     * the customizer a RuntimeException will be thrown.
     * Otherwise unknown methods are treated as a no-op.
     * Setting this to false must be used with care as
     * any method with a scalar return will throw a null
     * pointer exception if no method can be found in the
     * customizer methods since converting null to a
     * scalar value does not work
     * 
     * @return true if an unknown method called on a bean
     * at runtime which does not have a mirrored method
     * on the customizer should raise a RuntimeException
     */
    public boolean failWhenMethodNotFound() default true;

}
