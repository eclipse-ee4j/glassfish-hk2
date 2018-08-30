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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Named;
import javax.inject.Singleton;

import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.JustInTimeInjectionResolver;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.springframework.beans.factory.BeanFactory;

/**
 * @author jwells
 *
 */
@Singleton
public class SpringToHK2JITResolver implements JustInTimeInjectionResolver {
    private final ServiceLocator locator;
    private final BeanFactory beanFactory;
    
    /* package */ SpringToHK2JITResolver(ServiceLocator locator, BeanFactory beanFactory) {
        this.locator = locator;
        this.beanFactory = beanFactory;
    }
    
    private void addMe(Class<?> lookForMe, String name, Injectee injectee) {
        HashSet<Type> contracts = new HashSet<Type>();
        contracts.add(injectee.getRequiredType());
        
        Set<Annotation> qualifiers = new HashSet<Annotation>(injectee.getRequiredQualifiers());
        
        SpringServiceHK2Bean<Object> springHK2Bean = new SpringServiceHK2Bean<Object>(
                name,
                contracts,
                qualifiers,
                lookForMe,
                beanFactory);
        
        ServiceLocatorUtilities.addOneDescriptor(locator, springHK2Bean, false);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.JustInTimeInjectionResolver#justInTimeResolution(org.glassfish.hk2.api.Injectee)
     */
    @Override
    public boolean justInTimeResolution(Injectee failedInjectionPoint) {
        Class<?> lookForMe = getClassFromType(failedInjectionPoint.getRequiredType());
        String name = getName(failedInjectionPoint);
        
        try {
            if (name != null) {
                if (beanFactory.containsBean(name) && beanFactory.isTypeMatch(name, lookForMe)) {
                    addMe(lookForMe, name, failedInjectionPoint);
                    return true;
                }
            }
            else {
                if (beanFactory.getBean(lookForMe) != null) {
                    addMe(lookForMe, null, failedInjectionPoint);
                    return true;
                }
                
            }
        }
        catch (Throwable th) {
            return false;
        }
        
        return false;
    }
    
    private static String getName(Injectee injectee) {
        for (Annotation anno : injectee.getRequiredQualifiers()) {
            if (Named.class.equals(anno.annotationType())) {
                Named named = (Named) anno;
                
                return named.value();
            }
            
        }
        
        return null;
    }
    
    /**
     * Gets the class from the given type
     * 
     * @param type The type to find the class from
     * @return The class associated with this type, or null
     * if the class cannot be found
     */
    private static Class<?> getClassFromType(Type type) {
        if (type instanceof Class) return (Class<?>) type;
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            
            return (Class<?>) pt.getRawType();
        }
        
        return null;
    }

}
