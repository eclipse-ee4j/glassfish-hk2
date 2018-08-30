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

package org.jvnet.hk2.spring.bridge.test.bidirectional;

import junit.framework.Assert;

import org.junit.Test;
import org.jvnet.hk2.spring.bridge.test.utilities.LocatorAndContext;
import org.jvnet.hk2.spring.bridge.test.utilities.Utilities;
import org.springframework.context.ApplicationContext;

/**
 * @author jwells
 *
 */
public class BiDirectionalBridgeTest {
    /**
     * Tests that the bi-directional spring bridge works
     */
    @Test
    public void testBiDirectionalSpringBridge() {
        LocatorAndContext lac = Utilities.createSpringTestLocator("bidirectional.xml", "BiDirectionalSpringBridge",
                HK2Service1_2.class,
                HK2Service1_0.class);
        
        ApplicationContext context = lac.getApplicationContext();
        
        SpringService1_3 oneThree = context.getBean(SpringService1_3.class);
        Assert.assertNotNull(oneThree);
        
        HK2Service1_0 oneZero = oneThree.check();
        Assert.assertNotNull(oneZero);
        
        // This makes sure the spring service got the one created by HK2
        Assert.assertEquals(oneZero, lac.getServiceLocator().getService(HK2Service1_0.class));
    }

}
