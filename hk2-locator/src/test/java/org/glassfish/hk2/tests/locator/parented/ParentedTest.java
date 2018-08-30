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

package org.glassfish.hk2.tests.locator.parented;

import java.util.List;

import javax.inject.Inject;

import org.glassfish.hk2.api.ClassAnalyzer;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.Binder;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.NamedImpl;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Assert;
import org.junit.Test;
import org.jvnet.hk2.external.runtime.ServiceLocatorRuntimeBean;

/**
 * @author jwells
 *
 */
public class ParentedTest {
    private final ServiceLocatorFactory factory = ServiceLocatorFactory.getInstance();
    
    private final static String TEST_NAME = "ParentedTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new ParentedModule());
    
    private final static String GRANDPA = "Grandpa";
    private final static String DAD = "Dad";
    private final static String ME = "Me";
    
    private final static String CHILD1 = "Child1";
    private final static String CHILD2 = "Child2";
    
    private final static String PARENT3 = "Parent3";
    private final static String CHILD3 = "Child3";
    
    private final static String GRANDPARENT4 = "Grandparent4";
    private final static String PARENT4 = "Parent4";
    private final static String CHILD4 = "Child4";
    
    private final static String PARENT5 = "Parent5";
    private final static String CHILD5 = "Child5";
    
    private final static String PARENT6 = "Parent6";
    private final static String CHILD6 = "Child6";
    
    private final static String PARENT7 = "Parent7";
    private final static String CHILD7 = "Child7";
    
    private final static String PARENT8 = "Parent8";
    private final static String CHILD8 = "Child8";
    
    private final static String PARENT9 = "Parent9";
    private final static String CHILD9 = "Child9";
    
    /**
     * Tests three generations of locators
     */
    @Test
    public void testBasicParenting() {
        ServiceLocator grandpa = factory.create(GRANDPA);
        ServiceLocator dad = factory.create(DAD, grandpa);
        ServiceLocator me = factory.create(ME, dad);
        
        SimpleService grandPaSimpleService = new SimpleService();
        SimpleService dadSimpleService = new SimpleService();
        SimpleService meSimpleService = new SimpleService();
        
        ServiceLocatorUtilities.addOneConstant(grandpa, grandPaSimpleService);
        ServiceLocatorUtilities.addOneConstant(dad, dadSimpleService);
        ServiceLocatorUtilities.addOneConstant(me, meSimpleService);
        
        List<SimpleService> grandpaLocators = grandpa.getAllServices(SimpleService.class);
        List<SimpleService> dadLocators = dad.getAllServices(SimpleService.class);
        List<SimpleService> meLocators = me.getAllServices(SimpleService.class);
        
        Assert.assertNotNull(grandpaLocators);
        Assert.assertEquals(1, grandpaLocators.size());
        Assert.assertEquals(grandPaSimpleService, grandpaLocators.get(0));
        
        Assert.assertNotNull(dadLocators);
        Assert.assertEquals(2, dadLocators.size());
        Assert.assertEquals(dadSimpleService, dadLocators.get(0));
        Assert.assertEquals(grandPaSimpleService, dadLocators.get(1));
        
        Assert.assertNotNull(meLocators);
        Assert.assertEquals(3, meLocators.size());
        Assert.assertEquals(meSimpleService, meLocators.get(0));
        Assert.assertEquals(dadSimpleService, meLocators.get(1));
        Assert.assertEquals(grandPaSimpleService, meLocators.get(2));
        
        // Make sure the most normal case returns the right guy
        Assert.assertEquals(meSimpleService, me.getService(SimpleService.class));
    }
    
    /**
     * Tests that a child getting shutdown will not adversly affect the services
     * in the parent
     */
    @Test
    public void testShutdownOfChild() {
        ServiceLocator child1 = factory.create(CHILD1, locator);
        
        ServiceLocatorUtilities.bind(child1, new Binder() {

            @Override
            public void bind(DynamicConfiguration config) {
                config.addActiveDescriptor(SingletonServiceInChild.class);
                
            }
            
        });
        
        SingletonServiceInParent ssip = child1.getService(SingletonServiceInParent.class);
        Assert.assertNotNull(ssip);
        Assert.assertFalse(ssip.isShutdown());
        
        SingletonServiceInChild ssic = child1.getService(SingletonServiceInChild.class);
        Assert.assertNotNull(ssic);
        Assert.assertFalse(ssic.isShutdown());
        
        // Shutting down the child should NOT shutdown the service in the parent
        factory.destroy(child1);
        
        Assert.assertFalse(ssip.isShutdown());
        Assert.assertTrue(ssic.isShutdown());
    }
    
    /**
     * First looks up a service in a parent-defined context
     * from the child, the closes the child and looks it up
     * again from the parent.
     * 
     * This test was inspired by http://fuegotracker.ar.oracle.com/browse/FPP-480
     */
    @Test
    public void testContextInParent() {
        ServiceLocator child2 = factory.create(CHILD2, locator);
        
        ServiceWithParentContextInjectionPoint swpcip = child2.getService(ServiceWithParentContextInjectionPoint.class);
        Assert.assertNotNull(swpcip);
        
        factory.destroy(child2);
        
        swpcip = locator.getService(ServiceWithParentContextInjectionPoint.class);
        Assert.assertNotNull(swpcip);
    }
    
    /**
     * Tests that if we dynamically add a descriptor to the parent AFTER
     * it has been looked up in the child that we can find it in the child
     */
    @Test
    public void testDynamicallyAddServiceToParentAfterALookup() {
        ServiceLocator parent3 = factory.create(PARENT3);
        ServiceLocator child3 = factory.create(CHILD3, parent3);
        
        Assert.assertNull(child3.getService(SimpleService.class));
        
        // Now add the service in the parent
        Descriptor d = BuilderHelper.link(SimpleService.class).build();
        
        ServiceLocatorUtilities.addOneDescriptor(parent3, d);
        
        Assert.assertNotNull(child3.getService(SimpleService.class));
    }
    
    /**
     * Tests that if we dynamically add a descriptor to the GRAND-parent AFTER
     * it has been looked up in the child that we can find it in the child
     */
    @Test
    public void testDynamicallyAddServiceToGrandParentAfterALookup() {
        ServiceLocator grandparent4 = factory.create(GRANDPARENT4);
        ServiceLocator parent4 = factory.create(PARENT4, grandparent4);
        ServiceLocator child4 = factory.create(CHILD4, parent4);
        
        Assert.assertNull(child4.getService(SimpleService.class));
        
        // Now add the service in the parent
        Descriptor d = BuilderHelper.link(SimpleService.class).build();
        
        ServiceLocatorUtilities.addOneDescriptor(grandparent4, d);
        
        Assert.assertNotNull(child4.getService(SimpleService.class));
    }
    
    /**
     * Tests that ServiceLocator is a LOCAL service
     */
    @Test
    public void testServiceLocatorIsLocal() {
        ServiceLocator parent5 = factory.create(PARENT5);
        ServiceLocator child5 = factory.create(CHILD5, parent5);
        
        List<ServiceLocator> parentLocators = parent5.getAllServices(ServiceLocator.class);
        List<ServiceLocator> childLocators = child5.getAllServices(ServiceLocator.class);
        
        Assert.assertEquals(1, parentLocators.size());
        Assert.assertEquals(1, childLocators.size());
        
        Assert.assertEquals(parent5, parentLocators.get(0));
        Assert.assertEquals(child5, childLocators.get(0));
        
        Assert.assertEquals(parent5, parent5.getService(ServiceLocator.class));
        Assert.assertEquals(child5, child5.getService(ServiceLocator.class));
        
    }
    
    /**
     * Tests that DynamicConfigurationService is a LOCAL service
     */
    @Test
    public void testDynamicConfigurationIsLocal() {
        ServiceLocator parent6 = factory.create(PARENT6);
        ServiceLocator child6 = factory.create(CHILD6, parent6);
        
        List<DynamicConfigurationService> parentLocators = parent6.getAllServices(DynamicConfigurationService.class);
        List<DynamicConfigurationService> childLocators = child6.getAllServices(DynamicConfigurationService.class);
        
        Assert.assertEquals(1, parentLocators.size());
        Assert.assertEquals(1, childLocators.size());
        
        Assert.assertEquals(parent6.getService(DynamicConfigurationService.class), parentLocators.get(0));
        Assert.assertEquals(child6.getService(DynamicConfigurationService.class), childLocators.get(0));
    }
    
    /**
     * Tests that getParent works properly
     */
    @Test
    public void testGetParent() {
        ServiceLocator parent7 = factory.create(PARENT7);
        ServiceLocator child7 = factory.create(CHILD7, parent7);
        
        ServiceLocator gottenParent7 = child7.getParent();
        Assert.assertNotNull(gottenParent7);
        
        Assert.assertEquals(PARENT7, gottenParent7.getName());
        
        Assert.assertNull(gottenParent7.getParent());
    }
    
    /**
     * Tests a context from the parent is not destroyed when the child is destroyed
     */
    @Test
    public void testParentContextNotDestroyedUponChildDestruction() {
        ServiceLocator parent = factory.create(PARENT8);
        ServiceLocator child = factory.create(CHILD8, parent);
        
        ServiceLocatorUtilities.addClasses(parent, CustomPerLookupContext.class);
        
        ServiceLocatorUtilities.addClasses(parent, CustomPerLookupServiceInParent.class);
        ServiceLocatorUtilities.addClasses(child, CustomPerLookupServiceInChild.class);
        
        CustomPerLookupServiceInChild ptsic = child.getService(CustomPerLookupServiceInChild.class);
        
        Assert.assertNotNull(ptsic);
        
        factory.destroy(child);
        
        // Make sure we can still use the PerThread context after the child was destroyed!
        CustomPerLookupServiceInParent ptsip = parent.getService(CustomPerLookupServiceInParent.class);
        
        Assert.assertNotNull(ptsip);
    }
    
    /**
     * Tests a context from the parent is not destroyed when the child is destroyed
     */
    @Test
    public void testOtherInitialServicesAllLocal() {
        ServiceLocator parent = factory.create(PARENT9);
        ServiceLocator child = factory.create(CHILD9, parent);
        
        TypeLiteral<InjectionResolver<Inject>> threeThirtyLiteral = new TypeLiteral<InjectionResolver<Inject>>() {};
        
        Assert.assertEquals(1, child.getAllServices(ClassAnalyzer.class, new NamedImpl(ClassAnalyzer.DEFAULT_IMPLEMENTATION_NAME)).size());
        Assert.assertEquals(1, child.getAllServices(ServiceLocatorRuntimeBean.class).size());
        Assert.assertEquals(1, child.getAllServices(threeThirtyLiteral.getType(), new NamedImpl(InjectionResolver.SYSTEM_RESOLVER_NAME)).size());
    }
}
