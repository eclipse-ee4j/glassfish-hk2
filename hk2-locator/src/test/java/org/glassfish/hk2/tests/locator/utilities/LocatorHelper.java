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

package org.glassfish.hk2.tests.locator.utilities;

import junit.framework.Assert;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;

/**
 * @author jwells
 * 
 */
public class LocatorHelper {
    /** This should be thrown from negative tests */
    public final static String EXPECTED = "Expected Exception";

    private final static ServiceLocatorFactory factory = ServiceLocatorFactory
            .getInstance();

    /**
     * Creates an unnamed, untracked service locator
     * 
     * @return An unnamed, untracked service locator
     */
    public static ServiceLocator create() {
        return factory.create(null);
    }

    /**
     * Creates an unnamed, untracked service locator with the given parent
     * 
     * @param parent
     *            The non-null parent to be associated with this locator
     * @return An unnamed, untracked service locator with the given parent
     */
    public static ServiceLocator create(ServiceLocator parent) {
        return factory.create(null, parent);
    }

    /**
     * Will create a ServiceLocator after doing test-specific bindings from the
     * TestModule
     * 
     * @param name
     *            The name of the service locator to create. Should be unique
     *            per test, otherwise this method will fail.
     * @param module
     *            The test module, that will do test specific bindings. May be
     *            null
     * @return A service locator with all the test specific bindings bound
     */
    public static ServiceLocator create(String name, TestModule module) {
        return create(name, null, module);
    }

    /**
     * Will create a ServiceLocator after doing test-specific bindings from the
     * TestModule
     * 
     * @param name
     *            The name of the service locator to create. Should be unique
     *            per test, otherwise this method will fail.
     * @param parent
     *            The parent locator this one should have. May be null
     * @param module
     *            The test module, that will do test specific bindings. May be
     *            null
     * @return A service locator with all the test specific bindings bound
     */
    public static ServiceLocator create(String name, ServiceLocator parent,
            TestModule module) {
        ServiceLocator retVal = factory.find(name);
        Assert.assertNull(
                "There is already a service locator of this name, change names to ensure a clean test: "
                        + name, retVal);

        retVal = factory.create(name, parent);

        if (module == null) return retVal;

        DynamicConfigurationService dcs = retVal
                .getService(DynamicConfigurationService.class);
        Assert.assertNotNull(
                "Their is no DynamicConfigurationService.  Epic fail", dcs);

        DynamicConfiguration dc = dcs.createDynamicConfiguration();
        Assert.assertNotNull("DynamicConfiguration creation failure", dc);

        module.configure(dc);

        dc.commit();

        return retVal;
    }

    /**
     * Creates a ServiceLocator equipped with a RunLevelService and the set of
     * classes given
     * 
     * @param classes
     *            The set of classes to also add to the descriptor (should
     *            probably contain some run level services, right?)
     * @return The ServiceLocator to use
     */
    @SuppressWarnings("unchecked")
    public static ServiceLocator getServiceLocator(Class<?>... classes) {
        ServiceLocator locator = ServiceLocatorFactory.getInstance().create(null);
        
        DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
        DynamicConfiguration config = dcs.createDynamicConfiguration();
        
        for (Class<?> clazz : classes) {
            if (Factory.class.isAssignableFrom(clazz)) {
                Class<? extends Factory<Object>> fClass = (Class<? extends Factory<Object>>) clazz;
                
                config.addActiveFactoryDescriptor(fClass);
            }
            else {
                config.addActiveDescriptor(clazz);
            }
        }
        
        config.commit();
        
        return locator;
    }
}
