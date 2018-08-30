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

package org.glassfish.hk2.tests.locator.negative.proxiable;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.ProxyCtl;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.DescriptorImpl;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class NegativeProxyTest {
    private final static String TEST_NAME = "NegativeProxyTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new NegativeProxyModule());
    
    /**
     * Tests that a scope marked {@link Unproxiable} cannot have isProxiable set to true
     */
    @Test
    public void testBadProxiable() {
        DescriptorImpl di = BuilderHelper.link(SimpleService.class.getName()).
            in(PerLookup.class.getName()).
            proxy(true).
            build();
        
        try {
            locator.reifyDescriptor(di);
            Assert.fail("A descriptor from an Unproxiable service must not have isProxiable return true");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage().contains("The descriptor is in an Unproxiable scope but has " +
                " isProxiable set to true"));
            
        }
        
    }
    
    @Test
    public void testBadProxiableScope() {
        DescriptorImpl di = BuilderHelper.link(ServiceInBadScope.class.getName()).
            in(BadScope.class.getName()).
            build();
        
        try {
            locator.reifyDescriptor(di);
            Assert.fail("BadScope is both proxiable and unproxiable, reify should fail");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage().contains(" is marked both @Proxiable and @Unproxiable"));
        }
        
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testInvalidAutomaticActiveDescriptor() {
        DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
        DynamicConfiguration dc = dcs.createDynamicConfiguration();
        
        dc.addActiveDescriptor(InvalidlyAnnotatedServices.class);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testInvalidActiveDescriptor() {
        DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
        DynamicConfiguration dc = dcs.createDynamicConfiguration();
        
        AbstractActiveDescriptor<?> ad = BuilderHelper.activeLink(InvalidlyAnnotatedServices.class).
                in(PerLookup.class).
                proxy().
                build();
               
        dc.addActiveDescriptor(ad);
    }
    
    /**
     * The UnavailableScopeService is proxied but there is no
     * context for it.  Ensure that if a method is called the
     * proper exception is thrown (IllegalStateException)
     */
    @Test(expected=IllegalStateException.class)
    public void testProxiedServiceWithUnavailableContext() {
        UnavailableScopeService uss = locator.getService(UnavailableScopeService.class);
        Assert.assertNotNull(uss);
        Assert.assertTrue(uss instanceof ProxyCtl);

        // Must fail with an IllegalStateException
        uss.callMe();
    }
}
