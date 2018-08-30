/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.jvnet.hk2.testing.test;

import java.util.LinkedList;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvnet.hk2.testing.junit.HK2Runner;

/**
 * Tests basic functionality
 * 
 * @author jwells
 */
public class RunnerTest extends HK2Runner {
    /** Alice as a name */
    public final static String ALICE = "Alice";
    /** Bob as a name */
    public final static String BOB = "bob";
    private final static String CAROL = "Carol";  // default Named
    
    @Inject
    private SimpleService3 injectMe;
    
    @Before
    public void before() {
        LinkedList<String> packages = new LinkedList<String>();
        packages.add(this.getClass().getPackage().getName());
        
        initialize(this.getClass().getName(), packages, null);
    }
    
    /**
     * Tests that services in this package are found
     */
    @Test
    public void testAllPackageServicesInLocator() {
        SimpleService0 ss0 = testLocator.getService(SimpleService0.class);
        Assert.assertNotNull(ss0);
        
        SimpleService1 ss1 = testLocator.getService(SimpleService1.class);
        Assert.assertNotNull(ss1);
    }
    
    /**
     * Tests a class that is JIT resolved
     */
    @Test
    public void testJITInjectedClass() {
        
        ServiceInjectedWithGuyNotInPackage nonPackageService = testLocator.getService(ServiceInjectedWithGuyNotInPackage.class);
        Assert.assertNotNull(nonPackageService);
        Assert.assertNotNull(nonPackageService.getAltService());
    }
    
    /**
     * Tests a class via contract and qualifier
     */
    @Test
    public void testByContractAndQualifier() {
        SimpleService ss = testLocator.getService(SimpleService.class, new IAmAQualifierImpl());
        Assert.assertNotNull(ss);
        
        Assert.assertTrue(ss instanceof SimpleService2);
    }
    
    /**
     * Tests a class with a name on Service
     */
    @Test
    public void testNameFromService() {
        SimpleService ss = testLocator.getService(SimpleService.class, ALICE);
        Assert.assertNotNull(ss);
        
        Assert.assertTrue(ss instanceof Alice);
    }
    
    /**
     * Tests a class with a name on Named (explicitly set)
     */
    @Test
    public void testNameFromNamedExplicit() {
        SimpleService ss = testLocator.getService(SimpleService.class, BOB);
        Assert.assertNotNull(ss);
        
        Assert.assertTrue(ss instanceof Bob);
    }
    
    /**
     * Tests a class with a name on Named (default set)
     */
    @Test
    public void testNameFromNamedDefault() {
        SimpleService ss = testLocator.getService(SimpleService.class, CAROL);
        Assert.assertNotNull(ss);
        
        Assert.assertTrue(ss instanceof Carol);
    }
    
    /**
     * Tests that the test itself gets injected
     */
    @Test
    public void testTestInjection() {
        Assert.assertNotNull(injectMe);
    }
    
    /**
     * Ensures that a complex test service is properly found
     */
    @Test
    public void testComplexService() {
        SimpleContract0 sc0 = testLocator.getService(SimpleContract0.class);
        
        Assert.assertNotNull(sc0);
        
    }
}
