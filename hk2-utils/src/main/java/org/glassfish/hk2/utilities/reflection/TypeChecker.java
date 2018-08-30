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

package org.glassfish.hk2.utilities.reflection;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

/**
 * This class contains various utilities for ensuring
 * java type safety
 * 
 * @author jwells
 *
 */
public class TypeChecker {
    /**
     * Returns true if the given requiredType is safely assignable
     * from the given beanType.  In otherwords, if<code>
     * requiredType = beanType
     * </code>
     * without any cast.  It should be noted that this
     * checker is using the CDI rules (as stated in CDI version 1.1
     * in section 
     * 
     * @param requiredType The type being assigned into
     * @param beanType the type being assigned
     * @return true if things are type safe
     */
    public static boolean isRawTypeSafe(Type requiredType, Type beanType) {
        Class<?> requiredClass = ReflectionHelper.getRawClass(requiredType);
        if (requiredClass == null) {
            return false;
        }
        requiredClass = ReflectionHelper.translatePrimitiveType(requiredClass);
        
        Class<?> beanClass = ReflectionHelper.getRawClass(beanType);
        if (beanClass == null) {
            return false;
        }
        beanClass = ReflectionHelper.translatePrimitiveType(beanClass);
        
        if (!requiredClass.isAssignableFrom(beanClass)) {
            return false;
        }
        
        if ((requiredType instanceof Class) ||
            (requiredType instanceof GenericArrayType)) {
            // Both types are raw, and already passed assignability check above
            return true;
        }
        
        if (!(requiredType instanceof ParameterizedType)) {
            throw new IllegalArgumentException("requiredType " + requiredType + " is of unknown type");
            
        }
        
        // By the check of null of getRawClass the required type MUST be a parameterized type at this time
        ParameterizedType requiredPT = (ParameterizedType) requiredType;
        
        Type requiredTypeVariables[] = requiredPT.getActualTypeArguments();
        Type beanTypeVariables[];
        if (beanType instanceof Class) {
            beanTypeVariables = ((Class<?>) beanType).getTypeParameters();
        }
        else if (beanType instanceof ParameterizedType) {
            beanTypeVariables = ((ParameterizedType) beanType).getActualTypeArguments();
        }
        else {
            throw new IllegalArgumentException("Uknown beanType " + beanType);
        }
        
        if (requiredTypeVariables.length != beanTypeVariables.length) {
            // Possible when beanType is a class
            return false;
        }
        
        for (int lcv = 0; lcv < requiredTypeVariables.length; lcv++) {
            Type requiredTypeVariable = requiredTypeVariables[lcv];
            Type beanTypeVariable = beanTypeVariables[lcv];
            
            if (isActualType(requiredTypeVariable) && isActualType(beanTypeVariable)) {
                if (!isRawTypeSafe(requiredTypeVariable, beanTypeVariable)) return false;
            }
            else if (isArrayType(requiredTypeVariable) && isArrayType(beanTypeVariable)) {
                Type requiredArrayType = getArrayType(requiredTypeVariable);
                Type beanArrayType = getArrayType(beanTypeVariable);
                
                if (!isRawTypeSafe(requiredArrayType, beanArrayType)) return false;
            }
            else if (isWildcard(requiredTypeVariable) && isActualType(beanTypeVariable)) {
                WildcardType wt = getWildcard(requiredTypeVariable);
                Class<?> beanActualType = ReflectionHelper.getRawClass(beanTypeVariable);
                
                if (!isWildcardActualSafe(wt, beanActualType)) return false;
            }
            else if (isWildcard(requiredTypeVariable) && isTypeVariable(beanTypeVariable)) {
                WildcardType wt = getWildcard(requiredTypeVariable);
                TypeVariable<?> tv = getTypeVariable(beanTypeVariable);
                
                if (!isWildcardTypeVariableSafe(wt, tv)) return false;
            }
            else if (isActualType(requiredTypeVariable) && isTypeVariable(beanTypeVariable)) {
                Class<?> requiredActual = ReflectionHelper.getRawClass(requiredTypeVariable);
                TypeVariable<?> tv = getTypeVariable(beanTypeVariable);
                
                if (!isActualTypeVariableSafe(requiredActual, tv)) return false;
            }
            else if (isTypeVariable(requiredTypeVariable) && isTypeVariable(beanTypeVariable)) {
                TypeVariable<?> rtv = getTypeVariable(requiredTypeVariable);
                TypeVariable<?> btv = getTypeVariable(beanTypeVariable);
                
                if (!isTypeVariableTypeVariableSafe(rtv, btv)) return false;
            }
            else {
                // Other combinations of types are not valid
                return false;
            }
        }
        
        return true;
    }
    
    private static boolean isTypeVariableTypeVariableSafe(TypeVariable<?> rtv, TypeVariable<?> btv) {
        Class<?> rtvBound = getBound(rtv.getBounds());
        if (rtvBound == null) {
            // TODO:  I don't think this is possible
            return false;
        }
        
        Class<?> btvBound = getBound(btv.getBounds());
        if (btvBound == null) {
            // TODO:  I don't think this is possible
            return false;
        }
        
        if (!btvBound.isAssignableFrom(rtvBound)) {
            return false;
        }
        
        return true;
    }
    
    private static boolean isActualTypeVariableSafe(Class<?> actual, TypeVariable<?> tv) {
        Class<?> tvBound = getBound(tv.getBounds());
        if (tvBound == null) {
            // TODO:  I don't think this is possible
            return false;
        }
        
        if (!actual.isAssignableFrom(tvBound)) {
            return false;
        }
        
        return true;
    }
    
    private static boolean isWildcardTypeVariableSafe(WildcardType wildcard, TypeVariable<?> tv) {
        Class<?> tvBound = getBound(tv.getBounds());
        if (tvBound == null) {
            // TODO:  I don't think this is possible
            return false;
        }
        
        Class<?> upperBound = getBound(wildcard.getUpperBounds());
        if (upperBound == null) {
            // TODO: I don't think this is possible
            return false;
        }
        
        if (!upperBound.isAssignableFrom(tvBound)) {
            return false;
        }
        
        Class<?> lowerBound = getBound(wildcard.getLowerBounds());
        if (lowerBound == null) {
            return true;
        }
        
        if (!tvBound.isAssignableFrom(lowerBound)) {
            return false;
        }
        
        return true;
    }
    
    private static Class<?> getBound(Type bounds[]) {
        if (bounds == null) return null;
        if (bounds.length < 1) return null;
        if (bounds.length > 1) {
            throw new AssertionError("Do not understand multiple bounds");
        }
        
        return ReflectionHelper.getRawClass(bounds[0]);
    }
    
    private static boolean isWildcardActualSafe(WildcardType wildcard, Class<?> actual) {
        Class<?> upperBound = getBound(wildcard.getUpperBounds());
        if (upperBound == null) {
            // TODO: Is this possible, I think if not present this should be Object.class
            return false;
        }
        
        if (!upperBound.isAssignableFrom(actual)) {
            return false;
        }
        
        Class<?> lowerBound = getBound(wildcard.getLowerBounds());
        if (lowerBound == null) {
            return true;
        }
        
        if (!actual.isAssignableFrom(lowerBound)) {
            return false;
        }
        
        return true;
    }
    
    private static WildcardType getWildcard(Type type) {
        if (type == null) return null;
        
        if (type instanceof WildcardType) {
            return (WildcardType) type;
        }
        
        return null;
    }
    
    private static TypeVariable<?> getTypeVariable(Type type) {
        if (type == null) return null;
        
        if (type instanceof TypeVariable) {
            return (TypeVariable<?>) type;
        }
        
        return null;
    }
    
    private static boolean isWildcard(Type type) {
        if (type == null) return false;
        
        return (type instanceof WildcardType) ;
    }
    
    private static boolean isTypeVariable(Type type) {
        if (type == null) return false;
        
        return (type instanceof TypeVariable) ;
    }
    
    /**
     * An actual type is either a Class or a ParameterizedType
     * 
     * @param type The type to test
     * @return true if this is an actual type
     */
    private static boolean isActualType(Type type) {
        if (type == null) return false;
        
        return ((type instanceof Class) || (type instanceof ParameterizedType));
        
    }
    
    /**
     * An array type can be a class that is an array
     * or a GenericArrayType
     * 
     * @param type The type to test
     * @return true if this is an actual type
     */
    private static boolean isArrayType(Type type) {
        if (type == null) return false;
        
        if (type instanceof Class) {
            Class<?> clazz = (Class<?>) type;
            return clazz.isArray();
        }
        
        return (type instanceof GenericArrayType);
    }
    
    /**
     * An array type can be a class that is an array
     * or a GenericArrayType
     * 
     * @param type The type to test
     * @return true if this is an actual type
     */
    private static Type getArrayType(Type type) {
        if (type == null) return null;
        
        if (type instanceof Class) {
            Class<?> clazz = (Class<?>) type;
            return clazz.getComponentType();
        }
        
        if (type instanceof GenericArrayType) {
            GenericArrayType gat = (GenericArrayType) type;
            return gat.getGenericComponentType();
        }
        
        return null;
    }
}
