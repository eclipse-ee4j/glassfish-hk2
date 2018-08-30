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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.glassfish.hk2.utilities.reflection.ClassReflectionHelper;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;
import org.glassfish.hk2.xml.internal.alt.AltAnnotation;
import org.glassfish.hk2.xml.internal.alt.AltClass;
import org.glassfish.hk2.xml.internal.alt.AltMethod;
import org.glassfish.hk2.xml.internal.alt.MethodInformationI;

/**
 * @author jwells
 *
 */
public class MethodAltMethodImpl implements AltMethod {
    private final Method method;
    private final ClassReflectionHelper helper;
    private List<AltClass> parameterTypes;
    private List<AltAnnotation> altAnnotations;
    private MethodInformationI methodInformation;
    
    public MethodAltMethodImpl(Method method, ClassReflectionHelper helper) {
        this.method = method;
        this.helper = helper;
    }
    
    public Method getOriginalMethod() {
        return method;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.AltMethod#getName()
     */
    @Override
    public String getName() {
        return method.getName();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.AltMethod#getReturnType()
     */
    @Override
    public AltClass getReturnType() {
        Class<?> retVal = method.getReturnType();
        if (retVal == null) retVal = void.class;
        
        return new ClassAltClassImpl(retVal, helper);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.AltMethod#getParameterTypes()
     */
    @Override
    public synchronized List<AltClass> getParameterTypes() {
        if (parameterTypes != null) return parameterTypes;
        
        Class<?> pTypes[] = method.getParameterTypes();
        List<AltClass> retVal = new ArrayList<AltClass>(pTypes.length);
        
        for (Class<?> pType : pTypes) {
            retVal.add(new ClassAltClassImpl(pType, helper));
        }
        
        parameterTypes = Collections.unmodifiableList(retVal);
        return parameterTypes;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.AltMethod#getFirstTypeArgument()
     */
    @Override
    public AltClass getFirstTypeArgument() {
        Type type = method.getGenericReturnType();
        if (type == null) return null;
        
        Type first = ReflectionHelper.getFirstTypeArgument(type);
        if (first == null) return null;
        
        Class<?> retVal = ReflectionHelper.getRawClass(first);
        if (retVal == null) return null;
        
        return new ClassAltClassImpl(retVal, helper);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.AltMethod#getFirstTypeArgumentOfParameter(int)
     */
    @Override
    public AltClass getFirstTypeArgumentOfParameter(int index) {
        Type pTypes[] = method.getGenericParameterTypes();
        Type pType = pTypes[index];
        
        Type first = ReflectionHelper.getFirstTypeArgument(pType);
        if (first == null) return null;
        
        Class<?> retVal = ReflectionHelper.getRawClass(first);
        if (retVal == null) return null;
        
        return new ClassAltClassImpl(retVal, helper);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.AltMethod#getAnnotation(java.lang.String)
     */
    @Override
    public AltAnnotation getAnnotation(String annotation) {
        if (annotation == null) return null;
        
        Annotation annotations[] = method.getAnnotations();
        
        for (Annotation anno : annotations) {
            if (annotation.equals(anno.annotationType().getName())) {
                return new AnnotationAltAnnotationImpl(anno, helper);
            }
        }
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.AltMethod#getAnnotations()
     */
    @Override
    public synchronized List<AltAnnotation> getAnnotations() {
        if (altAnnotations != null) return altAnnotations;
        
        Annotation annotations[] = method.getAnnotations();
        ArrayList<AltAnnotation> retVal = new ArrayList<AltAnnotation>(annotations.length);
        
        for (Annotation annotation : annotations) {
            retVal.add(new AnnotationAltAnnotationImpl(annotation, helper));
        }
        
        altAnnotations = Collections.unmodifiableList(retVal);
        return altAnnotations;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.internal.alt.AltMethod#isVarArgs()
     */
    @Override
    public boolean isVarArgs() {
        return method.isVarArgs();
    }
    
    @Override
    public void setMethodInformation(MethodInformationI methodInfo) {
        methodInformation = methodInfo;
        
    }

    @Override
    public MethodInformationI getMethodInformation() {
        return methodInformation;
    }
    
    @Override
    public String toString() {
        return "MethodAltMethodImpl(" + method + "," + System.identityHashCode(this) + ")";
    }

    
}
