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

package org.jvnet.hk2.guice.bridge.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;
import org.jvnet.hk2.guice.bridge.api.GuiceScope;

import com.google.inject.Binding;

/**
 *
 * @param <T> The cache type
 * @author jwells
 */
public class GuiceServiceHk2Bean<T> extends AbstractActiveDescriptor<T> {
    /**
     * For serialization
     */
    private static final long serialVersionUID = 4339256124914729858L;
    
    private Class<?> implClass = null;
    private Binding<T> binding = null;
    
    /**
     * For serialization
     */
    public GuiceServiceHk2Bean() {
    }
    
    /* package */ GuiceServiceHk2Bean(
            Set<Type> contracts,
            Set<Annotation> qualifiers,
            Class<?> implClass,
            Binding<T> binding) {
        super(contracts,
                GuiceScope.class,
                ReflectionHelper.getNameFromAllQualifiers(qualifiers, implClass),
                qualifiers,
                DescriptorType.CLASS,
                DescriptorVisibility.NORMAL,
                0,
                false,
                null,
                (String) null,
                new HashMap<String, List<String>>()
               );
        
        this.implClass = implClass;
        super.setImplementation(implClass.getName());
        
        this.binding = binding;
    }

    @Override
    public Class<?> getImplementationClass() {
        return implClass;
    }

    @Override
    public Type getImplementationType() {
        return implClass;
    }

    @Override
    public T create(ServiceHandle<?> root) {
        T retVal = binding.getProvider().get();
        return retVal;
    }
    
    @Override
    public String toString() {
        return "GuiceServiceHk2Bean( " + super.toString() + ")";
    }

}
