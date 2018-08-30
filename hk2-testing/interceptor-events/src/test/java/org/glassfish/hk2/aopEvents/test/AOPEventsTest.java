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

package org.glassfish.hk2.aopEvents.test;

import java.util.HashSet;

import org.glassfish.hk2.aopEvents.Event;
import org.glassfish.hk2.aopEvents.EventPublisher;
import org.glassfish.hk2.aopEvents.EventSubscriberService;
import org.glassfish.hk2.aopEvents.MethodInterceptorImpl;
import org.glassfish.hk2.aopEvents.PackageEventSubscriberService;
import org.glassfish.hk2.aopEvents.PrivateEventSubscriberService;
import org.glassfish.hk2.aopEvents.ProtectedEventSubscriberService;
import org.glassfish.hk2.extras.ExtrasUtilities;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvnet.hk2.testing.junit.HK2Runner;

/**
 * Tests for the combo of AOP interception and event processing
 * 
 * @author jwells
 *
 */
public class AOPEventsTest extends HK2Runner {
    @Before
    public void before() {
        super.before();
        
        ExtrasUtilities.enableTopicDistribution(testLocator);
    }
    
    /**
     * Tests that public event methods can be intercepted
     */
    @Test
    public void testPublicSimpleInterceptedEvent() {
        MethodInterceptorImpl mi = testLocator.getService(MethodInterceptorImpl.class);
        mi.clearAndGetIntercepted();
        
        EventSubscriberService subscriber = testLocator.getService(EventSubscriberService.class);
        EventPublisher publisher = testLocator.getService(EventPublisher.class);
        
        Event event = new Event();
        publisher.publish(event);
        
        // Intercepted and invoked
        Assert.assertEquals(event, subscriber.getLastEvent());
        
        HashSet<String> intercepted = mi.clearAndGetIntercepted();
        Assert.assertTrue(intercepted.contains(EventSubscriberService.class.getName()));
    }
    
    /**
     * Tests that protected event methods can be intercepted
     */
    @Test
    public void testProtectedSimpleInterceptedEvent() {
        MethodInterceptorImpl mi = testLocator.getService(MethodInterceptorImpl.class);
        mi.clearAndGetIntercepted();
        
        ProtectedEventSubscriberService subscriber = testLocator.getService(ProtectedEventSubscriberService.class);
        EventPublisher publisher = testLocator.getService(EventPublisher.class);
        
        Event event = new Event();
        publisher.publish(event);
        
        // Intercepted and invoked
        Assert.assertEquals(event, subscriber.getLastEvent());
        
        HashSet<String> intercepted = mi.clearAndGetIntercepted();
        Assert.assertTrue(intercepted.contains(ProtectedEventSubscriberService.class.getName()));
    }
    
    /**
     * Tests that package event methods can be intercepted
     */
    @Test
    public void testPackageSimpleInterceptedEvent() {
        MethodInterceptorImpl mi = testLocator.getService(MethodInterceptorImpl.class);
        mi.clearAndGetIntercepted();
        
        PackageEventSubscriberService subscriber = testLocator.getService(PackageEventSubscriberService.class);
        EventPublisher publisher = testLocator.getService(EventPublisher.class);
        
        Event event = new Event();
        publisher.publish(event);
        
        // Intercepted and invoked
        Assert.assertEquals(event, subscriber.getLastEvent());
        
        HashSet<String> intercepted = mi.clearAndGetIntercepted();
        Assert.assertTrue(intercepted.contains(PackageEventSubscriberService.class.getName()));
    }
    
    /**
     * Tests that private event methods can be NOT intercepted but are invoked anyway
     */
    @Test
    public void testPrivateSimpleInterceptedEvent() {
        MethodInterceptorImpl mi = testLocator.getService(MethodInterceptorImpl.class);
        mi.clearAndGetIntercepted();
        
        PrivateEventSubscriberService subscriber = testLocator.getService(PrivateEventSubscriberService.class);
        EventPublisher publisher = testLocator.getService(EventPublisher.class);
        
        Event event = new Event();
        publisher.publish(event);
        
        // Intercepted and invoked
        Assert.assertEquals(event, subscriber.getLastEvent());
        
        HashSet<String> intercepted = mi.clearAndGetIntercepted();
        Assert.assertFalse(intercepted.contains(PrivateEventSubscriberService.class.getName()));
    }
}
