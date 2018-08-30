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

package org.glassfish.hk2.tests.locator.proxysamescope;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.tests.locator.utilities.TestModule;

/**
 * @author jwells
 *
 */
public class ProxiableSameScopeModule implements TestModule {

    /* (non-Javadoc)
     * @see org.glassfish.hk2.tests.locator.utilities.TestModule#configure(org.glassfish.hk2.api.DynamicConfiguration)
     */
    @Override
    public void configure(DynamicConfiguration config) {
        config.addActiveDescriptor(ProxiableSingletonNoLazyContext.class);
        config.addActiveDescriptor(ProxiableSingletonNoLazy2Context.class);
        config.addActiveDescriptor(ProxiableSingletonContext.class);
        
        config.addActiveDescriptor(ProxiableServiceA.class);
        config.addActiveDescriptor(ProxiableServiceB.class);
        config.addActiveDescriptor(ProxiableServiceC.class);
        config.addActiveDescriptor(ProxiableServiceD.class);
        config.addActiveDescriptor(ProxiableServiceDPrime.class);
        config.addActiveDescriptor(ProxiableServiceE.class);
        config.addActiveDescriptor(ProxiableServiceF.class);
        config.addActiveDescriptor(ProxiableServiceFPrime.class);
        config.addActiveDescriptor(ProxiableServiceG.class);
        config.addActiveDescriptor(ProxiableServiceH.class);
        config.addActiveDescriptor(ProxiableServiceI.class);
        
        config.addActiveDescriptor(PerLookupServiceA.class);
        
        config.addActiveDescriptor(SingletonServiceA.class);
    }

}
