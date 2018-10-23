/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2018 Payara Foundation
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

package org.glassfish.hk2.tests.locator.messaging.operation;

import java.util.List;
import java.util.Map;

import org.glassfish.hk2.api.AnnotationLiteral;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.extras.ExtrasUtilities;
import org.glassfish.hk2.extras.operation.OperationHandle;
import org.glassfish.hk2.extras.operation.OperationManager;
import org.glassfish.hk2.tests.extras.internal.Utilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * These are tests for the combination of HK2 messaging and
 * HK2 Operation scope working together
 * 
 * @author jwells
 *
 */
public class MessagingOperationTest {
    private final static EventReceivingOperation OPERATION = new EventReceivingOperationImpl();
    
    private static ServiceLocator createLocator(Class<?>... clazzes) {
        ServiceLocator locator = Utilities.getUniqueLocator(clazzes);
        ExtrasUtilities.enableOperations(locator);
        ExtrasUtilities.enableTopicDistribution(locator);
        
        return locator;
    }
    
    /**
     * Tests that events are not sent to closed operation
     * services
     */
    @Test
    public void testEventsNotSentToClosedOperation() {
        ServiceLocator locator = createLocator(
                EventReceivingOperationContext.class,
                EventReceivingService.class,
                Publisher.class);
        
        OperationManager manager = locator.getService(OperationManager.class);
        Publisher publisher = locator.getService(Publisher.class);
        
        OperationHandle<EventReceivingOperation> opHandle = manager.createAndStartOperation(OPERATION);
        EventReceivingService ers = locator.getService(EventReceivingService.class);
        int id0 = ers.doOperation();  // unproxies it
        
        publisher.publish(0);
        
        opHandle.close();
        
        // Second instance
        opHandle = manager.createAndStartOperation(OPERATION);
        ers = locator.getService(EventReceivingService.class);
        
        int id1 = ers.doOperation();
        
        publisher.publish(1);
        
        opHandle.close();
        
        publisher.publish(2);
        
        Map<Integer, List<Integer>> eventMap = EventReceivingService.getEventMap();
        
        Assert.assertEquals(2, eventMap.size());
        
        List<Integer> firstEvents = eventMap.get(id0);
        Assert.assertEquals(1, firstEvents.size());
        
        Assert.assertEquals(0, firstEvents.get(0).intValue());
        
        List<Integer> secondEvents = eventMap.get(id1);
        Assert.assertEquals(1, secondEvents.size());
        
        Assert.assertEquals(1, secondEvents.get(0).intValue());
    }
    
    /**
     * Tests that events are not sent to closed operation
     * services
     */
    @Test // @org.junit.Ignore
    public void testEventsNotSentToClosedOperationWithFactory() {
        ServiceLocator locator = createLocator(
                EventReceivingOperationContext.class,
                EventReceivingFactory.class,
                Publisher.class);
        
        OperationManager manager = locator.getService(OperationManager.class);
        Publisher publisher = locator.getService(Publisher.class);
        
        OperationHandle<EventReceivingOperation> opHandle = manager.createAndStartOperation(OPERATION);
        
        // Causes factory to get invoked
        int firstFactoryId = locator.getService(Integer.class);
        
        // Ensures factory production was not disposed yet
        List<Integer> firstDisposalList = EventReceivingFactory.getDisposedMap().get(firstFactoryId);
        Assert.assertNull(firstDisposalList);
        
        // Ensures factory itself was not destroyed
        Assert.assertEquals(0, EventReceivingFactory.getDisposedFactories().size());
        
        publisher.publish(0);
        
        opHandle.close();
        
        // Ensures the factory production was disposed
        firstDisposalList = EventReceivingFactory.getDisposedMap().get(firstFactoryId);
        Assert.assertEquals(1, firstDisposalList.size()); 
        Assert.assertEquals(firstFactoryId, firstDisposalList.get(0).intValue());
        
        // Ensures factory was itself destroyed
        Assert.assertEquals(1, EventReceivingFactory.getDisposedFactories().size());
        Assert.assertEquals(firstFactoryId, EventReceivingFactory.getDisposedFactories().get(0).intValue());
        
        // Second instance
        opHandle = manager.createAndStartOperation(OPERATION);
        
        // Causes different factory to get invoked
        int secondFactoryId = locator.getService(Integer.class); 
        
        publisher.publish(1);
        
        opHandle.close();
        
        publisher.publish(2);
        
        Map<Integer, List<Integer>> eventMap = EventReceivingFactory.getEventMap();
        
        Assert.assertEquals(2, eventMap.size());
        
        List<Integer> firstEvents = eventMap.get(firstFactoryId);
        Assert.assertEquals(1, firstEvents.size());
        
        Assert.assertEquals(0, firstEvents.get(0).intValue());
        
        List<Integer> secondEvents = eventMap.get(secondFactoryId);
        Assert.assertEquals(1, secondEvents.size());
        
        Assert.assertEquals(1, secondEvents.get(0).intValue());
        
        // Now check the disposals
        Map<Integer, List<Integer>> disposalMap = EventReceivingFactory.getDisposedMap();
        Assert.assertEquals(2, disposalMap.size());
        
        firstDisposalList = disposalMap.get(firstFactoryId);
        Assert.assertEquals(1, firstDisposalList.size()); 
        Assert.assertEquals(firstFactoryId, firstDisposalList.get(0).intValue());
        
        List<Integer> secondDisposalList = disposalMap.get(secondFactoryId);
        Assert.assertEquals(1, secondDisposalList.size());
        Assert.assertEquals(secondFactoryId, secondDisposalList.get(0).intValue());
        
        Assert.assertEquals(2, EventReceivingFactory.getDisposedFactories().size());
        Assert.assertEquals(firstFactoryId, EventReceivingFactory.getDisposedFactories().get(0).intValue());
        Assert.assertEquals(secondFactoryId, EventReceivingFactory.getDisposedFactories().get(1).intValue());
    }
    
    private static class EventReceivingOperationImpl extends AnnotationLiteral<EventReceivingOperation> implements EventReceivingOperation {
    }

}
