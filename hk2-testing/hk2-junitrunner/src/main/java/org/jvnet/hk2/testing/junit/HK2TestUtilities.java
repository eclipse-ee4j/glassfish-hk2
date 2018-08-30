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

package org.jvnet.hk2.testing.junit;

import junit.framework.Assert;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;

/**
 * These are other useful test utilities that can be used
 * @author jwells
 *
 */
public class HK2TestUtilities {
    private final static ServiceLocatorFactory factory = ServiceLocatorFactory.getInstance();
    
    public static ServiceLocator create(String name, HK2TestModule... modules) {
        return create(name, null, modules);
    }
    
    /**
     * Will create a ServiceLocator after doing test-specific bindings from the TestModule
     * 
     * @param name The name of the service locator to create.  Should be unique per test, otherwise
     * this method will fail.
     * @param parent The parent locator this one should have.  May be null
     * @param modules The test modules, that will do test specific bindings.  May be null
     * @return A service locator with all the test specific bindings bound
     */
    public static ServiceLocator create(String name, ServiceLocator parent, HK2TestModule... modules) {
        ServiceLocator retVal = factory.find(name);
        Assert.assertNull("There is already a service locator of this name, change names to ensure a clean test: " + name, retVal);
        
        retVal = factory.create(name, parent);
        
        if (modules == null || modules.length <= 0) return retVal;
        
        DynamicConfigurationService dcs = retVal.getService(DynamicConfigurationService.class);
        Assert.assertNotNull("Their is no DynamicConfigurationService.  Epic fail", dcs);
        
        DynamicConfiguration dc = dcs.createDynamicConfiguration();
        Assert.assertNotNull("DynamicConfiguration creation failure", dc);
        
        for (HK2TestModule module : modules) {
            module.configure(dc);
        }
        
        dc.commit();
        
        return retVal;
    }

}
