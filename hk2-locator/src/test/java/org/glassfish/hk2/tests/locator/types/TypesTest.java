/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.locator.types;

import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Descriptor;
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
public class TypesTest {
    public static final int INTEGER_FACTORY_VALUE = 13;
    public static final float FLOAT_KEY = (float) 14.00;
    public static final Double DOUBLE_VALUE = new Double(15.00);
    public static final String BEST_TEAM = "Eagles";
    public static final long LONG_FACTORY_VALUE = 16L;
    
    /**
     * FullService extends a typed abstract class, but is itself a
     * fully qualified version of that interface (String, String).
     * Hence, it should be injectable into InjectedService
     */
    @Test // @org.junit.Ignore
    public void testAbstractSuperclass() {
        ServiceLocator locator = LocatorHelper.create();
        
        ServiceLocatorUtilities.addClasses(locator,
                FullService.class, InjectedService.class);
        
        InjectedService is = locator.getService(InjectedService.class);
        Assert.assertNotNull(is);
        Assert.assertNotNull(is.getInjectedService());
    }
    
    /**
     * FullService extends a typed abstract class, but is itself a
     * fully qualified version of that interface (String, String).
     * Hence, it should be injectable into InjectedService
     */
    @Test // @org.junit.Ignore
    public void testAbstractSuperclassFromBasicDescriptor() {
        ServiceLocator locator = LocatorHelper.create();
        
        ServiceLocatorUtilities.addClasses(locator,
                InjectedService.class);
        
        Descriptor addMe = BuilderHelper.link(FullService.class.getName())
            .to(ServiceInterface.class.getName())
            .in(Singleton.class.getName()).build();
        
        ActiveDescriptor<?> added = ServiceLocatorUtilities.addOneDescriptor(locator, addMe);
        
        added = locator.reifyDescriptor(added);
        
        InjectedService is = locator.getService(InjectedService.class);
        Assert.assertNotNull(is);
        Assert.assertNotNull(is.getInjectedService());
    }
    
    /**
     * InjectedBaseClass has injected types that are fully specified as classes by the subclass
     */
    @Test // @org.junit.Ignore
    public void testSuperclassHasTypeInjectees() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(AlphaService.class,
                BetaService.class,
                AlphaInjectedService.class,
                BetaInjectedService.class);
        
        {
            AlphaInjectedService ais = locator.getService(AlphaInjectedService.class);
        
            Assert.assertNotNull(ais.getFromConstructor());
            Assert.assertTrue(ais.getFromConstructor() instanceof AlphaService);
        
            Assert.assertNotNull(ais.getFromField());
            Assert.assertTrue(ais.getFromField() instanceof AlphaService);
            
            Assert.assertNotNull(ais.getFromMethod());
            Assert.assertTrue(ais.getFromMethod() instanceof AlphaService);
        }
        
        {
            BetaInjectedService bis = locator.getService(BetaInjectedService.class);
        
            Assert.assertNotNull(bis.getFromConstructor());
            Assert.assertTrue(bis.getFromConstructor() instanceof BetaService);
        
            Assert.assertNotNull(bis.getFromField());
            Assert.assertTrue(bis.getFromField() instanceof BetaService);
            
            Assert.assertNotNull(bis.getFromMethod());
            Assert.assertTrue(bis.getFromMethod() instanceof BetaService);
        }
        
    }
    
    /**
     * Tests that services can have parameterized types all filled in
     * by the subclasses
     */
    @Test // @org.junit.Ignore
    public void testHardenedParameterizedTypes() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(
                ListMapServiceIntIntLong.class,
                ListMapServiceStringFloatDouble.class,
                IntLongMapFactory.class,
                FloatDoubleMapFactory.class,
                ListIntFactory.class,
                ListStringFactory.class);
        
        {
            ListMapServiceIntIntLong iil = locator.getService(ListMapServiceIntIntLong.class);
        
            List<Integer> aList = iil.getAList();
            Assert.assertNotNull(aList);
            
            Map<Integer, Long> aMap = iil.getAMap();
            Assert.assertNotNull(aMap);
            
            Assert.assertEquals(aList, iil.getIList());
            Assert.assertEquals(aMap, iil.getIMap());
            
            int fromList = aList.get(0);
            Assert.assertEquals(INTEGER_FACTORY_VALUE, fromList);
            
            long fromMap = aMap.get(INTEGER_FACTORY_VALUE);
            Assert.assertEquals(LONG_FACTORY_VALUE, fromMap);
        }
        
        {
            ListMapServiceStringFloatDouble sfd = locator.getService(ListMapServiceStringFloatDouble.class);
        
            List<String> aList = sfd.getAList();
            Assert.assertNotNull(aList);
            
            Map<Float, Double> aMap = sfd.getAMap();
            Assert.assertNotNull(aMap);
            
            Assert.assertEquals(aList, sfd.getIList());
            Assert.assertEquals(aMap, sfd.getIMap());
            
            String fromList = aList.get(0);
            Assert.assertEquals(BEST_TEAM, fromList);
            
            Double fromMap = aMap.get(FLOAT_KEY);
            Assert.assertEquals(DOUBLE_VALUE, fromMap);
        }
    }
    
    /**
     * Tests that services can have parameterized types all filled in
     * by the subclasses
     */
    @Test // @org.junit.Ignore
    public void testHardenedArrayTypes() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(
                AlphaArrayFactory.class,
                BetaArrayFactory.class,
                BetaArrayInjectedService.class,
                AlphaArrayInjectedService.class);
        
        {
            AlphaArrayInjectedService aais = locator.getService(AlphaArrayInjectedService.class);
            
            AlphaService as[] = aais.getFromField();
            Assert.assertNotNull(as);
            Assert.assertEquals(0, as.length);
        
            as = aais.getFromMethod();
            Assert.assertNotNull(as);
            Assert.assertEquals(0, as.length);
        }
        
        {
            BetaArrayInjectedService bais = locator.getService(BetaArrayInjectedService.class);
            
            BetaService bs[] = bais.getFromField();
            Assert.assertNotNull(bs);
            Assert.assertEquals(0, bs.length);
        
            bs = bais.getFromMethod();
            Assert.assertNotNull(bs);
            Assert.assertEquals(0, bs.length);
        }
        
        
    }

}
