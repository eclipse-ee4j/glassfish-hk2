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

package org.glassfish.hk2.tests.locator.optional;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class OptionalTest {
    private final static String TEST_NAME = "OptionalTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new OptionalModule());

    /**
     * All the true validation is done in the
     * InjectedManyTimes service
     */
    @Test
    public void testOptionalAndOptionalButThere() {
        InjectedManyTimes many = locator.getService(InjectedManyTimes.class);
        Assert.assertNotNull(many);

        Assert.assertTrue(many.isValid());
    }
    
    /**
     * Tests that a service that is present but which is in an inactive
     * context can be NOT injected into a service (assuming it is Optional)
     */
    @Test
    public void testInjectPresentOptionalServiceFromInactiveContext() {
        ServiceLocator testLocator = LocatorHelper.getServiceLocator(InactiveContext.class,
                ServiceInInactiveContext.class,
                ServiceInjectedWithOptionalServiceFromInactiveContext.class);
        
        ServiceInjectedWithOptionalServiceFromInactiveContext s = testLocator.getService(ServiceInjectedWithOptionalServiceFromInactiveContext.class);
        Assert.assertNotNull(s);
        Assert.assertNull(s.getServiceInInactiveContext());
    }
}
