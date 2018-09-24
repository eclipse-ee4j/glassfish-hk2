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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

import org.glassfish.hk2.configuration.api.Configured;
import org.glassfish.hk2.configuration.api.Dynamicity;
import org.glassfish.hk2.utilities.reflection.TypeChecker;

/**
 * For JavaBean or Bean-Like-Map utilities
 * 
 * @author jwells
 *
 */
public class BeanUtilities {
    private final static String GET = "get";
    private final static String IS = "is";
    
    private static String firstUpper(String s) {
        if (s == null || s.length() <= 0) {
            return s;
        }
        
        char firstChar = Character.toUpperCase(s.charAt(0));
        
        return firstChar + s.substring(1);
    }
    
    /**
     * Gets the value from the given attribute from the given bean
     * Safe to give both a bean-like map and a java bean
     * 
     * @param requiredType the type the attribute must be
     * @param attribute the attribute to get. The bean must have a method
     * with the name format getAttribute
     * @param beanInfo the bean info
     * @return the value of the attribute
     * @throws IllegalStateException if unable to get the attribute
     */
    @SuppressWarnings("unchecked")
    public static Object getBeanPropertyValue(Type requiredType, String attribute, BeanInfo beanInfo) {
        if (Configured.BEAN_KEY.equals(attribute)) {
            Object bean = beanInfo.getBean();
            if (bean == null) return null;
            
            if (TypeChecker.isRawTypeSafe(requiredType, bean.getClass())) {
                return bean;
            }
              
            Object metadata = beanInfo.getMetadata();
            if (metadata != null &&
                TypeChecker.isRawTypeSafe(requiredType, metadata.getClass())) {
                return metadata;
            }
            
            // This isn't going to work, but the best shot is the bean itself
            return bean;
        }
        if (Configured.TYPE.equals(attribute)) return beanInfo.getTypeName();
        if (Configured.INSTANCE.equals(attribute)) return beanInfo.getInstanceName();
        
        Object bean = beanInfo.getBean();
        if (bean instanceof Map) {
            
            Map<String, Object> beanLikeMap = (Map<String, Object>) bean;
            return beanLikeMap.get(attribute);
        }
        
        attribute = firstUpper(attribute);
        
        String methodName = GET + attribute;
        
        Class<?> beanClass = bean.getClass();
        
        Method m = null;
        try {
            m = beanClass.getMethod(methodName, new Class[0]);
        }
        catch (NoSuchMethodException me) {
            methodName = IS + attribute;
            
            try {
                m = beanClass.getMethod(methodName, new Class[0]);
            }
            catch (NoSuchMethodException me2) {
                throw new IllegalArgumentException("The bean " + bean + " has no getter for attribute " + attribute);
            }
        }
        
        m.setAccessible(true);
        
        try {
            return m.invoke(bean, new Object[0]);
        }
        catch (InvocationTargetException e) {
            Throwable th = e.getTargetException();
            throw new IllegalStateException(th);
        }
        catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalStateException(e);
        }
        
    }
    
    private final static String EMPTY = "";
    
    public static boolean isEmpty(String s) {
        if (s == null) return true;
        return EMPTY.equals(s);
    }
    
    /**
     * Gets the parameter name from a field
     * 
     * @param f the field annotated with {@link Configured}
     * @param onlyDynamic if true and the {@link Configured} annotation
     * is not {@link Dynamicity#FULLY_DYNAMIC} the null will be returned
     * @return the value of the {@link Configured} annotation if non-empty
     * or the name of the field otherwise
     */
    public static String getParameterNameFromField(Field f, boolean onlyDynamic) {
        Configured c = f.getAnnotation(Configured.class);
        if (c == null) return null;
        
        if (onlyDynamic && !Dynamicity.FULLY_DYNAMIC.equals(c.dynamicity())) {
            return null;
        }
        
        String key = c.value();
        if (isEmpty(key)) {
            key = f.getName();
        }
        
        return key;
    }
    
    public static String getParameterNameFromMethod(Method m, int paramIndex) {
        Annotation annotations[] = m.getParameterAnnotations()[paramIndex];
        
        for (Annotation annotation : annotations) {
            if (Configured.class.equals(annotation.annotationType())) {
                Configured configured = (Configured) annotation;
                if (!Dynamicity.FULLY_DYNAMIC.equals(configured.dynamicity())) return null;
                
                String retVal = ((Configured) annotation).value();
                if (isEmpty(retVal)) return null;
                return retVal;
            }
        }
        
        return null;
    }
    
    public static boolean hasDynamicParameter(Method m) {
        for (Annotation annotations[] : m.getParameterAnnotations()) {
            for (Annotation annotation : annotations) {
                if (Configured.class.equals(annotation.annotationType())) {
                    Configured configured = (Configured) annotation;
                    
                    if (Dynamicity.FULLY_DYNAMIC.equals(configured.dynamicity())) return true;
                }
            }
        }
        
        return false;
    }

}
