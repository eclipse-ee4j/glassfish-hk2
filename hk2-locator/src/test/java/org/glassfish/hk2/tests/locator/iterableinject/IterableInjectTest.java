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

package org.glassfish.hk2.tests.locator.iterableinject;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class IterableInjectTest {
    public static final String ALICE = "Alice";
    public static final String BOB = "Bob";
    
    /**
     * Tests the most basic iterable injection
     */
    @Test // @org.junit.Ignore
    public void testBasicListInjection() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(
                AliceService.class,
                BobService.class,
                BasicIterableInjectingService.class);
        
        BasicIterableInjectingService blis = locator.getService(BasicIterableInjectingService.class);
        Iterable<NamedService> allNamed = blis.getAllNamed();
        
        AliceService alice = null;
        BobService bob = null;
        int count = 0;
        for (NamedService ns : allNamed) {
            count++;
            if (ns instanceof AliceService) {
                alice = (AliceService) ns;
            }
            else if (ns instanceof BobService) {
                bob = (BobService) ns;
            }
            else {
                Assert.fail("unknown ns type: " + ns);
            }
        }
        
        Assert.assertEquals(2, count);
        
        Assert.assertNotNull(alice);
        Assert.assertNotNull(bob);
        
        Assert.assertEquals(ALICE, alice.getName());
        Assert.assertEquals(BOB, bob.getName());
    }
    
    /**
     * Tests the most basic iterable injection
     */
    @Test // @org.junit.Ignore
    public void testListInjectionWithQualifier() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(
                AliceService.class,
                BobService.class,
                AliceIterableInjectionService.class);
        
        AliceIterableInjectionService blis = locator.getService(AliceIterableInjectionService.class);
        Iterable<NamedService> allNamed = blis.getAllAlice();
        
        AliceService alice = null;
        BobService bob = null;
        int count = 0;
        for (NamedService ns : allNamed) {
            count++;
            if (ns instanceof AliceService) {
                alice = (AliceService) ns;
            }
            else if (ns instanceof BobService) {
                bob = (BobService) ns;
            }
            else {
                Assert.fail("unknown ns type: " + ns);
            }
        }
        
        Assert.assertEquals(1, count);
        
        Assert.assertNotNull(alice);
        Assert.assertNull(bob);
        
        Assert.assertEquals(ALICE, alice.getName());
    }
    
    /**
     * Tests that this works with a qualifier that contains values
     */
    @Test // @org.junit.Ignore
    public void testQualifierWithValue() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(
                TernaryServices.NeitherOne.class,
                TernaryServices.NeitherTwo.class,
                TernaryServices.NeitherThree.class,
                TernaryServices.TrueOne.class,
                TernaryServices.TrueTwo.class,
                TernaryInjectedService.class);
        
        TernaryInjectedService tis = locator.getService(TernaryInjectedService.class);
        
        Assert.assertEquals(3, tis.getNumNeithers());
        Assert.assertEquals(2, tis.getNumTrues());
        Assert.assertEquals(0, tis.getNumFalses());
        Assert.assertEquals(5, tis.getNumAlls());
    }

}
