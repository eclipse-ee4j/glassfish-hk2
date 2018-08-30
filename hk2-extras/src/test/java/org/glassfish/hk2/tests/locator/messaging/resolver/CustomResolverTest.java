/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.locator.messaging.resolver;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.extras.internal.Utilities;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class CustomResolverTest {
    public final static String HEADER = "Soccer";
    
    /**
     * Tests that JIT resolvers can be used with
     * event handler methods
     */
    @Test
    // @org.junit.Ignore
    public void testJITResolverInEventMethod() {
        ServiceLocator locator = Utilities.getLocatorWithTopics(
                EventHeaderJIT.class);
        
        ServiceLocatorUtilities.addClasses(locator, EventSender.class, EventHandler.class);
        
        EventHandler handler = locator.getService(EventHandler.class);
        EventSender sender = locator.getService(EventSender.class);
        Event event = new Event();
        
        sender.sendEvent(event);
        
        Assert.assertEquals(event, handler.getLastEvent());
        Assert.assertEquals(HEADER, handler.getLastHeader());
    }
    
    /**
     * Tests that custom resolvers can be used with
     * event handler methods
     */
    @Test
    // @org.junit.Ignore
    public void testCustomResolverInEventMethod() {
        ServiceLocator locator = Utilities.getLocatorWithTopics(
                EventHeaderInjectionResolver.class);
        
        ServiceLocatorUtilities.addClasses(locator, EventSender.class, EventHandler.class);
        
        EventHandler handler = locator.getService(EventHandler.class);
        EventSender sender = locator.getService(EventSender.class);
        Event event = new Event();
        
        sender.sendEvent(event);
        
        Assert.assertEquals(event, handler.getLastEvent());
        Assert.assertEquals(HEADER, handler.getLastHeader());
    }

}
