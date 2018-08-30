/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.api;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

/**
 * Supports inline instantiation of objects that represent parameterized
 * types with actual type parameters.
 *
 * An object that represents any parameterized type may be obtained by
 * subclassing {@code TypeLiteral}.
 * 
 * <pre>
 *  TypeLiteral&lt;List&lt;String>> stringListType = new TypeLiteral&lt;List&lt;String>>() {};
 * </pre>
 *
 * @param <T> 
 */
public abstract class TypeLiteral<T> {

    /**
     * Store the actual type (direct subclass of TypeLiteral).
     */
    private transient Type type;

    /**
     * Store the actual raw parameter type.
     */
    private transient Class<T> rawType;

    protected TypeLiteral() {
    }

    /**
     * @return the actual type represented by this object
     */
    public final Type getType() {
        if (type == null) {
            // Get the class that directly extends TypeLiteral<?>
            Class<?> typeLiteralSubclass = getTypeLiteralSubclass(this.getClass());
            if (typeLiteralSubclass == null) {
                throw new RuntimeException(getClass() + " is not a subclass of TypeLiteral<T>");
            }

            // Get the type parameter of TypeLiteral<T> (aka the T value)
            type = getTypeParameter(typeLiteralSubclass);
            if (type == null) {
                throw new RuntimeException(getClass() + " does not specify the type parameter T of TypeLiteral<T>");
            }
        }
        return type;
    }

    /**
     * Gets the types associated with this literal
     * @return A non-null (but possibly empty) array of types associated with this literal
     */
    public final Type[] getParameterTypes() {
        type = getType();
        if (type instanceof ParameterizedType) {
            return ((ParameterizedType) type).getActualTypeArguments();
        }
        
        return new Type[0];
    }

    /**
     * @return the raw type represented by this object
     */
    @SuppressWarnings("unchecked")
    public final Class<T> getRawType() {

        if (rawType == null) {

            // Get the actual type
            Type t = getType();
            return (Class<T>) getRawType(t);
        }

        return rawType;
    }

    /**
     * Gets the base associated class from this type
     * @param type The non-null type to analyze
     * @return The base class for the type, or null if there is
     * none (e.g., Wildcard)
     */
    public static Class<?> getRawType(Type type) {
        if (type instanceof Class) {

            return (Class<?>) type;

        } else if (type instanceof ParameterizedType) {

            ParameterizedType parameterizedType = (ParameterizedType) type;
            return (Class<?>) parameterizedType.getRawType();

        } else if (type instanceof GenericArrayType) {

            return Object[].class;

        } else if (type instanceof WildcardType) {
            return null;
        } else {
            throw new RuntimeException("Illegal type");
        }
    }

    /**
     * Return the direct child class that extends TypeLiteral<T>
     * @param clazz processed class
     */
    private static Class<?> getTypeLiteralSubclass(Class<?> clazz) {

        // Start with super class
        Class<?> superClass = clazz.getSuperclass();

        if (superClass.equals(TypeLiteral.class)) {
            // Super class is TypeLiteral, return the current class
            return clazz;
        } else if (superClass.equals(Object.class)) {
            // Hmm, strange case, we don not extends TypeLiteral !
            return null;
        } else {
            // Continue processing, one level deeper
            return (getTypeLiteralSubclass(superClass));
        }
    }

    /**
     * Return the value of the type parameter of TypeLiteral<T>.
     * @param typeLiteralSubclass subClass of TypeLiteral<T> to analyze
     * @return the parametrized type of TypeLiteral<T> (aka T)
     */
    private static Type getTypeParameter(Class<?> typeLiteralSubclass) {

        // Access the typeLiteral<T> super class using generics
        Type type = typeLiteralSubclass.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            // TypeLiteral is indeed parametrized
            ParameterizedType parameterizedType = (ParameterizedType) type;
            if (parameterizedType.getActualTypeArguments().length == 1) {
                // Return the value of the type parameter (aka T)
                return parameterizedType.getActualTypeArguments()[0];
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TypeLiteral<?>) {

            // Compare inner type for equality
            TypeLiteral<?> that = (TypeLiteral<?>) obj;
            return this.getType().equals(that.getType());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getType().hashCode();
    }

    @Override
    public String toString() {
        return getType().toString();
    }
}
