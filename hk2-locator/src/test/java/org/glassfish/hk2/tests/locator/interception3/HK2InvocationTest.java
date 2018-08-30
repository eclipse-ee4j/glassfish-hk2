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

package org.glassfish.hk2.tests.locator.interception3;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class HK2InvocationTest {
    /** this counter is added by one and incremented in every step up and down */
    public static final String COUNTER_1 = "Counter1";
    /** This counter is added by two and removed by two, incremented by three both up and down */
    public static final String COUNTER_2 = "Counter2";
    /** This counter is added AFTER the method invocation and only incremented on the way down */
    public static final String COUNTER_3 = "Counter3";
    /** This counter is added by one and three, incremented by three on the way down and removed by two */
    public static final String COUNTER_4 = "Counter4";
    
    /**
     * Tests the HK2Invocation on methods and constructors
     */
    @Test // @org.junit.Ignore
    public void testHK2Invocation() {
        ServiceLocator locator = LocatorHelper.create();
        
        ServiceLocatorUtilities.addClasses(locator, InterceptionServiceImpl.class,
                InterceptedService.class);
        
        InterceptedService is = locator.getService(InterceptedService.class);
        Assert.assertNotNull(is);
        
        // Just calling this will do the whole test
        is.method();
    }

}
