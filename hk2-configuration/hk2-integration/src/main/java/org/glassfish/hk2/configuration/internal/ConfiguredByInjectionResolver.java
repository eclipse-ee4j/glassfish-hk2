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
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.Visibility;
import org.glassfish.hk2.configuration.api.Configured;

/**
 * @author jwells
 *
 */
@Singleton
@Visibility(DescriptorVisibility.LOCAL)
public class ConfiguredByInjectionResolver implements
        InjectionResolver<Configured> {
    @Inject @Named(InjectionResolver.SYSTEM_RESOLVER_NAME)
    private InjectionResolver<Inject> systemResolver;
    
    @Inject
    private ConfiguredByContext context;
    
    private final ConcurrentHashMap<ActiveDescriptor<?>, BeanInfo> beanMap = new ConcurrentHashMap<ActiveDescriptor<?>, BeanInfo>(); 
    
    private static String getParameterNameFromConstructor(Constructor<?> cnst, int position) {
        Annotation paramAnnotations[] = cnst.getParameterAnnotations()[position];
        
        Configured c = null;
        for (Annotation anno : paramAnnotations) {
            if (Configured.class.equals(anno.annotationType())) {
                c = (Configured) anno;
                break;
            }
        }
        if (c == null) return null;
        
        String key = c.value();
        if (BeanUtilities.isEmpty(key)) {
            throw new AssertionError("Not enough in @Configured annotation in constructor " + cnst + " at parameter index " + position);
        }
        
        return key;
    }
    
    private static String getParameterNameFromMethod(Method method, int position) {
        Annotation paramAnnotations[] = method.getParameterAnnotations()[position];
        
        Configured c = null;
        for (Annotation anno : paramAnnotations) {
            if (Configured.class.equals(anno.annotationType())) {
                c = (Configured) anno;
                break;
            }
        }
        if (c == null) return null;
        
        String key = c.value();
        if (BeanUtilities.isEmpty(key)) {
            throw new AssertionError("Not enough in @Configured annotation in method " + method + " at parameter index " + position);
        }
        
        return key;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InjectionResolver#resolve(org.glassfish.hk2.api.Injectee, org.glassfish.hk2.api.ServiceHandle)
     */
    @Override
    public synchronized Object resolve(Injectee injectee, ServiceHandle<?> root) {
        ActiveDescriptor<?> injecteeParent = injectee.getInjecteeDescriptor();
        if (injecteeParent == null) return systemResolver.resolve(injectee, root);
        
        AnnotatedElement ae = injectee.getParent();
        if (ae == null) return systemResolver.resolve(injectee, root);
        
        String parameterName = null;
        if (ae instanceof Field) {
            parameterName = BeanUtilities.getParameterNameFromField((Field) ae, false);
        }
        else if (ae instanceof Constructor) {
            parameterName = getParameterNameFromConstructor((Constructor<?>) ae, injectee.getPosition());
        }
        else if (ae instanceof Method){
            parameterName = getParameterNameFromMethod((Method) ae, injectee.getPosition());
        }
        else {
            return systemResolver.resolve(injectee, root);
        }
        
        if (parameterName == null) return systemResolver.resolve(injectee, root);
        
        ActiveDescriptor<?> workingOn = context.getWorkingOn();
        if (workingOn == null) return systemResolver.resolve(injectee, root);
        
        BeanInfo beanInfo = beanMap.get(workingOn);
        if (beanInfo == null) {
            throw new IllegalStateException("Could not find a configuration bean for " + injectee + " with descriptor " + workingOn);
        }
        
        return BeanUtilities.getBeanPropertyValue(injectee.getRequiredType(), parameterName, beanInfo);
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
    
    /* package */ synchronized BeanInfo addBean(ActiveDescriptor<?> descriptor, Object bean, String type, Object metadata) {
        BeanInfo retVal = new BeanInfo(type, descriptor.getName(), bean, metadata);
        beanMap.put(descriptor, retVal);
        return retVal;
    }
    
    /* package */ synchronized void removeBean(ActiveDescriptor<?> descriptor) {
        beanMap.remove(descriptor);
    }
    
    @Override
    public String toString() {
        return "ConfiguredByInjectionResolver(" + System.identityHashCode(this) + ")";
    }
}
