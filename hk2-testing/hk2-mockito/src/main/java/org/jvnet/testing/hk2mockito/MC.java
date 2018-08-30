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

package org.jvnet.testing.hk2mockito;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import org.mockito.Answers;
import org.mockito.MockSettings;

/**
 * {@literal @}MC (Mock Collaborator) annotation is used on fields and methods
 * of a Test class to inject a mock of the {@literal @}SUT's collaborating
 * services.
 *
 * @author Sharmarke Aden
 * @see MockSettings
 */
@Documented
@Retention(RUNTIME)
@Target({METHOD, FIELD, PARAMETER})
public @interface MC {

    /**
     * If the collaborator service being injected is a constructor or method
     * parameter of the SUT, this value should indicate the index of the
     * parameter. By default the collaborator will be detected but in instances
     * (i.e. injecting two or more services with the same type) you may want to
     * explicitly specify the index of the service.
     *
     * @return the index of the parameter.
     */
    int value() default 0;

    /**
     * If the collaborator service being injected is a field of the SUT, this
     * value should indicate the name of the field. By default the
     * {@literal @}SC field name is used as the collaborator field name but in
     * instances (i.e. field injection of two or more services with the same
     * type) you may want to explicitly specify the name of the field.
     *
     * @return the name of the field.
     */
    String field() default "";

    /**
     * Specifies default answers to interactions.
     *
     * @return default answer to be used by mock when not stubbed.
     */
    Answers answer() default Answers.RETURNS_DEFAULTS;

    /**
     * Specifies mock name. Naming mocks can be helpful for debugging - the name
     * is used in all verification errors. By default the field name will be
     * used as mock name.
     *
     * @return the name of the mock.
     */
    String name() default "";

    /**
     * Specifies extra interfaces the mock should implement. Might be useful for
     * legacy code or some corner cases. For background, see issue 51 <a
     * href="http://code.google.com/p/mockito/issues/detail?id=51">here</a>
     *
     * @return extra interfaces that should be implemented.
     */
    Class<?>[] extraInterfaces() default {};
}
