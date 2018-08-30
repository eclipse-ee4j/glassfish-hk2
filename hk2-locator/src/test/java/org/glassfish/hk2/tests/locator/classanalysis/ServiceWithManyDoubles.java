/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.locator.classanalysis;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import junit.framework.Assert;

import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.ServiceLocator;

/**
 * @author jwells
 *
 */
@PerLookup
public class ServiceWithManyDoubles {
    public Double d1;
    
    @Inject
    public Float f1;
    
    @Inject
    public String s1;
    
    @Inject
    public ServiceLocator locator;
    
    @Inject
    public Double d2;
    
    public boolean pickedCorrectConstructor = false;
    
    public boolean incorrectInitializerCalled = false;
    public boolean incorrectPostConstructCalled = false;
    public boolean incorrectPreDestroyCalled = false;
    
    public boolean setD1Called = false;
    public boolean setD2Called = false;
    
    public boolean correctPostConstructCalled = false;
    public boolean correctPreDestroyCalled = false;
    
    public ServiceWithManyDoubles() {
    }
    
    @Inject
    public ServiceWithManyDoubles(String s) {
        s1 = s;
    }

    public ServiceWithManyDoubles(Double d) {
        pickedCorrectConstructor = true;
    }
    
    public ServiceWithManyDoubles(Float f1, Float f2) {
    }
    
    @Inject
    public void setServiceLocator(ServiceLocator sl) {
        incorrectInitializerCalled = true;
    }
    
    public void setD1(Double d1, ServiceLocator sl) {
        if (sl == null) throw new AssertionError("ServiceLocator is null in setD1");
        if (d1 == null) throw new AssertionError("d1 is null in setD1");
        setD1Called = true;
    }
    
    @Inject
    public void setD2(Double d2) {
        if (d2 == null) throw new AssertionError("d2 is null in setD2");
        setD2Called = true;
    }
    
    @PostConstruct
    public void postConstruct() {
        incorrectPostConstructCalled = true;
        
    }
    
    @PreDestroy
    public void preDestory() {
        incorrectPreDestroyCalled = true;
        
    }
    
    public void doublePostConstruct() {
        correctPostConstructCalled = true;
        
    }
    
    public void doublePreDestroy() {
        correctPreDestroyCalled = true; 
    }
    
    public void checkCalls() {
        Assert.assertTrue(pickedCorrectConstructor);
        
        Assert.assertFalse(incorrectInitializerCalled);
        Assert.assertFalse(incorrectPostConstructCalled);
        Assert.assertFalse(incorrectPreDestroyCalled);
 
        Assert.assertTrue(setD1Called);
        Assert.assertTrue(setD2Called);
        
        Assert.assertTrue(correctPostConstructCalled);
        Assert.assertTrue(correctPreDestroyCalled);
        
        Assert.assertEquals(DoubleFactory.DOUBLE, d1);
        Assert.assertEquals(DoubleFactory.DOUBLE, d2);
        
        Assert.assertNull(f1);
        Assert.assertNull(s1);
        Assert.assertNull(locator);
    }
    
    public void checkAfterConstructor() {
        Assert.assertTrue(pickedCorrectConstructor);
        
        Assert.assertFalse(incorrectInitializerCalled);
        Assert.assertFalse(incorrectPostConstructCalled);
        Assert.assertFalse(incorrectPreDestroyCalled);
 
        Assert.assertFalse(setD1Called);
        Assert.assertFalse(setD2Called);
        
        Assert.assertFalse(correctPostConstructCalled);
        Assert.assertFalse(correctPreDestroyCalled);
        
        Assert.assertNull(d1);
        Assert.assertNull(d2);
        
        Assert.assertNull(f1);
        Assert.assertNull(s1);
        Assert.assertNull(locator);
    }
    
    public void checkAfterInitializeBeforePostConstruct() {
        Assert.assertTrue(pickedCorrectConstructor);
        
        Assert.assertFalse(incorrectInitializerCalled);
        Assert.assertFalse(incorrectPostConstructCalled);
        Assert.assertFalse(incorrectPreDestroyCalled);
 
        Assert.assertTrue(setD1Called);
        Assert.assertTrue(setD2Called);
        
        Assert.assertFalse(correctPostConstructCalled);
        Assert.assertFalse(correctPreDestroyCalled);
        
        Assert.assertEquals(DoubleFactory.DOUBLE, d1);
        Assert.assertEquals(DoubleFactory.DOUBLE, d2);
        
        Assert.assertNull(f1);
        Assert.assertNull(s1);
        Assert.assertNull(locator);
    }
    
    public void checkAfterPostConstructWithNoInitialization() {
        Assert.assertTrue(pickedCorrectConstructor);
        
        Assert.assertFalse(incorrectInitializerCalled);
        Assert.assertFalse(incorrectPostConstructCalled);
        Assert.assertFalse(incorrectPreDestroyCalled);
 
        Assert.assertFalse(setD1Called);
        Assert.assertFalse(setD2Called);
        
        Assert.assertTrue(correctPostConstructCalled);
        Assert.assertFalse(correctPreDestroyCalled);
        
        Assert.assertNull(d1);
        Assert.assertNull(d2);
        
        Assert.assertNull(f1);
        Assert.assertNull(s1);
        Assert.assertNull(locator);
    }
    
    public void checkFullCreateWithoutDestroy() {
        Assert.assertTrue(pickedCorrectConstructor);
        
        Assert.assertFalse(incorrectInitializerCalled);
        Assert.assertFalse(incorrectPostConstructCalled);
        Assert.assertFalse(incorrectPreDestroyCalled);
 
        Assert.assertTrue(setD1Called);
        Assert.assertTrue(setD2Called);
        
        Assert.assertTrue(correctPostConstructCalled);
        Assert.assertFalse(correctPreDestroyCalled);
        
        Assert.assertEquals(DoubleFactory.DOUBLE, d1);
        Assert.assertEquals(DoubleFactory.DOUBLE, d2);
        
        Assert.assertNull(f1);
        Assert.assertNull(s1);
        Assert.assertNull(locator);
    }
}
