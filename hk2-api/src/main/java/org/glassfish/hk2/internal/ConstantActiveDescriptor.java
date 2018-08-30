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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.DescriptorVisibility;
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
    private static final long serialVersionUID = -9196390718074767455L;

    private final T theOne;

    /**
     * For serializable
     */
    public ConstantActiveDescriptor() {
        super();
        theOne = null;
    }

    /**
     * Creates the constant descriptor
     *
     * @param theOne May not be null
     * @param advertisedContracts
     * @param scope
     * @param name
     * @param qualifiers
     * @param descriptorVisibility 
     * @param proxy 
     * @param proxyForSameScope 
     * @param classAnalysisName 
     * @param metadata 
     * @param rank
     */
    public ConstantActiveDescriptor(T theOne,
            Set<Type> advertisedContracts,
            Class<? extends Annotation> scope,
            String name,
            Set<Annotation> qualifiers,
            DescriptorVisibility descriptorVisibility,
            Boolean proxy,
            Boolean proxyForSameScope,
            String classAnalysisName,
            Map<String, List<String>> metadata,
            int rank) {
        super(advertisedContracts,
                scope,
                name,
                qualifiers,
                DescriptorType.CLASS,
                descriptorVisibility,
                rank,
                proxy,
                proxyForSameScope,
                classAnalysisName,
                metadata);
        if (theOne == null) throw new IllegalArgumentException();

        this.theOne = theOne;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#getImplementation()
     */
    @Override
    public String getImplementation() {
        return theOne.getClass().getName();
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

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ActiveDescriptor#create(org.glassfish.hk2.api.ServiceHandle)
     */
    @Override
    public T create(ServiceHandle<?> root) {
        return theOne;
    }
}
