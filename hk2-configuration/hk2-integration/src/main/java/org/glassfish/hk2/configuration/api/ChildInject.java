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

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.glassfish.hk2.api.InjectionPointIndicator;

/**
 * This Injection point indicator can be used for
 * services that have a hierarchical names.  The name space
 * of the name fields of the ActiveDescriptors must form
 * a directed acyclical graph.  For example, this is useful if
 * using a naming scheme based on an XML hierarchy.
 * <p>
 * If the injection point of this annotation is of type
 * {@link ChildIterable} then the generic type of the
 * {@link ChildIterable} must contain the Type
 * of the underlying service, and the {@link ChildIterable}
 * will contain all of the children services whose
 * name starts with the name of the parent ActiveDescriptor
 * appended with the value field of this annotation.
 * <p>
 * If the injection point is NOT a {@link ChildIterable} then
 * the type is as per a normal injection point, but the chosen
 * instance of that type will have a name that starts with the
 * name of the parent ActiveDescriptor appended with the value
 * field of this annotation
 * 
 * @author jwells
 *
 */
@Retention(RUNTIME)
@Target( { FIELD, PARAMETER })
@InjectionPointIndicator
public @interface ChildInject {
    /**
     * The string that will be appended to the
     * name field of the ActiveDescriptor of
     * the parent of this injection point
     * 
     * @return The value to append to the name
     * field of the ActiveDescriptor of the parent
     * of this injection point
     */
    public String value() default "";
    
    /**
     * This field returns the separator that is used to
     * separate heirarchical name fields, for use by the
     * {@link ChildIterable#byKey(String)} method.  This
     * value will be pre-pended to the name given to the
     * {@link ChildIterable#byKey(String)} key parameter
     * @return The separator used to separate a hierachical
     * namespace
     */
    public String separator() default ".";
}
