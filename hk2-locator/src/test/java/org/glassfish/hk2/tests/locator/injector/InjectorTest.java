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

package org.glassfish.hk2.tests.locator.injector;

import java.lang.reflect.Method;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.MethodParameterImpl;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class InjectorTest {
    private final static String TEST_NAME = "InjectorTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new InjectorModule());
    
    /**
     * This only creates the object, it does not inject it further, and does not post construct it
     */
    @Test
    public void testCreateOnly() {
        DontManageMe dmm = locator.create(DontManageMe.class);
        Assert.assertNotNull(dmm);
        
        Assert.assertNotNull(dmm.getByConstructor());
        Assert.assertNull(dmm.getByField());
        Assert.assertNull(dmm.getByMethod());
        Assert.assertNull(dmm.getSpecialService());
        Assert.assertNull(dmm.getSecondMethod());
        Assert.assertNull(dmm.getSecondSpecial());
        Assert.assertNull(dmm.getUnknown());
        Assert.assertFalse(dmm.isPostConstructCalled());
        Assert.assertFalse(dmm.isPreDestroyCalled());
    }
    
    /**
     * This creates and injects the object, but does not post construct it
     */
    @Test
    public void testInjectOnly() {
        DontManageMe dmm = locator.create(DontManageMe.class);
        locator.inject(dmm);
        
        Assert.assertNotNull(dmm.getByConstructor());
        Assert.assertNotNull(dmm.getByField());
        Assert.assertNotNull(dmm.getByMethod());
        Assert.assertNotNull(dmm.getSpecialService());
        Assert.assertNotNull(dmm.getSecondMethod());
        Assert.assertNotNull(dmm.getSecondSpecial());
        Assert.assertNull(dmm.getUnknown());
        Assert.assertFalse(dmm.isPostConstructCalled());
        Assert.assertFalse(dmm.isPreDestroyCalled());
    }
    
    /**
     * This creates, injects and post constructs the object
     */
    @Test
    public void testPostConstructOnly() {
        DontManageMe dmm = locator.create(DontManageMe.class);
        locator.inject(dmm);
        locator.postConstruct(dmm);
        
        Assert.assertNotNull(dmm.getByConstructor());
        Assert.assertNotNull(dmm.getByField());
        Assert.assertNotNull(dmm.getByMethod());
        Assert.assertNotNull(dmm.getSpecialService());
        Assert.assertNotNull(dmm.getSecondMethod());
        Assert.assertNotNull(dmm.getSecondSpecial());
        Assert.assertNull(dmm.getUnknown());
        Assert.assertTrue(dmm.isPostConstructCalled());
        Assert.assertFalse(dmm.isPreDestroyCalled());
    }
    
    /**
     * This creates, injects, post constructs and pre destroys the object
     */
    @Test
    public void testPreDestroyOnly() {
        DontManageMe dmm = locator.create(DontManageMe.class);
        locator.inject(dmm);
        locator.postConstruct(dmm);
        locator.preDestroy(dmm);
        
        Assert.assertNotNull(dmm.getByConstructor());
        Assert.assertNotNull(dmm.getByField());
        Assert.assertNotNull(dmm.getByMethod());
        Assert.assertNotNull(dmm.getSpecialService());
        Assert.assertNotNull(dmm.getSecondMethod());
        Assert.assertNotNull(dmm.getSecondSpecial());
        Assert.assertNull(dmm.getUnknown());
        Assert.assertTrue(dmm.isPostConstructCalled());
        Assert.assertTrue(dmm.isPreDestroyCalled());
    }
    
    /**
     * This creates, injects and post constructs the object
     */
    @Test
    public void testNoPostConstructOrPreDestroy() {
        NoPostConstruct npc = locator.create(NoPostConstruct.class);
        locator.inject(npc);  // Nothing to inject, should work anyway
        locator.postConstruct(npc);  // No postConstruct, should work anyway
        locator.preDestroy(npc);  // No preDestroy, should work anyway
    }
    
    /**
     * This creates, injects and post constructs the object
     */
    @Test
    public void testImplementsLifecycleAPI() {
        ImplementsLifecycleInterfaces lii = locator.create(ImplementsLifecycleInterfaces.class);
        Assert.assertFalse(lii.isPostCalled());
        Assert.assertFalse(lii.isPreCalled());
        
        locator.postConstruct(lii);
        Assert.assertTrue(lii.isPostCalled());
        Assert.assertFalse(lii.isPreCalled());
        
        locator.preDestroy(lii);
        Assert.assertTrue(lii.isPostCalled());
        Assert.assertTrue(lii.isPreCalled());
    }
    
    /**
     * Tests an assisted injection
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testAssistedInjection() throws Exception {
        AssistedInjectionService ais = new AssistedInjectionService();
        
        Method method = ais.getClass().getMethod("aMethod", Event.class,
                SpecialService.class, SimpleService.class, double.class,
                UnknownService.class);
        
        Event event = new Event();
        Double fooMe = new Double(2.71);
        
        locator.assistedInject(ais, method, new MethodParameterImpl(0, event), new MethodParameterImpl(3, fooMe));
        
        Assert.assertEquals(event, ais.getEvent());
        Assert.assertEquals(fooMe, new Double(ais.getFoo()));
        Assert.assertNotNull(ais.getSimple());
        Assert.assertNotNull(ais.getSpecial());
        Assert.assertNull(ais.getUnknown());
    }
    
    /**
     * Tests an assisted injection with a root
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Test
    // @org.junit.Ignore
    public void testAssistedInjectionWithRoot() throws Exception {
        AssistedInjectionService ais = new AssistedInjectionService();
        
        Method method = ais.getClass().getMethod("aMethod", Event.class,
                SpecialService.class, SimpleService.class, double.class,
                UnknownService.class);
        
        Event event = new Event();
        Double fooMe = new Double(2.71);
        
        ActiveDescriptor<?> descriptor = locator.getBestDescriptor(BuilderHelper.createContractFilter(ServiceLocator.class.getName()));
        Assert.assertNotNull(descriptor);
        
        ServiceHandle<ServiceLocator> root = locator.getServiceHandle((ActiveDescriptor<ServiceLocator>) descriptor);
        
        Assert.assertTrue(root.getSubHandles().isEmpty());
        
        locator.assistedInject(ais, method, root, new MethodParameterImpl(0, event), new MethodParameterImpl(3, fooMe));
        
        Assert.assertEquals(event, ais.getEvent());
        Assert.assertEquals(fooMe, new Double(ais.getFoo()));
        Assert.assertNotNull(ais.getSimple());
        Assert.assertNotNull(ais.getSpecial());
        Assert.assertNull(ais.getUnknown());
        
        Assert.assertEquals(1, root.getSubHandles().size());
        
        ServiceHandle<?> found = root.getSubHandles().get(0);
        Assert.assertNotNull(found);
        
        ActiveDescriptor<?> foundAD = found.getActiveDescriptor();
        Assert.assertEquals(SimpleService.class.getName(), foundAD.getImplementation());
    }
}
