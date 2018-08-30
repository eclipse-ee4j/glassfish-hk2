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

package org.glassfish.hk2.tests.locator.negative.self;

import java.util.List;

import junit.framework.Assert;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class InvalidSelfInjectionTest {
    private final static String TEST_NAME = "InvalidSelfInjectionTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new InvalidSelfInjectionModule());
    
    /**
     * This tests that we catch all the bad things wrong with the
     * self injection points
     */
    @Test
    public void testCatchAllWrongSelfInjections() {
        ActiveDescriptor<?> ad =
                locator.getBestDescriptor(BuilderHelper.createContractFilter(AllBadSelfInjectionService.class.getName()));
        Assert.assertNotNull(ad);
        
        try {
            locator.reifyDescriptor(ad);
            Assert.fail("Should have failed to reify due to bad @Self injection points");
        }
        catch (MultiException me) {
            List<Throwable> errors = me.getErrors();
            
            int badOptional = 0;
            int badType = 0;
            int badQualifier = 0;
            
            for (Throwable th : errors) {
                if (th.getMessage().contains(" does not have the required type of ActiveDescriptor")) {
                    badType++;
                }
                if (th.getMessage().contains(" is marked both @Optional and @Self")) {
                    badOptional++;
                }
                if (th.getMessage().contains(" is marked @Self but has other qualifiers")) {
                    badQualifier++;
                }
            }
            
            Assert.assertTrue(2 == badOptional);
            Assert.assertTrue(2 == badType);
            Assert.assertTrue(1 == badQualifier);
        }
        
    }
    
    /**
     * This tests a service that has an Self in the constructor but is being constructed via the API
     */
    @Test
    public void testCreateWithSelf() {
        try {
            locator.create(AValidSelfInjectedService.class);
            Assert.fail("Should have failed to reify due to bad @Self injection point");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage().contains(
                    " is being created or injected via the non-managed ServiceLocator API"));
        }
        
    }
    
    /**
     * This tests a service that has an Self in the constructor but is being constructed via the API
     */
    @Test
    public void testInjectWithMethodSelf() {
        try {
            AnotherValidSelfInjectedService obj = locator.create(AnotherValidSelfInjectedService.class);
            locator.inject(obj);
            
            Assert.fail("Should have failed to reify due to bad @Self injection point");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage().contains(
                    " is being created or injected via the non-managed ServiceLocator API"));
        }
        
    }
    
    /**
     * This tests a service that has an Self in the constructor but is being constructed via the API
     */
    @Test
    public void testInjectWithFieldSelf() {
        try {
            ThirdValidSelfInjectedService obj = locator.create(ThirdValidSelfInjectedService.class);
            locator.inject(obj);
            
            Assert.fail("Should have failed to reify due to bad @Self injection point");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage().contains(
                    " is being created or injected via the non-managed ServiceLocator API"));
        }
        
    }

}
