/*
 * Copyright (c) 2019 Payara Services Ltd.
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package org.glassfish.hk2.tests.locator.children;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorState;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.junit.Test;
import org.testng.Assert;

/**
 *
 * @author jonathan coustick
 */
public class ChildServiceLocatorTest {
    
    
    @Test
    public void CreationTest() {
        ServiceLocator parent = LocatorHelper.create();
        ServiceLocator child = LocatorHelper.create(parent);
        ServiceLocator child2 = LocatorHelper.create(parent);
        parent.shutdown();
        Assert.assertEquals(child.getState(), ServiceLocatorState.SHUTDOWN);
        Assert.assertEquals(child2.getState(), ServiceLocatorState.SHUTDOWN);
    }
    
    
}
