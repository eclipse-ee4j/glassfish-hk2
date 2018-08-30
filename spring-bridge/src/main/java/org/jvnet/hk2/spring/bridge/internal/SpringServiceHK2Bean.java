/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.jvnet.hk2.spring.bridge.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;
import org.jvnet.hk2.spring.bridge.api.SpringScope;
import org.springframework.beans.factory.BeanFactory;

/**
 * @param <T> Type of cache
 *  
 * @author jwells
 *
 */
public class SpringServiceHK2Bean<T> extends AbstractActiveDescriptor<T> {
    private Class<?> byType;
    private BeanFactory factory;
    
    /**
     * For serialization
     */
    public SpringServiceHK2Bean() {
        
    }
    
    /* package */ SpringServiceHK2Bean(
            String name,
            Set<Type> contracts,
            Set<Annotation> qualifiers,
            Class<?> byType,
            BeanFactory factory) {
        super(contracts,
                SpringScope.class,
                name,
                qualifiers,
                DescriptorType.CLASS,
                DescriptorVisibility.NORMAL,
                0,
                false,
                null,
                (String) null,
                new HashMap<String, List<String>>()
               );
        
        this.byType = byType;
        super.setImplementation(byType.getName());
        this.factory = factory;
    }

    @Override
    public Class<?> getImplementationClass() {
        return byType;
    }

    @Override
    public Type getImplementationType() {
        return byType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T create(ServiceHandle<?> root) {
        if (getName() != null) {
            return (T) factory.getBean(getName(), byType);
        }
        
        return (T) factory.getBean(byType);
    }

}
