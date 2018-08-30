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

package org.glassfish.hk2.api;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation marks a method of an annotation as providing a value that
 * should be placed into the metadata of a {@link Descriptor}.
 * <p>
 * One downside of using Qualifiers (or values in the Scope annotation) is that
 * in order to get these values the underlying classes must be reified.  In order
 * to relieve the system from having to reify classes to get the data in the
 * scope and qualifier annotations this annotation can be placed on the methods
 * of an annotation to indicate that the values found in the annotation should be
 * placed into the metadata of the descriptor.  Since the metadata of a descriptor
 * can be accessed without classloading the underlying class the descriptor is
 * describing this data can then be accessed without needing to reify the class.
 * <p>
 * This qualifier will be honored whenever the system does automatic analysis of
 * a class (for example, when analyzing a pre-reified class file or object).  It
 * will also be used by the automatic inhabitant generator when analyzing class files
 * marked &#64;Service.  However, if the programmatic API is being used to build up
 * a descriptor file this annotation is not taken into account, and it is hence the
 * responsibility of the user of the programmatic API to fill in the metadata values
 * itself.
 * <p>
 * This annotation can be placed on any method of an annotation marked with
 * {@link javax.inject.Scope} or {@link javax.inject.Qualifier}.  The "toString" of the object returned
 * from that method will be placed in the metadata of the descriptor that is
 * created (unless the object returned is a Class, in which case the name of
 * the Class is used)
 * <p>
 * @see Descriptor ActiveDescriptor
 * 
 * @author jwells
 *
 */
@Documented
@Retention(RUNTIME)
@Target({METHOD})
public @interface Metadata {
    /**
     * This is the key that will be used in the metadata field of the descriptor.
     * Values returned from the methods annotated with this annotation will have
     * their toString called and the result will be added to the metadata key with
     * this value (unless the return type is Class, in which case the name of
     * the class will be used)
     * 
     * @return The key of the metadata field that will be added to
     */
    public String value();

}
