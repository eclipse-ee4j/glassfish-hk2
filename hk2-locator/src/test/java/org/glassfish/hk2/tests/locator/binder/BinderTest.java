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

package org.glassfish.hk2.tests.locator.binder;

import java.lang.reflect.Type;

import javax.inject.Named;
import javax.inject.Singleton;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.Immediate;
import org.glassfish.hk2.api.ProxyCtl;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.Binder;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.NamedImpl;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.hk2.utilities.reflection.ParameterizedTypeImpl;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class BinderTest {
    private final static String TEST_NAME = "BinderTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, null);
    
    private final static String BIFUR = "Bifur";
    private final static String BOFUR = "Bofur";
    private final static String BOMBUR = "Bombur";
    
    /**
     * Tests adding in bindings
     */
    @Test
    public void testAddInAFewBindings() {
        Assert.assertNull(locator.getBestDescriptor(BuilderHelper.createContractFilter(Nazgul.class.getName())));
        Assert.assertNull(locator.getBestDescriptor(BuilderHelper.createContractFilter(Elves.class.getName())));
        
        ServiceLocatorUtilities.bind(locator, new NazgulBinder(), new ElvesBinder());
        
        Assert.assertNotNull(locator.getBestDescriptor(BuilderHelper.createContractFilter(Nazgul.class.getName())));
        Assert.assertNotNull(locator.getBestDescriptor(BuilderHelper.createContractFilter(Elves.class.getName())));
    }
    
    /**
     * Tests creating a new locator with bindings
     */
    @Test
    public void testCreateNewLocatorAndAddBindings() {
        ServiceLocator locator2 = ServiceLocatorUtilities.bind(TEST_NAME + "2", new NazgulBinder(), new ElvesBinder());
        
        Assert.assertNotNull(locator2.getBestDescriptor(BuilderHelper.createContractFilter(Nazgul.class.getName())));
        Assert.assertNotNull(locator2.getBestDescriptor(BuilderHelper.createContractFilter(Elves.class.getName())));
    }
    
    /**
     * Tests a constant factory
     */
    @Test
    public void testFactoryBindingWithConstantFactory() {
        ServiceLocator locator = ServiceLocatorFactory.getInstance().create(null);
        
        final MountDoom myFactory = new MountDoom();
        
        ServiceLocatorUtilities.bind(locator, new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(myFactory).to(RingOfPower.class).proxy(true).in(Singleton.class);
            }
            
        });
        
        Factory<RingOfPower> isaConstant = locator.getService(new ParameterizedTypeImpl(Factory.class, RingOfPower.class));
        Assert.assertTrue(isaConstant == myFactory);  // More than just equals
        
        RingOfPower oneRing = locator.getService(RingOfPower.class);
        Assert.assertNotNull(oneRing);

        // Make sure it is proxied
        Assert.assertTrue(oneRing instanceof ProxyCtl);
        ProxyCtl pc = (ProxyCtl) oneRing;
        
        RingOfPower secondRing = (RingOfPower) pc.__make();  // Makes sure factory gets called
        Assert.assertNotNull(secondRing);
    }
    
    /**
     * Tests a class factory
     */
    @Test
    public void testFactoryBindingWithClassFactory() {
        ServiceLocator locator = ServiceLocatorFactory.getInstance().create(null);
        
        ServiceLocatorUtilities.bind(locator, new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(MountDoom.class).to(RingOfPower.class).proxy(true).in(Singleton.class);
            }
            
        });
        
        Factory<RingOfPower> myFactory = locator.getService(new ParameterizedTypeImpl(Factory.class, RingOfPower.class));
        Assert.assertNotNull(myFactory);
        
        RingOfPower oneRing = locator.getService(RingOfPower.class);
        Assert.assertNotNull(oneRing);

        // Make sure it is proxied
        Assert.assertTrue(oneRing instanceof ProxyCtl);
        ProxyCtl pc = (ProxyCtl) oneRing;
        
        RingOfPower secondRing = (RingOfPower) pc.__make();  // Makes sure factory gets called
        Assert.assertNotNull(secondRing);
    }
    
    /**
     * Tests that you can use a {@link Type} for to
     */
    @Test
    public void testTypeTo() {
        ServiceLocator locator = ServiceLocatorFactory.getInstance().create(null);
        
        final Type bindTo = Legolas.class.getGenericInterfaces()[0];
        Assert.assertFalse(bindTo instanceof Class);
        
        ServiceLocatorUtilities.bind(locator, new AbstractBinder() {
            @Override
            protected void configure() {
                bind(Legolas.class).to(bindTo);
            }
            
        });
        
        CouncilOfElrond<Elves> legolas = locator.getService(new ParameterizedTypeImpl(CouncilOfElrond.class, Elves.class));
        Assert.assertNotNull(legolas);
        
        Assert.assertNull(locator.getService(new ParameterizedTypeImpl(CouncilOfElrond.class, Dwarves.class)));
    }
    
    /**
     * Tests that you can use a direct annotation for a name
     */
    @Test // @org.junit.Ignore
    public void testUseDirectNamedQualifierAsName() {
        ServiceLocator locator = ServiceLocatorFactory.getInstance().create(null);
        
        ServiceLocatorUtilities.bind(locator, new AbstractBinder() {
            @Override
            protected void configure() {
                bind(Dwarves.class).to(Dwarves.class).qualifiedBy(new NamedImpl(BIFUR));
                bind(Dwarves.class).to(Dwarves.class).qualifiedBy(new NamedImpl(BOFUR));
                bind(Dwarves.class).to(Dwarves.class).qualifiedBy(new NamedImpl(BOMBUR));
            }
            
        });
        
        Dwarves bifur = locator.getService(Dwarves.class, BIFUR);
        Dwarves bofur = locator.getService(Dwarves.class, BOFUR);
        Dwarves bombur = locator.getService(Dwarves.class, BOMBUR);
        
        Assert.assertNotNull(bifur);
        Assert.assertNotNull(bofur);
        Assert.assertNotNull(bombur);
        
        Assert.assertEquals(BIFUR, bifur.getName());
        Assert.assertEquals(BOFUR, bofur.getName());
        Assert.assertEquals(BOMBUR, bombur.getName());
        
        Assert.assertEquals(BIFUR, bifur.getNameViaQualifiers());
        Assert.assertEquals(BOFUR, bofur.getNameViaQualifiers());
        Assert.assertEquals(BOMBUR, bombur.getNameViaQualifiers());
    }
    
    /**
     * Tests that you can use a direct annotation for a name
     */
    @Test // @org.junit.Ignore
    public void testUseDirectNamedQualifierAsNameWithBuilderHelper() {
        Descriptor bifur = BuilderHelper.link(Dwarves.class.getName()).qualifiedBy(new NamedImpl(BIFUR)).build();
        Descriptor bofur = BuilderHelper.link(Dwarves.class.getName()).qualifiedBy(new NamedImpl(BOFUR)).build();
        Descriptor bombur = BuilderHelper.link(Dwarves.class.getName()).qualifiedBy(new NamedImpl(BOMBUR)).build();
        
        Assert.assertEquals(BIFUR, bifur.getName());
        Assert.assertEquals(BOFUR, bofur.getName());
        Assert.assertEquals(BOMBUR, bombur.getName());
        
        Assert.assertTrue(bifur.getQualifiers().contains(Named.class.getName()));
        Assert.assertTrue(bofur.getQualifiers().contains(Named.class.getName()));
        Assert.assertTrue(bombur.getQualifiers().contains(Named.class.getName()));
    }
    
    /**
     * Tests that you can use a direct annotation for a name
     */
    @Test // @org.junit.Ignore
    public void testUseDirectNamedQualifierAsNameWithBuilderHelperActive() {
        ActiveDescriptor<?> bifur = BuilderHelper.activeLink(Dwarves.class).qualifiedBy(new NamedImpl(BIFUR)).build();
        ActiveDescriptor<?> bofur = BuilderHelper.activeLink(Dwarves.class).qualifiedBy(new NamedImpl(BOFUR)).build();
        ActiveDescriptor<?> bombur = BuilderHelper.activeLink(Dwarves.class).qualifiedBy(new NamedImpl(BOMBUR)).build();
        
        Assert.assertEquals(BIFUR, bifur.getName());
        Assert.assertEquals(BOFUR, bofur.getName());
        Assert.assertEquals(BOMBUR, bombur.getName());
        
        Assert.assertTrue(bifur.getQualifiers().contains(Named.class.getName()));
        Assert.assertTrue(bofur.getQualifiers().contains(Named.class.getName()));
        Assert.assertTrue(bombur.getQualifiers().contains(Named.class.getName()));
        
        Assert.assertTrue(bifur.getQualifierAnnotations().contains(new NamedImpl(BIFUR)));
        Assert.assertTrue(bofur.getQualifierAnnotations().contains(new NamedImpl(BOFUR)));
        Assert.assertTrue(bombur.getQualifierAnnotations().contains(new NamedImpl(BOMBUR)));
    }
    
    /**
     * Tests that you can use a direct annotation for a name
     */
    @Test // @org.junit.Ignore
    public void testUseNameAsName() {
        ServiceLocator locator = ServiceLocatorFactory.getInstance().create(null);
        
        ServiceLocatorUtilities.bind(locator, new AbstractBinder() {
            @Override
            protected void configure() {
                bind(Dwarves.class).to(Dwarves.class).named(BIFUR);
                bind(Dwarves.class).to(Dwarves.class).named(BOFUR);
                bind(Dwarves.class).to(Dwarves.class).named(BOMBUR);
            }
            
        });
        
        Dwarves bifur = locator.getService(Dwarves.class, BIFUR);
        Dwarves bofur = locator.getService(Dwarves.class, BOFUR);
        Dwarves bombur = locator.getService(Dwarves.class, BOMBUR);
        
        Assert.assertNotNull(bifur);
        Assert.assertNotNull(bofur);
        Assert.assertNotNull(bombur);
        
        Assert.assertEquals(BIFUR, bifur.getName());
        Assert.assertEquals(BOFUR, bofur.getName());
        Assert.assertEquals(BOMBUR, bombur.getName());
        
        Assert.assertEquals(BIFUR, bifur.getNameViaQualifiers());
        Assert.assertEquals(BOFUR, bofur.getNameViaQualifiers());
        Assert.assertEquals(BOMBUR, bombur.getNameViaQualifiers());
    }
    
    /**
     * Tests that adding the whole annotation adds the class and string
     */
    @Test
    public void testDescWithAnnotationGivenDirectly() {
        ServiceLocator locator = ServiceLocatorFactory.getInstance().create(null);
        
        ServiceLocatorUtilities.bind(locator, new AbstractBinder() {
            @Override
            protected void configure() {
                bind(Dwarves.class).to(Dwarves.class).in(ServiceLocatorUtilities.getImmediateAnnotation());
            }
            
        });
        
        ActiveDescriptor<?> desc = locator.getBestDescriptor(BuilderHelper.createContractFilter(Dwarves.class.getName()));
        desc = locator.reifyDescriptor(desc);
        
        Assert.assertEquals(desc.getScopeAsAnnotation(), ServiceLocatorUtilities.getImmediateAnnotation());
        Assert.assertEquals(desc.getScopeAnnotation(), Immediate.class);
        Assert.assertEquals(desc.getScope(), Immediate.class.getName()); 
    }
    
    /**
     * Tests that you can use a direct annotation for a name
     */
    @Test
    public void testUseNameAsNameWithBuilderHelper() {
        Descriptor bifur = BuilderHelper.link(Dwarves.class.getName()).named(BIFUR).build();
        Descriptor bofur = BuilderHelper.link(Dwarves.class.getName()).named(BOFUR).build();
        Descriptor bombur = BuilderHelper.link(Dwarves.class.getName()).named(BOMBUR).build();
        
        Assert.assertEquals(BIFUR, bifur.getName());
        Assert.assertEquals(BOFUR, bofur.getName());
        Assert.assertEquals(BOMBUR, bombur.getName());
        
        Assert.assertTrue(bifur.getQualifiers().contains(Named.class.getName()));
        Assert.assertTrue(bofur.getQualifiers().contains(Named.class.getName()));
        Assert.assertTrue(bombur.getQualifiers().contains(Named.class.getName()));
    }
    
    
    
    private static class NazgulBinder implements Binder {

        /* (non-Javadoc)
         * @see org.glassfish.hk2.utilities.Binder#bind(org.glassfish.hk2.api.DynamicConfiguration)
         */
        @Override
        public void bind(DynamicConfiguration config) {
            config.bind(BuilderHelper.link(Nazgul.class.getName()).build());
        }
        
    }
    
    private static class ElvesBinder implements Binder {

        /* (non-Javadoc)
         * @see org.glassfish.hk2.utilities.Binder#bind(org.glassfish.hk2.api.DynamicConfiguration)
         */
        @Override
        public void bind(DynamicConfiguration config) {
            config.bind(BuilderHelper.link(Elves.class.getName()).build());
        }
        
    }

}
