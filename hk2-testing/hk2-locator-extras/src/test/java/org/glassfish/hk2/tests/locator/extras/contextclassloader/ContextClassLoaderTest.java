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

package org.glassfish.hk2.tests.locator.extras.contextclassloader;

import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class ContextClassLoaderTest {
    private ServiceLocator locator;
    
    /**
     * Called prior to the tests
     */
    @Before
    public void before() {
        locator = ServiceLocatorFactory.getInstance().create(null);
        
        ServiceLocatorUtilities.addClasses(locator,
                CCLChangingService.class,
                ServiceA.class);
        
    }
    
    /**
     * Tests that the locator is CCL neutral by default
     */
    @Test
    public void testCCLNeutral() {
        ServiceHandle<?> handle = locator.getServiceHandle(CCLChangingService.class);
        
        ClassLoader cclClassLoader = new MyClassLoader();
        Thread.currentThread().setContextClassLoader(cclClassLoader);
        
        try {
            /**
             * Will be CCL neutral
             */
            handle.getService();
        
            Assert.assertEquals(cclClassLoader, Thread.currentThread().getContextClassLoader());
            
            handle.destroy();
            
            Assert.assertEquals(cclClassLoader, Thread.currentThread().getContextClassLoader());
        }
        finally {
            Thread.currentThread().setContextClassLoader(null);   
        }
    }
    
    /**
     * Tests that you can make the locator non CCL neutral
     */
    @Test
    public void testCCLNotNeutral() {
        locator.setNeutralContextClassLoader(false);
        
        ServiceHandle<?> handle = locator.getServiceHandle(CCLChangingService.class);
        
        ClassLoader cclClassLoader = new MyClassLoader();
        Thread.currentThread().setContextClassLoader(cclClassLoader);
        
        try {
            /**
             * Will be CCL neutral
             */
            handle.getService();
        
            ClassLoader currentCCL = Thread.currentThread().getContextClassLoader();
            Assert.assertNotSame(cclClassLoader, currentCCL);
            
            handle.destroy();
            
            Assert.assertNotSame(currentCCL, Thread.currentThread().getContextClassLoader());
        }
        finally {
            Thread.currentThread().setContextClassLoader(null);
            locator.setNeutralContextClassLoader(true);
        }
    }
    
    /**
     * Tests the raw operations are naturally neutral
     */
    @Test
    public void testRawOperationsNeutral() {
        ClassLoader cclClassLoader = new MyClassLoader();
        Thread.currentThread().setContextClassLoader(cclClassLoader);
        
        try {
            Object o = locator.create(CCLChangingService.class);
        
            Assert.assertEquals(cclClassLoader, Thread.currentThread().getContextClassLoader());
            
            locator.inject(o);
            
            Assert.assertEquals(cclClassLoader, Thread.currentThread().getContextClassLoader());
            
            locator.postConstruct(o);
            
            Assert.assertEquals(cclClassLoader, Thread.currentThread().getContextClassLoader());
            
            locator.preDestroy(o);
            
            Assert.assertEquals(cclClassLoader, Thread.currentThread().getContextClassLoader());
        }
        finally {
            Thread.currentThread().setContextClassLoader(null);
        }
    }
    
    /**
     * Tests the raw operations are naturally neutral
     */
    @Test
    public void testRawOperationsCanNotBeNeutral() {
        locator.setNeutralContextClassLoader(false);
        
        ClassLoader cclClassLoader = new MyClassLoader();
        Thread.currentThread().setContextClassLoader(cclClassLoader);
        
        try {
            Object o = locator.create(CCLChangingService.class);
        
            ClassLoader currentCCL = Thread.currentThread().getContextClassLoader();
            Assert.assertNotSame(cclClassLoader, currentCCL);
            
            locator.inject(o);
            
            Assert.assertNotSame(currentCCL, Thread.currentThread().getContextClassLoader());
            currentCCL = Thread.currentThread().getContextClassLoader();
            
            locator.postConstruct(o);
            
            Assert.assertNotSame(currentCCL, Thread.currentThread().getContextClassLoader());
            currentCCL = Thread.currentThread().getContextClassLoader();
            
            locator.preDestroy(o);
            
            Assert.assertNotSame(currentCCL, Thread.currentThread().getContextClassLoader());
        }
        finally {
            Thread.currentThread().setContextClassLoader(null);
            locator.setNeutralContextClassLoader(true);
        }
    }
    
    private static class MyClassLoader extends ClassLoader {
        
    }

}
