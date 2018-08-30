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

package org.glassfish.hk2.runlevel;


import org.glassfish.hk2.api.Metadata;
import org.jvnet.hk2.annotations.Contract;

import javax.inject.Scope;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * Defines a run/start level.
 *
 * @author jdochez, jtrent, tbeerbower
 */
@Scope
@Retention(RUNTIME)
@Target({TYPE, ANNOTATION_TYPE, METHOD})
@Documented
@Inherited
@Contract
public @interface RunLevel {
    // ----- Constants ------------------------------------------------------

    /**
     * The metadata key for run level value.  If this value is
     * present it MUST match the value of this annotation.  If
     * this value is set then the system will not have to reify the
     * descriptor in order to determine its level
     */
    public static final String RUNLEVEL_VAL_META_TAG  = "runLevelValue";

    /**
     * The metadata key for run level mode.  If this value is
     * present is MUST match the mode of this annotation.  If
     * this value is set then the system will not have to reify the
     * descriptor in order to determine its mode
     */
    public static final String RUNLEVEL_MODE_META_TAG = "runLevelMode";

    /**
     * The initial run level.
     */
    public static final int RUNLEVEL_VAL_INITIAL = -2;

    /**
     * The immediate run level.  Services set to this run level will be
     * activated immediately.
     */
    public static final int RUNLEVEL_VAL_IMMEDIATE = -1;

    /**
     * Services set to have a non-validating run level mode will be
     * activated by their associated run level service or through
     * injection into another service.  These services will not be
     * checked during activation which means that the service can be
     * activated prior to the run level service reaching the run level.
     * The run level serves only as a fail safe for activation.
     */
    public static final int RUNLEVEL_MODE_NON_VALIDATING = 0;

    /**
     * Services set to have a validating run level mode will be activated
     * and deactivated by their associated run level service but may also
     * be activated through injection into another service.  The current
     * run level of the associated run level service will be checked
     * during activation of these services to ensure that the service
     * is being activated in at an appropriate run level.
     */
    public static final int RUNLEVEL_MODE_VALIDATING = 1;


    // ----- Elements -------------------------------------------------------

    /**
     * Defines the run level.
     *
     * @return the run level
     */
    @Metadata(RUNLEVEL_VAL_META_TAG)
    public int value() default 0;

    /**
     * Defines the run level mode.
     *
     * @return the mode
     */
    @Metadata(RUNLEVEL_MODE_META_TAG)
    public int mode() default RUNLEVEL_MODE_VALIDATING;
}
