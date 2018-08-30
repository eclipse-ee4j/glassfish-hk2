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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import junit.framework.Assert;

/**
 * Tests for ParameterizedTypeImpl
 * 
 * @author jwells
 *
 */
public class ParameterizedTypeTest {
    
    /**
     * Tests the equals of ParameterizedTypeImpl against a Java provided
     * ParameterizedType
     */
    @Test
    public void testGoodEqualsOfPTI() {
        Class<ExtendsBase> ebc = ExtendsBase.class;
        Type ebcGenericSuperclass= ebc.getGenericSuperclass();
        
        Assert.assertTrue(ebcGenericSuperclass instanceof ParameterizedType);
        
        ParameterizedType ebcPT = (ParameterizedType) ebcGenericSuperclass;
        
        ParameterizedTypeImpl pti = new ParameterizedTypeImpl(Base.class, String.class);
        
        Assert.assertTrue(pti.equals(ebcPT));
        
        Assert.assertTrue(ebcPT.equals(pti));
        
        Assert.assertEquals(ebcPT.hashCode(), pti.hashCode());
    }
    
    /**
     * Tests the toString code of PTI
     */
    @Test
    public void testToStringOfPTI() {
        ParameterizedTypeImpl pti = new ParameterizedTypeImpl(Base.class, String.class);
        
        Assert.assertTrue(pti.toString().contains("Base<String>"));
    }
    
    /**
     * Tests that null passed to equals returns false
     */
    @Test
    public void testNullDoesNotEqualsPTI() {
        ParameterizedTypeImpl pti = new ParameterizedTypeImpl(Base.class, String.class);
        Assert.assertFalse(pti.equals(null));
    }
    
    /**
     * Tests that a non-parameterized type passed to equals returns false
     */
    @Test
    public void testNotPTDoesNotEqualsPTI() {
        ParameterizedTypeImpl pti = new ParameterizedTypeImpl(Base.class, String.class);
        Assert.assertFalse(pti.equals(new String()));
    }
    
    /**
     * Tests that a parameterized type with different raw type are not equal
     */
    @Test
    public void testNotSameRawTypeDoesNotEqualsPTI() {
        ParameterizedTypeImpl pti = new ParameterizedTypeImpl(Base.class, String.class);
        ParameterizedTypeImpl pti1 = new ParameterizedTypeImpl(List.class, String.class);
        
        Assert.assertFalse(pti.equals(pti1));
    }
    
    /**
     * Tests that a parameterized type with different actual type are not equal
     */
    @Test
    public void testNotSameActualTypesDoesNotEqualsPTI() {
        ParameterizedTypeImpl pti = new ParameterizedTypeImpl(Base.class, String.class);
        ParameterizedTypeImpl pti1 = new ParameterizedTypeImpl(Base.class, Integer.class);
        
        Assert.assertFalse(pti.equals(pti1));
    }
    
    /**
     * Tests that a parameterized type a different number of actual arguments
     */
    @Test
    public void testNotSameNumberOfActualTypesDoesNotEqualsPTI() {
        ParameterizedTypeImpl pti = new ParameterizedTypeImpl(Map.class, String.class);
        ParameterizedTypeImpl pti1 = new ParameterizedTypeImpl(Map.class, Integer.class, String.class);
        
        Assert.assertFalse(pti.equals(pti1));
    }
    
    /**
     * Tests that a parameterized type a different number of actual arguments
     */
    @Test
    public void testNullOwnerType() {
        ParameterizedTypeImpl pti = new ParameterizedTypeImpl(Map.class, String.class);
        
        Assert.assertNull(pti.getOwnerType());
    }

}
