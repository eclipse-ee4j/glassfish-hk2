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

package org.glassfish.hk2.tests.locator.perlookup;

import junit.framework.Assert;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class PerLookupTest {
    private final static String TEST_NAME = "PerLookupTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new PerLookupModule());
    
    /**
     * All PerLookups should be different
     */
    @Test
    public void testThreeDifferentValues() {
        ThriceInjectedService tis = locator.getService(ThriceInjectedService.class);
        Assert.assertNotNull(tis);
        
        SimpleService byConstructor = tis.getByConstructor();
        SimpleService byField = tis.getByField();
        SimpleService byMethod = tis.getByMethod();
        
        Assert.assertNotNull(byConstructor);
        Assert.assertNotNull(byField);
        Assert.assertNotNull(byMethod);
        
        Assert.assertNotSame("The constructor " + byConstructor + " and field " + byField + " are the same", byConstructor, byField);
        Assert.assertNotSame(byConstructor, byMethod);
        Assert.assertNotSame(byField, byMethod);
    }
    
    /**
     * An injection of null is properly disposed
     */
    @Test
    public void testNullInjectionProperlyDisposed() {
        ServiceHandle<NullInjectedPerLookupService> serviceHandle =
                locator.getServiceHandle(NullInjectedPerLookupService.class);
        Assert.assertNotNull(serviceHandle);
        
        NullInjectedPerLookupService nips = serviceHandle.getService();
        Assert.assertNotNull(nips);
        Assert.assertNull(nips.getShouldBeNull());
        
        NullInterfaceFactory nif = locator.getService(
                (new TypeLiteral<Factory<NullInterface>>() {}).getType());
        Assert.assertNotNull(nif);
        
        Assert.assertFalse(nif.getDisposeCalled());
        
        serviceHandle.destroy();
        
        Assert.assertTrue(nif.getDisposeCalled());
    }

}
