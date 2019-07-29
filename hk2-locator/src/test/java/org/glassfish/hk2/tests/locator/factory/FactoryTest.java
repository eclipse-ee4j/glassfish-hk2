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

package org.glassfish.hk2.tests.locator.factory;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.FactoryDescriptors;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class FactoryTest {
    private final static String TEST_NAME = "FactoryTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new FactoryModule());
    
    /** Never told a lie.  If you believe him */
    public final static String WASHINGTON_NAME = "Washington";
    /** First president */
    public final static int WASHINGTON_NUMBER = 1;
    
    /** Wrote many historic documents */
    public final static String JEFFERSON_NAME = "Jefferson";
    /** Third president */
    public final static int JEFFERSON_NUMBER = 3;
    
    /** First Vice President */
    public final static int ADAMS_VP_NUMBER = 1;
    /** Second Vice President */
    public final static int JEFFERSON_VP_NUMBER = 2;
    /** Third Vice President */
    public final static int BURR_VP_NUMBER = 3;
    
    /**
     * A very simple factory test
     */
    @Test
    public void testSimpleFactory() {
        Date date = locator.getService(Date.class);
        Assert.assertNotNull(date);
    }
    
    /**
     * Factory injected with Provider
     */
    @Test
    public void testFactoryProvided() {
        DateInjectee dateInjectee = locator.getService(DateInjectee.class);
        Assert.assertNotNull(dateInjectee);
        
        Date rawDate = dateInjectee.getRawInject();
        Assert.assertNotNull(rawDate);
        
        Date providedDate1 = dateInjectee.getProvidedInject();
        Assert.assertNotNull(providedDate1);
        
        Date providedDate2 = dateInjectee.getProvidedInject();
        Assert.assertNotNull(providedDate2);
        
        Date optionalDate = dateInjectee.getOptionalInject();
        Assert.assertNotNull(optionalDate);
        
        Assert.assertNotSame(rawDate, providedDate1);
        Assert.assertNotSame(rawDate, providedDate2);
        Assert.assertNotSame(providedDate1, providedDate2);
    }
    
    /**
     * Factory into a custom scope
     */
    @Test
    public void testFactoryProducingIntoCustomScope() {
        FruitContext fruitContext = locator.getService(FruitContext.class);
        Assert.assertNotNull(fruitContext);
        
        // Nothing here yet, haven't asked for an apple
        Assert.assertTrue(fruitContext.getContextStoredFruits().isEmpty());
        
        Apple apple = locator.getService(Apple.class);
        Assert.assertNotNull(apple);
        
        Assert.assertEquals("Expected 1 apple but got " + fruitContext.getContextStoredFruits().size(),
                1, fruitContext.getContextStoredFruits().size());
        
        Assert.assertTrue(fruitContext.getContextStoredFruits().values().contains(apple));
        
        // Ask again, expect the same result back
        Apple apple2 = locator.getService(Apple.class);
        Assert.assertEquals(apple, apple2);
        
        Assert.assertEquals("Expected 1 apple but got " + fruitContext.getContextStoredFruits().size(),
                1, fruitContext.getContextStoredFruits().size());
    }
    
    /**
     * Factories of same type but different names
     */
    @Test
    public void testGetNamedFactories() {
        President washington = locator.getService(President.class, WASHINGTON_NAME);
        Assert.assertNotNull(washington);
        Assert.assertEquals(WASHINGTON_NUMBER, washington.getNumber());
        
        President jefferson = locator.getService(President.class, JEFFERSON_NAME);
        Assert.assertNotNull(jefferson);
        Assert.assertEquals(JEFFERSON_NUMBER, jefferson.getNumber());
    }
    
    /**
     * This tests a factory that was not created with buildFactory
     */
    @Test
    public void testFactoryCreatedWithBuild() {
        Widget widget = locator.getService(Widget.class);
        Assert.assertNotNull(widget);
    }
    
    /**
     * Tests a proxiable factory creating a proxiable service
     */
    @Test
    public void testProxiableAbstractFactoryProducingProxiableServices() {
        AdamsVP adamsVP = locator.getService(AdamsVP.class);
        Assert.assertNotNull(adamsVP);
        Assert.assertEquals(ADAMS_VP_NUMBER, adamsVP.getNumber());
    }
    
    /**
     * Tests a proxiable factory created a proxiable service using a
     * superclass
     */
    @Test
    public void testProxiableAbstractFactoryProducingProxiableServicesFromSuperclass() {
        JeffersonVP jeffersonVP = locator.getService(JeffersonVP.class);
        Assert.assertNotNull(jeffersonVP);
        Assert.assertEquals(JEFFERSON_VP_NUMBER, jeffersonVP.getNumber());
    }
    
    /**
     * Tests a proxiable factory where the provide method has a wildcard.
     * 
     * This uses a round-about way to get the burrVP service in order to ensure it works
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testProxiableFactoryWithWildcardProvideMethod() {
        ActiveDescriptor<?> burrVPDescriptor = locator.getBestDescriptor(BuilderHelper.createContractFilter(BurrVP.class.getName()));
        Assert.assertNotNull(burrVPDescriptor);
        
        ServiceHandle<BurrVP> handle = (ServiceHandle<BurrVP>) locator.getServiceHandle(burrVPDescriptor);
        
        BurrVP burrVP = handle.getService();
        Assert.assertNotNull(burrVP);
        Assert.assertEquals(BURR_VP_NUMBER, burrVP.getNumber());
    }
    
    /**
     * Tests that we can add a
     */
    @Test // @org.junit.Ignore
    public void testAddConstantFactory() {
        ServiceLocator locator = LocatorHelper.create();
        ServiceLocatorUtilities.addClasses(locator, ProxiableSingletonContext.class);
        
        BurrVPFactory burrFactoryConstant = new BurrVPFactory();
        
        List<FactoryDescriptors> added = ServiceLocatorUtilities.addFactoryConstants(locator, burrFactoryConstant);
        Assert.assertNotNull(added);
        Assert.assertEquals(1, added.size());
        
        BurrVP burr = locator.getService(BurrVP.class);
        Assert.assertNotNull(burr);
        Assert.assertEquals(BURR_VP_NUMBER, burr.getNumber());
        
        Factory<?> factory = locator.getService(Factory.class);
        Assert.assertTrue(factory.equals(burrFactoryConstant));
    }
}
