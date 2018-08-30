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

package org.glassfish.hk2.tests.locator.customresolver;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class CustomResolverTest {
    private final static String TEST_NAME = "CustomResolverTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new CustomResolverModule());

    /**
     * Tests custom resolution
     */
    @Test
    public void testCustomInjectResolver() {
        ServiceWithCustomInjections cwci = locator.getService(ServiceWithCustomInjections.class);
        Assert.assertNotNull(cwci);

        Assert.assertTrue(cwci.isValid());
    }

    /**
     * Tests custom resolution with the resolver on the constuctor (and only valid on the constructor)
     */
    @Test
    public void testConstructorOnly() {
        ConstructorOnlyInjectedService service = locator.getService(ConstructorOnlyInjectedService.class);
        Assert.assertNotNull(service);

        Assert.assertNotNull(service.getViaConstructor());
        Assert.assertNull(service.getViaMethod());
    }

    /**
     * Tests custom resolution with the resolver on the method (and only valid on the method)
     */
    @Test
    public void testMethodOnly() {
        MethodOnlyInjectedService service = locator.getService(MethodOnlyInjectedService.class);
        Assert.assertNotNull(service);

        Assert.assertNull(service.getViaConstructor());
        Assert.assertNotNull(service.getViaMethod());
    }

    /**
     * Tests custom resolution with the resolver on both the constructor and the method
     */
    @Test
    public void testBothMethodAndConstructor() {
        ParameterInjectionService service = locator.getService(ParameterInjectionService.class);
        Assert.assertNotNull(service);

        Assert.assertNotNull(service.getViaConstructor());
        Assert.assertNotNull(service.getViaMethod());
    }
    
    /**
     * Tests custom resolution with the resolver on both the constructor and the method
     */
    @Test
    public void testBothMethodAndConstructorDuex() {
        ParameterInjectionServiceDuex service = locator.getService(ParameterInjectionServiceDuex.class);
        Assert.assertNotNull(service);

        Assert.assertNotNull(service.getViaConstructor());
        Assert.assertNotNull(service.getViaMethod());
        
        Assert.assertNotNull(service.getViaConstructorDuex());
        Assert.assertNotNull(service.getViaMethodDuex());
    }

    /**
     * Tests custom resolution with two different resolvers and injection annotations
     */
    @Test
    public void testDifferentParametersInConstructor() {
        ParameterABInjectionService service = locator.getService(ParameterABInjectionService.class);
        Assert.assertNotNull(service);

        Assert.assertEquals("Parameter A", service.getParameterA());
        Assert.assertEquals("Parameter B", service.getParameterB());
        Assert.assertEquals("Parameter A", service.getAnotherParameterA());
    }
    
    /**
     * Tests custom resolution
     */
    @Test // @org.junit.Ignore
    public void testCustomInjectResolverInChild() {
        ServiceLocator child = LocatorHelper.create(locator);
        
        ServiceLocatorUtilities.addClasses(child, ServiceWithCustomInjections2.class);
        
        ServiceWithCustomInjections2 cwci = child.getService(ServiceWithCustomInjections2.class);
        Assert.assertNotNull(cwci);

        Assert.assertTrue(cwci.isValid());
        
        child.shutdown();
        
        ServiceWithCustomInjections cwci1 = locator.getService(ServiceWithCustomInjections.class);
        Assert.assertNotNull(cwci1);

        Assert.assertTrue(cwci1.isValid());
    }

}
