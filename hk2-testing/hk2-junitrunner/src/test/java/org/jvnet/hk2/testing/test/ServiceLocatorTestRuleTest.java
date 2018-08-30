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

package org.jvnet.hk2.testing.test;

import javax.inject.Inject;
import javax.inject.Named;

import org.glassfish.hk2.api.ServiceLocator;

import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

import org.junit.Rule;
import org.junit.Test;

import org.junit.rules.TestRule;

import org.junit.runner.Description;

import org.jvnet.hk2.testing.junit.ServiceLocatorTestRule;
import org.jvnet.hk2.testing.junit.ServiceLocatorTestRule.ServiceLocatorIsolation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ServiceLocatorTestRuleTest {

  @Rule
  public final ServiceLocatorTestRule<ServiceLocatorTestRuleTest> serviceLocatorTestRule;

  @Inject
  private ServiceLocator serviceLocator;

  @Inject
  private Description description;
  
  public ServiceLocatorTestRuleTest() {
    super();
    this.serviceLocatorTestRule = new ServiceLocatorTestRule<ServiceLocatorTestRuleTest>(this, ServiceLocatorIsolation.PER_TEST);
  }

  @Test
  public void testServiceLocatorIsPresent() {
    assertNotNull(this.serviceLocator);
  }

  @Test
  public void testServiceLocatorNameIsCorrect() {
    assertNotNull(this.serviceLocator);
    assertEquals(this.getClass().getName() + "." + "testServiceLocatorNameIsCorrect", this.serviceLocator.getName());
  }

  @Test
  public void testDescriptionIsNonNull() {
    assertNotNull(this.description);
  }

  @Test
  public void testDescriptionIsForThisMethod() {
    assertNotNull(this.description);
    assertEquals(this.getClass().getName(), this.description.getClassName());
    assertEquals("testDescriptionIsForThisMethod", this.description.getMethodName());
  }

}
