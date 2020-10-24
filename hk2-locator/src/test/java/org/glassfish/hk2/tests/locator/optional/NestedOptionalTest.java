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

import java.util.Optional;
import java.util.function.Consumer;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testng.Assert;

/** @author Balthasar Schüss */
public class NestedOptionalTest {

  private  class TestBinder extends AbstractBinder {

    private final Consumer<AbstractBinder> configure;

    public TestBinder(Consumer<AbstractBinder> configure) {
      this.configure = configure;
    }

    @Override
    protected void configure() {
      bindAsContract(NestedOptionalInjection.class);
      configure.accept(TestBinder.this);
    }
  }

  private ServiceLocator locator;

  @Before
  public void setup() {
    locator = ServiceLocatorFactory.getInstance().create("NestedOptionalTest");
  }

  @After
  public void cleanup() {
    locator.shutdown();
    locator = null;
  }

  private void configure(Consumer<AbstractBinder> configure) {
    ServiceLocatorUtilities.bind(locator, new TestBinder(configure));
  }

  @Test
  public void testNotBound() {
    configure(binder -> {});
    NestedOptionalInjection service = locator.getService(NestedOptionalInjection.class);
    Assert.assertTrue(service.providedOptional.isPresent());
    Assert.assertTrue(service.providedOptional.get() instanceof Optional);
    Assert.assertFalse(service.providedOptional.get().isPresent());
  }

  @Test
  public void testBoundFully() {
    configure(
        binder -> {
          binder
              .bind(Optional.of(Optional.of("test0")))
              .to(new TypeLiteral<Optional<Optional<String>>>() {});
        });

    NestedOptionalInjection service = locator.getService(NestedOptionalInjection.class);
    Assert.assertTrue(service.providedOptional.isPresent());
    Assert.assertTrue(service.providedOptional.get() instanceof Optional);
    Assert.assertTrue(service.providedOptional.get().isPresent());
    Assert.assertTrue(service.providedOptional.get().get().equals("test0"));
  }

  @Test
  public void testBoundPartially() {
    configure(
        binder -> binder.bind(Optional.of("test0")).to(new TypeLiteral<Optional<String>>() {}));
    NestedOptionalInjection service = locator.getService(NestedOptionalInjection.class);
    Assert.assertTrue(service.providedOptional.isPresent());
    Assert.assertTrue(service.providedOptional.get() instanceof Optional);
    Assert.assertTrue(service.providedOptional.get().isPresent());
    Assert.assertTrue(service.providedOptional.get().get().equals("test0"));
  }

  @Test
  public void testBoundPartiallyEmpty() {
    configure(binder -> binder.bind(Optional.empty()).to(new TypeLiteral<Optional<String>>() {}));
    NestedOptionalInjection service = locator.getService(NestedOptionalInjection.class);
    Assert.assertTrue(service.providedOptional.isPresent());
    Assert.assertTrue(service.providedOptional.get() instanceof Optional);
    Assert.assertFalse(service.providedOptional.get().isPresent());
  }

  @Test
  public void testBoundAtomically() {
    configure(binder -> binder.bind("test0").to(String.class));
    NestedOptionalInjection service = locator.getService(NestedOptionalInjection.class);
    Assert.assertTrue(service.providedOptional.isPresent());
    Assert.assertTrue(service.providedOptional.get() instanceof Optional);
    Assert.assertTrue(service.providedOptional.get().isPresent());
    Assert.assertTrue(service.providedOptional.get().get().equals("test0"));
  }

  @Test
  public void testSpecifityPrecedenceFullyBeforeAll() {
	configure(binder -> {
		binder.bind(Optional.of(Optional.of("fully"))).to(new TypeLiteral<Optional<Optional<String>>>() {});
		binder.bind(Optional.of("partial")).to(new TypeLiteral<Optional<String>>() {});
		binder.bind("atomic").to(String.class);
	});
    NestedOptionalInjection service = locator.getService(NestedOptionalInjection.class);
    Assert.assertTrue(service.providedOptional.isPresent());
    Assert.assertTrue(service.providedOptional.get() instanceof Optional);
    Assert.assertTrue(service.providedOptional.get().isPresent());
    Assert.assertTrue(service.providedOptional.get().get().equals("fully"));
  }

  @Test
  public void testSpecifityPrecedencePartialBeforeAtmoic() {
	configure(binder -> {
		binder.bind(Optional.of("partial")).to(new TypeLiteral<Optional<String>>() {});
		binder.bind("atomic").to(new TypeLiteral<Optional<String>>() {});
	});
    NestedOptionalInjection service = locator.getService(NestedOptionalInjection.class);
    Assert.assertTrue(service.providedOptional.isPresent());
    Assert.assertTrue(service.providedOptional.get() instanceof Optional);
    Assert.assertTrue(service.providedOptional.get().isPresent());
    Assert.assertTrue(service.providedOptional.get().get().equals("partial"));
  }
}
