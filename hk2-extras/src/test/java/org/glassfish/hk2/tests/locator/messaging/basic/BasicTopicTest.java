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

package org.glassfish.hk2.tests.locator.messaging.basic;

import java.util.List;

import javax.inject.Singleton;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.FactoryDescriptors;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.messaging.MessageReceiver;
import org.glassfish.hk2.extras.events.internal.TopicDistributionModule;
import org.glassfish.hk2.tests.extras.internal.Utilities;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.ImmediateScopeModule;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * @author jwells
 *
 */
public class BasicTopicTest {
    @Rule
    public TestWatcher watchmap = new TestWatcher() {
        @Override
        public void starting(Description d) {
            System.out.println("BasicTopicTest " + d.getMethodName() + " is starting");
        }
        
        @Override
        public void succeeded(Description d) {
            System.out.println("BasicTopicTest " + d.getMethodName() + " has succeeded");
        }
        
        @Override
        public void failed(Throwable th, Description d) {
            System.out.println("BasicTopicTest " + d.getMethodName() + " has failed");
        }
        
    };
    
    /**
     * Tests the most basic form of topic/subscriber
     */
    @Test
    public void testEventDistributedToAllSubscribers() {
        ServiceLocator locator = Utilities.getLocatorWithTopics();
        
        ServiceLocatorUtilities.enableImmediateScope(locator);
        
        ServiceLocatorUtilities.addClasses(locator, FooPublisher.class,
                ImmediateSubscriber.class,
                PerLookupSubscriber.class,
                SingletonSubscriber.class);
        
        FooPublisher publisher = locator.getService(FooPublisher.class);
        SingletonSubscriber singletonSubscriber = locator.getService(SingletonSubscriber.class);
        ImmediateSubscriber immediateSubscriber = locator.getService(ImmediateSubscriber.class);
        
        publisher.publishFoo(12);
        
        Foo singletonFoo = singletonSubscriber.getAndClearLastEvent();
        Assert.assertNotNull(singletonFoo);
        Assert.assertEquals(12, singletonFoo.getFooValue());
        
        Foo perLookupFoo1 = singletonSubscriber.getAndClearDependentLastEvent();
        Assert.assertNotNull(perLookupFoo1);
        Assert.assertEquals(12, perLookupFoo1.getFooValue());
        
        Foo immediateFoo = immediateSubscriber.getAndClearLastEvent();
        Assert.assertNotNull(immediateFoo);
        Assert.assertEquals(12, immediateFoo.getFooValue());
        
        Foo perLookupFoo2 = immediateSubscriber.getAndClearDependentLastEvent();
        Assert.assertNotNull(perLookupFoo2);
        Assert.assertEquals(12, perLookupFoo2.getFooValue());
    }
    
    /**
     * Tests the most basic form of topic/subscriber via module
     * initialization
     */
    @Test
    public void testEventDistributedToAllSubscribersViaModules() {
        ServiceLocator locator = Utilities.getUniqueLocator();
        
        ServiceLocatorUtilities.bind(locator, new TopicDistributionModule(),
                new ImmediateScopeModule());
        
        ServiceLocatorUtilities.addClasses(locator, FooPublisher.class,
                ImmediateSubscriber.class,
                PerLookupSubscriber.class,
                SingletonSubscriber.class);
        
        FooPublisher publisher = locator.getService(FooPublisher.class);
        SingletonSubscriber singletonSubscriber = locator.getService(SingletonSubscriber.class);
        ImmediateSubscriber immediateSubscriber = locator.getService(ImmediateSubscriber.class);
        
        publisher.publishFoo(12);
        
        Foo singletonFoo = singletonSubscriber.getAndClearLastEvent();
        Assert.assertNotNull(singletonFoo);
        Assert.assertEquals(12, singletonFoo.getFooValue());
        
        Foo perLookupFoo1 = singletonSubscriber.getAndClearDependentLastEvent();
        Assert.assertNotNull(perLookupFoo1);
        Assert.assertEquals(12, perLookupFoo1.getFooValue());
        
        Foo immediateFoo = immediateSubscriber.getAndClearLastEvent();
        Assert.assertNotNull(immediateFoo);
        Assert.assertEquals(12, immediateFoo.getFooValue());
        
        Foo perLookupFoo2 = immediateSubscriber.getAndClearDependentLastEvent();
        Assert.assertNotNull(perLookupFoo2);
        Assert.assertEquals(12, perLookupFoo2.getFooValue());
    }
    
    /**
     * Tests a single subscriber with many different subscription methods
     */
    @Test
    public void testEventDistributedToAllSubscribersOnOneService() {
        ServiceLocator locator = Utilities.getLocatorWithTopics();
        
        ServiceLocatorUtilities.addClasses(locator, FooPublisher.class,
                PerLookupService.class,
                SingletonService.class,
                SubscriberWithInjectionPoints.class);
        
        FooPublisher publisher = locator.getService(FooPublisher.class);
        
        SubscriberWithInjectionPoints subscriber = locator.getService(SubscriberWithInjectionPoints.class);
        
        publisher.publishFoo(0);
        
        subscriber.check();
        
    }
    
    /**
     * Tests a single subscriber subscribing to different Types
     */
    @Test
    public void testEventDistributionByType() {
        ServiceLocator locator = Utilities.getLocatorWithTopics();
        
        ServiceLocatorUtilities.addClasses(locator, FooPublisher.class,
                ColorPublisher.class,
                DifferentTypesSubscriber.class);
        
        FooPublisher publisher = locator.getService(FooPublisher.class);
        ColorPublisher colorPublisher = locator.getService(ColorPublisher.class);
        
        DifferentTypesSubscriber subscriber = locator.getService(DifferentTypesSubscriber.class);
        
        // Will only activate the Foo subscription, not the Bar subscription
        publisher.publishFoo(1);
        
        Assert.assertEquals(1, subscriber.getFooValue());
        Assert.assertEquals(0, subscriber.getBarValue());
        Assert.assertNull(subscriber.getLastColorEvent());
        
        // Will activate both the Foo and Bar subscribers
        publisher.publishBar(1);
        
        Assert.assertEquals(3, subscriber.getFooValue());  // One for Foo subscriber, One for Bar subscriber, One from previous publish
        Assert.assertEquals(1, subscriber.getBarValue());  // One from the Bar subscriber
        Assert.assertNull(subscriber.getLastColorEvent());
        
        colorPublisher.publishBlackEvent();
        
        Assert.assertEquals(3, subscriber.getFooValue());  // One for Foo subscriber, One for Bar subscriber, One from previous publish
        Assert.assertEquals(1, subscriber.getBarValue());  // One from the Bar subscriber
        Assert.assertEquals(Color.BLACK, subscriber.getLastColorEvent());
        
    }
    
    /**
     * Tests a single subscriber subscribing to different Types by qualifier
     */
    @Test
    public void testEventDistributionByQualifier() {
        ServiceLocator locator = Utilities.getLocatorWithTopics();
        
        ServiceLocatorUtilities.addClasses(locator,
                ColorPublisher.class,
                ColorSubscriber.class);
        
        ColorPublisher colorPublisher = locator.getService(ColorPublisher.class);
        
        ColorSubscriber subscriber = locator.getService(ColorSubscriber.class);
        
        colorPublisher.publishGreenEvent();
        
        Assert.assertEquals(1, subscriber.getGreenCount());
        Assert.assertEquals(0, subscriber.getRedCount());
        Assert.assertEquals(0, subscriber.getBlackCount());
        Assert.assertEquals(1, subscriber.getNotRedCount());
        
        colorPublisher.publishRedEvent();
        
        Assert.assertEquals(1, subscriber.getGreenCount());
        Assert.assertEquals(1, subscriber.getRedCount());
        Assert.assertEquals(0, subscriber.getBlackCount());
        Assert.assertEquals(1, subscriber.getNotRedCount());
        
        colorPublisher.publishBlackEvent();
        
        Assert.assertEquals(1, subscriber.getGreenCount());
        Assert.assertEquals(1, subscriber.getRedCount());
        Assert.assertEquals(1, subscriber.getBlackCount());
        Assert.assertEquals(2, subscriber.getNotRedCount());
    }
    
    /**
     * Tests that a factory that returns different classes on subsequent
     * calls gets events on all created objects
     */
    @Test
    public void testDifferentClassFactory() {
        ServiceLocator locator = Utilities.getLocatorWithTopics();
        
        ServiceLocatorUtilities.addClasses(locator,
                FooPublisher.class);
        
        FactoryDescriptors factoryDescriptors = BuilderHelper.link(GreekFactory.class).
            to(Greek.class.getName()).
            in(PerLookup.class.getName()).
            qualifiedBy(MessageReceiver.class.getName()).
            buildFactory(Singleton.class.getName());
        
        ServiceLocatorUtilities.addFactoryDescriptors(locator, false, factoryDescriptors);
        
        FooPublisher fooPublisher = locator.getService(FooPublisher.class);
        
        Greek greek1 = locator.getService(Greek.class);
        Greek greek2 = locator.getService(Greek.class);
        Greek greek3 = locator.getService(Greek.class);
        
        Assert.assertNotSame(greek1.getClass(), greek2.getClass());
        Assert.assertNotSame(greek1.getClass(), greek3.getClass());
        Assert.assertNotSame(greek2.getClass(), greek3.getClass());
        
        fooPublisher.publishFoo(3);
        
        Assert.assertEquals(3, greek1.getFooValue());
        Assert.assertEquals(3, greek2.getFooValue());
        Assert.assertEquals(3, greek3.getFooValue());
    }
    
    /**
     * Tests that unbound services no longer get notified
     * 
     * See https://java.net/jira/browse/HK2-222
     */
    @Test @org.junit.Ignore
    public void testUnboundServiceNoLongerGetsNotified() {
        ServiceLocator locator = Utilities.getLocatorWithTopics();
        
        ServiceLocatorUtilities.enableImmediateScope(locator);
        
        List<ActiveDescriptor<?>> added = ServiceLocatorUtilities.addClasses(locator, FooPublisher.class,
                ImmediateSubscriber.class,
                PerLookupSubscriber.class,
                SingletonSubscriber.class);
        
        FooPublisher publisher = locator.getService(FooPublisher.class);
        SingletonSubscriber singletonSubscriber = locator.getService(SingletonSubscriber.class);
        ImmediateSubscriber immediateSubscriber = locator.getService(ImmediateSubscriber.class);
        
        publisher.publishFoo(-12);
        
        // Now remove everything except the publisher
        ServiceLocatorUtilities.removeOneDescriptor(locator, added.get(1));
        ServiceLocatorUtilities.removeOneDescriptor(locator, added.get(2));
        ServiceLocatorUtilities.removeOneDescriptor(locator, added.get(3));
        
        publisher.publishFoo(20);
        
        // These guys should still have -12 since they should NOT have received the
        // event setting the value to 20
        Foo singletonFoo = singletonSubscriber.getAndClearLastEvent();
        Assert.assertNotNull(singletonFoo);
        Assert.assertEquals(-12, singletonFoo.getFooValue());
        
        Foo perLookupFoo1 = singletonSubscriber.getAndClearDependentLastEvent();
        Assert.assertNotNull(perLookupFoo1);
        Assert.assertEquals(-12, perLookupFoo1.getFooValue());
        
        Foo immediateFoo = immediateSubscriber.getAndClearLastEvent();
        Assert.assertNotNull(immediateFoo);
        Assert.assertEquals(-12, immediateFoo.getFooValue());
        
        Foo perLookupFoo2 = immediateSubscriber.getAndClearDependentLastEvent();
        Assert.assertNotNull(perLookupFoo2);
        Assert.assertEquals(-12, perLookupFoo2.getFooValue());
    }
    
    /**
     * Tests that unbound services no longer get notified
     */
    @Test
    public void testDestroyedServiceNoLongerGetsNotified() {
        ServiceLocator locator = Utilities.getLocatorWithTopics();
        
        ServiceLocatorUtilities.addClasses(locator, FooPublisher.class,
                PerLookupSubscriber.class,
                SingletonSubscriber.class);
        
        FooPublisher publisher = locator.getService(FooPublisher.class);
        
        ServiceHandle<SingletonSubscriber> singletonHandle = locator.getServiceHandle(SingletonSubscriber.class);
        SingletonSubscriber singletonSubscriber = singletonHandle.getService();
        
        publisher.publishFoo(-13);
        
        // Now destroy these services
        singletonHandle.destroy();
        
        publisher.publishFoo(21);
        
        // These guys should still have -13 since they should NOT have received the
        // event setting the value to 21
        Foo singletonFoo = singletonSubscriber.getAndClearLastEvent();
        Assert.assertNotNull(singletonFoo);
        Assert.assertEquals(-13, singletonFoo.getFooValue());
        
        Foo perLookupFoo1 = singletonSubscriber.getAndClearDependentLastEvent();
        Assert.assertNotNull(perLookupFoo1);
        Assert.assertEquals(-13, perLookupFoo1.getFooValue());
    }
    
    /**
     * Tests that a per lookup service is destroyed after being
     * given to a subscription method
     */
    @Test
    public void testPerLookupDestroyedAfterPassedToSubscriptionMethod() {
        ServiceLocator locator = Utilities.getLocatorWithTopics();
        
        ServiceLocatorUtilities.addClasses(locator, FooPublisher.class,
                PerLookupService.class,
                ServiceWithPerLookupSubscription.class);
        
        ServiceWithPerLookupSubscription subscriber = locator.getService(ServiceWithPerLookupSubscription.class);
        FooPublisher publisher = locator.getService(FooPublisher.class);
        
        publisher.publishBar(10);
        
        Assert.assertTrue(subscriber.isSubscriptionServiceDead());
    }
    
    /**
     * Tests that a publisher that never had any subscribers is a-ok
     */
    @Test
    public void testNeverAnySubscibers() {
        ServiceLocator locator = Utilities.getLocatorWithTopics();
        
        ServiceLocatorUtilities.addClasses(locator, ZeroPublisher.class);
        
        ZeroPublisher publisher = locator.getService(ZeroPublisher.class);
        
        publisher.publish();
        
    }

}
