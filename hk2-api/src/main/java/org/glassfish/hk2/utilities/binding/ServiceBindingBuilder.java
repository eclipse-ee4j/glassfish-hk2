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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.TypeLiteral;

/**
 * Service binding builder.
 *
 * @param <T> service type.
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
public interface ServiceBindingBuilder<T> extends BindingBuilder<T> {
    /**
     * Bind a new contract to a service.
     *
     * @param contract contract type.
     * @return updated binder.
     */
    public ServiceBindingBuilder<T> to(Class<? super T> contract);

    /**
     * Bind a new contract to a service.
     *
     * @param contract contract type.
     * @return updated binder.
     */
    public ServiceBindingBuilder<T> to(TypeLiteral<?> contract);
    
    /**
     * Bind a new contract to a service.
     *
     * @param contract contract type.
     * @return updated binder.
     */
    public ServiceBindingBuilder<T> to(Type contract);

    /**
     * Custom HK2 loader to be used when service class is about to be loaded.
     *
     * @param loader custom service loader.
     * @return updated binder.
     */
    public ServiceBindingBuilder<T> loadedBy(HK2Loader loader);

    /**
     * Add binding descriptor metadata.
     *
     * The metadata can be later used to e.g. {@link org.glassfish.hk2.api.Filter filter} binding
     * descriptors.  If this is for {@link org.glassfish.hk2.api.Factory} descriptors the metadata
     * will be placed on both the Factory as a service and on the
     * Factories {@link org.glassfish.hk2.api.Factory#provide()} method
     *
     * @param key metadata key.
     * @param value metadata value.
     * @return updated binder.
     */
    public ServiceBindingBuilder<T> withMetadata(String key, String value);

    /**
     * Add binding descriptor metadata.
     *
     * The metadata can be later used to e.g. {@link org.glassfish.hk2.api.Filter filter} binding
     * descriptors.  If this is for {@link org.glassfish.hk2.api.Factory} descriptors the metadata
     * will be placed on both the Factory as a service and on the
     * Factories {@link org.glassfish.hk2.api.Factory#provide()} method
     *
     * @param key metadata key.
     * @param values metadata values.
     * @return updated binder.
     */
    public ServiceBindingBuilder<T> withMetadata(String key, List<String> values);

    /**
     * Add a binding qualifier annotation.  If this is
     * being used with a {@link org.glassfish.hk2.api.Factory} then both the
     * Factory Service and the {@link org.glassfish.hk2.api.Factory#provide()}
     * method will get the qualifier
     *
     * @param annotation qualifier annotation.
     * @return updated binder.
     */
    public ServiceBindingBuilder<T> qualifiedBy(Annotation annotation);

    /**
     * Scope a binding.
     *
     * @param scopeAnnotation scope annotation.
     * @return updated binding.
     */
    public ScopedBindingBuilder<T> in(Annotation scopeAnnotation);
    
    /**
     * Scope a binding.
     *
     * @param scopeAnnotation scope annotation.
     * @return updated binding.
     */
    public ScopedBindingBuilder<T> in(Class<? extends Annotation> scopeAnnotation);

    /**
     * {@link javax.inject.Named Name} the binding.
     *
     * @param name new name value.
     * @return updated binding.
     */
    public NamedBindingBuilder<T> named(String name);

    /**
     * Rank the binding. The higher rank, the more prominent position in an injected
     * {@link org.glassfish.hk2.api.IterableProvider iterable provider} for a contract.
     *
     * @param rank binding rank to be used to resolve ordering in case of multiple services
     *             are bound to the same contract.
     */
    public void ranked(int rank);

    /**
     * Set proxy flag on the binding.
     *
     * @param proxiable flag to determine if the binding should be proxiable.
     */
    public ServiceBindingBuilder<T> proxy(boolean proxiable);
    
    /**
     * Set proxyForSameScope flag on the binding
     * 
     * @param proxyForSameScope flag to determine if proxies should be generated
     * even within the same scope
     * @return A further refined ServiceBindingBuilder
     */
    public ServiceBindingBuilder<T> proxyForSameScope(boolean proxyForSameScope);
    
    /**
     * Set the name of the {@link org.glassfish.hk2.api.ClassAnalyzer} on the binding.
     *
     * @param analyzer The name of the analyzer that should be used.  May be null
     * to indicate the default class analzyer
     */
    public ServiceBindingBuilder<T> analyzeWith(String analyzer);
    
    /**
     * Call this if the parameterized type of the implementation
     * class is known.  This must only be called with a
     * ParameterizedType
     * 
     * @param t The non-null ParameterizedType describing the implementation
     * @return A DescriptorBuilder with the given implementationType
     */
    public ServiceBindingBuilder<T> asType(Type t);
}
