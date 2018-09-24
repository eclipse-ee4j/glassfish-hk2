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

package org.jvnet.hk2.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;

/**
 * @author jwells
 * @param <T> The type of the constant
 *
 */
public class ConstantActiveDescriptor<T> extends AbstractActiveDescriptor<T> {
    /**
     * For serialization
     */
    private static final long serialVersionUID = 3663054975929743877L;
    
    private T theOne;
    private Long locatorId;
    
    /**
     * For serialization
     */
    public ConstantActiveDescriptor() {
        super();
    }
    
    /**
     * Creates a constant active descriptor with the given locator
     * 
     * @param theOne the object to create it from
     * @param locator the locator this is being created for
     */
    public ConstantActiveDescriptor(T theOne, ServiceLocatorImpl locator) {
        super(new HashSet<Type>(),
                PerLookup.class,
                null,
                new HashSet<Annotation>(),
                DescriptorType.CLASS,
                DescriptorVisibility.NORMAL,
                0,
                null,
                null,
                locator.getPerLocatorUtilities().getAutoAnalyzerName(theOne.getClass()),
                null);
        
        this.theOne = theOne;
        this.locatorId = locator.getLocatorId();
    }
    
    /**
     * Constructor with more control over the fields of the descriptor
     *
     * @param theOne The non-null constant
     * @param advertisedContracts its advertised contracts
     * @param scope its scope
     * @param name its possibly null name
     * @param qualifiers its set of qualifiers
     * @param visibility its visibility
     * @param ranking its starting rank
     * @param proxy can it be proxied (null for default)
     * @param proxyForSameScope  will it be proxied for the same scope (null for default)
     * @param analyzerName The name of the analyzer (null for default)
     * @param locatorId its locator parent
     * @param metadata The metadata associated with it
     */
    public ConstantActiveDescriptor(T theOne,
            Set<Type> advertisedContracts,
            Class<? extends Annotation> scope,
            String name,
            Set<Annotation> qualifiers,
            DescriptorVisibility visibility,
            int ranking,
            Boolean proxy,
            Boolean proxyForSameScope,
            String analyzerName,
            long locatorId,
            Map<String, List<String>> metadata) {
        super(advertisedContracts,
                scope,
                name,
                qualifiers,
                DescriptorType.CLASS,
                visibility,
                ranking,
                proxy,
                proxyForSameScope,
                analyzerName,
                metadata);
        if (theOne == null) throw new IllegalArgumentException();
        
        this.theOne = theOne;
        this.locatorId = locatorId;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#getImplementation()
     */
    @Override
    public String getImplementation() {
        return theOne.getClass().getName();
    }
    
    public Long getLocatorId() {
        return locatorId;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.SingleCache#getCache()
     */
    @Override
    public T getCache() {
        return theOne;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.SingleCache#isCacheSet()
     */
    @Override
    public boolean isCacheSet() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ActiveDescriptor#getImplementationClass()
     */
    @Override
    public Class<?> getImplementationClass() {
        return theOne.getClass();
    }
    
    @Override
    public Type getImplementationType() {
        return theOne.getClass();
    }
    
    @Override
    public void setImplementationType(Type t) {
        throw new AssertionError("Can not set type of a constant descriptor");
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ActiveDescriptor#create(org.glassfish.hk2.api.ServiceHandle)
     */
    @Override
    public T create(ServiceHandle<?> root) {
        return theOne;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ActiveDescriptor#dispose(java.lang.Object, org.glassfish.hk2.api.ServiceHandle)
     */
    @Override
    public void dispose(T instance) {
        // Do nothing
        
    }

}
