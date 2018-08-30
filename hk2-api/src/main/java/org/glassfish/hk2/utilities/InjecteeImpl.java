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

package org.glassfish.hk2.utilities;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.Unqualified;
import org.glassfish.hk2.utilities.reflection.Pretty;

/**
 * This is a writeable version of the Injectee interface.  Using this
 * implementation may make your code more portable, as new methods
 * added to the interface will be reflected in this class.
 * 
 * @author jwells
 *
 */
public class InjecteeImpl implements Injectee {
    private Type requiredType;
    private Set<Annotation> qualifiers;
    private int position = -1;
    private Class<?> pClass;
    private AnnotatedElement parent;
    private boolean isOptional = false;
    private boolean isSelf = false;
    private Unqualified unqualified = null;
    private ActiveDescriptor<?> injecteeDescriptor;
    
    /**
     * None of the fields of the returned object will be set
     */
    public InjecteeImpl() {
    }
    
    /**
     * Only the requiredType field will be set
     * 
     * @param requiredType The possibly null required type
     */
    public InjecteeImpl(Type requiredType) {
        this.requiredType = requiredType;
    }
    
    /**
     * This is the copy constructor, which will copy all the values from the incoming Injectee
     * @param copyMe The non-null Injectee to copy the values from
     */
    public InjecteeImpl(Injectee copyMe) {
        requiredType = copyMe.getRequiredType();
        position = copyMe.getPosition();
        parent = copyMe.getParent();
        qualifiers = Collections.unmodifiableSet(copyMe.getRequiredQualifiers());
        isOptional = copyMe.isOptional();
        isSelf = copyMe.isSelf();
        injecteeDescriptor = copyMe.getInjecteeDescriptor();
        // unqualified = copyMe.getUnqualified();
        
        if (parent == null) {
            pClass = null;
        }
        else if (parent instanceof Field) {
            pClass = ((Field) parent).getDeclaringClass();
        }
        else if (parent instanceof Constructor) {
            pClass = ((Constructor<?>) parent).getDeclaringClass();
        }
        else if (parent instanceof Method) {
            pClass = ((Method) parent).getDeclaringClass();
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Injectee#getRequiredType()
     */
    @Override
    public Type getRequiredType() {
        return requiredType;
    }
    
    /**
     * Sets the required type of this Injectee
     * @param requiredType The required type of this injectee
     */
    public void setRequiredType(Type requiredType) {
        this.requiredType = requiredType;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Injectee#getRequiredQualifiers()
     */
    @Override
    public Set<Annotation> getRequiredQualifiers() {
        if (qualifiers == null) return Collections.emptySet();
        return qualifiers;
    }
    
    /**
     * Sets the required qualifiers for this Injectee
     * @param requiredQualifiers The non-null set of required qualifiers
     */
    public void setRequiredQualifiers(Set<Annotation> requiredQualifiers) {
        qualifiers = Collections.unmodifiableSet(requiredQualifiers);
        
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Injectee#getPosition()
     */
    @Override
    public int getPosition() {
        return position;
    }
    
    /**
     * Sets the position of this Injectee.  The position represents the index of
     * the parameter, or -1 if this Injectee is describing a field.
     * 
     * @param position The index position of the parameter, or -1 if descrbing a field
     */
    public void setPosition(int position) {
        this.position = position;
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
    
    /**
     * This setter sets both the parent and the injecteeClass fields.
     * 
     * @param parent The parent (Field, Constructor or Method) which is
     * the parent of this Injectee
     */
    public void setParent(AnnotatedElement parent) {
        this.parent = parent;
        
        if (parent instanceof Field) {
            pClass = ((Field) parent).getDeclaringClass();
        }
        else if (parent instanceof Constructor) {
            pClass = ((Constructor<?>) parent).getDeclaringClass();
        }
        else if (parent instanceof Method) {
            pClass = ((Method) parent).getDeclaringClass();
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Injectee#isOptional()
     */
    @Override
    public boolean isOptional() {
        return isOptional;
    }
    
    /**
     * Sets whether or not this Injectee should be considered optional
     * 
     * @param optional true if this injectee is optional, false if required
     */
    public void setOptional(boolean optional) {
        this.isOptional = optional;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Injectee#isSelf()
     */
    @Override
    public boolean isSelf() {
        return isSelf;
    }
    
    /**
     * Sets whether or not this is a self-referencing injectee
     * 
     * @param self true if this is a self-referencing Injectee, and false otherwise
     */
    public void setSelf(boolean self) {
        isSelf = self;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Injectee#getUnqualified()
     */
    @Override
    public Unqualified getUnqualified() {
        return unqualified;
    }
    
    /**
     * Sets the unqualified annotation to be associated with this injectee
     * 
     * @param unqualified The unqualified annotation to be associated with this injectee
     */
    public void setUnqualified(Unqualified unqualified) {
        this.unqualified = unqualified;
    }
    
    @Override
    public ActiveDescriptor<?> getInjecteeDescriptor() {
        return injecteeDescriptor;
    }
    
    /**
     * Sets the descriptor to be associated with this injectee
     * 
     * @param injecteeDescriptor The injectee to be associated with this injectee
     */
    public void setInjecteeDescriptor(ActiveDescriptor<?> injecteeDescriptor) {
        this.injecteeDescriptor = injecteeDescriptor;
    }

    public String toString() {
        return "InjecteeImpl(requiredType=" + Pretty.type(requiredType) +
                ",parent=" + Pretty.clazz(pClass) +
                ",qualifiers=" + Pretty.collection(qualifiers) +
                ",position=" + position +
                ",optional=" + isOptional +
                ",self=" + isSelf +
                ",unqualified=" + unqualified +
                "," + System.identityHashCode(this) + ")";
    }

    
}
