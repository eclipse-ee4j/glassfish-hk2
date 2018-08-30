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

package org.glassfish.hk2.tests.locator.justintime;

import javax.inject.Singleton;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.JustInTimeInjectionResolver;
import org.glassfish.hk2.tests.locator.utilities.TestModule;
import org.glassfish.hk2.utilities.BuilderHelper;

/**
 * @author jwells
 *
 */
public class JustInTimeModule implements TestModule {

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Module#configure(org.glassfish.hk2.api.Configuration)
     */
    @Override
    public void configure(DynamicConfiguration configurator) {
        // Whoops, I forgot to add my service here
        // Lucky for me, I can add it with my just
        // in time resolver:
        configurator.bind(BuilderHelper.link(
                SimpleServiceJITResolver.class).to(JustInTimeInjectionResolver.class).in(Singleton.class.getName()).build());
        
        configurator.bind(BuilderHelper.link(
                InjectedThriceService.class).in(Singleton.class.getName()).build());
        
        // This next set is for the DoubleTrouble resolver, which has its own resolution issues
        configurator.bind(BuilderHelper.link(
                DoubleTroubleJITResolver.class).to(JustInTimeInjectionResolver.class).in(Singleton.class.getName()).build());
        
        configurator.bind(BuilderHelper.link(
                DoubleTroubleService.class).in(Singleton.class.getName()).build());

        // XXX Isn't this the same as the first bind invocation above?
        // This JIT resolver is for the lookup case
        configurator.bind(BuilderHelper.link(SimpleServiceJITResolver.class).
                to(JustInTimeInjectionResolver.class).
                in(Singleton.class.getName()).
                build());
    }

}
