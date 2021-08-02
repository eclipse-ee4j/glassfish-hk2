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

package org.glassfish.hk2.tests.locator.proxiable2;

import jakarta.inject.Singleton;

import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.tests.locator.utilities.TestModule;
import org.glassfish.hk2.utilities.BuilderHelper;

/**
 * @author jwells
 *
 */
public class ProxiableModule implements TestModule {

    /* (non-Javadoc)
     * @see org.glassfish.hk2.tests.locator.utilities.TestModule#configure(org.glassfish.hk2.api.DynamicConfiguration)
     */
    @Override
    public void configure(DynamicConfiguration config) {
        config.bind(BuilderHelper.link(ProxiableService.class.getName()).
                in(Singleton.class.getName()).
                proxy().
                build());
        
        config.bind(BuilderHelper.link(ProxiableService2.class.getName()).
                in(Singleton.class.getName()).
                proxy().
                build());
        
        config.bind(BuilderHelper.link(ProxiableSingletonContext.class.getName()).
                to(Context.class.getName()).
                in(Singleton.class.getName()).
                build());
        
        config.bind(BuilderHelper.link(ProxiableServiceInContext.class.getName()).
                in(ProxiableSingleton.class.getName()).
                build());
        
        config.bind(BuilderHelper.link(ProxiableServiceInContext2.class.getName()).
                in(ProxiableSingleton.class.getName()).
                build());
        
        config.bind(BuilderHelper.link(NotProxiableService.class.getName()).
                in(ProxiableSingleton.class.getName()).
                proxy(false).
                build());
        
        config.bind(BuilderHelper.link(ProxiableServiceFactory.class.getName()).
                to(ProxiableServiceFromFactory.class.getName()).
                in(Singleton.class.getName()).
                proxy(true).
                buildFactory(Singleton.class.getName()));

    }

}