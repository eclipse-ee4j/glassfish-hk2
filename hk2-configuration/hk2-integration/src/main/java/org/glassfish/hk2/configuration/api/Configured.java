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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.glassfish.hk2.api.InjectionPointIndicator;

/**
 * This annotation is placed on fields or on parameters
 * of methods or constructors to indicate that these
 * fields or parameters should come from the configuration
 * instance of the type defined by the {@link ConfiguredBy}
 * annotation on the class.
 * <p>
 * The key field gives the name of the parameter to get from
 * the java bean instance upon which the instance of this service
 * is based.  If the configuration bean is a java bean then
 * a method name starting with &quot;get&quot; and having the
 * key name (with the first letter capitalized) will be invoked
 * to get the value.  if the configuration bean is a map then
 * the value of the key is the value of the key in the map from
 * which to get the value
 * <p>
 * In the case of a field the key field can come from the name
 * of the field (or can be explicitly set, which will override the name
 * of the field).  In the case of a parameter the key field must
 * be filled in with the name of the field on the java bean to
 * use to inject into this parameter
 * 
 * @author jwells
 *
 */
@Documented
@Retention(RUNTIME)
@Target( { FIELD, PARAMETER })
@InjectionPointIndicator
public @interface Configured {
    /** This value can be used to indicate that the injection point should be the whole bean */
    public final static String BEAN_KEY = "$bean";
    
    /**
     * This value can be used to indicate that the injection point should be given the instance name.
     * The injection point must be of type String
     */
    public final static String INSTANCE = "$instance";
    
    /**
     * This value can be used to indicate that the injection point should be given the type name.
     * The injection point must be of type String
     */
    public final static String TYPE = "$type";
    
    /**
     * The name of the field in the java bean or
     * bean-like map to use for injecting into
     * this field or parameter.  If this field is
     * set to &quot$bean&quot then the whole bean
     * upon which this instance is based will be
     * injected into this location
     * 
     * @return The name of the field to use for
     * injecting into this field or parameter
     */
    public String value() default "";
    
    /**
     * Describes how dynamic a configured field or parameter must be.
     * All parameters of a constructor must be STATIC.
     * All parameters of a method must have the same dynamicity value
     * 
     * @return The dynamicicty of this field or parameter
     */
    public Dynamicity dynamicity() default Dynamicity.STATIC;

}
