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

package org.glassfish.hk2.tests.locator.proxiable;

import javax.inject.Singleton;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.tests.locator.utilities.TestModule;
import org.glassfish.hk2.utilities.BuilderHelper;

/**
 * @author jwells
 *
 */
public class ProxiableModule implements TestModule {

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Module#configure(org.glassfish.hk2.api.Configuration)
     */
    @Override
    public void configure(DynamicConfiguration configurator) {
        configurator.bind(
                BuilderHelper.link(SeasonContext.class).to(Context.class).in(Singleton.class.getName()).build());
        
        configurator.bind(
                BuilderHelper.link(Spring.class).to(Season.class).qualifiedBy(SeasonIndicator.class.getName()).in(SeasonScope.class).build());
        configurator.bind(
                BuilderHelper.link(Summer.class).to(Season.class).qualifiedBy(SeasonIndicator.class.getName()).in(SeasonScope.class).build());
        configurator.bind(
                BuilderHelper.link(Fall.class).to(Season.class).qualifiedBy(SeasonIndicator.class.getName()).in(SeasonScope.class).build());
        configurator.bind(
                BuilderHelper.link(Winter.class).to(Season.class).qualifiedBy(SeasonIndicator.class.getName()).in(SeasonScope.class).build());
        
        // For the ProxyCtl test
        configurator.addActiveDescriptor(PostConstructedProxiedService.class);
        
        // For the method access test
        configurator.bind(
        		BuilderHelper.link(NorthernHemisphere.class).in(SeasonScope.class).build());
        configurator.bind(
        		BuilderHelper.link(SouthernHemisphere.class).in(Singleton.class).build());
    }

}
