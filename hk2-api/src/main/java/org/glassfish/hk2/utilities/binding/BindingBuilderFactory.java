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

package org.glassfish.hk2.utilities.binding;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.HK2Loader;

import java.lang.annotation.Annotation;

/**
 * HK2 injection binding utility methods.
 *
 * @author Tom Beerbower
 * @author Marek Potociar (marek.potociar at oracle.com)
 * @author Mason Taube (mason.taube at oracle.com)
 */
public class BindingBuilderFactory {


    /**
     * Add a binding represented by the binding builder to the HK2 dynamic configuration.
     *
     * @param builder       binding builder.
     * @param configuration HK2 dynamic configuration.
     */
    public static void addBinding(BindingBuilder<?> builder, DynamicConfiguration configuration) {
        if (builder instanceof AbstractBindingBuilder) {
            ((AbstractBindingBuilder<?>) builder).complete(configuration, null);
        } else {
            throw new IllegalArgumentException("Unknown binding builder type: " + builder.getClass().getName());
        }
    }

    /**
     * Add a binding represented by the binding builder to the HK2 dynamic configuration.
     *
     * @param builder       binding builder.
     * @param configuration HK2 dynamic configuration.
     * @param defaultLoader default HK2 service loader that should be used to load the service class
     *                      in case a custom loader has not been set.
     */
    public static void addBinding(BindingBuilder<?> builder, DynamicConfiguration configuration, HK2Loader defaultLoader) {
        if (builder instanceof AbstractBindingBuilder) {
            ((AbstractBindingBuilder<?>) builder).complete(configuration, defaultLoader);
        } else {
            throw new IllegalArgumentException("Unknown binding builder type: " + builder.getClass().getName());
        }
    }

    /**
     * Get a new factory class-based service binding builder.
     *
     * @param <T>          service type.
     * @param factoryType  service factory class.
     * @param factoryScope factory scope.
     * @return initialized binding builder.
     */
    public static <T> ServiceBindingBuilder<T> newFactoryBinder(
            Class<? extends Factory<T>> factoryType, Class<? extends Annotation> factoryScope) {
        return AbstractBindingBuilder.<T>createFactoryBinder(factoryType, factoryScope);
    }

    /**
     * Get a new factory class-based service binding builder.
     *
     * The factory itself is bound in a {@link org.glassfish.hk2.api.PerLookup per-lookup} scope.
     *
     * @param <T>         service type.
     * @param factoryType service factory class.
     * @return initialized binding builder.
     */
    public static <T> ServiceBindingBuilder<T> newFactoryBinder(Class<? extends Factory<T>> factoryType) {
        return AbstractBindingBuilder.<T>createFactoryBinder(factoryType, null);
    }

    /**
     * Get a new factory instance-based service binding builder.
     *
     * @param <T>     service type.
     * @param factory service instance.
     * @return initialized binding builder.
     */
    public static <T> ServiceBindingBuilder<T> newFactoryBinder(Factory<T> factory) {
        return AbstractBindingBuilder.createFactoryBinder(factory);
    }

    /**
     * Get a new class-based service binding builder.
     *
     * Does NOT bind the service type itself as a contract type.
     *
     * @param <T>         service type.
     * @param serviceType service class.
     * @return initialized binding builder.
     */
    public static <T> ServiceBindingBuilder<T> newBinder(Class<T> serviceType) {
        return AbstractBindingBuilder.create(serviceType, false);
    }

    /**
     * Get a new instance-based service binding builder. The binding is naturally
     * considered to be a {@link javax.inject.Singleton singleton-scoped}.
     *
     * Does NOT bind the service type itself as a contract type.
     *
     * @param <T>     service type.
     * @param service service instance.
     * @return initialized binding builder.
     */
    public static <T> ScopedBindingBuilder<T> newBinder(T service) {
        return AbstractBindingBuilder.create(service);
    }
}
