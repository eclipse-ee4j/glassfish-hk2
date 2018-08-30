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

package org.glassfish.hk2.configuration.api;

import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.configuration.hub.api.ManagerUtilities;
import org.glassfish.hk2.configuration.internal.ChildInjectResolverImpl;
import org.glassfish.hk2.configuration.internal.ConfigurationListener;
import org.glassfish.hk2.configuration.internal.ConfigurationValidationService;
import org.glassfish.hk2.configuration.internal.ConfiguredByContext;
import org.glassfish.hk2.configuration.internal.ConfiguredByInjectionResolver;
import org.glassfish.hk2.configuration.internal.ConfiguredValidator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

/**
 * Useful utilities for using the hk2 configuration system
 * @author jwells
 *
 */
public class ConfigurationUtilities {
    /**
     * Enables the Configuration subsystem of HK2. This call is idempotent
     * 
     * @param locator The non-null service locator in which to enable the
     * configuration subsystem
     */
    public static void enableConfigurationSystem(ServiceLocator locator) {
        ServiceHandle<ConfiguredByContext> alreadyThere = locator.getServiceHandle(ConfiguredByContext.class);
        if (alreadyThere != null) {
            // The assumption is that if this service is there then this is already on, don't do it again
            return;
        }
        
        ManagerUtilities.enableConfigurationHub(locator);
        
        ServiceLocatorUtilities.addClasses(locator, true, ConfiguredValidator.class);
        
        ServiceLocatorUtilities.addClasses(locator, true,
                ConfiguredByContext.class,
                ConfigurationValidationService.class,
                ConfiguredByInjectionResolver.class,
                ConfigurationListener.class,
                ChildInjectResolverImpl.class);
        
        // Creates demand, starts the thing off
        locator.getService(ConfigurationListener.class);
    }

}
