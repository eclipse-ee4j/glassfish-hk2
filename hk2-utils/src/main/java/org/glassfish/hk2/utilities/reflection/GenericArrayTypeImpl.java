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

package org.glassfish.hk2.utilities.reflection;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Arrays;

import org.glassfish.hk2.utilities.general.GeneralUtilities;

/**
 * An implementation of GenericArrayType for those times we need to create
 * this on the fly
 * 
 * @author jwells
 *
 */
public class GenericArrayTypeImpl implements GenericArrayType {
    private final Type genericComponentType;
    
    /**
     * Creates the GenericArrayType with the given array type
     * 
     * @param gct the non-null type for this GenericArrayType
     */
    public GenericArrayTypeImpl(Type gct) {
        genericComponentType = gct;
    }

    /* (non-Javadoc)
     * @see java.lang.reflect.GenericArrayType#getGenericComponentType()
     */
    @Override
    public Type getGenericComponentType() {
        return genericComponentType;
    }
    
    @Override
    public int hashCode() {
        return genericComponentType.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof GenericArrayType)) return false;
        
        GenericArrayType other = (GenericArrayType) o;
        
        return GeneralUtilities.safeEquals(genericComponentType, other.getGenericComponentType());
    }
    
    public String toString() {
        return "GenericArrayTypeImpl(" + genericComponentType + ")";
    }

}
