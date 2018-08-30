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

package org.glassfish.hk2.utilities.binding;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.glassfish.hk2.api.*;
import org.glassfish.hk2.utilities.Binder;
import org.glassfish.hk2.utilities.DescriptorImpl;
import org.glassfish.hk2.utilities.FactoryDescriptorsImpl;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Test;

import javax.inject.Singleton;

import static org.easymock.EasyMock.*;
import static org.glassfish.hk2.utilities.binding.BindingBuilderFactory.newBinder;
import static org.glassfish.hk2.utilities.binding.BindingBuilderFactory.newFactoryBinder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BindingTest {
    private final static String MY_CUSTOM_ANALYZER = "MyCustomAnalyzer";

    @Test
    public void testBindingBuilderFactory () {
        ServiceBindingBuilder<Foo> binderFactory = newBinder(Foo.class);

        HK2Loader hk2Loader = new HK2Loader() {
            @Override
            public Class<?> loadClass(String className) throws MultiException {
                try {
                    return getClass().getClassLoader().loadClass(className);
                } catch (ClassNotFoundException e) {
                    throw new MultiException(e);
                }

        }};

        ScopedNamedBindingBuilder<Foo> bindingBuilder = binderFactory.
                in(Singleton.class).
                loadedBy(hk2Loader).
                named("foo").
                withMetadata("foo", "bar").
                to(MyContract.class).
                analyzeWith(MY_CUSTOM_ANALYZER);

        DynamicConfiguration dc = EasyMock.createMock(DynamicConfiguration.class);

        DescriptorImpl expectedDescriptor = new DescriptorImpl();
        expectedDescriptor.setImplementation("org.glassfish.hk2.utilities.binding.BindingTest$Foo");
        expectedDescriptor.setScope(Singleton.class.getName());
        expectedDescriptor.setLoader(hk2Loader);
        expectedDescriptor.setName("foo");
        expectedDescriptor.addMetadata("foo", "bar");
        expectedDescriptor.addQualifier("javax.inject.Named");
        expectedDescriptor.addAdvertisedContract("org.glassfish.hk2.utilities.binding.BindingTest$MyContract");
        expectedDescriptor.setClassAnalysisName(MY_CUSTOM_ANALYZER);

        EasyMock.expect(dc.bind(expectedDescriptor, false)).andReturn(null);

        EasyMock.replay(dc);
        BindingBuilderFactory.addBinding(bindingBuilder, dc);

        EasyMock.verify(dc);

    }

    @Test
    public void testAbstractBinder() {
        Binder b = new AbstractBinder() {

            @Override
            protected void configure() {
                bind(new Foo()).to(MyContract.class);
            }
        };

      ServiceLocator sl = createMock(ServiceLocator.class);
      DynamicConfigurationService dcs = createMock(DynamicConfigurationService.class);
      DynamicConfiguration dc = createMock(DynamicConfiguration.class);

      // Find DynamicConfigurationService and create a DynamicConfiguration
      expect(sl.getService(DynamicConfigurationService.class)).andReturn(dcs);
      expect(dcs.createDynamicConfiguration()).andReturn(dc);

      // expect a descriptor to be bound, capture it so the fields can be checked later
      Capture<Descriptor> capturedDescriptor = new Capture<Descriptor>();

      expect(dc.bind(capture(capturedDescriptor), eq(false))).andReturn(null);

      dc.commit();
      expectLastCall();

      EasyMock.replay(sl,dcs,dc);
      ServiceLocatorUtilities.bind(sl, b);

      verify(sl, dcs, dc);

      assertEquals("Wrong implementation", "org.glassfish.hk2.utilities.binding.BindingTest$Foo", capturedDescriptor.getValue().getImplementation());
      assertTrue( "Missing contract", capturedDescriptor.getValue().getAdvertisedContracts().contains("org.glassfish.hk2.utilities.binding.BindingTest$MyContract"));

    }
    
    /**
     * Ensures that the provide method and the service both get the qualifiers
     */
    @Test
    public void testBindingBuilderFactoryPutsQualifiesOnBothProvideMethodAndService() {
        final Fantastic fantasticAnnotationLiteral = new FantasticLiteral(4);
        BindingBuilder<Widget> bb = newFactoryBinder(WidgetFactory.class, Singleton.class)
            .to(Widget.class)
            .in(PerLookup.class)
            .qualifiedBy(fantasticAnnotationLiteral);
        assertNotNull(bb);
        
        final DynamicConfiguration dc = createMock(DynamicConfiguration.class);

        final DescriptorImpl descriptorForFactoryAsAService = new DescriptorImpl();
        descriptorForFactoryAsAService.setImplementation(WidgetFactory.class.getName());
        descriptorForFactoryAsAService.setScope(Singleton.class.getName());
        descriptorForFactoryAsAService.addAdvertisedContract(Factory.class.getName());
        descriptorForFactoryAsAService.addQualifier(Fantastic.class.getName());

        final DescriptorImpl descriptorForFactoryAsProvideMethod = new DescriptorImpl();
        descriptorForFactoryAsProvideMethod.setImplementation(WidgetFactory.class.getName());
        descriptorForFactoryAsProvideMethod.setScope(PerLookup.class.getName());
        descriptorForFactoryAsProvideMethod.setDescriptorType(DescriptorType.PROVIDE_METHOD);
        descriptorForFactoryAsProvideMethod.addAdvertisedContract(Widget.class.getName());
        descriptorForFactoryAsProvideMethod.addQualifier(Fantastic.class.getName());

        final FactoryDescriptorsImpl factoryDescriptors = new FactoryDescriptorsImpl(descriptorForFactoryAsAService, descriptorForFactoryAsProvideMethod);

        expect(dc.bind(factoryDescriptors)).andReturn(null);
        replay(dc);
        
        BindingBuilderFactory.addBinding(bb, dc);

        verify(dc);
    }
    
    /**
     * Ensures that the provide method and the service with metadata and
     * onlye the provide method gets the metadata
     */
    @Test
    public void testBindingBuilderFactoryWithMetadata() {
        final Fantastic fantasticAnnotationLiteral = new FantasticLiteral(4);
        BindingBuilder<Widget> bb = newFactoryBinder(WidgetFactory.class, Singleton.class)
            .to(Widget.class)
            .in(PerLookup.class)
            .withMetadata("key", "value");
        assertNotNull(bb);
        
        final DynamicConfiguration dc = createMock(DynamicConfiguration.class);

        final DescriptorImpl descriptorForFactoryAsAService = new DescriptorImpl();
        descriptorForFactoryAsAService.setImplementation(WidgetFactory.class.getName());
        descriptorForFactoryAsAService.setScope(Singleton.class.getName());
        descriptorForFactoryAsAService.addAdvertisedContract(Factory.class.getName());
        descriptorForFactoryAsAService.addMetadata("key", "value");

        final DescriptorImpl descriptorForFactoryAsProvideMethod = new DescriptorImpl();
        descriptorForFactoryAsProvideMethod.setImplementation(WidgetFactory.class.getName());
        descriptorForFactoryAsProvideMethod.setScope(PerLookup.class.getName());
        descriptorForFactoryAsProvideMethod.setDescriptorType(DescriptorType.PROVIDE_METHOD);
        descriptorForFactoryAsProvideMethod.addAdvertisedContract(Widget.class.getName());
        descriptorForFactoryAsProvideMethod.addMetadata("key", "value");

        final FactoryDescriptorsImpl factoryDescriptors = new FactoryDescriptorsImpl(descriptorForFactoryAsAService, descriptorForFactoryAsProvideMethod);

        expect(dc.bind(factoryDescriptors)).andReturn(null);
        replay(dc);
        
        BindingBuilderFactory.addBinding(bb, dc);

        verify(dc);
    }
    
    /**
     * Makes sure this fails out with an NPE
     */
    @Test(expected=IllegalArgumentException.class)
    public void testNullPassedToCreateWithInstanceFails() {
        AbstractBindingBuilder.create(null);
    }

    class Foo implements MyContract{

    }


    public interface MyContract {}
    
    private static final class FantasticLiteral extends AnnotationLiteral<Fantastic> implements Fantastic {

        private final int level;

        private FantasticLiteral(final int level) {
            super();
            this.level = level;
        }

        @Override
        public int level() {
            return this.level;
        }
    }
}
