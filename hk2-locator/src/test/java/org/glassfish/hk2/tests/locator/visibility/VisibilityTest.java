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

package org.glassfish.hk2.tests.locator.visibility;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author jwells
 */
public class VisibilityTest {
    private final static String TEST_NAME = "VisibilityTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new VisibilityModule());
    
    /**
     * Tests basic visibility
     */
    @Test
    public void testLocalVisibility() {
        ServiceLocator child = LocatorHelper.create(TEST_NAME + "child", locator, null);
        
        // First make sure both services are available in the parent
        Assert.assertNotNull(locator.getService(LocalService.class));
        Assert.assertNotNull(locator.getService(NormalService.class));
        
        // But only one should be in the child
        Assert.assertNull(child.getService(LocalService.class));
        Assert.assertNotNull(child.getService(NormalService.class));
    }

}
