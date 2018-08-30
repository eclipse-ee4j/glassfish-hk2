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

package org.glassfish.hk2.internal;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.inject.Named;

import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.FactoryDescriptors;
import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.utilities.DescriptorBuilder;
import org.glassfish.hk2.utilities.DescriptorImpl;
import org.glassfish.hk2.utilities.FactoryDescriptorsImpl;

/**
 * This is a simple implementation of the {@link DescriptorBuilder}
 * 
 * @author jwells
 */
public class DescriptorBuilderImpl implements DescriptorBuilder {
    private String name;
    private final HashSet<String> contracts = new HashSet<String>();
    private String scope;
    private final HashSet<String> qualifiers = new HashSet<String>();
    private final HashMap<String, List<String>> metadatas = new HashMap<String, List<String>>();
    private String implementation;
    private HK2Loader loader = null;
    private int rank = 0;
    private Boolean proxy = null;
    private Boolean proxyForSameScope = null;
    private DescriptorVisibility visibility = DescriptorVisibility.NORMAL;
    private String analysisName = null;

    /**
     * The basid constructor
     */
    public DescriptorBuilderImpl() {
    }

    /**
     * A descriptor builder with the given implementation
     * 
     * @param implementation
     *            The implementation this should take
     * @param addToContracts
     *            Whether or not to add the implementation to the set of
     *            contracts
     */
    public DescriptorBuilderImpl(String implementation, boolean addToContracts) {
        this.implementation = implementation;
        if (addToContracts) {
            contracts.add(implementation);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.glassfish.hk2.utilities.DescriptorBuilder#named(java.lang.String)
     */
    @Override
    public DescriptorBuilder named(String name) throws IllegalArgumentException {
        if (this.name != null) {
            throw new IllegalArgumentException();
        }

        this.name = name;
        qualifiers.add(Named.class.getName());

        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.glassfish.hk2.utilities.DescriptorBuilder#withContract(java.lang.
     * Class)
     */
    @Override
    public DescriptorBuilder to(Class<?> contract)
            throws IllegalArgumentException {
        if (contract == null) throw new IllegalArgumentException();

        return to(contract.getName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.glassfish.hk2.utilities.DescriptorBuilder#withContract(java.lang.
     * String)
     */
    @Override
    public DescriptorBuilder to(String contract)
            throws IllegalArgumentException {
        if (contract == null) throw new IllegalArgumentException();

        contracts.add(contract);

        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.glassfish.hk2.utilities.DescriptorBuilder#in(org.glassfish.hk2.Scope)
     */
    @Override
    public DescriptorBuilder in(Class<? extends Annotation> scope)
            throws IllegalArgumentException {
        if (scope == null) {
            throw new IllegalArgumentException();
        }

        return in(scope.getName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glassfish.hk2.utilities.DescriptorBuilder#in(java.lang.String)
     */
    @Override
    public DescriptorBuilder in(String scope) throws IllegalArgumentException {
        if (scope == null) {
            throw new IllegalArgumentException();
        }

        this.scope = scope;
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.glassfish.hk2.utilities.DescriptorBuilder#annotatedBy(java.lang.Class
     * )
     */
    @Override
    public DescriptorBuilder qualifiedBy(Annotation annotation)
            throws IllegalArgumentException {
        if (annotation == null) throw new IllegalArgumentException();
        
        if (Named.class.equals(annotation.annotationType())) {
            this.name = ((Named) annotation).value();
        }

        return qualifiedBy(annotation.annotationType().getName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.glassfish.hk2.utilities.DescriptorBuilder#annotatedBy(java.lang.String
     * )
     */
    @Override
    public DescriptorBuilder qualifiedBy(String annotation)
            throws IllegalArgumentException {
        if (annotation == null) throw new IllegalArgumentException();

        qualifiers.add(annotation);
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glassfish.hk2.utilities.DescriptorBuilder#with(java.lang.String,
     * java.lang.String)
     */
    @Override
    public DescriptorBuilder has(String key, String value)
            throws IllegalArgumentException {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }

        LinkedList<String> values = new LinkedList<String>();
        values.add(value);

        return has(key, values);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glassfish.hk2.utilities.DescriptorBuilder#with(java.lang.String,
     * java.util.List)
     */
    @Override
    public DescriptorBuilder has(String key, List<String> values)
            throws IllegalArgumentException {
        if (key == null || values == null || values.size() <= 0) {
            throw new IllegalArgumentException();
        }

        metadatas.put(key, values);

        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glassfish.hk2.utilities.DescriptorBuilder#ofRank(int)
     */
    @Override
    public DescriptorBuilder ofRank(int rank) {
        this.rank = rank;
        return this;
    }

    @Override
    public DescriptorBuilder proxy() {
        return proxy(true);
    }

    @Override
    public DescriptorBuilder proxy(boolean forceProxy) {
        if (forceProxy) {
            proxy = Boolean.TRUE;
        }
        else {
            proxy = Boolean.FALSE;
        }

        return this;
    }

    @Override
    public DescriptorBuilder proxyForSameScope() {
        return proxyForSameScope(true);
    }

    @Override
    public DescriptorBuilder proxyForSameScope(boolean proxyForSameScope) {
        if (proxyForSameScope) {
            this.proxyForSameScope = Boolean.TRUE;
        }
        else {
            this.proxyForSameScope = Boolean.FALSE;
        }

        return this;
    }

    @Override
    public DescriptorBuilder localOnly() {
        visibility = DescriptorVisibility.LOCAL;

        return this;
    }

    @Override
    public DescriptorBuilder visibility(DescriptorVisibility visibility) {
        if (visibility == null) throw new IllegalArgumentException();

        this.visibility = visibility;

        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.glassfish.hk2.utilities.DescriptorBuilder#andLoadWith(org.glassfish
     * .hk2.api.HK2Loader)
     */
    @Override
    public DescriptorBuilder andLoadWith(HK2Loader loader)
            throws IllegalArgumentException {
        if (this.loader != null) throw new IllegalArgumentException();

        this.loader = loader;
        return this;
    }

    @Override
    public DescriptorBuilder analyzeWith(String serviceName) {
        this.analysisName = serviceName;

        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glassfish.hk2.utilities.DescriptorBuilder#build()
     */
    @Override
    public DescriptorImpl build() throws IllegalArgumentException {
        return new DescriptorImpl(contracts, name, scope, implementation,
                metadatas, qualifiers, DescriptorType.CLASS, visibility,
                loader, rank, proxy, proxyForSameScope, analysisName, null, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glassfish.hk2.utilities.DescriptorBuilder#buildFactory()
     */
    @Override
    public FactoryDescriptors buildFactory(String factoryScope)
            throws IllegalArgumentException {
        Set<String> factoryContracts = new HashSet<String>();
        factoryContracts.add(implementation);
        factoryContracts.add(Factory.class.getName());
        Set<String> factoryQualifiers = Collections.emptySet();
        Map<String, List<String>> factoryMetadata = Collections.emptyMap();

        DescriptorImpl asService = new DescriptorImpl(factoryContracts, null,
                factoryScope, implementation, factoryMetadata,
                factoryQualifiers, DescriptorType.CLASS,
                DescriptorVisibility.NORMAL, loader, rank, null, null, analysisName,
                null, null);

        // We want to remove the impl class from the contracts in this case
        Set<String> serviceContracts = new HashSet<String>(contracts);
        if (implementation != null) serviceContracts.remove(implementation);

        DescriptorImpl asFactory = new DescriptorImpl(serviceContracts, name,
                scope, implementation, metadatas, qualifiers,
                DescriptorType.PROVIDE_METHOD, visibility, loader, rank, proxy,
                proxyForSameScope, null, null, null);

        return new FactoryDescriptorsImpl(asService, asFactory);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glassfish.hk2.utilities.DescriptorBuilder#buildFactory()
     */
    @Override
    public FactoryDescriptors buildFactory() throws IllegalArgumentException {
        return buildFactory(PerLookup.class.getName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.glassfish.hk2.utilities.DescriptorBuilder#buildFactory(java.lang.
     * Class)
     */
    @Override
    public FactoryDescriptors buildFactory(
            Class<? extends Annotation> factoryScope)
            throws IllegalArgumentException {
        if (factoryScope == null) factoryScope = PerLookup.class;

        return buildFactory(factoryScope.getName());
    }
}
