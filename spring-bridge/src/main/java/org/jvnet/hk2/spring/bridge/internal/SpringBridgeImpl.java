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

package org.jvnet.hk2.spring.bridge.internal;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.jvnet.hk2.spring.bridge.api.SpringBridge;
import org.jvnet.hk2.spring.bridge.api.SpringIntoHK2Bridge;

/**
 * @author jwells
 *
 */
public class SpringBridgeImpl extends SpringBridge {

    /* (non-Javadoc)
     * @see org.jvnet.hk2.spring.bridge.api.SpringBridge#initializeSpringBridge(org.glassfish.hk2.api.ServiceLocator)
     */
    @Override
    public void initializeSpringBridge(ServiceLocator locator)
            throws MultiException {
        boolean addService = true;
        if (locator.getBestDescriptor(BuilderHelper.createContractFilter(SpringIntoHK2Bridge.class.getName())) != null) {
            addService = false;
        }
        
        boolean addContext = true;
        if (locator.getBestDescriptor(BuilderHelper.createContractFilter(SpringScopeContext.class.getName())) != null) {
            addContext = false;
        }
        
        if (addService == false && addContext == false) return;
        
        DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
        if (dcs == null) return;
        
        DynamicConfiguration config = dcs.createDynamicConfiguration();
        
        if (addService) {
            config.addActiveDescriptor(SpringIntoHK2BridgeImpl.class);
        }
        
        if (addContext) {
            config.addActiveDescriptor(SpringScopeContext.class);
        }
        
        config.commit();
    }

}
