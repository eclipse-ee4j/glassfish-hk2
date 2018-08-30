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

package org.glassfish.hk2.tests.locator.customresolver;

import javax.inject.Singleton;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.tests.locator.utilities.TestModule;
import org.glassfish.hk2.utilities.BuilderHelper;

/**
 * @author jwells
 *
 */
public class CustomResolverModule implements TestModule {

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Module#configure(org.glassfish.hk2.api.Configuration)
     */
    @Override
    public void configure(DynamicConfiguration configurator) {
        configurator.addActiveDescriptor(ServiceWithCustomInjections.class);

        // Setting it to rank 1 makes it supercede the system injection resolver
        configurator.bind(BuilderHelper.link(CustomInjectResolver.class).
                to(InjectionResolver.class).
                in(Singleton.class.getName()).
                ofRank(1).build());

        configurator.addActiveDescriptor(ConstructorOnlyInjectionResolver.class);
        configurator.addActiveDescriptor(MethodOnlyInjectionResolver.class);
        configurator.addActiveDescriptor(ParameterOnlyInjectionResolver.class);

        configurator.addActiveDescriptor(ParameterAInjectionResolver.class);
        configurator.addActiveDescriptor(ParameterBInjectionResolver.class);

        configurator.bind(BuilderHelper.link(ConstructorOnlyInjectedService.class.getName()).build());
        configurator.bind(BuilderHelper.link(MethodOnlyInjectedService.class.getName()).build());
        configurator.bind(BuilderHelper.link(ParameterInjectionService.class.getName()).build());
        configurator.bind(BuilderHelper.link(ParameterInjectionServiceDuex.class.getName()).build());

        configurator.bind(BuilderHelper.link(ParameterABInjectionService.class.getName()).build());

        configurator.addActiveDescriptor(SimpleService.class);
        configurator.addActiveDescriptor(SimpleServiceDuex.class);
    }

}
