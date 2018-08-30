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

package org.glassfish.hk2.tests.locator.locator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;

/**
 * @author jwells
 *
 */
public class ForeignActiveDescriptor<T> extends AbstractActiveDescriptor<T> {
    /**
     * 
     */
    private static final long serialVersionUID = -6841153780662164886L;
    
    private final Class<?> implClass;

    /**
     * @param advertisedContracts
     * @param scope
     * @param name
     * @param qualifiers
     * @param descriptorType
     * @param ranking
     */
    protected ForeignActiveDescriptor(Set<Type> advertisedContracts,
            Class<? extends Annotation> scope, String name,
            Set<Annotation> qualifiers, DescriptorType descriptorType,
            DescriptorVisibility descriptorVisibility,
            int ranking,
            Class<?> implClass) {
        super(advertisedContracts, scope, name, qualifiers, descriptorType, descriptorVisibility, ranking, null, null, null, null);
        
        this.implClass = implClass;
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ActiveDescriptor#isReified()
     */
    @Override
    public boolean isReified() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ActiveDescriptor#getImplementationClass()
     */
    @Override
    public Class<?> getImplementationClass() {
        return implClass;
    }
    
    @Override
    public Type getImplementationType() {
        return implClass;
    }
    
    @Override
    public void setImplementationType(Type t) {
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ActiveDescriptor#create(org.glassfish.hk2.api.ServiceHandle)
     */
    @SuppressWarnings("unchecked")
    @Override
    public T create(ServiceHandle<?> root) {
        try {
            return (T) implClass.newInstance();
        }
        catch (InstantiationException e) {
            throw new MultiException(e);
        }
        catch (IllegalAccessException e) {
            throw new MultiException(e);
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#getImplementation()
     */
    @Override
    public String getImplementation() {
        return implClass.getName();
    }

}
