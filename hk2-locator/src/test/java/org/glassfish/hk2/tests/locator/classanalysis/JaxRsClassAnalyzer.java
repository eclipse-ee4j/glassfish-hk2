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

package org.glassfish.hk2.tests.locator.classanalysis;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.glassfish.hk2.api.ClassAnalyzer;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.reflection.Logger;

/**
 * Implementation of the ClassAnalyzer that prefers the
 * largest number of parameters in the constructor over
 * the smallest.  Other than that it is exactly the
 * same as the default analyzer
 * 
 * @author jwells
 *
 */
@Singleton @Named(JaxRsClassAnalyzer.PREFER_LARGEST_CONSTRUCTOR)
public class JaxRsClassAnalyzer implements ClassAnalyzer {
    public final static String PREFER_LARGEST_CONSTRUCTOR = "PreferLargestConstructor";
    
    @Inject @Named(ClassAnalyzer.DEFAULT_IMPLEMENTATION_NAME)
    private ClassAnalyzer defaultAnalyzer;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ClassAnalyzer#getConstructor(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> Constructor<T> getConstructor(Class<T> clazz)
            throws MultiException, NoSuchMethodException {
        Constructor<T> retVal = null;
        try {
            retVal = defaultAnalyzer.getConstructor(clazz);
            
            Class<?> args[] = retVal.getParameterTypes();
            if (args.length != 0) return retVal;
            
            // Is zero length, but is it specifically marked?
            Inject i = retVal.getAnnotation(Inject.class);
            if (i != null) return retVal;
            
            // In this case, the default chose a zero-arg constructor since it could find no other
        }
        catch (NoSuchMethodException nsme) {
           // In this case, the default failed because it found no constructor it could use
        }
        
        // At this point, we simply need to find the constructor with the largest number of parameters
        Constructor<?> allCs[] = clazz.getDeclaredConstructors();
        List<Constructor<?>> allMaximums = new LinkedList<Constructor<?>>();
        int currentBestSize = -1;
        
        for (Constructor<?> candidate : allCs) {
            Class<?> params[] = candidate.getParameterTypes();
            if (params.length > currentBestSize) {
                currentBestSize = params.length;
                allMaximums.clear();
                
                allMaximums.add(candidate);
            }
            else if (params.length == currentBestSize) {
                allMaximums.add(candidate);
            }
        }
        
        if (allMaximums.isEmpty()) {
            // Is it possible to get here?
            throw new NoSuchMethodException("Could not find any constructors on " + clazz.getName());
        }
        
        return (Constructor<T>) allMaximums.get(0);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ClassAnalyzer#getInitializerMethods(java.lang.Class)
     */
    @Override
    public <T> Set<Method> getInitializerMethods(Class<T> clazz)
            throws MultiException {
        return defaultAnalyzer.getInitializerMethods(clazz);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ClassAnalyzer#getFields(java.lang.Class)
     */
    @Override
    public <T> Set<Field> getFields(Class<T> clazz) throws MultiException {
        return defaultAnalyzer.getFields(clazz);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ClassAnalyzer#getPostConstructMethod(java.lang.Class)
     */
    @Override
    public <T> Method getPostConstructMethod(Class<T> clazz)
            throws MultiException {
        return defaultAnalyzer.getPostConstructMethod(clazz);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ClassAnalyzer#getPreDestroyMethod(java.lang.Class)
     */
    @Override
    public <T> Method getPreDestroyMethod(Class<T> clazz) throws MultiException {
        return defaultAnalyzer.getPreDestroyMethod(clazz);
    }

}
