/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.locator.factory2;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.inject.Singleton;

import org.junit.Assert;
import org.junit.Test;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.FactoryDescriptors;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InstantiationData;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.DescriptorImpl;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

/**
 * @author jwells
 *
 */
public class Factory2Test {
    public static final String ALICE = "Alice";
    public static final String BOB = "Bob";
    public static final String CAROL = "Carol";
    
    /**
     * Tests that a factory can generate new services
     * based on the identity of the lookup
     */
    @Test // @org.junit.Ignore
    public void testFactoryCanCorrelate() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(CorrelatedServiceOne.class,
                CorrelatedServiceTwo.class,
                CorrelatedServiceThree.class,
                CorrelationFactory.class);
        
        CorrelatedServiceThree three = locator.getService(CorrelatedServiceThree.class);
        Assert.assertEquals(CAROL, three.getName());
        
        CorrelatedServiceOne one = locator.getService(CorrelatedServiceOne.class);
        Assert.assertEquals(ALICE, one.getName());
        
        CorrelatedServiceTwo two = locator.getService(CorrelatedServiceTwo.class);
        Assert.assertEquals(BOB, two.getName());
        
        // Twice because it is per lookup
        three = locator.getService(CorrelatedServiceThree.class);
        Assert.assertEquals(CAROL, three.getName());
    }
    
    /**
     * Tests that a factory can generate new services
     * based on the identity of the lookup.  This test
     * caused problems in the past because the unreified
     * ActiveDescriptor for the Factory tried to look
     * at the Injectees and failed for IllegalStateException
     */
    @Test // @org.junit.Ignore
    public void testFactoryCanCorrelateUnreifiedFactory() {
        ServiceLocator locator = LocatorHelper.getServiceLocator();
        ServiceLocatorUtilities.enableLookupExceptions(locator);
        
        DescriptorImpl correlatedOne = BuilderHelper.link(CorrelatedServiceOne.class.getName()).
                in(Singleton.class.getName()).
                qualifiedBy(Correlator.class.getName()).
                build();
        
        DescriptorImpl correlatedTwo = BuilderHelper.link(CorrelatedServiceTwo.class.getName()).
                in(Singleton.class.getName()).
                qualifiedBy(Correlator.class.getName()).
                build();
        
        DescriptorImpl correlatedThree = BuilderHelper.link(CorrelatedServiceThree.class.getName()).
                in(PerLookup.class.getName()).
                qualifiedBy(Correlator.class.getName()).
                build();
        
        ServiceLocatorUtilities.addOneDescriptor(locator, correlatedOne);
        ServiceLocatorUtilities.addOneDescriptor(locator, correlatedTwo);
        ServiceLocatorUtilities.addOneDescriptor(locator, correlatedThree);
        
        final AbstractActiveDescriptor<?> factoryDesc = BuilderHelper.activeLink(CorrelationFactory.class).
                to(Factory.class).
                in(Singleton.class).
                build();
        
        final AbstractActiveDescriptor<?> provideMethodDesc = BuilderHelper.activeLink(CorrelationFactory.class).
            to(PerLookupServiceWithName.class).
            in(PerLookup.class).
            buildProvideMethod();
        
        ServiceLocatorUtilities.addFactoryDescriptors(locator, new FactoryDescriptors() {

            @Override
            public Descriptor getFactoryAsAService() {
                return factoryDesc;
            }

            @Override
            public Descriptor getFactoryAsAFactory() {
                return provideMethodDesc;
            }
            
        });
        
        CorrelatedServiceThree three = locator.getService(CorrelatedServiceThree.class);
        Assert.assertEquals(CAROL, three.getName());
        
        CorrelatedServiceOne one = locator.getService(CorrelatedServiceOne.class);
        Assert.assertEquals(ALICE, one.getName());
        
        CorrelatedServiceTwo two = locator.getService(CorrelatedServiceTwo.class);
        Assert.assertEquals(BOB, two.getName());
        
        // Twice because it is per lookup
        three = locator.getService(CorrelatedServiceThree.class);
        Assert.assertEquals(CAROL, three.getName());
    }
    
    /**
     * Tests service injected with another service that comes from a factory
     * has the proper Injectee for the original service.  The lookup of the
     * original service is done via ServiceHandle
     */
    @Test // @org.junit.Ignore
    public void testGetInjecteeOfPerLookupInFactoryWithServiceHandle() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(InjectsPerLookupViaFactoryService.class,
                PerLookupFactory.class,
                SimplePerLookupServiceOne.class,
                SimplePerLookupServiceTwo.class,
                SingletonServiceFactory.class,
                NestedSingletonService.class);
        
        ServiceHandle<InjectsPerLookupViaFactoryService> handle = locator.getServiceHandle(InjectsPerLookupViaFactoryService.class);
        Assert.assertNotNull(handle);
        
        InjectsPerLookupViaFactoryService handleService = handle.getService();
        Injectee injectee = handleService.getParentInjectee();
        
        Assert.assertNotNull(injectee);
        Assert.assertNotNull(injectee.getParent());
        
        Assert.assertTrue(injectee.getParent() instanceof Field);
        Assert.assertEquals(((Field) injectee.getParent()).getType(), PerLookupService.class);
        
        SingletonService singleton = handleService.getFactoryCreatedSingletonService().getSingletonService();
        InstantiationData singletonData = singleton.getData();
        Field singletonField = (Field) singletonData.getParentInjectee().getParent();
        
        Assert.assertEquals(SingletonService.class, singletonField.getType());
        Assert.assertEquals(NestedSingletonService.class, singletonData.getParentInjectee().getInjecteeClass());
    }
    
    /**
     * Tests service injected with another service that comes from a factory
     * has the proper Injectee for the original service.  The lookup of the
     * original service is done via direct lookup
     */
    @Test // @org.junit.Ignore
    public void testGetInjecteeOfPerLookupInFactoryWithDirectService() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(InjectsPerLookupViaFactoryService.class,
                PerLookupFactory.class,
                SimplePerLookupServiceOne.class,
                SimplePerLookupServiceTwo.class,
                SingletonServiceFactory.class,
                NestedSingletonService.class);
        
        InjectsPerLookupViaFactoryService handleService = locator.getService(InjectsPerLookupViaFactoryService.class);
        Injectee injectee = handleService.getParentInjectee();
        
        Assert.assertNotNull(injectee);
        Assert.assertNotNull(injectee.getParent());
        
        Assert.assertTrue(injectee.getParent() instanceof Field);
    }
    
    /**
     * Tests service injected with another service that comes from a factory
     * has the proper Injectee for the original service.  The lookup of the
     * original service is done via direct lookup
     */
    @Test // @org.junit.Ignore
    public void testGetInjecteeOfProxyWithDirectService() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(InjectsProxiedService.class,
                ProxiedServiceFactory.class);
        InjectsProxiedService handleService = locator.getService(InjectsProxiedService.class);
        Injectee injectee = handleService.getProxiedInjectee();
        
        Assert.assertNotNull(injectee);
        Assert.assertNotNull(injectee.getParent());
        
        Assert.assertTrue(injectee.getParent() instanceof Method);
    }
    
    /**
     * Tests service injected with another service that comes from a factory
     * has the proper Injectee for the original service.  The lookup of the
     * original service is done via direct lookup
     */
    @Test // @org.junit.Ignore
    public void testGetInjecteeOfProxyWithHandleService() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(InjectsProxiedService.class,
                ProxiedServiceFactory.class);
        
        ServiceHandle<InjectsProxiedService> handleService = locator.getServiceHandle(InjectsProxiedService.class);
        Injectee injectee = handleService.getService().getProxiedInjectee();
        
        Assert.assertNotNull(injectee);
        Assert.assertNotNull(injectee.getParent());
        
        Assert.assertTrue(injectee.getParent() instanceof Method);
        
    }

}
