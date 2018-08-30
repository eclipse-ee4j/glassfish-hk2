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

package org.glassfish.hk2.tests.locator.negative.scope;

import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class NegativeScopeTest {
    private final static String TEST_NAME = "NegativeScopeTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new NegativeScopeModule());
    
    /**
     * A class with two scopes
     */
    @Test
    public void testDoubleScope() {
        try {
            locator.reifyDescriptor(locator.getBestDescriptor(BuilderHelper.createContractFilter(
                    TwoScopeService.class.getName())));
            Assert.fail("two scope service should cause failure");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage(), me.getMessage().contains(" may not have more than one scope.  It has at least "));
        }
    }
    
    /**
     * A class with wrong bound scope
     */
    @Test
    public void testWrongScope() {
        try {
            locator.reifyDescriptor(locator.getBestDescriptor(BuilderHelper.createContractFilter(
                    WrongScopeService.class.getName())));
            Assert.fail("wrong scope service should cause failure");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage(), me.getMessage().contains("The scope name given in the descriptor ("));
        }
    }
    
    /**
     * This tests a service in a scope with no cooresponding
     * implementation of Context
     */
    @Test
    public void testNoContextScope() {
        try {
            locator.getService(NoContextService.class);
            Assert.fail("The service has no Context and cannot be created");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage(),
                    me.getMessage().contains("Could not find an active context for "));
            
        }
        
    }

}
