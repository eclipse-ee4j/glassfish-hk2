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

package org.jvnet.hk2.guice.bridge.test.bidirectional;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.jvnet.hk2.guice.bridge.internal.GuiceIntoHK2BridgeImpl;
import org.jvnet.hk2.guice.bridge.internal.GuiceScopeContext;
import org.jvnet.hk2.testing.junit.HK2TestModule;

/**
 * @author jwells
 *
 */
public class BiDirectionalBridgeModule implements HK2TestModule {

    /* (non-Javadoc)
     * @see org.jvnet.hk2.testing.junit.HK2TestModule#configure(org.glassfish.hk2.api.DynamicConfiguration)
     */
    @Override
    public void configure(DynamicConfiguration config) {
        config.addActiveDescriptor(HK2Service1_0.class);
        config.addActiveDescriptor(HK2Service1_2.class);
    }

}
