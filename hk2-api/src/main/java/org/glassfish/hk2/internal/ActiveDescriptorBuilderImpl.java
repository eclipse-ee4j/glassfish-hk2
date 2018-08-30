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
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Named;

import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;
import org.glassfish.hk2.utilities.ActiveDescriptorBuilder;

/**
 * @author jwells
 *
 */
public class ActiveDescriptorBuilderImpl implements ActiveDescriptorBuilder {
    private String name;
    private final HashSet<Type> contracts = new HashSet<Type>();
    private Annotation scopeAnnotation = null;
    private Class<? extends Annotation> scope = PerLookup.class;
    private final HashSet<Annotation> qualifiers = new HashSet<Annotation>();
    private final HashMap<String, List<String>> metadatas = new HashMap<String, List<String>>();
    private final Class<?> implementation;
    private HK2Loader loader = null;
    private int rank = 0;
    private Boolean proxy = null;
    private Boolean proxyForSameScope = null;
    private DescriptorVisibility visibility = DescriptorVisibility.NORMAL;
    private String classAnalysisName = null;
    private Type implementationType;
    
    /**
     * constructor with the impl class
     * 
     * @param implementation The implementation class (may be null)
     */
    public ActiveDescriptorBuilderImpl(Class<?> implementation) {
        this.implementation = implementation;
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.ActiveDescriptorBuilder#named(java.lang.String)
     */
    @Override
    public ActiveDescriptorBuilder named(String name) throws IllegalArgumentException {
        this.name = name;
        
        return this;
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.ActiveDescriptorBuilder#to(java.lang.reflect.Type)
     */
    @Override
    public ActiveDescriptorBuilder to(Type contract) throws IllegalArgumentException {
        if (contract != null) contracts.add(contract);
        
        return this;
    }
    
    @Override
    public ActiveDescriptorBuilder in(Annotation scopeAnnotation)
            throws IllegalArgumentException {
        if (scopeAnnotation == null) throw new IllegalArgumentException();
        
        this.scopeAnnotation = scopeAnnotation;
        this.scope = scopeAnnotation.annotationType();
        
        return this;
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.ActiveDescriptorBuilder#in(java.lang.Class)
     */
    @Override
    public ActiveDescriptorBuilder in(Class<? extends Annotation> scope)
            throws IllegalArgumentException {
        this.scope = scope;
        if (scope == null) {
            scopeAnnotation = null;
        }
        else if (scopeAnnotation != null &&  !scope.equals(scopeAnnotation.annotationType())) {
            throw new IllegalArgumentException("Scope set to different class (" + scope.getName() + ") from the scope annotation (" +
              scopeAnnotation.annotationType().getName());
        }
       
        
        
        return this;
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.ActiveDescriptorBuilder#qualifiedBy(java.lang.annotation.Annotation)
     */
    @Override
    public ActiveDescriptorBuilder qualifiedBy(Annotation annotation)
            throws IllegalArgumentException {
        if (annotation != null) {
            if (Named.class.equals(annotation.annotationType())) {
                name = ((Named) annotation).value();
            }
            qualifiers.add(annotation);
        }
        
        return this;
    }
    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.ActiveDescriptorBuilder#has(java.lang.String, java.lang.String)
     */
    @Override
    public ActiveDescriptorBuilder has(String key, String value)
            throws IllegalArgumentException {
        return has(key, Collections.singletonList(value));
    }
    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.ActiveDescriptorBuilder#has(java.lang.String, java.util.List)
     */
    @Override
    public ActiveDescriptorBuilder has(String key, List<String> values)
            throws IllegalArgumentException {
        if (key == null || values == null || values.size() <= 0) {
            throw new IllegalArgumentException();
        }
        
        metadatas.put(key, values);
        
        return this;
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.ActiveDescriptorBuilder#ofRank(int)
     */
    @Override
    public ActiveDescriptorBuilder ofRank(int rank) {
        this.rank = rank;
        
        return this;
    }
    
    @Override
    public ActiveDescriptorBuilder proxy() {
        return proxy(true);
    }
    
    @Override
    public ActiveDescriptorBuilder proxy(boolean forceProxy) {
        if (forceProxy) {
            proxy = Boolean.TRUE;
        }
        else {
            proxy = Boolean.FALSE;
        }
        
        return this;
    }
    
    @Override
    public ActiveDescriptorBuilder proxyForSameScope() {
        return proxy(true);
    }
    
    @Override
    public ActiveDescriptorBuilder proxyForSameScope(boolean forceProxyForSameScope) {
        if (forceProxyForSameScope) {
            proxyForSameScope = Boolean.TRUE;
        }
        else {
            proxyForSameScope = Boolean.FALSE;
        }
        
        return this;
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.ActiveDescriptorBuilder#andLoadWith(org.glassfish.hk2.api.HK2Loader)
     */
    @Override
    public ActiveDescriptorBuilder andLoadWith(HK2Loader loader)
            throws IllegalArgumentException {
        this.loader = loader;
        
        return this;
    }
    
    public ActiveDescriptorBuilder analyzeWith(String serviceName) {
        classAnalysisName = serviceName;
        
        return this;
    }
    
    @Override
    public ActiveDescriptorBuilder localOnly() {
        visibility = DescriptorVisibility.LOCAL;
        
        return this;
    }

    @Override
    public ActiveDescriptorBuilder visibility(DescriptorVisibility visibility) {
        if (visibility == null) throw new IllegalArgumentException();
        
        this.visibility = visibility;
        
        return this;
    }
    
    @Override
    public ActiveDescriptorBuilder asType(Type t) {
        if (t == null) throw new IllegalArgumentException();
        
        this.implementationType = t;
        
        return this;
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.ActiveDescriptorBuilder#build()
     */
    @Override
    public <T> AbstractActiveDescriptor<T> build() throws IllegalArgumentException {
        return new BuiltActiveDescriptor<T>(
                implementation,
                contracts,
                scopeAnnotation,
                scope,
                name,
                qualifiers,
                DescriptorType.CLASS,
                visibility,
                rank,
                proxy,
                proxyForSameScope,
                classAnalysisName,
                metadatas,
                loader,
                implementationType);
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.ActiveDescriptorBuilder#buildProvideMethod()
     */
    @Override
    @Deprecated
    public <T> AbstractActiveDescriptor<T> buildFactory() throws IllegalArgumentException {
        return buildProvideMethod();
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.ActiveDescriptorBuilder#buildProvideMethod()
     */
    @Override
    public <T> AbstractActiveDescriptor<T> buildProvideMethod() throws IllegalArgumentException {
        return new BuiltActiveDescriptor<T>(
                implementation,
                contracts,
                scopeAnnotation,
                scope,
                name,
                qualifiers,
                DescriptorType.PROVIDE_METHOD,
                visibility,
                rank,
                proxy,
                proxyForSameScope,
                classAnalysisName,
                metadatas,
                loader,
                implementationType);
    }
    
    private static class BuiltActiveDescriptor<T> extends AbstractActiveDescriptor<T> {
        /**
         * For serialization
         */
        private static final long serialVersionUID = 2434137639270026082L;
        
        private Class<?> implementationClass;
        private Type implementationType;
        
        /**
         * For serialization
         */
        @SuppressWarnings("unused")
        public BuiltActiveDescriptor() {
            super();
        }
        
        private BuiltActiveDescriptor(Class<?> implementationClass,
                Set<Type> advertisedContracts,
                Annotation scopeAnnotation,
                Class<? extends Annotation> scope,
                String name,
                Set<Annotation> qualifiers,
                DescriptorType descriptorType,
                DescriptorVisibility descriptorVisibility,
                int ranking,
                Boolean proxy,
                Boolean proxyForSameScope,
                String classAnalysisName,
                Map<String, List<String>> metadata,
                HK2Loader loader,
                Type implementationType) {
            super(advertisedContracts,
                    scope,
                    name,
                    qualifiers,
                    descriptorType,
                    descriptorVisibility,
                    ranking,
                    proxy,
                    proxyForSameScope,
                    classAnalysisName,
                    metadata);
            
            super.setReified(false);
            super.setLoader(loader);
            super.setScopeAsAnnotation(scopeAnnotation);
            
            this.implementationClass = implementationClass;
            super.setImplementation(implementationClass.getName());
            
            if (implementationType == null) {
                implementationType = implementationClass;
            }
            this.implementationType = implementationType;
        }
        
        /* (non-Javadoc)
         * @see org.glassfish.hk2.api.ActiveDescriptor#getImplementationClass()
         */
        @Override
        public Class<?> getImplementationClass() {
            return implementationClass;
        }
        
        @Override
        public Type getImplementationType() {
            return implementationType;
        }

        /* (non-Javadoc)
         * @see org.glassfish.hk2.api.ActiveDescriptor#create(org.glassfish.hk2.api.ServiceHandle)
         */
        @Override
        public T create(ServiceHandle<?> root) {
            throw new AssertionError("Should not be called directly");
        }

        @Override
        public void setImplementationType(Type t) {
            implementationType = t;
        }
    }

    

    
    

}
