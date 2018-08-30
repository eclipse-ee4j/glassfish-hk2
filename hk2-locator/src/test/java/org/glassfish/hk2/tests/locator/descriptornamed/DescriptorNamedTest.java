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

package org.glassfish.hk2.tests.locator.descriptornamed;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.customresolver.CustomResolverModule;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class DescriptorNamedTest {
    private final static String TEST_NAME = "DescriptorNamedTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new DescriptorNamedModule());
    
    public final static String ALICE = "Alice";
    public final static String BOB = "Bob";
    
    /**
     * This tests that I can get services with different names
     */
    @Test
    public void getServiceWithName() {
        DescriptorNamedService alice = locator.getService(DescriptorNamedService.class, ALICE);
        Assert.assertNotNull(alice);
        Assert.assertEquals(ALICE, alice.getName());
        
        DescriptorNamedService bob = locator.getService(DescriptorNamedService.class, BOB);
        Assert.assertNotNull(bob);
        Assert.assertEquals(BOB, bob.getName());
        
        Assert.assertNotSame(alice, bob);
        
        DescriptorNamedService none = locator.getService(DescriptorNamedService.class);
        Assert.assertNotNull(none);
        Assert.assertNull(none.getName());
        
        Assert.assertNotSame(alice, none);
        Assert.assertNotSame(bob, none);
    }

}
