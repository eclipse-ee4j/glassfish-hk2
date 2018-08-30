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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation is placed on an injection point that is to be injected with the {@link ActiveDescriptor} that was used
 * to create the instance.
 * <p>
 * The following things must be true of injection points annotated with {@link Self}<UL>
 * <LI>The injection point must have the type {@link ActiveDescriptor}.  The generic type is ignored</LI>
 * <LI>The injection point must have no qualifiers</LI>
 * <LI>The injection point must not be annotated {@link org.jvnet.hk2.annotations.Optional}</LI>
 * <LI>The class must not be getting injected with the {@link ServiceLocator#inject(Object)} method</LI>
 * </UL>
 * Furthermore, the {@link ActiveDescriptor} that can be injected have the following restrictions:<UL>
 * <LI>The {@link ActiveDescriptor} was not pre-reified prior to being bound (i.e. third-party ActiveDescriptors)</LI>
 * <LI>The {@link ActiveDescriptor} is not a {@link DescriptorType#PROVIDE_METHOD}</LI>
 * </UL>
 * A valid injection point that is annotated with {@link Self} will not go through the normal resolution process, and
 * hence cannot be customized.
 * 
 * @author jwells
 *
 */
@Documented
@Retention(RUNTIME)
@Target( { FIELD, PARAMETER } )
public @interface Self {
}
