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

package org.glassfish.hk2.tests.locator.lifecycle;

import java.util.List;

import junit.framework.Assert;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class LifecycleTest {
    private final static String TEST_NAME = "LifecycleTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new LifecycleModule());
    
    private final static String MESSAGE_ONE = "One";
    private final static String MESSAGE_TWO = "Two";
    
    /**
     * Tests basic lifecycle notification
     */
    @Test
    public void testBasicLifecycleNotification() {
        Notifier alice = locator.getService(Notifier.class, Notifier.DEFAULT_NAME);
        
        // This notification should not be there, since the notifyee isn't instantiated yet
        alice.notify("NOT THERE");
        
        ServiceHandle<KnownInjecteeNotifyee> knownInjecteeHandle = locator.getServiceHandle(
                KnownInjecteeNotifyee.class);
        KnownInjecteeNotifyee knownInjectee = knownInjecteeHandle.getService();
        
        Assert.assertTrue(knownInjectee.getNotifications().isEmpty());
        
        alice.notify(MESSAGE_ONE);
        
        List<String> notifications = knownInjectee.getNotifications();
        Assert.assertEquals(1, notifications.size());
        
        String notification = notifications.get(0);
        
        Assert.assertTrue("Unknown notification message: " + notification, notification.contains(Notifier.DEFAULT_NAME));
        Assert.assertTrue("Unknown notification message: " + notification, notification.contains(MESSAGE_ONE));
        
        // Now destroy the known injectee, verify it no longer receives notifications
        knownInjecteeHandle.close();
        
        alice.notify(MESSAGE_TWO);
        
        // should NOT have changed
        notifications = knownInjectee.getNotifications();
        Assert.assertEquals(1, notifications.size());
        
        notification = notifications.get(0);
        
        Assert.assertTrue("Unknown notification message: " + notification, notification.contains(Notifier.DEFAULT_NAME));
        Assert.assertTrue("Unknown notification message: " + notification, notification.contains(MESSAGE_ONE)); 
    }
    
    @Test
    public void testTrueCreateOrderForDescriptors() {
        OrderedLifecycleListener ordered = locator.getService(OrderedLifecycleListener.class);
        ordered.clear();
        
        // We are getting Fire here, but Fire depends on Wind which depends on Earth so
        // the true creation ordering is Earth, Wind and Fire
        Fire fire = locator.getService(Fire.class);
        Assert.assertNotNull(fire);
        
        List<ActiveDescriptor<?>> orderedList = ordered.getOrderedList();
        
        Assert.assertEquals(3, orderedList.size());
        
        ActiveDescriptor<?> earthDescriptor = orderedList.get(0);
        Assert.assertEquals(earthDescriptor.getImplementation(), Earth.class.getName());
        
        ActiveDescriptor<?> windDescriptor = orderedList.get(1);
        Assert.assertEquals(windDescriptor.getImplementation(), Wind.class.getName());
        
        ActiveDescriptor<?> fireDescriptor = orderedList.get(2);
        Assert.assertEquals(fireDescriptor.getImplementation(), Fire.class.getName());
    }
    
    @Test
    public void testTrueCreateOrderForAddActiveDescriptorAsClass() {
        OrderedLifecycleListener ordered = locator.getService(OrderedLifecycleListener.class);
        ordered.clear();
        
        // We are getting Space here, but Space depends on Sand which depends on Water so
        // the true creation ordering is Water, Sand and Space
        Space space = locator.getService(Space.class);
        Assert.assertNotNull(space);
        
        List<ActiveDescriptor<?>> orderedList = ordered.getOrderedList();
        
        Assert.assertEquals(3, orderedList.size());
        
        ActiveDescriptor<?> waterDescriptor = orderedList.get(0);
        Assert.assertEquals(waterDescriptor.getImplementation(), Water.class.getName());
        
        ActiveDescriptor<?> sandDescriptor = orderedList.get(1);
        Assert.assertEquals(sandDescriptor.getImplementation(), Sand.class.getName());
        
        ActiveDescriptor<?> spaceDescriptor = orderedList.get(2);
        Assert.assertEquals(spaceDescriptor.getImplementation(), Space.class.getName());
    }

}
