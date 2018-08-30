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

package org.glassfish.hk2.tests.utilities.hk2loader;

import java.util.List;

import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.utilities.HK2LoaderImpl;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class LoaderTest {
    /**
     * Tests loading a class
     */
    @Test
    public void testLoadAClass() {
        HK2Loader loader = new HK2LoaderImpl();
        
        Class<?> loaded = loader.loadClass("org.glassfish.hk2.tests.utilities.hk2loader.SimpleService");
        Assert.assertNotNull(loaded);
    }
    
    /**
     * Tests loading a class
     */
    @Test
    public void testDoNotLoadAClass() {
        HK2Loader loader = new HK2LoaderImpl();
        
        try {
            loader.loadClass("org.glassfish.hk2.tests.utilities.hk2loader.NoSimpleService");
            Assert.fail("Should have not been able to load class that does not exist");
        }
        catch (MultiException me) {
            List<Throwable> errors = me.getErrors();
            Assert.assertEquals(1, errors.size());
            
            for (Throwable th : errors) {
                Assert.assertTrue(th instanceof ClassNotFoundException);
            }
        }
        
        Assert.assertNotNull(loader.toString());
    }
    
    /**
     * Tests loading a class
     */
    @Test(expected=IllegalArgumentException.class)
    public void testNullInput() {
        new HK2LoaderImpl(null);
    }

}
