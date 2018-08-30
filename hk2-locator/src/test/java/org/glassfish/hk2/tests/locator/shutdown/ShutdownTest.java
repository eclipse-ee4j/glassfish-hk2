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

package org.glassfish.hk2.tests.locator.shutdown;

import junit.framework.Assert;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorState;
import org.glassfish.hk2.tests.locator.qualifiers.QualifierModule;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class ShutdownTest {
    private final static String TEST_NAME = "ShutdownTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, null, null);
    
    @Test
    public void testShutdown() {
        Assert.assertNotNull(locator.getService(ServiceLocator.class));
        
        long locatorId = locator.getLocatorId();
        String locatorName = locator.getName();
        
        Assert.assertEquals(ServiceLocatorState.RUNNING, locator.getState());
        
        locator.shutdown();
        
        try {
            locator.getService(ServiceLocator.class);
            Assert.fail("Should not work now since the locator has been shutdown");
        }
        catch (IllegalStateException ise) {
        }
        
        Assert.assertEquals(ServiceLocatorState.SHUTDOWN, locator.getState());
        
        // Test that you can in fact call shutdown again
        locator.shutdown();
        
        Assert.assertEquals(locatorId, locator.getLocatorId());
        Assert.assertEquals(locatorName, locator.getName());
        Assert.assertEquals(ServiceLocatorState.SHUTDOWN, locator.getState());
    }

}
