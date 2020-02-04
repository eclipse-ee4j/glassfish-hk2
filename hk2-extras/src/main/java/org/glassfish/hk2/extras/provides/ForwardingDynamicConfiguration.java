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

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.FactoryDescriptors;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.TwoPhaseResource;

/**
 * A dynamic configuration that forwards all its method calls to another dynamic
 * configuration.
 */
interface ForwardingDynamicConfiguration extends DynamicConfiguration {
  /**
   * The dynamic configuration instance to which this instance forwards all its
   * method calls.
   */
  DynamicConfiguration delegate();

  @Override
  default <T> ActiveDescriptor<T> bind(Descriptor key) {
    return delegate().bind(key);
  }

  @Override
  default <T> ActiveDescriptor<T> bind(Descriptor key, boolean requiresDeepCopy) {
    return delegate().bind(key, requiresDeepCopy);
  }

  @Override
  default FactoryDescriptors bind(FactoryDescriptors factoryDescriptors) {
    return delegate().bind(factoryDescriptors);
  }

  @Override
  default FactoryDescriptors bind(FactoryDescriptors factoryDescriptors,
                                  boolean requiresDeepCopy) {
    return delegate().bind(factoryDescriptors, requiresDeepCopy);
  }

  @Override
  default <T> ActiveDescriptor<T> addActiveDescriptor(ActiveDescriptor<T> activeDescriptor) {
    return delegate().addActiveDescriptor(activeDescriptor);
  }

  @Override
  default <T> ActiveDescriptor<T> addActiveDescriptor(ActiveDescriptor<T> activeDescriptor,
                                                      boolean requiresDeepCopy) {
    return delegate().addActiveDescriptor(activeDescriptor, requiresDeepCopy);
  }

  @Override
  default <T> ActiveDescriptor<T> addActiveDescriptor(Class<T> rawClass) {
    return delegate().addActiveDescriptor(rawClass);
  }

  @Override
  default <T> FactoryDescriptors addActiveFactoryDescriptor(Class<? extends Factory<T>> rawFactoryClass) {
    return delegate().addActiveFactoryDescriptor(rawFactoryClass);
  }

  @Override
  default void addUnbindFilter(Filter unbindFilter) {
    delegate().addUnbindFilter(unbindFilter);
  }

  @Override
  default void addIdempotentFilter(Filter... idempotentFilter) {
    delegate().addIdempotentFilter(idempotentFilter);
  }

  @Override
  default void registerTwoPhaseResources(TwoPhaseResource... resources) {
    delegate().registerTwoPhaseResources(resources);
  }

  @Override
  default void commit() {
    delegate().commit();
  }
}
