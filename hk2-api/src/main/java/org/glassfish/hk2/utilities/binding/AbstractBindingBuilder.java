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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Named;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;
import org.glassfish.hk2.utilities.ActiveDescriptorBuilder;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.FactoryDescriptorsImpl;
import org.glassfish.hk2.utilities.reflection.ParameterizedTypeImpl;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;
import org.jvnet.hk2.component.MultiMap;


/**
 * Abstract binding builder implementation.
 *
 * @param <T> bound service type.
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
abstract class AbstractBindingBuilder<T> implements
        ServiceBindingBuilder<T>, NamedBindingBuilder<T>, ScopedBindingBuilder<T>, ScopedNamedBindingBuilder<T> {

    /**
     * Contracts the service should be bound to.
     */
    Set<Type> contracts = new HashSet<Type>();
    /**
     * Bound service loader.
     */
    HK2Loader loader = null;
    /**
     * Binding metadata (e.g. useful for filtering).
     */
    final MultiMap<String, String> metadata = new MultiMap<String, String>();
    /**
     * Qualifiers (other than @Named).
     */
    Set<Annotation> qualifiers = new HashSet<Annotation>();
    /**
     * Binding scope as annotation
     */
    Annotation scopeAnnotation = null;
    /**
     * Binding scope.
     */
    Class<? extends Annotation> scope = null;
    /**
     * Binding rank.
     */
    Integer ranked = null;
    /**
     * Binding name (see @Named).
     */
    String name = null;
    /**
     * Injectee is proxiable.
     */
    Boolean proxiable = null;
    /**
     * Injectee should be proxied even inside the same scope
     */
    Boolean proxyForSameScope = null;
    
    Type implementationType = null;

    @Override
    public AbstractBindingBuilder<T> proxy(boolean proxiable) {
        this.proxiable = proxiable;
        return this;
    }
    
    @Override
    public AbstractBindingBuilder<T> proxyForSameScope(boolean proxyForSameScope) {
        this.proxyForSameScope = proxyForSameScope;
        return this;
    }
    
    /**
     * Analyzer to use with this descriptor
     */
    String analyzer = null;
    
    @Override
    public AbstractBindingBuilder<T> analyzeWith(String analyzer) {
        this.analyzer = analyzer;
        return this;
    }

    @Override
    public AbstractBindingBuilder<T> to(Class<? super T> contract) {
        contracts.add(contract);
        return this;
    }

    @Override
    public AbstractBindingBuilder<T> to(TypeLiteral<?> contract) {
        contracts.add(contract.getType());
        return this;
    }
    
    @Override
    public AbstractBindingBuilder<T> to(Type contract) {
        contracts.add(contract);
        return this;
    }

    @Override
    public AbstractBindingBuilder<T> loadedBy(HK2Loader loader) {
        this.loader = loader;
        return this;
    }

    @Override
    public AbstractBindingBuilder<T> withMetadata(String key, String value) {
        this.metadata.add(key, value);
        return this;
    }

    @Override
    public AbstractBindingBuilder<T> withMetadata(String key, List<String> values) {
        for (String value : values) {
            this.metadata.add(key,value);
        }
        return this;
    }

    @Override
    public AbstractBindingBuilder<T> qualifiedBy(Annotation annotation) {
        if (Named.class.equals(annotation.annotationType())) {
            this.name = ((Named) annotation).value();
        }
        this.qualifiers.add(annotation);
        return this;
    }
    
    @Override
    public AbstractBindingBuilder<T> in(Annotation scopeAnnotation) {
        this.scopeAnnotation = scopeAnnotation;
        return this;
    }

    @Override
    public AbstractBindingBuilder<T> in(Class<? extends Annotation> scopeAnnotation) {
        this.scope = scopeAnnotation;
        return this;
    }

    @Override
    public AbstractBindingBuilder<T> named(String name) {
        this.name = name;
        return this;
    }

    @Override
    public void ranked(int rank) {
        this.ranked = rank;
    }
    
    public AbstractBindingBuilder<T> asType(Type t) {
        this.implementationType = t;
        return this;
    }

    /**
     * Build the binding descriptor and bind it in the {@link DynamicConfiguration
     * dynamic configuration}.
     *
     * @param configuration dynamic binding configuration.
     * @param defaultLoader default HK2 loader that should be used in case a custom loader
     *                      was not set.
     */
    abstract void complete(DynamicConfiguration configuration, HK2Loader defaultLoader);

    private static class ClassBasedBindingBuilder<T> extends AbstractBindingBuilder<T> {
        private final Class<T> service;

        public ClassBasedBindingBuilder(Class<T> service, Type serviceContractType) {
            this.service = service;
            if (serviceContractType != null) {
                super.contracts.add(serviceContractType);
            }
        }

        @Override
        void complete(final DynamicConfiguration configuration, final HK2Loader defaultLoader) {
            if (this.loader == null) {
                this.loader = defaultLoader;
            }

            ActiveDescriptorBuilder builder = BuilderHelper.activeLink(service)
                    .named(name)
                    .andLoadWith(this.loader)
                    .analyzeWith(this.analyzer);

            if (scopeAnnotation != null) {
                builder.in(scopeAnnotation);
            }
            if (scope != null) {
                builder.in(scope);
            }

            if (ranked != null) {
                builder.ofRank(ranked);
            }

            for (String key : metadata.keySet()) {
                for (String value : metadata.get(key)) {
                    builder.has(key, value);
                }
            }

            for (Annotation annotation : qualifiers) {
                builder.qualifiedBy(annotation);
            }

            for (Type contract : contracts) {
                builder.to(contract);
            }

            if (proxiable != null) {
                builder.proxy(proxiable);
            }
            
            if (proxyForSameScope != null) {
                builder.proxyForSameScope(proxyForSameScope);
            }
            
            if (implementationType != null) {
                builder.asType(implementationType);
            }

            configuration.bind(builder.build(), false);
        }
    }

    private static class InstanceBasedBindingBuilder<T> extends AbstractBindingBuilder<T> {
        private final T service;

        public InstanceBasedBindingBuilder(T service) {
            if (service == null) throw new IllegalArgumentException();
            this.service = service;
        }

        @Override
        void complete(DynamicConfiguration configuration, HK2Loader defaultLoader) {
            if (this.loader == null) {
                this.loader = defaultLoader;
            }
            AbstractActiveDescriptor<?> descriptor = BuilderHelper.createConstantDescriptor(service);
            descriptor.setName(name);
            descriptor.setLoader(this.loader);
            descriptor.setClassAnalysisName(this.analyzer);

            if (scope != null) {
                descriptor.setScope(scope.getName());
            }

            if (ranked != null) {
                descriptor.setRanking(ranked);
            }

            for (String key : metadata.keySet()) {
                for (String value : metadata.get(key)) {
                    descriptor.addMetadata(key, value);
                }
            }

            for (Annotation annotation : qualifiers) {
                descriptor.addQualifierAnnotation(annotation);
            }

            for (Type contract : contracts) {
                descriptor.addContractType(contract);
            }

            if (proxiable != null) {
                descriptor.setProxiable(proxiable);
            }
            
            if (proxyForSameScope != null) {
                descriptor.setProxyForSameScope(proxyForSameScope);
            }

            configuration.bind(descriptor, false);
        }
    }

    private static class FactoryInstanceBasedBindingBuilder<T> extends AbstractBindingBuilder<T> {
        private final Factory<T> factory;

        public FactoryInstanceBasedBindingBuilder(Factory<T> factory) {
            this.factory = factory;
        }

        @Override
        void complete(DynamicConfiguration configuration, HK2Loader defaultLoader) {
            if (this.loader == null) {
                this.loader = defaultLoader;
            }

            AbstractActiveDescriptor<?> factoryContractDescriptor = BuilderHelper.createConstantDescriptor(factory);
            factoryContractDescriptor.addContractType(factory.getClass());
            factoryContractDescriptor.setLoader(this.loader);

            ActiveDescriptorBuilder descriptorBuilder = BuilderHelper.activeLink(factory.getClass())
                    .named(name)
                    .andLoadWith(this.loader)
                    .analyzeWith(this.analyzer);
            
            if (scope != null) {
                descriptorBuilder.in(scope);
            }

            if (ranked != null) {
                descriptorBuilder.ofRank(ranked);
            }

            for (Annotation qualifier : qualifiers) {
                factoryContractDescriptor.addQualifierAnnotation(qualifier);
                descriptorBuilder.qualifiedBy(qualifier);
            }

            for (Type contract : contracts) {
                factoryContractDescriptor.addContractType(new ParameterizedTypeImpl(Factory.class, contract));
                descriptorBuilder.to(contract);
            }
            
            Set<String> keys = metadata.keySet();
            for (String key : keys) {
                List<String> values = metadata.get(key);
                for (String value : values) {
                    factoryContractDescriptor.addMetadata(key, value);
                    descriptorBuilder.has(key, value);
                }
            }
            
            if (proxiable != null) {
                descriptorBuilder.proxy(proxiable);
            }
            
            if (proxyForSameScope != null) {
                descriptorBuilder.proxyForSameScope(proxyForSameScope);
            }

            configuration.bind(new FactoryDescriptorsImpl(
                    factoryContractDescriptor,
                    descriptorBuilder.buildProvideMethod()));
        }
    }

    private static class FactoryTypeBasedBindingBuilder<T> extends AbstractBindingBuilder<T> {
        private final Class<? extends Factory<T>> factoryClass;
        private final Class<? extends Annotation> factoryScope;

        public FactoryTypeBasedBindingBuilder(Class<? extends Factory<T>> factoryClass, Class<? extends Annotation> factoryScope) {
            this.factoryClass = factoryClass;
            this.factoryScope = factoryScope;
        }

        @Override
        void complete(DynamicConfiguration configuration, HK2Loader defaultLoader) {
            if (this.loader == null) {
                this.loader = defaultLoader;
            }

            ActiveDescriptorBuilder factoryDescriptorBuilder = BuilderHelper.activeLink(factoryClass)
             .named(name)
             .andLoadWith(this.loader)
             .analyzeWith(this.analyzer);
            
            if (factoryScope != null) {
                factoryDescriptorBuilder.in(factoryScope);
            }

            ActiveDescriptorBuilder descriptorBuilder = BuilderHelper.activeLink(factoryClass)
                    .named(name)
                    .andLoadWith(this.loader)
                    .analyzeWith(this.analyzer);

            if (scope != null) {
                descriptorBuilder.in(scope);
            }

            if (ranked != null) {
//                factoryContractDescriptor.ofRank(factoryRank);
                descriptorBuilder.ofRank(ranked);
            }

            for (Annotation qualifier : qualifiers) {
                factoryDescriptorBuilder.qualifiedBy(qualifier);
                descriptorBuilder.qualifiedBy(qualifier);
            }

            for (Type contract : contracts) {
                factoryDescriptorBuilder.to(new ParameterizedTypeImpl(Factory.class, contract));
                descriptorBuilder.to(contract);
            }
            
            Set<String> keys = metadata.keySet();
            for (String key : keys) {
                List<String> values = metadata.get(key);
                for (String value : values) {
                    factoryDescriptorBuilder.has(key, value);
                    descriptorBuilder.has(key, value);
                }
            }
            
            if (proxiable != null) {
                descriptorBuilder.proxy(proxiable);
            }
            
            if (proxyForSameScope != null) {
                descriptorBuilder.proxyForSameScope(proxyForSameScope);
            }

            configuration.bind(new FactoryDescriptorsImpl(
                    factoryDescriptorBuilder.build(),
                    descriptorBuilder.buildProvideMethod()));
        }
    }


    /**
     * Create a new service binding builder.
     *
     * @param <T>            service type.
     * @param serviceType    service class.
     * @param bindAsContract if {@code true}, the service class will be bound as one of the contracts.
     * @return initialized binding builder.
     */
    static <T> AbstractBindingBuilder<T> create(Class<T> serviceType, boolean bindAsContract) {
        return new ClassBasedBindingBuilder<T>(serviceType, bindAsContract ? serviceType : null);
    }
    
    /**
     * Create a new service binding builder.
     *
     * @param <T>            service type.
     * @param serviceType    service class.
     * @param bindAsContract if {@code true}, the service class will be bound as one of the contracts.
     * @return initialized binding builder.
     */
    @SuppressWarnings("unchecked")
    static <T> AbstractBindingBuilder<T> create(Type serviceType, boolean bindAsContract) {
        return new ClassBasedBindingBuilder<T>(
                (Class<T>) ReflectionHelper.getRawClass(serviceType), bindAsContract ? serviceType : null).asType(serviceType);
    }
    /**
     * Create a new service binding builder.
     *
     * @param <T>            service type.
     * @param serviceType    generic service type.
     * @param bindAsContract if {@code true}, the service class will be bound as one of the contracts.
     * @return initialized binding builder.
     */
    static <T> AbstractBindingBuilder<T> create(TypeLiteral<T> serviceType, boolean bindAsContract) {
        Type type = serviceType.getType();
        return new ClassBasedBindingBuilder<T>(serviceType.getRawType(), bindAsContract ? serviceType.getType() : null).asType(type);
    }

    /**
     * Create a new service binding builder.
     *
     * @param service service instance.
     * @return initialized binding builder.
     */
    static <T> AbstractBindingBuilder<T> create(T service) {
        return new InstanceBasedBindingBuilder<T>(service);
    }

    /**
     * Create a new service binding builder.
     *
     * @param factory service factory instance.
     * @return initialized binding builder.
     */
    static <T> AbstractBindingBuilder<T> createFactoryBinder(Factory<T> factory) {
        return new FactoryInstanceBasedBindingBuilder<T>(factory);
    }

    /**
     * Create a new service binding builder.
     *
     * @param factoryType service factory class.
     * @param factoryScope service factory scope.
     * @return initialized binding builder.
     */
    static <T> AbstractBindingBuilder<T> createFactoryBinder(Class<? extends Factory<T>> factoryType, Class<? extends Annotation> factoryScope) {
        return new FactoryTypeBasedBindingBuilder<T>(factoryType, factoryScope);
    }
}
