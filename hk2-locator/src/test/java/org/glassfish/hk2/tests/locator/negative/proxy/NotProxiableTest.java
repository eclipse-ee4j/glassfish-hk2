/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.locator.negative.proxy;

import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class NotProxiableTest {
    /**
     * Sanity test, just makes sure ProxiableSingleton basically works
     */
    @Test
    public void testSanity() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(ProxiableSingletonContext.class,
                SimpleService.class,
                InjectAProxiableService.class);
        
        InjectAProxiableService iaps = locator.getService(InjectAProxiableService.class);
        iaps.checkSS();
    }
    
    /**
     * Makes sure a non-proxiable scalar type (integer) cannot be
     * proxied
     */
    @Test
    public void testInjectingAProxiableInteger() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(ProxiableSingletonContext.class,
                IntegerFactory.class,
                InjectAProxiableInteger.class);
        
        try {
          locator.getService(InjectAProxiableInteger.class);
          Assert.fail("Should not have been able to proxy an integer");
        }
        catch (MultiException me) {
            for (Throwable th : me.getErrors()) {
                if (th instanceof RuntimeException) {
                    Assert.assertTrue(th.getMessage().contains("final"));
                    return;
                }
            }
            
            // Fail the test, didn't get expected exception
            throw me;
        }
    }
    
    /**
     * Makes sure a non-proxiable scalar type (integer) cannot be
     * proxied
     */
    @Test
    public void testInjectingANonProxiableClass() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(ProxiableSingletonContext.class,
                NonProxiableFinalClass.class,
                InjectANonProxiableClass.class);
        
        try {
          locator.getService(InjectANonProxiableClass.class);
          Assert.fail("Should not have been able to proxy a non proxiable class");
        }
        catch (MultiException me) {
            for (Throwable th : me.getErrors()) {
                if (th instanceof RuntimeException) {
                    Assert.assertTrue(th.getMessage().contains("final"));
                    return;
                }
            }
            
            // Fail the test, didn't get expected exception
            throw me;
        }
    }

}
