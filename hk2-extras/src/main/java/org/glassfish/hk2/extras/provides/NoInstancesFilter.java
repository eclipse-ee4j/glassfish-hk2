/*
 * Copyright (c) 2020 TechEmpower. All rights reserved.
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

package org.glassfish.hk2.extras.provides;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.DuplicateServiceException;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.jvnet.hk2.annotations.Contract;

/**
 * Matches service classes that do not provide instances of themselves through
 * automatic invocation of a constructor.
 *
 * <p>Use {@link #enableNoInstancesFilter(ServiceLocator)} to enable the
 * {@linkplain DefaultFilter default filter}, which matches abstract classes and
 * classes with no public constructors.
 *
 * <p>When this filter matches a class, it is implied that the {@link
 * ActiveDescriptor} for the class must disallow the {@linkplain
 * ActiveDescriptor#create(ServiceHandle) creation of instances} and must
 * advertise no {@linkplain Descriptor#getAdvertisedContracts() contracts}.
 *
 * <p>This class may be useful in combination with {@link ProvidesListener}, for
 * example, to permit registration of classes that do not provide themselves as
 * a service but that provide other services by way of static members annotated
 * with {@link Provides}.
 */
@Contract
public interface NoInstancesFilter {
  /**
   * Returns {@code true} if the specified class does not provide instances of
   * itself through automatic invocation of a constructor.
   */
  boolean matches(Class<?> clazz);

  /**
   * Matches abstract classes and classes with no public constructors.
   */
  final class DefaultFilter implements NoInstancesFilter {
    @Override
    public boolean matches(Class<?> clazz) {
      Objects.requireNonNull(clazz);
      return Modifier.isAbstract(clazz.getModifiers())
          || clazz.getConstructors().length == 0;
    }
  }

  /**
   * Enables the default {@link NoInstancesFilter}.
   *
   * <p>This method replaces the default {@link DynamicConfigurationService} of
   * the service locator.  Therefore, this method must be invoked prior to any
   * other configuration that relies on the {@link NoInstancesFilter} being
   * applied.  In other words, the filter will not be applied to classes that
   * were already added to the service locator or that were already added to an
   * {@linkplain DynamicConfiguration#commit() uncommitted} {@link
   * DynamicConfiguration} for the service locator.
   *
   * <p>This method is idempotent.
   */
  static void enableNoInstancesFilter(ServiceLocator locator) {
    Objects.requireNonNull(locator);
    try {
      ServiceLocatorUtilities.addClasses(
          locator,
          true,
          NoInstancesFilter.DefaultFilter.class,
          NoInstancesService.class);
    } catch (MultiException multiException) {
      List<Throwable> errors = multiException.getErrors();
      if (errors.isEmpty()
          || !errors.stream()
                    .allMatch(e -> e instanceof DuplicateServiceException))
        throw multiException;
    }
  }
}
