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

package org.glassfish.hk2.configuration.api;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Scope;

/**
 * This annotation is put onto classes to indicate that
 * they should be created based on the availability of
 * instances of a specify type of configuration in the 
 * {@link org.glassfish.hk2.configuration.hub.api.Hub}
 * 
 * @author jwells
 *
 */
@Documented
@Scope
@Retention(RUNTIME)
@Target(TYPE)
public @interface ConfiguredBy {
    /**
     * A service is created for each instance of this type,
     * with a name taken from the key of the instance
     * 
     * @return the name of the type to base instances
     * of this service on
     */
    public String value();
    
    /**
     * Specifies the creation policy for configured services
     * based on type instances.  The values it can take are:
     * <UL>
     * <LI>ON_DEMAND - Services are created when user code creates demand (via lookup or injection)</LI>
     * <LI>EAGER - Services are created as soon as configured instances become available</LI>
     * </UL>
     * The default value is ON_DEMAND
     * 
     * @return The creation policy for services configured by this type
     */
    public CreationPolicy creationPolicy() default CreationPolicy.ON_DEMAND;
    
    public enum CreationPolicy {
        /**
         * Instances of services with this policy will
         * be created when some user code creates explicit
         * demand for the service.  This is similar to most
         * other hk2 services
         */
        ON_DEMAND,
        
        /**
         * Instances of services with this policy will
         * be created as soon as their backing instances
         * become available
         */
        EAGER
    }

}
