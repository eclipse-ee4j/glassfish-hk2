/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.locator.context.multiples;

import java.util.Map;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class MultipleContextsTest {
    @Test // @org.junit.Ignore
    public void testMultipleRollingContexts() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(MultiContextA.class,
                MultiContextB.class,
                MultiContextC.class,
                Service1.class,
                Service2.class,
                Service3.class,
                Service4.class,
                Service5.class,
                Service6.class);
        
        Assert.assertNotNull(locator.getService(Service1.class));
        Assert.assertNotNull(locator.getService(Service2.class));
        Assert.assertNotNull(locator.getService(Service3.class));
        Assert.assertNotNull(locator.getService(Service4.class));
        Assert.assertNotNull(locator.getService(Service5.class));
        Assert.assertNotNull(locator.getService(Service6.class));
        
        MultiContextA contextA = locator.getService(MultiContextA.class);
        Assert.assertNotNull(contextA);
        
        MultiContextB contextB = locator.getService(MultiContextB.class);
        Assert.assertNotNull(contextB);
        
        MultiContextC contextC = locator.getService(MultiContextC.class);
        Assert.assertNotNull(contextC);
        
        Map<ActiveDescriptor<?>, Object> aInstances = contextA.getInstances();
        Assert.assertEquals(2, aInstances.size());
        
        Map<ActiveDescriptor<?>, Object> bInstances = contextB.getInstances();
        Assert.assertEquals(2, bInstances.size());
        
        Map<ActiveDescriptor<?>, Object> cInstances = contextC.getInstances();
        Assert.assertEquals(2, cInstances.size());
    }

}
