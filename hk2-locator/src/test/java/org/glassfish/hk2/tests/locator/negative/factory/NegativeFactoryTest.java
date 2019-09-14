/*
 * Copyright (c) 2012, 2019 Oracle and/or its affiliates. All rights reserved.
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

import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.fail;

/**
 * @author jwells
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
            locator.reifyDescriptor(
                locator.getBestDescriptor(BuilderHelper.createContractFilter(SimpleService2.class.getName())));
            fail("The SimpleService2 factory has a bad name and so is invalid");
        } catch (MultiException me) {
            assertThat(me.getMessage(), me.getMessage(),
                containsString("@Named on the provide method of a factory must have an explicit value"));
        }
    }

    /**
     * Ensures that a factory producing for the singleton scope works properly
     */
    @Test
    public void testFactoryThatThrowsInSingletonScope() {
        try {
            locator.getService(SimpleService.class);
            fail("The factory throws an exception, so should not have gotten here");
        } catch (MultiException me) {
            assertThat(me.getMessage(), me.getMessage(), containsString(THROW_STRING));
        }
    }

    /**
     * Ensures that a factory producing for the per lookup scope works properly
     */
    @Test
    public void testFactoryThatThrowsInPerLookupScope() {
        try {
            locator.getService(SimpleService3.class);
            fail("The factory throws an exception, so should not have gotten here");
        } catch (MultiException me) {
            assertThat(me.getMessage(), me.getMessage(), containsString(THROW_STRING));
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
            fail("The factory throws an exception, so should not have gotten here");
        } catch (MultiException me) {
            assertThat(me.getMessage(), me.getMessage(), containsString(THROW_STRING));
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
            fail("The factory throws an exception, so should not have gotten here");
        } catch (MultiException me) {
            assertThat(me.getMessage(), me.getMessage(), containsString(THROW_STRING));
        }
    }

    /**
     * Tests that a PerLookup factory depending on itself will notice
     * the infinite problem and throw an exception
     */
    @Test
    public void testInfiniteLoopPerLookupFactory() {
        ServiceLocator ownLocator = LocatorHelper.create();
        ServiceLocatorUtilities.addClasses(ownLocator, InfiniteFactory.class);
        try {
          ownLocator.getService(SimpleService5.class);
          fail("Should have failed due to recursion");
        } catch (MultiException me) {
            assertThat(me.getMessage(), me.getMessage(), containsString("A cycle was detected"));
            assertThat(me.getMessage(), me.getMessage(), containsString(InfiniteFactory.class.getName()));
        }

    }

}
