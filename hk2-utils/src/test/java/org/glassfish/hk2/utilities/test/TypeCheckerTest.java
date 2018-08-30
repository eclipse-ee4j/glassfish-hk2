/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.utilities.test;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.glassfish.hk2.utilities.reflection.TypeChecker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the TypeChecker utility.
 * <p>
 * This test case illustrates some of the differences between
 * the java rules for assigning and the CDI rules for safe
 * injection.  In general, the CDI rules are more strict, so
 * you will see in the tests places where what Java allows
 * assignment but CDI does not.  These places are marked in
 * the comments of the code
 * 
 * @author jwells
 *
 */
@SuppressWarnings("unused")
public class TypeCheckerTest {
    @SuppressWarnings("rawtypes")
    private Dao dao;
    
    private Dao<Order> order;
    private Dao<User> user;
    private Dao<?> wildCard;
    private Dao<? extends Persistent> wPersistent;
    private Dao<? extends User> wUser;
    
    private Type daoType;
    private Type orderType;
    private Type userType;
    private Type wildCardType;
    private Type wPersistentType;
    private Type wUserType;
    
    @Before
    public void before() {
        try {
            getTypes();
        }
        catch (NoSuchFieldException nsfe) {
            throw new RuntimeException(nsfe);
        }
    }
    
    private void getTypes() throws NoSuchFieldException {
        Class<?> clazz = getClass();
        Field field;
        
        field = clazz.getDeclaredField("dao");
        daoType = field.getGenericType();
        
        field = clazz.getDeclaredField("order");
        orderType = field.getGenericType();
        
        field = clazz.getDeclaredField("user");
        userType = field.getGenericType();
        
        field = clazz.getDeclaredField("wPersistent");
        wPersistentType = field.getGenericType();
        
        field = clazz.getDeclaredField("wildCard");
        wildCardType = field.getGenericType();
        
        field = clazz.getDeclaredField("wUser");
        wUserType = field.getGenericType();
    }
    
    /**
     * Class to itself
     */
    @Test
    public void testClassToClass() {
        Assert.assertTrue(TypeChecker.isRawTypeSafe(daoType, daoType));
    }
    
    /**
     * Check raw class into unbounded type variables
     */
    @Test // @org.junit.Ignore
    public void testClassToPT() {
        Assert.assertTrue(TypeChecker.isRawTypeSafe(daoType, userType));
        Assert.assertTrue(TypeUtils.isAssignable(daoType, userType));
        
        Assert.assertTrue(TypeChecker.isRawTypeSafe(daoType, orderType));
        Assert.assertTrue(TypeUtils.isAssignable(daoType, orderType));
        
        // Java less restrictive than CDI rule
        Assert.assertFalse(TypeChecker.isRawTypeSafe(userType, daoType));
        Assert.assertTrue(TypeUtils.isAssignable(userType, daoType));
        
        // Java less restrictive than CDI rule
        Assert.assertFalse(TypeChecker.isRawTypeSafe(orderType, daoType));
        Assert.assertTrue(TypeUtils.isAssignable(orderType, daoType));
    }
    
    /**
     * Check wildcard to wildcard
     */
    @Test // @org.junit.Ignore
    public void testWildcardToWildcard() {
        // Java less restrictive than CDI rule
        Assert.assertFalse(TypeChecker.isRawTypeSafe(wildCardType, wildCardType));
        Assert.assertTrue(TypeUtils.isAssignable(wildCardType, wildCardType));
        
    }
    
    /**
     * Check raw class into bounded wildcards
     */
    @Test // @org.junit.Ignore
    public void testClassToBoundedWildcards() {
        Assert.assertTrue(TypeChecker.isRawTypeSafe(daoType, wPersistentType));
        Assert.assertTrue(TypeUtils.isAssignable(daoType, wPersistentType));
        
        Assert.assertTrue(TypeChecker.isRawTypeSafe(wPersistentType, daoType));
        Assert.assertTrue(TypeUtils.isAssignable(wPersistentType, daoType));
        
        Assert.assertTrue(TypeChecker.isRawTypeSafe(daoType, wUserType));
        Assert.assertTrue(TypeUtils.isAssignable(daoType, wUserType));
        
        // Java less restrictive than CDI rule
        Assert.assertFalse(TypeChecker.isRawTypeSafe(wUserType, daoType));
        Assert.assertTrue(TypeUtils.isAssignable(wUserType, daoType));
    }
    
    private static class Dao<T extends Persistent> {}
    private static interface Order extends Persistent {}
    private static class User implements Persistent {}
    private static interface Persistent {}

}
