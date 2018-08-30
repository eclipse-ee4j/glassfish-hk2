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

package org.glassfish.hk2.tests.locator.initialization;

import java.util.List;

import junit.framework.Assert;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.extension.ServiceLocatorGenerator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.junit.Test;
import org.jvnet.hk2.external.generator.ServiceLocatorGeneratorImpl;

/**
 * @author jwells
 *
 */
public class InitializationTest {
    /** name of a test class A */
    public final static String TEST_CLASS_A = "this.thing.isnt.actually.There";
    /** name of test class B */
    public final static String TEST_CLASS_B = "this.thing.isnt.added.in.the.Module";
    /** The name of this test */
    public final static String SIMPLE_NAME = "InitializationTest";
    
    private final static Filter aFilter = BuilderHelper.createContractFilter(InitializationTest.TEST_CLASS_A);
    private final static Filter bFilter = BuilderHelper.createContractFilter(InitializationTest.TEST_CLASS_B);
    private final static Filter namedFilter = BuilderHelper.createNameFilter(SIMPLE_NAME);
    
    private final static ServiceLocator locator = LocatorHelper.create(SIMPLE_NAME, new InitializationModule());
    
    /**
     * Gets the name of the locator
     */
    @Test
    public void testGetName() {
        Assert.assertEquals(SIMPLE_NAME, locator.getName());
    }
    
    /**
     * Ensures the expected descriptors are there
     */
    @Test
    public void testFindDescriptors() {
        List<ActiveDescriptor<?>> descriptors = locator.getDescriptors(aFilter);
        Assert.assertNotNull(descriptors);
        Assert.assertTrue(descriptors.size() == 2);
        
        long bestId = -1L;
        long lastId = -1L;
        for (Descriptor d : descriptors) {
            Assert.assertEquals(TEST_CLASS_A, d.getImplementation());
            
            long id = d.getServiceId().longValue();
            
            if (bestId < 0L) {
                bestId = id;
            }
            
            Assert.assertTrue("lastId=" + lastId + " currentId=" + id, lastId < id);
            lastId = id;
        }
        
        Descriptor bestDescriptor = locator.getBestDescriptor(aFilter);
        Assert.assertNotNull(bestDescriptor);
        
        Assert.assertEquals(bestId, bestDescriptor.getServiceId().longValue());
    }
    
    /**
     * Tests a descriptor that is not there
     */
    @Test
    public void testDidNotFindDescriptors() {
        List<ActiveDescriptor<?>> descriptors = locator.getDescriptors(bFilter);
        Assert.assertNotNull(descriptors);
        Assert.assertTrue(descriptors.size() == 0);
        
        Assert.assertNull(locator.getBestDescriptor(bFilter));
    }
    
    /**
     * Tests a filter that matches nothing at all
     */
    @Test
    public void testNoMatchFilter() {
        List<ActiveDescriptor<?>> descriptors = locator.getDescriptors(new Filter() {

            @Override
            public boolean matches(Descriptor d) {
                // This silly filter matches nothing!
                return false;
            }
        });
        
        Assert.assertNotNull(descriptors);
        Assert.assertTrue(descriptors.size() == 0);
        
        Assert.assertNull(locator.getBestDescriptor(bFilter));
    }
    
    /**
     * Tests looking up via name
     */
    @Test
    public void testLookupByName() {
        List<ActiveDescriptor<?>> descriptors = locator.getDescriptors(namedFilter);
        
        Assert.assertNotNull(descriptors);
        Assert.assertTrue("Expecting 1 descriptor, found " + descriptors.size(), descriptors.size() == 1);
        
        for (Descriptor d : descriptors) {
            Assert.assertEquals(SIMPLE_NAME, d.getName());
        }
        
        Descriptor d = locator.getBestDescriptor(namedFilter);
        Assert.assertNotNull(d);
        
        Assert.assertEquals(SIMPLE_NAME, d.getName());
    }
    
    /**
     * Tests a faulty filter
     */
    @Test(expected=IllegalArgumentException.class)
    public void testBadFilter() {
        locator.getDescriptors(null);
    }
    
    /**
     * Tests another faulty filter
     */
    @Test(expected=IllegalArgumentException.class)
    public void testBadBestFilter() {
        locator.getBestDescriptor(null);
    }
    
    /**
     * Tests that a service can be reified due to method injection
     */
    @Test
    public void testMethodReification() {
        SimpleServiceMethodInjectee ssmi = locator.getService(SimpleServiceMethodInjectee.class);
        Assert.assertNotNull(ssmi.getSimpleService());
    }

    /**
     * Tests that we can create with a specific user generator
     */
    @Test
    public void testServiceLocatorFactoryWithSpecificGenerator() {
        ServiceLocatorGenerator generator = new ServiceLocatorGeneratorImpl();
        ServiceLocatorFactory factory = ServiceLocatorFactory.getInstance();
        
        ServiceLocator sl1 = factory.create(null, null, generator);
        Assert.assertNotNull(sl1);
        
        ServiceLocator sl2 = factory.create(null, sl1, generator);
        Assert.assertNotNull(sl2);
    }
}
