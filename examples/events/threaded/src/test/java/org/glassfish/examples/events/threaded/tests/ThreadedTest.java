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

package org.glassfish.examples.events.threaded.tests;

import junit.framework.Assert;

import org.glassfish.examples.events.threaded.ThreadedEventDistributor;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.extras.ExtrasUtilities;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Test;

/**
 * Tests for the ThreadedEventDistributor
 * 
 * @author jwells
 *
 */
public class ThreadedTest {
    private final static ServiceLocatorFactory factory = ServiceLocatorFactory.getInstance();
    
    private final ServiceLocator locator = factory.create(null);
    
    /**
     * Ensures that the event is given to the subscriber on a different thread
     * from the publisher
     * 
     * @throws InterruptedException
     */
    @Test
    public void testEventDeliveredOnADifferentThread() throws InterruptedException {
        // Adds in the default event implementation
        ExtrasUtilities.enableTopicDistribution(locator);
        
        ServiceLocatorUtilities.addClasses(locator,
                EventPublisherService.class,
                EventSubscriberService.class,
                ThreadedEventDistributor.class);
        
        EventSubscriberService subscriber = locator.getService(EventSubscriberService.class);
        EventPublisherService publisher = locator.getService(EventPublisherService.class);
        
        // This is my current thread, should  NOT be the same thread as the subscriber method call
        long myThreadId = Thread.currentThread().getId();
        
        // Publish the event
        publisher.publishEvent();
        
        long subscriberThreadId = subscriber.getEventThread();
        
        Assert.assertNotSame("Should have had different threadId from " + myThreadId,
                myThreadId, subscriberThreadId);
    }
}
