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

package org.glassfish.hk2.tests.utilities.classloaderpostprocessor;

import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.utilities.ClassLoaderPostProcessor;
import org.glassfish.hk2.utilities.DescriptorImpl;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class ClassLoaderPostProcessorTest {
    private final static ClassLoader loader = new ClassLoader() {
    };
    
    private final static HK2Loader hk2Loader = new HK2Loader() {

        @Override
        public Class<?> loadClass(String className) throws MultiException {
            throw new AssertionError("not called");
        }
        
    };
    
    /**
     * Tests that using the no-args constructor
     * will not change the loader if it is already
     * set
     */
    @Test
    public void testDefaultIsNotForce() {
        ClassLoaderPostProcessor cpp = new ClassLoaderPostProcessor(loader);
        
        DescriptorImpl di = new DescriptorImpl();
        di.setLoader(hk2Loader);
        
        DescriptorImpl result = cpp.process(null, di);
        
        Assert.assertEquals(di, result);
        Assert.assertEquals(hk2Loader, result.getLoader());
    }
    
    /**
     * Tests that using the no-args constructor
     * will set the loader if it was null
     */
    @Test
    public void testDefaultSetsLoader() {
        ClassLoaderPostProcessor cpp = new ClassLoaderPostProcessor(loader);
        
        DescriptorImpl di = new DescriptorImpl();
        
        DescriptorImpl result = cpp.process(null, di);
        
        Assert.assertEquals(di, result);
        Assert.assertNotNull(result.getLoader());
    }
    
    /**
     * Tests that the explicit force option true works
     */
    @Test
    public void testForceLoader() {
        ClassLoaderPostProcessor cpp = new ClassLoaderPostProcessor(loader, true);
        
        DescriptorImpl di = new DescriptorImpl();
        di.setLoader(hk2Loader);
        
        DescriptorImpl result = cpp.process(null, di);
        
        Assert.assertEquals(di, result);
        Assert.assertNotSame(hk2Loader, result.getLoader());
    }
    
    /**
     * Tests that the explicit force option true works
     * even if the original was null
     */
    @Test
    public void testForceLoaderOverNull() {
        ClassLoaderPostProcessor cpp = new ClassLoaderPostProcessor(loader, true);
        
        DescriptorImpl di = new DescriptorImpl();
        
        DescriptorImpl result = cpp.process(null, di);
        
        Assert.assertEquals(di, result);
        Assert.assertNotNull(result.getLoader());
    }

}
