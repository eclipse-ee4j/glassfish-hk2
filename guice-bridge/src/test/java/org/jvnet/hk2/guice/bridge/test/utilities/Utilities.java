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

package org.jvnet.hk2.guice.bridge.test.utilities;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.testing.junit.HK2TestModule;
import org.jvnet.hk2.testing.junit.HK2TestUtilities;

/**
 * @author jwells
 *
 */
public class Utilities {
    public static ServiceLocator createLocator(String name, HK2TestModule module) {
        ServiceLocator retVal = HK2TestUtilities.create(name, module);
        
        ServiceLocatorUtilities.enableLookupExceptions(retVal);
        
        GuiceBridge.getGuiceBridge().initializeGuiceBridge(retVal);
        
        return retVal;
    }

}
