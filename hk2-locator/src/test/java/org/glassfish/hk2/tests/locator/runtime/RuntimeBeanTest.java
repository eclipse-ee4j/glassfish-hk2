/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.locator.runtime;

import java.util.List;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Assert;
import org.junit.Test;
import org.jvnet.hk2.external.runtime.ServiceLocatorRuntimeBean;

/**
 * @author jwells
 *
 */
public class RuntimeBeanTest {
    /**
     * Tests that the number of descriptors
     * is correct
     */
    @Test // @org.junit.Ignore
    public void testNumberOfDescriptors() {
        ServiceLocator locator = LocatorHelper.create();
        ServiceLocatorRuntimeBean bean = locator.getService(ServiceLocatorRuntimeBean.class);
        
        int numDescriptors = bean.getNumberOfDescriptors();
        
        List<ActiveDescriptor<?>> descriptors = ServiceLocatorUtilities.addClasses(locator, SimpleService.class, SimpleService.class);
        
        int postAddNumDescriptors = bean.getNumberOfDescriptors();
        
        Assert.assertEquals(numDescriptors + 2, postAddNumDescriptors);
        
        ServiceLocatorUtilities.removeOneDescriptor(locator, descriptors.get(1));
        
        int postRemoveNumDescriptors = bean.getNumberOfDescriptors();
        
        Assert.assertEquals(numDescriptors + 1, postRemoveNumDescriptors);
    }
    
    /**
     * Tests the number of children statistic
     */
    @Test // @org.junit.Ignore
    public void testNumberOfChildren() {
        ServiceLocator locator = LocatorHelper.create();
        ServiceLocatorRuntimeBean bean = locator.getService(ServiceLocatorRuntimeBean.class);
        
        int numChildren = bean.getNumberOfChildren();
        
        Assert.assertEquals(0, numChildren);
        
        ServiceLocator child = LocatorHelper.create(locator);
        
        numChildren = bean.getNumberOfChildren();
        
        Assert.assertEquals(1, numChildren);
        
        ServiceLocator grandChild = LocatorHelper.create(child);
        
        numChildren = bean.getNumberOfChildren();
        
        Assert.assertEquals(1, numChildren);
        
        grandChild.shutdown();
        
        numChildren = bean.getNumberOfChildren();
        
        Assert.assertEquals(1, numChildren);
        
        child.shutdown();
        
        numChildren = bean.getNumberOfChildren();
        
        Assert.assertEquals(0, numChildren);
        
    }
    
    /**
     * Tests that the service cache can be zeroed
     */
    @Test // @org.junit.Ignore
    public void testServiceCacheClear() {
        ServiceLocator locator = LocatorHelper.create();
        ServiceLocatorRuntimeBean bean = locator.getService(ServiceLocatorRuntimeBean.class);
        
        int maxCacheSize = bean.getServiceCacheMaximumSize();
        int cacheSize = bean.getServiceCacheSize();
        
        Assert.assertTrue(maxCacheSize >= cacheSize);
        
        ServiceLocatorUtilities.addClasses(locator, SimpleService.class);
        
        Assert.assertNotNull(locator.getService(SimpleService.class));
        
        int postLookupCacheSize = bean.getServiceCacheSize();
        
        Assert.assertTrue("postLookupCacheSize=" + postLookupCacheSize + " cacheSize=" + cacheSize, postLookupCacheSize > cacheSize);
        
        // Lets see if the cache is working
        Assert.assertNotNull(locator.getService(SimpleService.class));
        
        int postDuexLookupCacheSize = bean.getServiceCacheSize();
        
        Assert.assertEquals(postLookupCacheSize, postDuexLookupCacheSize);
        
        bean.clearServiceCache();
        
        Assert.assertEquals(0, bean.getServiceCacheSize());
        
        Assert.assertNotNull(locator.getService(SimpleService.class));
        
        Assert.assertEquals(1, bean.getServiceCacheSize());
    }
    
    /**
     * Tests that the service cache can be zeroed
     */
    @Test // @org.junit.Ignore
    public void testReflectionCacheClear() {
        ServiceLocator locator = LocatorHelper.create();
        ServiceLocatorRuntimeBean bean = locator.getService(ServiceLocatorRuntimeBean.class);
        
        int cacheSize = bean.getReflectionCacheSize();
        
        List<ActiveDescriptor<?>> descriptors = ServiceLocatorUtilities.addClasses(locator, SimpleService.class);
        
        Assert.assertNotNull(locator.getService(SimpleService.class));
        
        int postLookupCacheSize = bean.getReflectionCacheSize();
        
        Assert.assertTrue(postLookupCacheSize > cacheSize);
        
        bean.clearReflectionCache();
        
        Assert.assertEquals(0, bean.getReflectionCacheSize());
        
        ServiceLocatorUtilities.removeOneDescriptor(locator, descriptors.get(0));
        ServiceLocatorUtilities.addClasses(locator, SimpleService.class);
        
        Assert.assertNotNull(locator.getService(SimpleService.class));
        
        Assert.assertTrue(bean.getReflectionCacheSize() > 0);
    }

}
