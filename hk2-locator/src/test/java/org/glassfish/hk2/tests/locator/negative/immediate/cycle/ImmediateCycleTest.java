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

package org.glassfish.hk2.tests.locator.negative.immediate.cycle;

import java.util.List;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class ImmediateCycleTest {

    /**
     * Tests that an ImmediateService with a cycle will get detected and reported
     * to the error handler
     * 
     * @throws InterruptedException
     */
    @Test // @org.junit.Ignore
    public void testImmediateWithCycleFails() throws InterruptedException {
        ServiceLocator locator = LocatorHelper.create();
        
        ServiceLocatorUtilities.enableImmediateScope(locator);
        
        ServiceLocatorUtilities.addClasses(locator,
                ImmedateErrorHandlerImpl.class,
                SingletonOneImpl.class);
        
        // Now truly start the cycle off
        List<ActiveDescriptor<?>> immediates = ServiceLocatorUtilities.addClasses(locator,
                ImmediateOneImpl.class,
                ImmediateTwoImpl.class);
        
        ImmedateErrorHandlerImpl handler = locator.getService(ImmedateErrorHandlerImpl.class);
        Assert.assertNotNull(handler);
        
        ActiveDescriptor<?> errorDesc = handler.waitForPostFailure(5 * 1000);
        Assert.assertNotNull(errorDesc);
        
        boolean found = false;
        for (ActiveDescriptor<?> compareMe : immediates) {
            if (compareMe.equals(errorDesc)) {
                found = true;
                break;
            }
        }
        
        Assert.assertTrue(found);
        
        Throwable th = handler.getPostException();
        Assert.assertNotNull(th);
        
        Assert.assertTrue(th.toString().contains("A circular dependency involving Immediate service "));
    }

}
