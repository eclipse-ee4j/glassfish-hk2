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

package org.jvnet.hk2.spring.bridge.test.spring2hk2;

import junit.framework.Assert;

import org.glassfish.hk2.api.ServiceLocator;
import org.junit.Test;
import org.jvnet.hk2.spring.bridge.test.utilities.Utilities;

/**
 * Tests for the spring-hk2 bridge
 * 
 * @author jwells
 *
 */
public class SpringBridgeTest {
    /* package */ final static String HELLO_WORLD = "hello world";
    
    /**
     * Tests that a basic (unnamed) Injection point works properly
     */
    @Test
    public void testSpringBeanIntoHk2() {
        ServiceLocator locator = Utilities.createSpringTestLocator(
                "spring-test-beans.xml",
                null,
                HK2ServiceWithSpringServiceInjected.class).getServiceLocator();
        
        HK2ServiceWithSpringServiceInjected hswssi = locator.getService(
                HK2ServiceWithSpringServiceInjected.class);
        Assert.assertNotNull(hswssi);
        
        Assert.assertEquals(HELLO_WORLD, hswssi.check());
        
    }
}
