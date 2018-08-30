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

/**
 * {@literal @}SUT (Service Under Test) is an annotation used on fields and
 * methods of a Test class to inject a spy of the real service. Note that
 * calling methods on the spy will call the methods of the real service unless
 * they are stubbed with when()/give(). You can disable spying by setting
 * {@link #value()} to false.
 * <p>
 * Example:
 * </p>
 * <pre>
 *<code>
 *&#64;Service
 *public class GreetingService {
 *
 *  public String greet() {
 *    return sayHello();
 *  }
 *
 *  public String sayHello() {
 *    return "Hello!";
 *  }
 *}
 *</code>
 * </pre>
 * <pre>
 *<code>
 *&#64;HK2
 *public class GreetingServiceTest {
 *
 *  &#64;SUT
 *  &#64;Inject
 *  GreetingService sut;
 *
 *  &#64;BeforeMethod
 *  public void init() {
 *    reset(sut);
 *  }
 *
 *  &#64;Test
 *  public void verifyInjection() {
 *    assertThat(sut)
 *      .isNotNull()
 *      .isInstanceOf(MockitoSpy.class);
 *  }
 *
 *  &#64;Test
 *  public void callToGreetShouldReturnHello() {
 *    String greeting = "Hello!";
 *
 *    String result = sut.greet();
 *
 *    assertThat(result).isEqualTo(greeting);
 *    verify(sut).greet();
 *    verify(sut).sayHello();
 *  }
 *
 *  &#64;Test
 *  public void callToGreetShouldReturnHola() {
 *    String greeting = "Hola!";
 *    when(sut.sayHello()).thenReturn(greeting);
 *
 *    String result = sut.greet();
 *
 *    assertThat(result).isEqualTo(greeting);
 *    verify(sut).greet();
 *    verify(sut).sayHello();
 *  }
 *}
 * </code>
 * </pre>
 *
 * @author Sharmarke Aden
 */
@Documented
@Retention(RUNTIME)
@Target({METHOD, FIELD, PARAMETER})
public @interface SUT {

    /**
     * Indicates whether a spy should be created. By default a spy of the real
     * service is created. Note that the spy calls real methods unless they are
     * stubbed.
     *
     * @return true if a spy should be created.
     */
    public boolean value() default true;
}
