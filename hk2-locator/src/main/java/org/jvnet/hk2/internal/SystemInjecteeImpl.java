/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.jvnet.hk2.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.Set;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.Unqualified;
import org.glassfish.hk2.utilities.reflection.Pretty;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;

/**
 * This is an implementation of Injectee that is used by the system.
 * 
 * @author jwells
 *
 */
public class SystemInjecteeImpl implements Injectee {
    private final Type requiredType;
    private final Set<Annotation> qualifiers;
    private final int position;
    private final Class<?> pClass;
    private final AnnotatedElement parent;
    private final boolean isOptional;
    private final boolean isSelf;
    private final Unqualified unqualified;
    private ActiveDescriptor<?> injecteeDescriptor;
    
    private final Object parentIdentifier;
    
    /* package */ SystemInjecteeImpl(
            Type requiredType,
            Set<Annotation> qualifiers,
            int position,
            AnnotatedElement parent,
            boolean isOptional,
            boolean isSelf,
            Unqualified unqualified,
            ActiveDescriptor<?> injecteeDescriptor) {
        this.requiredType = requiredType;
        this.position = position;
        this.parent = parent;
        this.qualifiers = Collections.unmodifiableSet(qualifiers);
        this.isOptional = isOptional;
        this.isSelf = isSelf;
        this.unqualified = unqualified;
        this.injecteeDescriptor = injecteeDescriptor;
        
        if (parent instanceof Field) {
            pClass = ((Field) parent).getDeclaringClass();
            parentIdentifier = ((Field) parent).getName();
        }
        else if (parent instanceof Constructor) {
            pClass = ((Constructor<?>) parent).getDeclaringClass();
            parentIdentifier = pClass;
        }
        else {
            pClass = ((Method) parent).getDeclaringClass();
            parentIdentifier = ReflectionHelper.createMethodWrapper((Method) parent);
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Injectee#getRequiredType()
     */
    @Override
    public Type getRequiredType() {
        if ((requiredType instanceof TypeVariable) &&
                (injecteeDescriptor != null) &&
                (injecteeDescriptor.getImplementationType() != null) &&
                (injecteeDescriptor.getImplementationType() instanceof ParameterizedType)) {
            TypeVariable<?> tv = (TypeVariable<?>) requiredType;
            ParameterizedType pt = (ParameterizedType) injecteeDescriptor.getImplementationType();
            
            Type translatedRequiredType = ReflectionHelper.resolveKnownType(tv, pt, pClass);
            if (translatedRequiredType != null) {
                return translatedRequiredType;
            }
        }
        
        return requiredType;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Injectee#getRequiredQualifiers()
     */
    @Override
    public Set<Annotation> getRequiredQualifiers() {
        return qualifiers;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Injectee#getPosition()
     */
    @Override
    public int getPosition() {
        return position;
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Injectee#getInjecteeClass()
     */
    @Override
    public Class<?> getInjecteeClass() {
        return pClass;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Injectee#getParent()
     */
    @Override
    public AnnotatedElement getParent() {
        return parent;
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Injectee#isOptional()
     */
    @Override
    public boolean isOptional() {
        return isOptional;
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Injectee#isSelf()
     */
    @Override
    public boolean isSelf() {
        return isSelf;
    }
    
    @Override
    public Unqualified getUnqualified() {
        return unqualified;
    }
    
    @Override
    public ActiveDescriptor<?> getInjecteeDescriptor() {
        return injecteeDescriptor;
    }
    
    /* package */ void resetInjecteeDescriptor(ActiveDescriptor<?> injecteeDescriptor) {
        this.injecteeDescriptor = injecteeDescriptor;
    }
    
    @Override
    public int hashCode() {
        return position ^ parentIdentifier.hashCode() ^ pClass.hashCode() ;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof SystemInjecteeImpl)) return false;
        
        SystemInjecteeImpl other = (SystemInjecteeImpl) o;
        
        if (position != other.getPosition()) return false;
        if (!pClass.equals(other.getInjecteeClass())) return false;
        
        return parentIdentifier.equals(other.parentIdentifier);
    }
    
    @Override
    public String toString() {
        return "SystemInjecteeImpl(requiredType=" + Pretty.type(requiredType) +
                ",parent=" + Pretty.clazz(pClass) +
                ",qualifiers=" + Pretty.collection(qualifiers) +
                ",position=" + position +
                ",optional=" + isOptional +
                ",self=" + isSelf +
                ",unqualified=" + unqualified +
                "," + System.identityHashCode(this) + ")";
    }

    

    
}
