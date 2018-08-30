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

import org.jvnet.hk2.testing.junit.annotations.InhabitantFiles;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@InhabitantFiles({
  "alternate/hk2-locator/another",
  "alternate/hk2-locator/alt"
})
public class ServiceLocatorTestRuleWithInhabitantFilesTest {

  @Rule
  public final ServiceLocatorTestRule<ServiceLocatorTestRuleWithInhabitantFilesTest> serviceLocatorTestRule;

  @Inject
  private ServiceLocator serviceLocator;

  public ServiceLocatorTestRuleWithInhabitantFilesTest() {
    super();
    this.serviceLocatorTestRule = new ServiceLocatorTestRule<ServiceLocatorTestRuleWithInhabitantFilesTest>(this, ServiceLocatorIsolation.PER_TEST);
  }

  @Test
  public void testAlternateLocationServiceIsPresent() {
    assertNotNull(this.serviceLocator);
    assertNotNull(this.serviceLocator.getService(AlternateLocationService.class));
  }

  @Test
  public void testAlternateLocationService2IsPresent() {
    assertNotNull(this.serviceLocator.getService(AlternateLocationService2.class));
  }
  
}
