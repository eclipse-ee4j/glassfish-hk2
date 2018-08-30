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

package org.glassfish.hk2.tests.locator.proxiable;

import org.junit.Assert;

import org.glassfish.hk2.api.ProxyCtl;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class ProxiableTest {
    private final static String TEST_NAME = "ProxiableTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new ProxiableModule());
    
    /** Many flowers */
    public final static String SPRING = "Spring";
    /** Beach time! */
    public final static String SUMMER = "Summer";
    /** Colorful leaves */
    public final static String FALL = "Fall";
    /** Snowstorms! */
    public final static String WINTER = "Winter";

    /**
     * This test proves that the underlying services are proxied because
     * there is a cycle (spring -> summer -> fall -> winter).  If these
     * were not proxied an infinite stack would occur
     */
    @Test
    public void testSeasonCycle() {
        Winter winter = locator.getService(Winter.class);
        Assert.assertNotNull(winter);
        Assert.assertEquals(WINTER, winter.getName());
        
        Season spring = winter.getNextSeason();
        Assert.assertNotNull(spring);
        Assert.assertEquals(SPRING, spring.getName());
        
        Season summer = spring.getNextSeason();
        Assert.assertNotNull(summer);
        Assert.assertEquals(SUMMER, summer.getName());
        
        Season fall = summer.getNextSeason();
        Assert.assertNotNull(fall);
        Assert.assertEquals(FALL, fall.getName());
        
        Season winter2 = fall.getNextSeason();
        Assert.assertNotNull(winter2);
        Assert.assertEquals(WINTER, winter2.getName());
    }
    
    /**
     * Tests the ProxyCtl interface of proxies
     */
    @Test
    public void testProxyCtl() {
        PostConstructedProxiedService pcps = locator.getService(PostConstructedProxiedService.class);
        Assert.assertNotNull(pcps);
        
        Assert.assertTrue(pcps instanceof ProxyCtl);
        
        ProxyCtl pc = (ProxyCtl) pcps;
        
        Assert.assertFalse(PostConstructedProxiedService.wasPostConstructCalled());
        
        Object o = pc.__make();
        
        Assert.assertNotNull(o);
        Assert.assertTrue(o instanceof PostConstructedProxiedService);
        
        Assert.assertTrue(PostConstructedProxiedService.wasPostConstructCalled());
        
    }
    
    /**
     * Tests method access levels in proxies
     */
    @Test // @org.junit.Ignore
    public void testMethodAccessInAProxy() {
    	SouthernHemisphere sh = locator.getService(SouthernHemisphere.class);
    	
    	// If this doesn't bomb, this test works
    	sh.check();
    }
}
