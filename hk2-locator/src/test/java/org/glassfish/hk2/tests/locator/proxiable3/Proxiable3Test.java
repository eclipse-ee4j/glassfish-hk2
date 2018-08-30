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

package org.glassfish.hk2.tests.locator.proxiable3;

import org.glassfish.hk2.api.ProxyCtl;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class Proxiable3Test {
    private final static String TEST_NAME = "Proxiable3Test";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new Proxiable3Module());
    
    /**
     * Tests I can lookup the same service with different proxiable interfaces
     */
    @Test
    public void testProxyViaInterfaceLookup() {
        Control control = locator.getService(Control.class);
        Assert.assertNotNull(control);
        
        control.resetInvocations();
        
        Foo foo = locator.getService(Foo.class);
        
        foo.foo();
        
        Bar bar = locator.getService(Bar.class);
        
        bar.bar();
        bar.bar();
        
        Assert.assertSame(1, control.getFooInvocations());
        Assert.assertSame(2, control.getBarInvocations());
        
        control.resetInvocations();
    }
    
    /**
     * Tests that proxies work when being injected into another object
     */
    @Test
    public void testProxyViaInterfaceInjection() {
        InjectedWithProxiesService injected = locator.getService(InjectedWithProxiesService.class);
        
        Assert.assertTrue(injected.didPass());
    }
    
    /**
     * Ensures that the interface proxies implement ProxyCtl
     */
    @Test
    public void testProxyViaInterfaceLookupImplementProxyCtl() {
        Control control = locator.getService(Control.class);
        Foo foo = locator.getService(Foo.class);
        Bar bar = locator.getService(Bar.class);
        
        Assert.assertTrue(control instanceof ProxyCtl);
        Assert.assertTrue(foo instanceof ProxyCtl);
        Assert.assertTrue(bar instanceof ProxyCtl);
    }
    
    /**
     * Ensures that the interface proxies implement ProxyCtl
     */
    @Test
    public void testProxyViaInterfaceInjectionImplementProxyCtl() {
        InjectedWithProxiesService injected = locator.getService(InjectedWithProxiesService.class);
        
        injected.areProxies();
    }
    
    /**
     * Ensures that an interface proxy works with a factory produced type
     */
    @Test
    public void testProxiedViaFactory() {
        FactoryImpl factory = locator.getService(FactoryImpl.class);
        Assert.assertFalse(factory.getProvideCalled());
        
        FactoryProducedFoo foo = locator.getService(FactoryProducedFoo.class);
        Assert.assertNotNull(foo);
        
        Assert.assertFalse(factory.getProvideCalled());
        
        ProxyCtl ctl = (ProxyCtl) foo;
        
        ctl.__make();
        
        Assert.assertTrue(factory.getProvideCalled());
    }

}
