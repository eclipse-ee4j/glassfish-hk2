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

package org.glassfish.hk2.tests.locator.negative.factory;

import javax.inject.Singleton;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.Immediate;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.PerThread;
import org.glassfish.hk2.tests.locator.utilities.TestModule;
import org.glassfish.hk2.utilities.BuilderHelper;

/**
 * @author jwells
 *
 */
public class NegativeFactoryModule implements TestModule {

    /* (non-Javadoc)
     * @see org.glassfish.hk2.tests.locator.utilities.TestModule#configure(org.glassfish.hk2.api.Configuration)
     */
    @Override
    public void configure(DynamicConfiguration config) {
        config.bind(BuilderHelper.link(BadlyNamedFactory.class).
                to(SimpleService2.class).
                buildFactory());
        
        config.bind(BuilderHelper.link(ThrowyFactory.class).
                to(SimpleService.class).
                in(Singleton.class.getName()).
                buildFactory(Singleton.class));
        
        config.bind(BuilderHelper.link(ThrowyPerLookupFactory.class).
                to(SimpleService3.class).
                in(PerLookup.class.getName()).
                buildFactory(Singleton.class));
        
        config.bind(BuilderHelper.link(ThrowyPerThreadFactory.class).
                to(SimpleService4.class).
                in(PerThread.class.getName()).
                buildFactory(Singleton.class));
        
        config.bind(BuilderHelper.link(ThrowyImmediateFactory.class).
                to(SimpleService5.class).
                in(Immediate.class.getName()).
                buildFactory(Singleton.class));
    }

}
