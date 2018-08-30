/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.jvnet.hk2.annotations;

import java.lang.annotation.*;

/**
 * Decorates a {@link java.lang.reflect.Method} with all the annotation which types are
 * specified using the {@link org.jvnet.hk2.annotations.Decorate#with()}.
 *
 * @author Jerome Dochez
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Decorate {

    /**
     * Returns the decorated type
     *
     * @return the decorated type
     */
    Class<?> targetType();

    /**
     * Returns the decorated method name defined in the Class returned by
     * {@link org.jvnet.hk2.annotations.Decorate#targetType()}
     *
     * @return the decorated method name
     */
    String methodName();

    /**
     * Returns the annotation type that is defined on the same
     * annotated element and decorate the method identified by the
     * {@link org.jvnet.hk2.annotations.Decorate#targetType()} and
     * {@link org.jvnet.hk2.annotations.Decorate#methodName()}.
     *
     * @return list of annotate types that decorates the {@link java.lang.reflect.Method}
     */
    Class<? extends Annotation> with();
}
