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

package org.glassfish.hk2.runlevel.tests.utilities;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.runlevel.RunLevelContext;
import org.glassfish.hk2.runlevel.RunLevelServiceModule;
import org.glassfish.hk2.runlevel.RunLevelServiceUtilities;
import org.glassfish.hk2.runlevel.internal.AsyncRunLevelContext;
import org.glassfish.hk2.runlevel.internal.RunLevelControllerImpl;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

/**
 * @author jwells
 *
 */
public class Utilities {
    public enum InitType {
        DYNAMIC,
        UTILITIES,
        MODULE
    }
    
    /**
     * Creates a ServiceLocator equipped with a RunLevelService and the set of classes given
     * 
     * @param classes The set of classes to also add to the descriptor (should probably contain some run level services, right?)
     * @return The ServiceLocator to use
     */
    public static ServiceLocator getServiceLocator(InitType initType, Class<?>... classes) {
        ServiceLocator locator = ServiceLocatorFactory.getInstance().create(null);
        
        if (InitType.DYNAMIC.equals(initType)) {
            DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
            DynamicConfiguration config = dcs.createDynamicConfiguration();
        
            config.addActiveDescriptor(RunLevelControllerImpl.class);
            config.addActiveDescriptor(AsyncRunLevelContext.class);
            config.addActiveDescriptor(RunLevelContext.class);
            
            config.commit();
        }
        else if (InitType.UTILITIES.equals(initType)) {
            RunLevelServiceUtilities.enableRunLevelService(locator);
            
            // Twice to test idempotentness.  Is that a word?
            RunLevelServiceUtilities.enableRunLevelService(locator);
        }
        else if (InitType.MODULE.equals(initType)) {
            ServiceLocatorUtilities.bind(locator, new RunLevelServiceModule());
        }
        
        ServiceLocatorUtilities.addClasses(locator, classes);
        
        return locator;
    }
    
    public static ServiceLocator getServiceLocator(Class<?>... classes) {
        return getServiceLocator(InitType.MODULE, classes);
        
    }

}
