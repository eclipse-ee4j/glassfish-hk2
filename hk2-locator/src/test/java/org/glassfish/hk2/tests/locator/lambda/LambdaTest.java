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

package org.glassfish.hk2.tests.locator.lambda;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class LambdaTest {
	/**
	 * Tests that a lambda can be used in a constructor
	 */
    @Test @org.junit.Ignore
    public void testLambdaInConstructor() {
    	ServiceLocator locator = LocatorHelper.getServiceLocator(AAndB.class, LambdaInConstructorService.class);
    	
    	LambdaInConstructorService lics = locator.getService(LambdaInConstructorService.class);
    	Assert.assertEquals(1, lics.getSum());
    	Assert.assertEquals(-1, lics.getDiff());
    }
    
    @Test
    public void testLambdaInConstructor2() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(LambdaInjectionResolver.class);
        
        ServiceLocatorUtilities.addClasses(locator, LambdaInConstructorService2.class, SupplierIntegerFactory.class);
        
        LambdaInConstructorService2 lics2 = locator.getService(LambdaInConstructorService2.class);
        Assert.assertEquals(9, lics2.getValue());
    }

}
