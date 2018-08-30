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

package org.glassfish.hk2.tests.locator.negative.factory;

import junit.framework.Assert;

import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class NegativeFactoryTest {
    private final static String TEST_NAME = "NegativeFactoryTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new NegativeFactoryModule());
    
    /* package */ final static String THROW_STRING = "Expected thrown exception";
    
    /**
     * Factories cannot have a Named annation with no value
     */
    @Test
    public void testFactoryWithBadName() {
        try {
            locator.reifyDescriptor(locator.getBestDescriptor(BuilderHelper.createContractFilter(
                    SimpleService2.class.getName())));
            Assert.fail("The SimpleService2 factory has a bad name and so is invalid");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage(), me.getMessage().contains(
                    "@Named on the provide method of a factory must have an explicit value"));
        }
        
    }
    
    /**
     * Ensures that a factory producing for the singleton scope works properly
     */
    @Test
    public void testFactoryThatThrowsInSingletonScope() {
        try {
            locator.getService(SimpleService.class);
            Assert.fail("The factory throws an exception, so should not have gotten here");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage().contains(THROW_STRING));
        }
    }
    
    /**
     * Ensures that a factory producing for the per lookup scope works properly
     */
    @Test
    public void testFactoryThatThrowsInPerLookupScope() {
        try {
            locator.getService(SimpleService3.class);
            Assert.fail("The factory throws an exception, so should not have gotten here");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage().contains(THROW_STRING));
        }
    }
    
    /**
     * Ensures that a factory producing for the per thread scope works properly
     */
    @Test
    public void testFactoryThatThrowsInPerThreadScope() {
        ServiceLocatorUtilities.enablePerThreadScope(locator);
        
        try {
            locator.getService(SimpleService4.class);
            Assert.fail("The factory throws an exception, so should not have gotten here");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage().contains(THROW_STRING));
        }
    }
    
    /**
     * Ensures that a factory producing for the immediate scope works properly
     */
    @Test
    public void testFactoryThatThrowsInImmediateScope() {
        ServiceLocatorUtilities.enableImmediateScope(locator);
        
        try {
            locator.getService(SimpleService5.class);
            Assert.fail("The factory throws an exception, so should not have gotten here");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage().contains(THROW_STRING));
        }
    }
    
    /**
     * Tests that a PerLookup factory depending on itself will notice
     * the infinite problem and throw an exception
     */
    @Test // @org.junit.Ignore
    public void testInfiniteLoopPerLookupFactory() {
        ServiceLocator locator = LocatorHelper.create();
        ServiceLocatorUtilities.addClasses(locator, InfiniteFactory.class);
        
        try {
          locator.getService(SimpleService5.class);
          Assert.fail("Should have failed due to recursion");
        }
        catch (MultiException me) {
            // Success
            Assert.assertTrue(me.getMessage().contains("A cycle was detected"));
            Assert.assertTrue(me.getMessage().contains(InfiniteFactory.class.getName()));
        }
        
    }

}
