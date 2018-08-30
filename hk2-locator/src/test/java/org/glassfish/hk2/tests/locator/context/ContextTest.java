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

package org.glassfish.hk2.tests.locator.context;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class ContextTest {
    private final static String TEST_NAME = "CustomContextTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new ContextModule());
    
    /**
     * Tests you can look up via context
     */
    @Test
    public void testLookupViaContext() {
        List<Object> viaContext = locator.<Object>getAllServices(CustomContext.class);
        Assert.assertNotNull(viaContext);
        
        Assert.assertTrue(2 == viaContext.size());
        
        Assert.assertTrue(viaContext.get(0) instanceof CustomService1);
        Assert.assertTrue(viaContext.get(1) instanceof CustomService2);
    }
    
    /**
     * Tests you can look up via context
     */
    @Test
    public void testDynamicallyAddAndRemoveFromCustomContext() {
        ActiveDescriptor<CustomService3> desc = ServiceLocatorUtilities.addOneDescriptor(locator,
                BuilderHelper.createDescriptorFromClass(CustomService3.class));
        
        List<Object> viaContext = locator.<Object>getAllServices(CustomContext.class);
        
        Assert.assertTrue("Expected size 3, got size " + viaContext.size(), 3 == viaContext.size());
        
        Assert.assertTrue(viaContext.get(0) instanceof CustomService1);
        Assert.assertTrue(viaContext.get(1) instanceof CustomService2);
        Assert.assertTrue(viaContext.get(2) instanceof CustomService3);
        
        ServiceLocatorUtilities.removeOneDescriptor(locator, desc);
        
        viaContext = locator.<Object>getAllServices(CustomContext.class);
        
        Assert.assertTrue(2 == viaContext.size());
        
        Assert.assertTrue(viaContext.get(0) instanceof CustomService1);
        Assert.assertTrue(viaContext.get(1) instanceof CustomService2);
    }
    
    /**
     * Tests that when using getService you get a null as the root
     */
    @Test
    public void testGetServiceHasNullRoot() {
        RootContext rootContext = locator.getService(RootContext.class);
        rootContext.clear();
        
        locator.getService(RootService1.class);
        
        List<RootContext.Root> roots = rootContext.getRoots();
        Assert.assertEquals(1, roots.size());
        
        Assert.assertNull(roots.get(0).getRoot());
    }
    
    /**
     * Tests that when using a service handle you get the handle
     * as the root
     */
    @Test
    public void testGetServiceWithHanldeHasRoot() {
        RootContext rootContext = locator.getService(RootContext.class);
        rootContext.clear();
        
        Object key = new Object();
        ServiceHandle<RootService1> handle = locator.getServiceHandle(RootService1.class);
        handle.setServiceData(key);
        handle.getService();
        
        List<RootContext.Root> roots = rootContext.getRoots();
        Assert.assertEquals(1, roots.size());
        
        ServiceHandle<?> rootHandle = roots.get(0).getRoot();
        Assert.assertNotNull(rootHandle);
        
        Assert.assertEquals(handle, rootHandle);
        Assert.assertEquals(key, rootHandle.getServiceData());
    }
    
    /**
     * Tests that when using getService in a dependency chain
     * you get null as the root
     */
    @Test
    public void testGetServiceHasNullRootWithInjectionChain() {
        RootContext rootContext = locator.getService(RootContext.class);
        rootContext.clear();
        
        locator.getService(RootService2.class);
        
        List<RootContext.Root> roots = rootContext.getRoots();
        Assert.assertEquals(2, roots.size());
        
        // the first one is null since it is a basic lookup
        Assert.assertNull(roots.get(0).getRoot());
        
        // the second is non-null because it is from an injection point
        Assert.assertNotNull(roots.get(1).getRoot());
    }
    
    /**
     * Tests that when using a service handle you get the handle
     * as the root in an injection chain
     */
    @Test
    public void testGetServiceWithHanldeHasRootWithInjectionChain() {
        RootContext rootContext = locator.getService(RootContext.class);
        rootContext.clear();
        
        Object key = new Object();
        ServiceHandle<RootService2> handle = locator.getServiceHandle(RootService2.class);
        handle.setServiceData(key);
        handle.getService();
        
        List<RootContext.Root> roots = rootContext.getRoots();
        Assert.assertEquals(2, roots.size());
        
        ServiceHandle<?> rootHandle0 = roots.get(0).getRoot();
        Assert.assertNotNull(rootHandle0);
        
        Assert.assertEquals(handle, rootHandle0);
        Assert.assertEquals(key, rootHandle0.getServiceData());
        
        ServiceHandle<?> rootHandle1 = roots.get(1).getRoot();
        Assert.assertNotNull(rootHandle1);
        
        Assert.assertEquals(handle, rootHandle1);
        Assert.assertEquals(key, rootHandle1.getServiceData());
    }

    /**
     * Tests that the root passes through different contexts
     */
    @Test
    public void testRootPassesThroughOtherContexts() {
        RootContext rootContext = locator.getService(RootContext.class);
        rootContext.clear();
        
        ServiceHandle<GetsRootServiceWithProvider> handle = locator.getServiceHandle(GetsRootServiceWithProvider.class);
        handle.getService();
        
        List<RootContext.Root> roots = rootContext.getRoots();
        Assert.assertEquals(1, roots.size());
        
        ServiceHandle<?> rootHandle0 = roots.get(0).getRoot();
        Assert.assertNotNull(rootHandle0);
        
        Assert.assertEquals(handle, rootHandle0);
    }
    
    /**
     * Ensures that the root is NOT set when using provider.get
     */
    @Test
    public void testRootWhenUsingProvider() {
        RootContext rootContext = locator.getService(RootContext.class);
        rootContext.clear();
        
        ServiceHandle<GetsRootServiceWithProvider> handle = locator.getServiceHandle(GetsRootServiceWithProvider.class);
        GetsRootServiceWithProvider gets = handle.getService();
        
        List<RootContext.Root> roots = gets.checkProvider();
        Assert.assertEquals(2, roots.size());
        
        // the first get call is like a getService call, and hence has null root
        ServiceHandle<?> rootHandle0 = roots.get(0).getRoot();
        Assert.assertNull(rootHandle0);
        
        // the second call is non-null, as it comes from an injection point
        ServiceHandle<?> rootHandle1 = roots.get(1).getRoot();
        Assert.assertNotNull(rootHandle1);
    }
    
    /**
     * Ensures that the root is set when using a provider iterator
     */
    @Test
    public void testRootWhenUsingProviderIterator() {
        RootContext rootContext = locator.getService(RootContext.class);
        rootContext.clear();
        
        ServiceHandle<GetsRootServiceWithProvider> handle = locator.getServiceHandle(GetsRootServiceWithProvider.class);
        GetsRootServiceWithProvider gets = handle.getService();
        
        List<RootContext.Root> roots = gets.checkProviderWithIterator();
        Assert.assertEquals(2, roots.size());
        
        ServiceHandle<?> rootHandle0 = roots.get(0).getRoot();
        Assert.assertNotNull(rootHandle0);
        
        ServiceHandle<?> rootHandle1 = roots.get(1).getRoot();
        Assert.assertNotNull(rootHandle1);
        
        Assert.assertNotSame(rootHandle0, handle);
        
        Assert.assertEquals(rootHandle0, rootHandle1);
    }
    
    /**
     * Ensures that a service that uses custom annotation for injection
     * point can be added via automatic class analysis even if the
     * context for it is not available.  (This tests the InjectionPointIndicator
     * annotation is working properly)
     */
    @Test
    public void testInjectionPointWithoutContext() {
        ServiceLocator locator = LocatorHelper.create();
        
        List<ActiveDescriptor<?>> added = ServiceLocatorUtilities.addClasses(locator,
                AnyplaceService.class);
        
        Assert.assertEquals(1, added.size());
        
        ActiveDescriptor<?> ad = added.get(0);
        
        List<Injectee> injectees = ad.getInjectees();
        
        boolean gotField = false;
        boolean gotConstructor = false;
        boolean gotMethod = false;
        
        for (Injectee injectee: injectees) {
            AnnotatedElement ae = injectee.getParent();
            Assert.assertNotNull(ae);
            
            if (ae instanceof Field) {
                Assert.assertFalse(gotField);
                gotField = true;
            }
            else if (ae instanceof Constructor) {
                Assert.assertFalse(gotConstructor);
                gotConstructor = true;
            }
            else if (ae instanceof Method) {
                Assert.assertFalse(gotMethod);
                gotMethod = true;
            }
            else {
                Assert.fail("Unknown type " + ae);
            }
        }
        
        Assert.assertTrue(gotField);
        Assert.assertTrue(gotConstructor);
        Assert.assertTrue(gotMethod);
    }
    
    /**
     * Ensures that a service that uses custom annotation for injection
     * point in a constructor parameter can be added via automatic class
     * analysis even if the context for it is not available.  (This tests
     * the InjectionPointIndicator Paraannotation is working properly)
     */
    @Test
    public void testConstructorParameterInjectionPointWithoutContext() {
        ServiceLocator locator = LocatorHelper.create();
        
        List<ActiveDescriptor<?>> added = ServiceLocatorUtilities.addClasses(locator,
                ConstructorParameterInjectionIndicatorService.class);
        
        Assert.assertEquals(1, added.size());
        
        ActiveDescriptor<?> ad = added.get(0);
        
        List<Injectee> injectees = ad.getInjectees();
        
        Assert.assertEquals(1, injectees.size());
        
        AnnotatedElement ae = injectees.get(0).getParent();
        Assert.assertNotNull(ae);
        
        Assert.assertTrue(ae instanceof Constructor);
    }
    
    /**
     * Ensures that a service that uses custom annotation for injection
     * point in a method parameter can be added via automatic class
     * analysis even if the context for it is not available.  (This tests
     * the InjectionPointIndicator Paraannotation is working properly)
     */
    @Test
    public void testMethodParameterInjectionPointWithoutContext() {
        ServiceLocator locator = LocatorHelper.create();
        
        List<ActiveDescriptor<?>> added = ServiceLocatorUtilities.addClasses(locator,
                MethodParameterInjectionIndicatorService.class);
        
        Assert.assertEquals(1, added.size());
        
        ActiveDescriptor<?> ad = added.get(0);
        
        List<Injectee> injectees = ad.getInjectees();
        
        Assert.assertEquals(1, injectees.size());
        
        AnnotatedElement ae = injectees.get(0).getParent();
        Assert.assertNotNull(ae);
        
        Assert.assertTrue(ae instanceof Method);
    }
}
