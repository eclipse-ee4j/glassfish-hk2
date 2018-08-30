/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.jvnet.hk2.config;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This is here because the hk2-inhabitant-generator does not have a dependency on
 * the configuration subsystem, though it does parse some configuration subsystem
 * annotations.  These annotations are here so as to be able to write test classes
 * that contain these annotations.
 * 
 * @author jwells
 */
@Documented
@Retention(RUNTIME)
@Target( ANNOTATION_TYPE )
public @interface GenerateServiceFromMethod {
    /**
     * This is the key in the metadata that will contain the actual type of the List return type of the
     * method where the user-supplied annotation has been placed
     */
    public final static String METHOD_ACTUAL = "MethodListActual";
    
    /**
     * This is the key in the metadata that will contain the name of the method where the user-supplied
     * annotation has been placed
     */
    public final static String METHOD_NAME = "MethodName";
    
    /**
     * This is the key in the metadata that will contain the fully qualified class name of the class marked
     * {@link Configured} that contains this annotation
     */
    public final static String PARENT_CONFIGURED = "ParentConfigured";
    
    /**
     * This must have the fully qualified class name of the implementation that is to be used in the
     * generated descriptor
     * 
     * @return The fully qualified class name of the implementation
     */
    public String implementation();
    
    /**
     * The set of fully qualified class names of the advertised contracts that are to be used in
     * the generated descriptor.  Note that the implementation class is not automatically added
     * to this list
     * 
     * @return The fully qualified class names of the advertised contracts the generated descriptor
     * should take
     */
    public String[] advertisedContracts();
    
    /**
     * The scope that the descriptor should take.  Defaults to PerLookup
     * 
     * @return The fully qualified class names of the scope the descriptor should take
     */
    public String scope() default "org.glassfish.hk2.api.PerLookup";
}
