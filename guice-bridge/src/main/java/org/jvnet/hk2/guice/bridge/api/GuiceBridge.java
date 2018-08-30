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

package org.jvnet.hk2.guice.bridge.api;

import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.jvnet.hk2.guice.bridge.internal.GuiceBridgeImpl;

/**
 * This class can be used to initialize a ServiceLocator for use with
 * the Guice/HK2 Bridge
 *
 * @author jwells
 *
 */
public abstract class GuiceBridge {
    
    private final static GuiceBridge INSTANCE = new GuiceBridgeImpl();
    
    public static GuiceBridge getGuiceBridge() {
        return INSTANCE;
    }
    
    /**
     * This method will initialize the given service locator for use with the Guice/HK2
     * bridge.  It adds into the service locator an implementation of GuiceIntoHK2Bridge
     * and also the custom scope needed for Guice services.  This method is idempotent,
     * in that if these services have already been added to the service locator
     * they will not be added again
     * 
     * @param locator A non-null locator to use with the Guice/HK2 bridge
     * @throws MultiException On failure
     */
    public abstract void initializeGuiceBridge(ServiceLocator locator) throws MultiException;
}
