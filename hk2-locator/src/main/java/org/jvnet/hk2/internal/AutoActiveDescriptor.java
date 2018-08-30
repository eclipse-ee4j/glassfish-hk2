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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;

/**
 * @author jwells
 * @param <T> The type from the cache
 *
 */
public class AutoActiveDescriptor<T> extends AbstractActiveDescriptor<T> {
    /**
     * For serialization
     */
    private static final long serialVersionUID = -7921574114250721537L;
    private Class<?> implClass;
    private Creator<T> creator;
    private SystemDescriptor<?> hk2Parent;
    private Type implType;
    
    /**
     * For serialization
     */
    public AutoActiveDescriptor() {
        super();
    }
    
    /**
     * Constructor with all relevant fields
     * 
     * @param clazz The class of the implementation
     * @param creator The creator to use (factory or clazz)
     * @param advertisedContracts The set of advertised contracts
     * @param scope The scope of the service
     * @param name The name of the service (may be null)
     * @param qualifiers The set of qualifier annotations
     * @param descriptorVisibility The visibility of this descriptor
     * @param ranking The initial rank
     * @param proxy Whether or not this can be proxied (null for default)
     * @param proxyForSameScope Whether or not to proxy within the same scope (null for default)
     * @param classAnalysisName The name of the class analyzer (null for default)
     * @param metadata The set of metadata associated with this descriptor
     * @param descriptorType The type of the descriptor
     */
    public AutoActiveDescriptor(
            Class<?> clazz,
            Creator<T> creator,
            Set<Type> advertisedContracts,
            Class<? extends Annotation> scope, String name,
            Set<Annotation> qualifiers,
            DescriptorVisibility descriptorVisibility,
            int ranking,
            Boolean proxy,
            Boolean proxyForSameScope,
            String classAnalysisName,
            Map<String, List<String>> metadata,
            DescriptorType descriptorType,
            Type clazzType) {
        super(advertisedContracts,
                scope,
                name,
                qualifiers,
                DescriptorType.CLASS,
                descriptorVisibility,
                ranking,
                proxy,
                proxyForSameScope,
                classAnalysisName,
                metadata);
        
        implClass = clazz;
        this.creator = creator;
        
        setImplementation(implClass.getName());
        setDescriptorType(descriptorType);
        
        if (clazzType == null) {
            implType = clazz;
        }
        else {
            implType = clazzType;
        }
    }
    
    /* package */ void resetSelfDescriptor(ActiveDescriptor<?> toMe) {
        if (!(creator instanceof ClazzCreator)) return;
        ClazzCreator<?> cc = (ClazzCreator<?>) creator;
        
        cc.resetSelfDescriptor(toMe);
    }
    
    /* package */ void setHK2Parent(SystemDescriptor<?> hk2Parent) {
        this.hk2Parent = hk2Parent;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ActiveDescriptor#getImplementationClass()
     */
    @Override
    public Class<?> getImplementationClass() {
        return implClass;
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ActiveDescriptor#getImplementationType()
     */
    @Override
    public Type getImplementationType() {
        return implType;
    }
    
    @Override
    public void setImplementationType(Type t) {
        this.implType = t;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ActiveDescriptor#create(org.glassfish.hk2.api.ServiceHandle)
     */
    @Override
    public T create(ServiceHandle<?> root) {
        return creator.create(root, hk2Parent);
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ActiveDescriptor#dispose(java.lang.Object, org.glassfish.hk2.api.ServiceHandle)
     */
    @Override
    public void dispose(T instance) {
        creator.dispose(instance);
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ActiveDescriptor#getInjectees()
     */
    @Override
    public List<Injectee> getInjectees() {
        return creator.getInjectees();
    }

    
}
