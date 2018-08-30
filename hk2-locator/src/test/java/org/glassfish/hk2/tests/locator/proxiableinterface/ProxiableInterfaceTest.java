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

package org.glassfish.hk2.tests.locator.proxiableinterface;

import junit.framework.Assert;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class ProxiableInterfaceTest {
    private final static String TEST_NAME = "ProxiableInterfaceTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new ProxiableInterfaceModule());
    
    /**
     * This tests that we can proxy an interface (produced by a factory)
     */
    @Test
    public void testProxiedInterfaceFromFactory() {
        Planet planet = locator.getService(Planet.class);
        Assert.assertNotNull(planet);
        
        Assert.assertEquals(Earth.NAME, planet.getName());
    }

}
