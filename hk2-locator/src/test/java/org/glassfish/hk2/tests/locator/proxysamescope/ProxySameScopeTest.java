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

package org.glassfish.hk2.tests.locator.proxysamescope;

import junit.framework.Assert;

import org.glassfish.hk2.api.ProxyCtl;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class ProxySameScopeTest {
    private final static String TEST_NAME = "ProxySameScopeTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new ProxiableSameScopeModule());
    
    /**
     * Tests a basic same-scope scenario.  Ensures the services injected
     * within the same scope are NOT proxied
     */
    @Test
    public void testSameScopeServicesNoProxy() {
        ProxiableServiceB b = locator.getService(ProxiableServiceB.class);
        Assert.assertNotNull(b);
        Assert.assertTrue(b instanceof ProxyCtl);
        
        Assert.assertNotNull(b.getViaConstructor());
        Assert.assertNotNull(b.getViaMethod());
        Assert.assertNotNull(b.getViaField());
        
        Assert.assertFalse(b.getViaConstructor() instanceof ProxyCtl);
        Assert.assertFalse(b.getViaField() instanceof ProxyCtl);
        Assert.assertFalse(b.getViaMethod() instanceof ProxyCtl);
    }
    
    /**
     * Two proxiable scopes have proxyForSameScope set to false, make
     * sure services in these two scopes are proxied
     */
    @Test
    public void testDifferentScopedProxyForSameScopeFalseAreProxied() {
        ProxiableServiceC c = locator.getService(ProxiableServiceC.class);
        Assert.assertNotNull(c);
        Assert.assertTrue(c instanceof ProxyCtl);
        
        c.check();
    }
    
    /**
     * Two services in the same scope with proxyForSameScope set to
     * false but the one service (ServiceD) has an explicit instruction
     * to proxy for same scope anyway
     */
    @Test
    public void testSameScopeButSpecificallyToldNotToProxy() {
        ProxiableServiceE e = locator.getService(ProxiableServiceE.class);
        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof ProxyCtl);
        
        e.check();
    }
    
    /**
     * Two services in the same scope that has ProxyForSameService set to true
     * but where one of the services is explicitly set to ProxyForSameService
     * set to to false
     */
    @Test
    public void testSameProxiableScopeWithSpecificProxySameScopeFalseService() {
        ProxiableServiceG g = locator.getService(ProxiableServiceG.class);
        Assert.assertNotNull(g);
        Assert.assertTrue(g instanceof ProxyCtl);
        
        g.check();
    }
    
    /**
     * Tests a proxiable singleton that has ProxyForSameScope set
     * to false injected into another singleton (should not be
     * proxied)
     */
    @Test
    public void testProxiableSingletonNotLazyIntoSingleton() {
        SingletonServiceA a = locator.getService(SingletonServiceA.class);
        Assert.assertNotNull(a);
        
        a.check();
    }
    
    /**
     * Tests a proxiable singleton that has ProxyForSameScope set
     * to false injected into a PerLookup (should be proxied)
     */
    @Test
    public void testProxiableSingletonNotLazyIntoPerLookup() {
        PerLookupServiceA a = locator.getService(PerLookupServiceA.class);
        Assert.assertNotNull(a);
        
        a.check();
    }

}
