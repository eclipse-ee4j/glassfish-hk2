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

package org.glassfish.hk2.noproxy2;

import org.glassfish.hk2.api.MultiException;
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
public class NoProxy2Test {
    private ServiceLocator locator;
    
    /**
     * Called prior to the tests
     */
    @Before
    public void before() {
        locator = ServiceLocatorFactory.getInstance().create(null);
        
        ServiceLocatorUtilities.addClasses(locator,
                SingletonService.class,
                ConstructorSingletonService.class,
                MethodInterceptionService.class,
                ConstructorInterceptionService.class);
        
    }
    
    /**
     * Tests that we will get a nice exception if javassist
     * is not in the classpath but someone tries to use
     * AOP method interception
     */
    @Test
    public void testGetServiceWithAOPMethodInterceptors() {
        try {
            locator.getService(SingletonService.class);
            Assert.fail("should not get here");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage().contains(" needs either method or constructor interception, but proxies are not available"));
            
        }
        
    }
    
    /**
     * Tests that we will get a nice exception if javassist
     * is not in the classpath but someone tries to use
     * AOP construction interception
     */
    @Test
    public void testGetServiceWithAOPConstructorInterceptors() {
        try {
            locator.getService(ConstructorSingletonService.class);
            Assert.fail("should not get here");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage().contains(" needs either method or constructor interception, but proxies are not available"));
        }
        
    }
}
