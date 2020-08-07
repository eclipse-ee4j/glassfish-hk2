/********************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package org.glassfish.hk2.tests.locator.optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/** @author Balthasar Schüss */
@RunWith(Parameterized.class)
public class NamedOptionalTest {

  static final String NAMED = "named";
  private static final String OTHER_NAMED = "other_named";
  private static final String UNNAMED = "unnamed";

  @Parameters(
      name =
          "otherNamedOptBound={0},otherNamedStrBound={1},unnamedOptBound={2},unnamedStrBound={3}")
  public static Collection<Object[]> data() {
    int size = 4;
    List<Object[]> result = new ArrayList<>();
    for (int i = 0; i < Math.pow(2, size); i++) {
      Object[] permutation = new Object[size];
      for (int j = 1; j <= size; j++) {
        permutation[j - 1] = i % Math.pow(2, j) - Math.pow(2, j) / 2 >= 0;
      }
      result.add(permutation);
    }
    return result;
  }

  private ServiceLocator locator;
  private boolean otherNamedOptBound;
  private boolean otherNamedStrBound;
  private boolean unnamedOptBound;
  private boolean unnamedStrBound;

  public NamedOptionalTest(
      boolean otherNamedOptBound,
      boolean otherNamedStrBound,
      boolean unnamedOptBound,
      boolean unnamedStrBound) {
    this.otherNamedOptBound = otherNamedOptBound;
    this.otherNamedStrBound = otherNamedStrBound;
    this.unnamedOptBound = unnamedOptBound;
    this.unnamedStrBound = unnamedStrBound;
  }

  @After
  public void cleanup() {
    locator.shutdown();
    locator = null;
  }

  @Test
  public void testBoundOptional() {
    locator = createLocator(true, true);
    NamedOptionalInjection testObject = locator.getService(NamedOptionalInjection.class);
    assertTrue(testObject.providedOptional.isPresent());
    assertEquals(NAMED, testObject.providedOptional.get());
  }

  @Test
  public void testNotBoundOptional() {
    locator = createLocator(true, false);
    NamedOptionalInjection testObject = locator.getService(NamedOptionalInjection.class);
    assertFalse(testObject.providedOptional.isPresent());
  }

  @Test
  public void testBoundOptionalString() {
    locator = createLocator(false, true);
    NamedOptionalInjection testObject = locator.getService(NamedOptionalInjection.class);
    assertTrue(testObject.providedOptional.isPresent());
    assertEquals(NAMED, testObject.providedOptional.get());
  }

  @Test
  public void testNotBoundOptionalString() {
    locator = createLocator(false, false);
    NamedOptionalInjection testObject = locator.getService(NamedOptionalInjection.class);
    assertFalse(testObject.providedOptional.isPresent());
  }

  private ServiceLocator createLocator(boolean optional, boolean bound) {
    ServiceLocator locator = ServiceLocatorFactory.getInstance().create("NamedOptionalTest");
    ServiceLocatorUtilities.bind(
        locator,
        new AbstractBinder() {

          @Override
          protected void configure() {
            TypeLiteral<Optional<String>> optType = new TypeLiteral<Optional<String>>() {};
            if (bound && optional) bind(Optional.of(NAMED)).named(NAMED).to(optType);
            if (bound && !optional) bind(NAMED).named(NAMED).to(String.class);
            if (otherNamedOptBound) bind(Optional.of(OTHER_NAMED)).named(OTHER_NAMED).to(optType);
            if (otherNamedStrBound) bind(OTHER_NAMED).named(OTHER_NAMED).to(String.class);
            if (unnamedOptBound) bind(Optional.of(UNNAMED)).to(optType);
            if (unnamedStrBound) bind(UNNAMED).to(String.class);
            bindAsContract(NamedOptionalInjection.class);
          }
        });
    return locator;
  }
}
