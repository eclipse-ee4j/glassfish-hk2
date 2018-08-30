/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.xml.internal.alt.clazz;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.glassfish.hk2.utilities.reflection.ClassReflectionHelper;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;
import org.glassfish.hk2.utilities.reflection.internal.ClassReflectionHelperImpl;
import org.glassfish.hk2.xml.internal.alt.AltAnnotation;
import org.glassfish.hk2.xml.internal.alt.AltClass;
import org.glassfish.hk2.xml.internal.alt.AltEnum;
import org.glassfish.hk2.xml.jaxb.internal.XmlElementImpl;

/**
 * @author jwells
 *
 */
public class AnnotationAltAnnotationImpl implements AltAnnotation {
    private final static Set<String> DO_NOT_HANDLE_METHODS = new HashSet<String>();
    static {
        DO_NOT_HANDLE_METHODS.add("hashCode");
        DO_NOT_HANDLE_METHODS.add("equals");
        DO_NOT_HANDLE_METHODS.add("toString");
        DO_NOT_HANDLE_METHODS.add("annotationType");
    }
    
    private final Annotation annotation;
    private final ClassReflectionHelper helper;
    private Map<String, Object> values;
    
    public AnnotationAltAnnotationImpl(Annotation annotation, ClassReflectionHelper helper) {
        this.annotation = annotation;
        if (helper == null) {
            this.helper = new ClassReflectionHelperImpl();
        }
        else {
            this.helper = helper;
        }
    }
    
    public Annotation getOriginalAnnotation() {
        return annotation;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.AltAnnotation#annotationType()
     */
    @Override
    public String annotationType() {
        return annotation.annotationType().getName();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.AltAnnotation#getStringValue(java.lang.String)
     */
    @Override
    public synchronized String getStringValue(String methodName) {
        if (values == null) getAnnotationValues();
        
        if (XmlElementImpl.class.equals(annotation.getClass()) &&
                "getTypeByName".equals(methodName)) {
            XmlElementImpl xei = (XmlElementImpl) annotation;
            return xei.getTypeByName();
        }
        
        return (String) values.get(methodName);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.AltAnnotation#getBooleanValue(java.lang.String)
     */
    @Override
    public synchronized boolean getBooleanValue(String methodName) {
        if (values == null) getAnnotationValues();
        
        return (Boolean) values.get(methodName);
    }
    
    @Override
    public synchronized String[] getStringArrayValue(String methodName) {
        if (values == null) getAnnotationValues();
        
        return (String[]) values.get(methodName);
    }
    
    @Override
    public AltAnnotation[] getAnnotationArrayValue(String methodName) {
        if (values == null) getAnnotationValues();
        
        return (AltAnnotation[]) values.get(methodName);
    }
    
    @Override
    public AltClass getClassValue(String methodName) {
        if (values == null) getAnnotationValues();
        
        return (AltClass) values.get(methodName);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.AltAnnotation#getAnnotationValues()
     */
    @Override
    public synchronized Map<String, Object> getAnnotationValues() {
        if (values != null) return values;
        
        Map<String, Object> retVal = new TreeMap<String, Object>();
        for (Method javaAnnotationMethod : annotation.annotationType().getMethods()) {
            if (javaAnnotationMethod.getParameterTypes().length != 0) continue;
            if (DO_NOT_HANDLE_METHODS.contains(javaAnnotationMethod.getName())) continue;
            
            String key = javaAnnotationMethod.getName();
            
            Object value;
            try {
                value = ReflectionHelper.invoke(annotation, javaAnnotationMethod, new Object[0], false);
                
                if (value == null) {
                    throw new AssertionError("Recieved null from annotation method " + javaAnnotationMethod.getName());
                }
            }
            catch (RuntimeException re) {
                throw re;
            }
            catch (Throwable th) {
                throw new RuntimeException(th);
            }
            
            if (value instanceof Class) {
                value = new ClassAltClassImpl((Class<?>) value, helper);
            }
            else if (Enum.class.isAssignableFrom(value.getClass())) {
                value = new EnumAltEnumImpl((Enum<?>) value);
            }
            else if (value.getClass().isArray() && Class.class.equals(value.getClass().getComponentType())) {
                Class<?> cValue[] = (Class<?>[]) value;
                
                AltClass[] translatedValue = new AltClass[cValue.length];
                
                for (int lcv = 0; lcv < cValue.length; lcv++) {
                    translatedValue[lcv] = new ClassAltClassImpl(cValue[lcv], helper);
                }
                
                value = translatedValue;
            }
            else if (value.getClass().isArray() && Enum.class.isAssignableFrom(value.getClass().getComponentType())) {
                Enum<?> eValue[] = (Enum<?>[]) value;
                
                AltEnum[] translatedValue = new AltEnum[eValue.length];
                
                for (int lcv = 0; lcv < eValue.length; lcv++) {
                    translatedValue[lcv] = new EnumAltEnumImpl(eValue[lcv]);
                }
                
                value = translatedValue;
            }
            else if (value.getClass().isArray() && Annotation.class.isAssignableFrom(value.getClass().getComponentType())) {
                Annotation aValue[] = (Annotation[]) value;
                
                AltAnnotation[] translatedValue = new AltAnnotation[aValue.length];
                
                for (int lcv = 0; lcv < aValue.length; lcv++) {
                    translatedValue[lcv] = new AnnotationAltAnnotationImpl(aValue[lcv], helper);
                }
                
                value = translatedValue;
            }
            
            retVal.put(key, value);
        }
        
        values = Collections.unmodifiableMap(retVal);
        return values;
    }
    
    @Override
    public String toString() {
        return "AnnotationAltAnnotationImpl(" + annotation + "," + System.identityHashCode(this) + ")";
    }

    

    
}
