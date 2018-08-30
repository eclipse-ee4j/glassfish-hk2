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

package org.glassfish.hk2.tests.locator.idempotent;

import java.util.List;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.DuplicateServiceException;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.DescriptorImpl;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class IdempotentTest {
    /**
     * Tests that in the most basic case, idempotence works (adding the
     * same service twice)
     */
    @Test // @org.junit.Ignore
    public void testBasicIdempotence() {
        ServiceLocator locator = LocatorHelper.create();
        DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
        
        Descriptor addMeOnce = BuilderHelper.link(SimpleService.class.getName()).build();
        Filter makeSureOnceOnly = BuilderHelper.createContractFilter(SimpleService.class.getName());
        
        {
            DynamicConfiguration config = dcs.createDynamicConfiguration();
        
            config.bind(addMeOnce);
            config.addIdempotentFilter(makeSureOnceOnly);
            
            config.commit();
        }
        
        List<ActiveDescriptor<?>> allSimpleServices = locator.getDescriptors(makeSureOnceOnly);
        Assert.assertEquals(1, allSimpleServices.size());
        
        {
            DynamicConfiguration config = dcs.createDynamicConfiguration();
        
            config.bind(addMeOnce);
            config.addIdempotentFilter(makeSureOnceOnly);
            
            try {
                config.commit();
                Assert.fail("Should have failed, SimpleService has already been added");
            }
            catch (MultiException me) {
                List<Throwable> errors = me.getErrors();
                Assert.assertEquals(1, errors.size());
                Assert.assertTrue(errors.get(0) instanceof DuplicateServiceException);
                
                DuplicateServiceException dse = (DuplicateServiceException) errors.get(0);
                Assert.assertTrue(DescriptorImpl.descriptorEquals(addMeOnce, dse.getExistingDescriptor()));
            }
        }
        
        allSimpleServices = locator.getDescriptors(makeSureOnceOnly);
        Assert.assertEquals(1, allSimpleServices.size());
    }

}
