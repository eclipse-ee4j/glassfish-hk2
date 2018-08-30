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
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.Visibility;
import org.glassfish.hk2.configuration.api.ChildInject;
import org.glassfish.hk2.configuration.api.ChildIterable;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;

/**
 * @author jwells
 *
 */
@Singleton
@Visibility(DescriptorVisibility.LOCAL)
public class ChildInjectResolverImpl implements InjectionResolver<ChildInject> {
    @Inject
    private ServiceLocator locator;
    
    @Inject
    private InjectionResolver<Inject> systemResolver;
    
    @Inject
    private ConfiguredByContext configuredByContext;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InjectionResolver#resolve(org.glassfish.hk2.api.Injectee, org.glassfish.hk2.api.ServiceHandle)
     */
    @Override
    public Object resolve(Injectee injectee, ServiceHandle<?> root) {
        ActiveDescriptor<?> parentDescriptor = injectee.getInjecteeDescriptor();
        if (parentDescriptor == null) {
            // We give up, ask the normal resolver
            return systemResolver.resolve(injectee, root);
        }
        
        // Need to get the real descriptor, not the seed
        parentDescriptor = configuredByContext.getWorkingOn();
        if (parentDescriptor == null) {
            // We give up, ask the normal resolver
            return systemResolver.resolve(injectee, root);
        }
        
        Type requiredType = injectee.getRequiredType();
        Class<?> requiredClass = ReflectionHelper.getRawClass(requiredType);
        if (requiredClass == null) {
            return systemResolver.resolve(injectee, root);
        }
        
        ChildInject childInject = getInjectionAnnotation(injectee.getParent(), injectee.getPosition());
        String prefixName = parentDescriptor.getName();
        if (prefixName == null) prefixName = "";
        String separator = childInject.separator();
        
        prefixName = prefixName + childInject.value();
        
        if (ChildIterable.class.equals(requiredClass) && (requiredType instanceof ParameterizedType)) {
            ParameterizedType pt = (ParameterizedType) requiredType;
            
            // Replace the required type
            requiredType = pt.getActualTypeArguments()[0];
            requiredClass = ReflectionHelper.getRawClass(requiredType);
            if (requiredClass == null) {
                return systemResolver.resolve(injectee, root);
            }
            
            return new ChildIterableImpl<Object>(locator, requiredType, prefixName, separator);
        }
        
        List<ActiveDescriptor<?>> matches = locator.getDescriptors(new ChildFilter(requiredType, prefixName));
        
        if (matches.isEmpty()) {
            if (injectee.isOptional()) {
                return null;
            }
            
            throw new IllegalStateException("Could not find a child injection point for " + injectee);
        }
        
        return locator.getServiceHandle(matches.get(0)).getService();
    }
    
    private static ChildInject getInjectionAnnotation(AnnotatedElement element, int position) {
        if (element instanceof Field) {
            Field field = (Field) element;
            
            return field.getAnnotation(ChildInject.class);
        }
        
        Annotation annotations[];
        if (element instanceof Constructor) {
            Constructor<?> constructor = (Constructor<?>) element;
            
            annotations = constructor.getParameterAnnotations()[position];
        }
        else if (element instanceof Method) {
            Method method = (Method) element;
            
            annotations = method.getParameterAnnotations()[position];
        }
        else {
            throw new IllegalArgumentException();
        }
        
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(ChildInject.class)) {
                return (ChildInject) annotation;
            }
        }
        
        throw new IllegalArgumentException();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InjectionResolver#isConstructorParameterIndicator()
     */
    @Override
    public boolean isConstructorParameterIndicator() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InjectionResolver#isMethodParameterIndicator()
     */
    @Override
    public boolean isMethodParameterIndicator() {
        return true;
    }

}
