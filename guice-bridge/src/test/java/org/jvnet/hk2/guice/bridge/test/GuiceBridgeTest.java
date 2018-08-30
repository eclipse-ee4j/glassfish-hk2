/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.jvnet.hk2.guice.bridge.test;


import org.glassfish.hk2.api.ServiceLocator;
import org.junit.Assert;
import org.junit.Test;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;
import org.jvnet.hk2.guice.bridge.api.HK2IntoGuiceBridge;
import org.jvnet.hk2.guice.bridge.test.utilities.Utilities;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests for Guice bridge
 *
 * @author jwells
 */
public class GuiceBridgeTest {
    /* package */ static final String ALICE = "Alice";
    /* package */ static final String HATTER = "Hatter";

    private static final ServiceLocator testLocator = Utilities.createLocator("GuiceBridgeTest", new GuiceBridgeTestModule());

    /**
     * Tests a service from Guice being injected into an HK2 service
     */
    @Test
    public void testGuiceServiceInHk2Service() {
        Injector injector = Guice.createInjector(new GuiceBridgeModule());
        Assert.assertNotNull(injector);

        GuiceIntoHK2Bridge guiceBridge = testLocator.getService(GuiceIntoHK2Bridge.class);
        Assert.assertNotNull(guiceBridge);

        guiceBridge.bridgeGuiceInjector(injector);

        HK2Service1 hk2Service = testLocator.getService(HK2Service1.class);
        Assert.assertNotNull(hk2Service);

        hk2Service.verifyGuiceService();

        HK2Service3 hk2Service3 = testLocator.getService(HK2Service3.class);

        hk2Service3.check();

        // Verify the implicit bind also works.
        HK2Service4 hk2Service4 = testLocator.getService(HK2Service4.class);
        Assert.assertNotNull(hk2Service4);

        hk2Service4.verifyGuiceService();
    }

    /**
     * Tests a service from hk2 being injected into a Guice service
     */
    @Test
    public void testHk2ServiceInGuiceService() {
        Injector injector = Guice.createInjector(
                new HK2IntoGuiceBridge(testLocator),
                new HK2BridgeModule());
        Assert.assertNotNull(injector);

        GuiceService2 guiceService2 = injector.getInstance(GuiceService2.class);
        Assert.assertNotNull(guiceService2);

        guiceService2.verifyHK2Service();
    }
}
