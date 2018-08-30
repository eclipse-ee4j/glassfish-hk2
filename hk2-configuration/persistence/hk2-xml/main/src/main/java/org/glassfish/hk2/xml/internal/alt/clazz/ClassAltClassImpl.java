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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.glassfish.hk2.utilities.reflection.ClassReflectionHelper;
import org.glassfish.hk2.utilities.reflection.MethodWrapper;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;
import org.glassfish.hk2.utilities.reflection.internal.ClassReflectionHelperImpl;
import org.glassfish.hk2.xml.internal.alt.AltAnnotation;
import org.glassfish.hk2.xml.internal.alt.AltClass;
import org.glassfish.hk2.xml.internal.alt.AltMethod;

/**
 * @author jwells
 *
 */
public class ClassAltClassImpl implements AltClass {
    private static final ClassReflectionHelper SCALAR_HELPER = new ClassReflectionHelperImpl();
    public static final AltClass VOID = new ClassAltClassImpl(void.class, SCALAR_HELPER);
    public static final AltClass BOOLEAN = new ClassAltClassImpl(boolean.class, SCALAR_HELPER);
    public static final AltClass BYTE = new ClassAltClassImpl(byte.class, SCALAR_HELPER);
    public static final AltClass CHAR = new ClassAltClassImpl(char.class, SCALAR_HELPER);
    public static final AltClass SHORT = new ClassAltClassImpl(short.class, SCALAR_HELPER);
    public static final AltClass INT = new ClassAltClassImpl(int.class, SCALAR_HELPER);
    public static final AltClass LONG = new ClassAltClassImpl(long.class, SCALAR_HELPER);
    public static final AltClass FLOAT = new ClassAltClassImpl(float.class, SCALAR_HELPER);
    public static final AltClass DOUBLE = new ClassAltClassImpl(double.class, SCALAR_HELPER);
    public static final AltClass OBJECT = new ClassAltClassImpl(Object.class, SCALAR_HELPER);
    public static final AltClass XML_ADAPTER = new ClassAltClassImpl(XmlAdapter.class, SCALAR_HELPER);
    
    private final Class<?> clazz;
    private final ClassReflectionHelper helper;
    private List<AltMethod> methods;
    private List<AltAnnotation> annotations;
    
    public ClassAltClassImpl(Class<?> clazz, ClassReflectionHelper helper) {
        this.clazz = clazz;
        this.helper = helper;
    }
    
    public Class<?> getOriginalClass() {
        return clazz;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.AltClass#getName()
     */
    @Override
    public String getName() {
        return clazz.getName();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.AltClass#getSimpleName()
     */
    @Override
    public String getSimpleName() {
        return clazz.getSimpleName();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.AltClass#getAnnotations()
     */
    @Override
    public synchronized List<AltAnnotation> getAnnotations() {
        if (annotations != null) return annotations;
        
        Annotation annotationz[] = clazz.getAnnotations();
        
        ArrayList<AltAnnotation> retVal = new ArrayList<AltAnnotation>(annotationz.length);
        for (Annotation annotation : annotationz) {
            retVal.add(new AnnotationAltAnnotationImpl(annotation, helper));
        }
        
        annotations = Collections.unmodifiableList(retVal);
        return annotations;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.AltClass#getMethods()
     */
    @Override
    public synchronized List<AltMethod> getMethods() {
        if (methods != null) return methods;
        
        Set<MethodWrapper> wrappers = helper.getAllMethods(clazz);
        ArrayList<AltMethod> retVal = new ArrayList<AltMethod>(wrappers.size());
        
        for (MethodWrapper method : wrappers) {
            retVal.add(new MethodAltMethodImpl(method.getMethod(), helper));
        }
        
        methods = Collections.unmodifiableList(retVal);
        return methods;
    }
    
    @Override
    public AltClass getSuperParameterizedType(AltClass superType, int paramIndex) {
        Class<?> previousClazz = clazz;
        
        Class<?> superClass = previousClazz.getSuperclass();
        while (superClass != null) {
            if (superType.getName().equals(superClass.getName())) {
                Type genericType = previousClazz.getGenericSuperclass();
                if (!(genericType instanceof ParameterizedType)) {
                    return null;
                }
                
                ParameterizedType pt = (ParameterizedType) genericType;
                Type actualType = pt.getActualTypeArguments()[paramIndex];
                
                Class<?> rawType = ReflectionHelper.getRawClass(actualType);
                if (rawType == null) return null;
                
                return new ClassAltClassImpl(rawType, helper);
                
            }
            
            previousClazz = superClass;
            superClass = previousClazz.getSuperclass();
        }
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.AltClass#isInterface()
     */
    @Override
    public boolean isInterface() {
        return clazz.isInterface();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.AltClass#isArray()
     */
    @Override
    public boolean isArray() {
        return clazz.isArray();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.AltClass#getComponentType()
     */
    @Override
    public AltClass getComponentType() {
        Class<?> cType = clazz.getComponentType();
        if (cType == null) return null;
        
        return new ClassAltClassImpl(cType, helper);
    }
    
    @Override
    public int hashCode() {
        return getName().hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof AltClass)) return false;
        
        AltClass other = (AltClass) o;
        
        return getName().equals(other.getName());
    }

    @Override
    public String toString() {
        return "ClassAltClassImpl(" + clazz.getName() + "," + System.identityHashCode(this) + ")";
    }

    

    
}
