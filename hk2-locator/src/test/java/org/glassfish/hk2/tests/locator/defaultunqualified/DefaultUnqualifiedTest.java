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

package org.glassfish.hk2.tests.locator.defaultunqualified;

import org.glassfish.hk2.api.IterableProvider;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.UnqualifiedImpl;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class DefaultUnqualifiedTest {
    /**
     * Tests that when using a system default unqualified
     * that the service gets the proper non-qualified
     * service.  Note that DollImpl has a low priority, and
     * hence would NOT be picked for the doll injection
     * point if the DefaultUnqualified were not set
     */
    @Test // @org.junit.Ignore
    public void testGetsUnqualifiedService() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(BoardGameImpl.class,
                DollImpl.class,
                TrainImpl.class,
                ChildsRoom.class);
        
        locator.setDefaultUnqualified(new UnqualifiedImpl());
        
        ChildsRoom childsRoom = locator.getService(ChildsRoom.class);
        
        Assert.assertEquals(BoardGameImpl.class, childsRoom.getBoardGame().getClass());
        Assert.assertEquals(TrainImpl.class, childsRoom.getTrain().getClass());
        
        // The real test:
        Assert.assertEquals(DollImpl.class, childsRoom.getDoll().getClass());
        Assert.assertEquals(DollImpl.class, childsRoom.getDollProvider().get().getClass());
    }
    
    /**
     * Tests that injection of IterableProvider does NOT have the
     * default Unqualified applied to it
     */
    @Test // @org.junit.Ignore
    public void testIterableProviderReturnsAllEvenIfUnqualified() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(BoardGameImpl.class,
                DollImpl.class,
                TrainImpl.class,
                ChildsRoom.class);
        
        locator.setDefaultUnqualified(new UnqualifiedImpl());
        
        ChildsRoom childsRoom = locator.getService(ChildsRoom.class);
        
        IterableProvider<Toy> allToys = childsRoom.getAllToys();
        Assert.assertEquals(3, allToys.getSize());
        
        int lcv = 0;
        for (Toy toy : allToys) {
            switch (lcv) {
            case 0:
                Assert.assertEquals(BoardGameImpl.class, toy.getClass());
                break;
            case 1:
                Assert.assertEquals(TrainImpl.class, toy.getClass());
                break;
            case 2:
                Assert.assertEquals(DollImpl.class, toy.getClass());
                break;
            default:
                Assert.fail("Should not get here");
            }
            
            lcv++;
        }
        
        
    }

}
