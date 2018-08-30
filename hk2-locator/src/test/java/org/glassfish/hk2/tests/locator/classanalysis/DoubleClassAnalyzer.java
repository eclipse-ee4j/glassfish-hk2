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
import java.util.HashSet;
import java.util.Set;

import javax.inject.Named;
import javax.inject.Singleton;

import org.glassfish.hk2.api.ClassAnalyzer;
import org.glassfish.hk2.api.MultiException;

/**
 * This method picks the constructor that takes a double,
 * the fields that are doubles and the postConstruct
 * the methods that are doubles and the postConstruct
 * method must also be named "doublePostConstruct" and
 * "doublePreDestroy"
 * 
 * @author jwells
 *
 */
@Singleton @Named(DoubleClassAnalyzer.DOUBLE_ANALYZER)
public class DoubleClassAnalyzer implements ClassAnalyzer {
    public final static String DOUBLE_ANALYZER = "DoubleStrategy";
    
    private final static String POST_NAME = "doublePostConstruct";
    private final static String PRE_NAME = "doublePreDestroy";

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ClassAnalyzer#getConstructor(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> Constructor<T> getConstructor(Class<T> clazz)
            throws MultiException {
        
        Constructor<?> allConstructors[] = clazz.getConstructors();
        for (Constructor<?> aConstructor : allConstructors) {
            Class<?> params[] = aConstructor.getParameterTypes();
            if (params.length != 1) continue;
            
            if (!params[0].equals(Double.class)) continue;
            
            return (Constructor<T>) aConstructor;
        }
        
        throw new MultiException(new AssertionError("Could not find a constructor that takes a Double parameter"));
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ClassAnalyzer#getInitializerMethods(java.lang.Class)
     */
    @Override
    public <T> Set<Method> getInitializerMethods(Class<T> clazz)
            throws MultiException {
        HashSet<Method> retVal = new HashSet<Method>();
        
        Method allMethods[] = clazz.getMethods();
        for (Method aMethod : allMethods) {
            Class<?> params[] = aMethod.getParameterTypes();
            if (params.length < 1) continue;
            
            if (!params[0].equals(Double.class)) continue;
            
            retVal.add(aMethod);
        }
        
        return retVal;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ClassAnalyzer#getFields(java.lang.Class)
     */
    @Override
    public <T> Set<Field> getFields(Class<T> clazz) throws MultiException {
        HashSet<Field> retVal = new HashSet<Field>();
        
        Field allFields[] = clazz.getFields();
        for (Field aField : allFields) {
            Class<?> fieldType = aField.getType();
            
            if (!fieldType.equals(Double.class)) continue;
            
            retVal.add(aField);
        }
        
        return retVal;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ClassAnalyzer#getPostConstructMethod(java.lang.Class)
     */
    @Override
    public <T> Method getPostConstructMethod(Class<T> clazz)
            throws MultiException {
        try {
            return clazz.getMethod(POST_NAME);
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ClassAnalyzer#getPreDestroyMethod(java.lang.Class)
     */
    @Override
    public <T> Method getPreDestroyMethod(Class<T> clazz) throws MultiException {
        try {
            return clazz.getMethod(PRE_NAME);
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }

}
