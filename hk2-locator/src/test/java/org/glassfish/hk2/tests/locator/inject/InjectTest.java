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

package org.glassfish.hk2.tests.locator.inject;

import java.util.HashSet;

import junit.framework.Assert;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class InjectTest {
    private final static String TEST_NAME = "InjectTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new InjectModule());
    
    @Test
    public void testAllInjected() {
        BeachHaven beachHaven = locator.getService(BeachHaven.class);
        
        HashSet<TrackerService> differenceDetector = new HashSet<TrackerService>();
        
        TrackerService tracker;
        
        tracker = beachHaven.getBeachHavenFieldTracker();
        Assert.assertNotNull(tracker);
        Assert.assertTrue(differenceDetector.add(tracker));
        
        tracker = beachHaven.getBeachHavenMethodTracker();
        Assert.assertNotNull(tracker);
        Assert.assertTrue(differenceDetector.add(tracker));
        
        tracker = beachHaven.getNewJerseyFieldTracker();
        Assert.assertNotNull(tracker);
        Assert.assertTrue(differenceDetector.add(tracker));
        
        tracker = beachHaven.getNewJerseyMethodTracker();
        Assert.assertNotNull(tracker);
        Assert.assertTrue(differenceDetector.add(tracker));
        
        tracker = beachHaven.getUSFieldTracker();
        Assert.assertNotNull(tracker);
        
        tracker = beachHaven.getUSMethodTracker();
        Assert.assertNotNull(tracker);
        Assert.assertTrue(differenceDetector.add(tracker));
        
        // Now test the *private* (and hence not overridden) methods
        tracker = beachHaven.getBeachHavenOverridenButPrivateMethodTracker();
        Assert.assertNotNull(tracker);
        Assert.assertTrue(differenceDetector.add(tracker));
        
        tracker = beachHaven.getNewJerseyOverridenButPrivateMethodTracker();
        Assert.assertNotNull(tracker);
        Assert.assertTrue(differenceDetector.add(tracker));
        
        tracker = beachHaven.getUSOverridenButPrivateMethodTracker();
        Assert.assertNotNull(tracker);
        Assert.assertTrue(differenceDetector.add(tracker));
        
        // Now test the public injector methods, which *should* have been overridden
        tracker = beachHaven.getBeachHavenOverridenPublicMethodTracker();
        Assert.assertNotNull(tracker);
        Assert.assertTrue(differenceDetector.add(tracker));
        
        tracker = beachHaven.getNewJerseyOverridenPublicMethodTracker();
        Assert.assertNull(tracker);
        
        tracker = beachHaven.getUSOverridenPublicMethodTracker();
        Assert.assertNull(tracker);
    }

}
