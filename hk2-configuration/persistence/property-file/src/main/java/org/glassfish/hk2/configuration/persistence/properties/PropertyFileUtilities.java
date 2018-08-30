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

package org.glassfish.hk2.configuration.persistence.properties;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.configuration.hub.api.ManagerUtilities;
import org.glassfish.hk2.configuration.persistence.properties.internal.PropertyFileServiceImpl;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

/**
 * This utility should be used when initializing hk2 in order to properly
 * add the PropertyFileService to a specific registry
 * 
 * @author jwells
 *
 */
public class PropertyFileUtilities {
    /**
     * This class adds the system implementation of {@link PropertyFileService}
     * to the given service locator.  If an implementation of {@link PropertyFileService}
     * already exists this method does nothing
     * 
     * @param locator The non-null ServiceLocator to add the PropertyFileService into
     */
    public static void enablePropertyFileService(ServiceLocator locator) {
        if (locator.getService(PropertyFileService.class) != null) return;
        
        ManagerUtilities.enableConfigurationHub(locator);
        
        ServiceLocatorUtilities.addClasses(locator, PropertyFileServiceImpl.class);
    }

}
