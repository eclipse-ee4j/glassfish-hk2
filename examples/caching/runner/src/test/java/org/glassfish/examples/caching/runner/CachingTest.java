/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.examples.caching.runner;

import junit.framework.Assert;

import org.glassfish.examples.caching.services.ExpensiveConstructor;
import org.glassfish.examples.caching.services.ExpensiveMethods;
import org.glassfish.examples.caching.services.InputFactory;
import org.junit.Test;
import org.jvnet.hk2.testing.junit.HK2Runner;

/**
 * These are junit tests that ensure that the caching
 * interceptors are working properly
 * 
 * @author jwells
 *
 */
public class CachingTest extends HK2Runner {
    /**
     * Tests that the expensive method on the ExpensiveMethods class is
     * properly cached
     */
    @Test
    public void testMethodsAreIntercepted() {
        ExpensiveMethods expensiveMethods = testLocator.getService(ExpensiveMethods.class);
        
        // Ensure that we are at zero calls to the expensive method
        expensiveMethods.clear();
        Assert.assertEquals(0, expensiveMethods.getNumTimesCalled());
        
        // Now call the expensive method
        int result = expensiveMethods.veryExpensiveCalculation(1);
        Assert.assertEquals(2, result);
        
        // The expensive method should have been called
        Assert.assertEquals(1, expensiveMethods.getNumTimesCalled());
        
        // Now call the expensive method ten more times
        for (int i = 0; i < 10; i++) {
            result = expensiveMethods.veryExpensiveCalculation(1);
            Assert.assertEquals(2, result);
        }
        
        // But the expensive call was never made again, since the result was cached!
        Assert.assertEquals(1, expensiveMethods.getNumTimesCalled());
        
        // Now call it again, with a different input
        result = expensiveMethods.veryExpensiveCalculation(2);
        Assert.assertEquals(3, result);
        
        // The expensive method was called again since it had not seen 2 before
        Assert.assertEquals(2, expensiveMethods.getNumTimesCalled());
        
        // Now call the expensive method with both 1 and 2 several times
        for (int i = 0; i < 10; i++) {
            result = expensiveMethods.veryExpensiveCalculation(1);
            Assert.assertEquals(2, result);
            
            result = expensiveMethods.veryExpensiveCalculation(2);
            Assert.assertEquals(3, result);
        }
        
        // But the expensive method was not called again, as the results were cached
        Assert.assertEquals(2, expensiveMethods.getNumTimesCalled());
        
        
    }
    
    /**
     * Tests that the ExpensiveConstructor class is only created
     * when the input parameter changes, even though it is in the
     * PerLookup scope.  The InputFactory is used to modify
     * the input parameter of ExpensiveConstructor
     */
    @Test
    public void testConstructorsAreIntercepted() {
        // Clears out the class so we can see how many times it is created
        ExpensiveConstructor.clear();
        
        // Gets the factory that changes the input to the ExpensiveConstructor constructor
        InputFactory inputFactory = testLocator.getService(InputFactory.class);
        
        inputFactory.setInput(2);
        
        // ExpensiveConstructor is PerLookup and is therefor nominally created for every lookup
        ExpensiveConstructor instanceOne = testLocator.getService(ExpensiveConstructor.class);
        
        // The real calculation is to multiply by two, ensure we got a good service
        int computation = instanceOne.getComputation();
        Assert.assertEquals(4, computation);
        
        // Also see that the service was created once
        Assert.assertEquals(1, ExpensiveConstructor.getNumTimesConstructed());
        
        // Now look it up again, only this time it will NOT be created (since it'll use the one from the intercepted cache)
        ExpensiveConstructor instanceTwo = testLocator.getService(ExpensiveConstructor.class);
        
        // Same object should have same computation
        computation = instanceTwo.getComputation();
        Assert.assertEquals(4, computation);
        
        // But amazingly, the object was NOT recreated:
        Assert.assertEquals(1, ExpensiveConstructor.getNumTimesConstructed());
        
        // Further proof that it was not recreated:
        Assert.assertTrue(instanceOne == instanceTwo);
        
        // Now change the input paramter
        inputFactory.setInput(8);
        
        // Lookup the service again
        ExpensiveConstructor instanceThree = testLocator.getService(ExpensiveConstructor.class);
        
        // This time the calculation should be different
        computation = instanceThree.getComputation();
        Assert.assertEquals(16, computation);
        
        // And this time the objects are NOT the same
        Assert.assertFalse(instanceOne.equals(instanceThree));
    }
} 
