/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.configuration.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Named;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.utilities.NamedImpl;

/**
 * @author jwells
 *
 */
public class DelegatingNamedActiveDescriptor implements
        ActiveDescriptor<Object> {
    private final ActiveDescriptor<?> parent;
    private final Named name;
    private final HashSet<String> qualifierNames;
    private final HashSet<Annotation> qualifiers;
    
    /* package */ DelegatingNamedActiveDescriptor(ActiveDescriptor<?> parent, String name) {
        this.parent = parent;
        this.name = new NamedImpl(name);
        
        qualifierNames = new HashSet<String>(parent.getQualifiers());
        qualifierNames.add(Named.class.getName());
        
        qualifiers = new HashSet<Annotation>(parent.getQualifierAnnotations());
        qualifiers.add(this.name);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#getImplementation()
     */
    @Override
    public String getImplementation() {
        return parent.getImplementation();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#getAdvertisedContracts()
     */
    @Override
    public Set<String> getAdvertisedContracts() {
        return parent.getAdvertisedContracts();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#getScope()
     */
    @Override
    public String getScope() {
        return parent.getScope();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#getName()
     */
    @Override
    public String getName() {
        return name.value();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#getQualifiers()
     */
    @Override
    public Set<String> getQualifiers() {
        return qualifierNames;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#getDescriptorType()
     */
    @Override
    public DescriptorType getDescriptorType() {
        return parent.getDescriptorType();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#getDescriptorVisibility()
     */
    @Override
    public DescriptorVisibility getDescriptorVisibility() {
        return parent.getDescriptorVisibility();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#getMetadata()
     */
    @Override
    public Map<String, List<String>> getMetadata() {
        return parent.getMetadata();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#getLoader()
     */
    @Override
    public HK2Loader getLoader() {
        return parent.getLoader();
    }
    
    private int ranking = 0;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#getRanking()
     */
    @Override
    public int getRanking() {
        return ranking;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#setRanking(int)
     */
    @Override
    public int setRanking(int ranking) {
        int retVal = ranking;
        this.ranking = ranking;
        return retVal;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#isProxiable()
     */
    @Override
    public Boolean isProxiable() {
        return parent.isProxiable();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#isProxyForSameScope()
     */
    @Override
    public Boolean isProxyForSameScope() {
        return parent.isProxyForSameScope();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#getClassAnalysisName()
     */
    @Override
    public String getClassAnalysisName() {
        return parent.getClassAnalysisName();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#getServiceId()
     */
    @Override
    public Long getServiceId() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#getLocatorId()
     */
    @Override
    public Long getLocatorId() {
        return null;
    }
    
    private Object lock = new Object();
    private Object cache;
    private boolean isSet = false;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.SingleCache#getCache()
     */
    @Override
    public Object getCache() {
        synchronized (lock) {
            return cache;
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.SingleCache#isCacheSet()
     */
    @Override
    public boolean isCacheSet() {
        synchronized (lock) {
            return isSet;
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.SingleCache#setCache(java.lang.Object)
     */
    @Override
    public void setCache(Object cacheMe) {
        synchronized (lock) {
            isSet = true;
            cache = cacheMe;
        }

    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.SingleCache#releaseCache()
     */
    @Override
    public void releaseCache() {
        synchronized (lock) {
            cache = null;
            isSet = false;
        }

    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ActiveDescriptor#isReified()
     */
    @Override
    public boolean isReified() {
        // But had BETTER be true
        return parent.isReified();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ActiveDescriptor#getImplementationClass()
     */
    @Override
    public Class<?> getImplementationClass() {
        return parent.getImplementationClass();
    }

    @Override
    public Type getImplementationType() {
        return parent.getImplementationType();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ActiveDescriptor#getContractTypes()
     */
    @Override
    public Set<Type> getContractTypes() {
        return parent.getContractTypes();
    }
    
    @Override
    public Annotation getScopeAsAnnotation() {
        return parent.getScopeAsAnnotation();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ActiveDescriptor#getScopeAnnotation()
     */
    @Override
    public Class<? extends Annotation> getScopeAnnotation() {
        return parent.getScopeAnnotation();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ActiveDescriptor#getQualifierAnnotations()
     */
    @Override
    public Set<Annotation> getQualifierAnnotations() {
        return qualifiers;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ActiveDescriptor#getInjectees()
     */
    @Override
    public List<Injectee> getInjectees() {
        return parent.getInjectees();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ActiveDescriptor#getFactoryServiceId()
     */
    @Override
    public Long getFactoryServiceId() {
        return parent.getFactoryServiceId();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ActiveDescriptor#getFactoryLocatorId()
     */
    @Override
    public Long getFactoryLocatorId() {
        return parent.getFactoryLocatorId();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ActiveDescriptor#create(org.glassfish.hk2.api.ServiceHandle)
     */
    @Override
    public Object create(ServiceHandle<?> root) {
        return parent.create(root);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ActiveDescriptor#dispose(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void dispose(Object instance) {
        ((ActiveDescriptor<Object>) parent).dispose(instance);

    }

}
