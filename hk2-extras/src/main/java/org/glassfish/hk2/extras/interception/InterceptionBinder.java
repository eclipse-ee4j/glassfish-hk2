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

package org.glassfish.hk2.extras.interception;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation is placed on an annotation that can be used
 * to indicate a binding between an interceptor (annotated
 * with {@link Interceptor}) and a class to be intercepted
 * (annotated with {@link Intercepted}).
 * 
 * The annotation on which this annotation is placed must
 * have RUNTIME retention and have a Target of TYPE or
 * METHOD (or ANNOTATION_TYPE for transitive bindings).
 * 
 * When an annotation annotated with this annotation is put
 * on an implementation of
 * {@link org.aopalliance.intercept.MethodInterceptor} or
 * {@link org.aopalliance.intercept.ConstructorInterceptor} and
 * which is also annotated with {@link Interceptor} then it becomes
 * associated with that interceptor.  These interceptors will
 * be called on methods of any service annotated with
 * {@link Intercepted} appropriately.
 * 
 * When an annotation annotated with this annotation is
 * used with an hk2 service marked with {@link Intercepted}
 * it can either be put on the entire class, in which case EVERY method
 * of that class will be intercepted, or it can be placed on individual
 * methods of the service to indicate that only those methods should be
 * intercepted.  If it is placed both at the class level and on individual
 * methods then every method will be intercepted.
 * 
 * Annotations annotated with InterceptionBinder are transitive.  In other
 * words if an annotation is annotated with ANOTHER annotation that is
 * marked with InterceptionBinder then any interceptor marked with the other
 * annotation also applies to any service or method marked with this annotation
 * 
 * @author jwells
 *
 */
@Inherited
@Target(ANNOTATION_TYPE)
@Retention(RUNTIME)
@Documented
public @interface InterceptionBinder {
}
