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

package org.glassfish.noproxy;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author jwells
 */
public class NoProxyTest {
    private ServiceLocator locator;
    
    /**
     * Called prior to the tests
     */
    @Before
    public void before() {
        locator = ServiceLocatorFactory.getInstance().create(null);
        
        ServiceLocatorUtilities.addClasses(locator,
                SingletonService.class,
                PerLookupService.class,
                ProxiedService.class);
        
    }
    
    /**
     * Tests that we can lookup and inject Singleton and PerLookup
     * services even if all the proxy jars are not in the classpath
     */
    @Test
    public void testGetServicesWithNoProxies() {
        SingletonService ss1 = locator.getService(SingletonService.class);
        Assert.assertNotNull(ss1);
        
        PerLookupService pls1 = ss1.getPerLookup();
        Assert.assertNotNull(pls1);
        
        PerLookupService pls2 = locator.getService(PerLookupService.class);
        Assert.assertNotSame(pls1, pls2);
        
        SingletonService ss2 = pls2.getSingleton();
        Assert.assertNotNull(ss2);
        
        Assert.assertEquals(ss1, ss2);
        
    }
    
    /**
     * Tests that a proxied service is rejected!
     */
    @Test
    public void testGetProxiedServiceFailNicely() {
        try {
            locator.getService(ProxiedService.class);
            Assert.fail("Should have failed, proxy library MUST not be on path");
        }
        catch (IllegalStateException ise) {
            Assert.assertTrue(ise.getMessage().contains(" requires a proxy, but the proxyable library is not on the classpath"));
        }
    }
}
