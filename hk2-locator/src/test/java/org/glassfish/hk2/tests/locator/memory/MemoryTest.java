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

package org.glassfish.hk2.tests.locator.memory;

import java.util.WeakHashMap;

import javax.inject.Singleton;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.api.ServiceLocatorState;
import org.glassfish.hk2.tests.locator.proxysamescope.ProxiableSingletonContext;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * These tests are funnny, in that they at one time or another exhibited a memory
 * leak.  In many cases the tests here are normal scenarios just run over
 * and over again, and the test can not detect the actual memory leak that
 * took place.  However, they are useful tests in any case, and should
 * each have some comment in them describing the memory leak that inspired
 * the test case
 * 
 * @author jwells
 *
 */
public class MemoryTest {
    private final static ServiceLocatorFactory factory = ServiceLocatorFactory.getInstance();
    
    /**
     * This test causes the injecteeToResolverCache in ServiceLocatorImpl
     * to grow, as every time reifyDescriptor is called a new SystemDescriptor
     * is created which has new Injectee instances.
     * Since the cache was based on the identity of the object, this
     * caused the cache to continually grow.  This leak has been fixed
     * by having SystemInjecteeImpl have a good hashCode/equals implementation,
     * so the keys are the same even if the objects are different
     */
    @Test // @org.junit.Ignore
    public void testDirectlyCallingCreate() {
        ServiceLocator locator = LocatorHelper.create();
        
        ServiceLocatorUtilities.addClasses(locator,
                SimpleService.class);
        
        for (int lcv = 0; lcv < 100; lcv++) {
            ActiveDescriptor<?> issDescriptor = BuilderHelper.activeLink(InjectsSimpleServiceService.class).
                    in(Singleton.class).
                    build();
            
            issDescriptor = locator.reifyDescriptor(issDescriptor);
            
            issDescriptor.create(null);
        }
        
    }
    
    /**
     * Tests that an empty ServiceLocator goes away
     * 
     * @throws Throwable
     */
    @Test
    public void testLocatorDestroyed() throws Throwable {
        WeakHashMap<ServiceLocator, Object> weakMap = new WeakHashMap<ServiceLocator, Object>();
        
        ServiceLocator locator = factory.create("testLocatorDestroyedServiceLocator");
        Assert.assertNotNull(locator);
        
        weakMap.put(locator, new Integer(0));
        
        Assert.assertEquals(1, weakMap.size());
        
        factory.destroy(locator);
        
        Assert.assertEquals(ServiceLocatorState.SHUTDOWN, locator.getState());
        
        locator = null;
        
        System.gc();
        
        for (int lcv = 0; lcv < 400; lcv++) {
            if (weakMap.isEmpty()) break;
            
            Thread.sleep(50);
        }
        
        Assert.assertTrue(weakMap.isEmpty());
    }
    
    /**
     * Tests that a service locator with some proxiable
     * services goes away.
     * 
     * See https://java.net/jira/browse/HK2-247
     * 
     * @throws Throwable
     */
    @Test @org.junit.Ignore
    public void testLocatorWithObjectProxiesDestroyed() throws Throwable {
        WeakHashMap<ServiceLocator, Object> weakMap = new WeakHashMap<ServiceLocator, Object>();
        
        ServiceLocator locator = factory.create("testLocatorWithObjectProxiesDestroyed");
        Assert.assertNotNull(locator);
        
        weakMap.put(locator, new Integer(0));
        
        ServiceLocatorUtilities.addClasses(locator, ProxiableSingletonContext.class,
                ProxiableSimpleService.class,
                SimpleService.class,
                InjectsProxiableStuff.class);
        
        InjectsProxiableStuff ips = locator.getService(InjectsProxiableStuff.class);
        ips.operate();
        ips = null;
        
        Assert.assertEquals(1, weakMap.size());
        
        factory.destroy(locator);
        
        Assert.assertEquals(ServiceLocatorState.SHUTDOWN, locator.getState());
        
        locator = null;
        
        System.gc();
        
        for (int lcv = 0; lcv < 400; lcv++) {
            if (weakMap.isEmpty()) break;
            
            Thread.sleep(50);
        }
        
        Assert.assertTrue(weakMap.isEmpty());
    }

}
