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

package org.glassfish.hk2.tests.locator.messaging.error;

import javax.inject.Singleton;

import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.messaging.Topic;
import org.glassfish.hk2.extras.events.DefaultTopicDistributionErrorService;
import org.glassfish.hk2.tests.extras.internal.Utilities;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests various failure scenarios
 * 
 * @author jwells
 */
public class TopicErrorCasesTest {
    public final static String EXPECTED_MESSAGE = "ExpectedMessage";
    
    /**
     * Tests that a subscribers exception does not stop another
     * subscriber from getting the message
     */
    @Test
    public void testSubscriberThrows() {
        ServiceLocator locator = Utilities.getLocatorWithTopics();
        
        ServiceLocatorUtilities.addClasses(locator, SubscriberThrowsException.class,
                Subscriber.class,
                Publisher.class,
                ErrorHandler.class);
        
        locator.getService(SubscriberThrowsException.class);
        Subscriber subscriber = locator.getService(Subscriber.class);
        Publisher publisher = locator.getService(Publisher.class);
        ErrorHandler errorHandler = locator.getService(ErrorHandler.class);
        
        MyEvent event = new MyEvent();
        publisher.publish(event);
        
        // Ensures that other subscribers get the event
        Assert.assertEquals(1, subscriber.getNumEvents());
        
        MultiException me = errorHandler.lastError;
        Assert.assertNotNull(me);
        
        Assert.assertTrue(me.toString().contains(EXPECTED_MESSAGE));
        
        Assert.assertEquals(event, errorHandler.lastMessage);
        Assert.assertEquals(publisher.getTopic(), errorHandler.lastTopic);
    }
    
    @Singleton
    private static class ErrorHandler implements DefaultTopicDistributionErrorService {
        private Topic<?> lastTopic;
        private Object lastMessage;
        private MultiException lastError;

        @Override
        public void subscribersFailed(Topic<?> topic, Object message,
                MultiException error) {
            lastTopic = topic;
            lastMessage = message;
            lastError = error;
        }
        
    }

}
