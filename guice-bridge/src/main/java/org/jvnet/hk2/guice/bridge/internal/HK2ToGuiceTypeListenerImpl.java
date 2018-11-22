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

import com.google.inject.BindingAnnotation;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.LinkedList;

import javax.inject.Qualifier;

import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.jvnet.hk2.guice.bridge.api.HK2Inject;
import org.jvnet.hk2.guice.bridge.api.HK2IntoGuiceBridge;

import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * @author jwells
 *
 */
public class HK2ToGuiceTypeListenerImpl implements TypeListener {
    private final ServiceLocator locator;
    
    /**
     * Creates the {@link HK2IntoGuiceBridge} TypeLocator that must
     * be bound into the Module with a call to bindListener.  The
     * ServiceLocator will be consulted at this time for any types
     * Guice cannot find.  If this type is found in the ServiceLocator
     * then that service will be instantiated by hk2
     * 
     * @param locator The non-null locator that should be used to discover
     * services
     */
    public HK2ToGuiceTypeListenerImpl(ServiceLocator locator) {
        this.locator = locator;
    }
    
    private static boolean isQualifier(Annotation anno) {
        Class<? extends Annotation> annoClass = anno.annotationType();
        
        return annoClass.isAnnotationPresent(Qualifier.class) || annoClass.isAnnotationPresent(BindingAnnotation.class);
        
    }

    /* (non-Javadoc)
     * @see com.google.inject.spi.TypeListener#hear(com.google.inject.TypeLiteral, com.google.inject.spi.TypeEncounter)
     */
    @Override
    public <I> void hear(TypeLiteral<I> literal, TypeEncounter<I> encounter) {
        Class<?> clazz = literal.getRawType();
        
        HashSet<String> dupFinder = new HashSet<String>();
        
        Class<?> walkingClass = clazz;
        while (walkingClass != null) {
            for (Field field : walkingClass.getDeclaredFields()) {
                if (dupFinder.contains(field.getName())) {
                    continue;
                }
                dupFinder.add(field.getName());
                
                if (!field.isAnnotationPresent(HK2Inject.class)) {
                    continue;
                }
                
                LinkedList<Annotation> qualifiers = new LinkedList<Annotation>();
                for (Annotation anno : field.getAnnotations()) {
                    if (!isQualifier(anno)) continue;
                    
                    qualifiers.add(anno);
                }
                
                encounter.register(new HK2FieldInjector<Object>(locator,
                        field.getGenericType(),
                        qualifiers.toArray(new Annotation[qualifiers.size()]),
                        field));
            }
            
            walkingClass = walkingClass.getSuperclass();
        }

    }

    private static class HK2FieldInjector<T> implements MembersInjector<T> {
        private final ServiceLocator locator;
        private final Type requiredType;
        private final Annotation qualifiers[];
        private final Field field;
        
        private HK2FieldInjector(ServiceLocator locator, Type requiredType, Annotation qualifiers[], Field field) {
            this.locator = locator;
            this.requiredType = requiredType;
            this.qualifiers = qualifiers;
            this.field = field;
            
            field.setAccessible(true);
        }

        @Override
        public void injectMembers(T arg0) {
            ServiceHandle<?> handle = locator.getServiceHandle(requiredType, qualifiers);
            if (handle == null) {
                throw new IllegalStateException("Could not find a service of type " +
                    requiredType);
            }
            
            Object injectMe = handle.getService();
            
            try {
                field.set(arg0, injectMe);
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            
            
        }
        
    }
}
