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

package org.glassfish.securitylockdown.test;

import junit.framework.Assert;

import org.glassfish.hk2.api.MultiException;
import org.junit.Test;
import org.jvnet.hk2.testing.junit.HK2Runner;

import com.alice.application.AliceApp;
import com.mallory.application.MalloryApp;

/**
 * 
 * @author jwells
 *
 */
public class SecurityLockdownTest extends HK2Runner {
    
    /**
     * Tests that we can do a lookup of AliceApp
     */
    @Test
    public void testAliceApp() {
        AliceApp aa = testLocator.getService(AliceApp.class);
        Assert.assertNotNull(aa);
    }
    
    /**
     * Tests that we can do a lookup of AliceApp
     */
    @Test
    public void testMalloryApp() {
        MalloryApp ma = testLocator.getService(MalloryApp.class);
        Assert.assertNotNull(ma);
    }
    
    /**
     * Tests that we can have Alice perform an operation on Mallory's behalf
     */
    @Test
    public void testMalloryCanLegallyHaveAliceDoAnOperation() {
        MalloryApp ma = testLocator.getService(MalloryApp.class);
        Assert.assertNotNull(ma);
        
        ma.doAnApprovedOperation();
    }
    
    /**
     * Tests that we can have Alice perform an operation on Mallory's behalf
     */
    @Test
    public void testMalloryCannotGetTheAuditServiceHimself() {
        MalloryApp ma = testLocator.getService(MalloryApp.class);
        Assert.assertNotNull(ma);
        
        try {
            ma.tryToGetTheAuditServiceMyself();
            Assert.fail("Mallory should not be able to get the audit service himself");
        }
        catch (NullPointerException npe) {
            // Good, should have failed for him!
        }
    }
    
    /**
     * Tests that Mallory cannot advertise a service
     */
    @Test
    public void testMalloryCannotAdvertiseAService() {
        MalloryApp ma = testLocator.getService(MalloryApp.class);
        Assert.assertNotNull(ma);
        
        try {
            ma.tryToAdvertiseAService();
            Assert.fail("Mallory should not be able to advertise a service himself");
        }
        catch (MultiException multi) {
            // Good, should have failed for him!
        }
    }
    
    /**
     * Tests that Mallory cannot advertise a service
     */
    @Test
    public void testMalloryCannotUnAdvertiseAService() {
        MalloryApp ma = testLocator.getService(MalloryApp.class);
        Assert.assertNotNull(ma);
        
        try {
            ma.tryToUnAdvertiseAService();
            Assert.fail("Mallory should not be able to unadvertise a service");
        }
        catch (MultiException multi) {
            // Good, should have failed for him!
        }
    }
    
    /**
     * Tests that Mallory cannot have a service that injects something it cannot
     */
    @Test
    public void testMalloryCannotInjectAnUnAuthorizedThing() {
        MalloryApp ma = testLocator.getService(MalloryApp.class);
        Assert.assertNotNull(ma);
        
        try {
            ma.tryToInstantiateAServiceWithABadInjectionPoint();
            Assert.fail("Mallory should not be able to inject a service it has no rights to");
        }
        catch (MultiException multi) {
            Assert.assertTrue(multi.getMessage().contains("There was no object available for injection at SystemInjecteeImpl"));
        }
    }
}
