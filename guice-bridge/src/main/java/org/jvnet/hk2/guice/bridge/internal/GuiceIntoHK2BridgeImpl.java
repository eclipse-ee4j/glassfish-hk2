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

package org.jvnet.hk2.guice.bridge.internal;

import javax.inject.Inject;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.jvnet.hk2.annotations.Service;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

import com.google.inject.Injector;

/**
 * @author jwells
 *
 */
@Service
public class GuiceIntoHK2BridgeImpl implements GuiceIntoHK2Bridge {
    @Inject
    private ServiceLocator locator;

    /* (non-Javadoc)
     * @see org.jvnet.hk2.guice.bridge.api.GuiceBridge#bridgeGuiceInjector(com.google.inject.Injector)
     */
    @Override
    public void bridgeGuiceInjector(Injector injector) {
        GuiceToHk2JITResolver resolver = new GuiceToHk2JITResolver(locator, injector);
        
        ServiceLocatorUtilities.addOneConstant(locator, resolver);
    }

}
