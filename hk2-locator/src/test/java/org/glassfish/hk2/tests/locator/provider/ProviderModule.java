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

package org.glassfish.hk2.tests.locator.provider;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.tests.locator.utilities.TestModule;
import org.glassfish.hk2.utilities.BuilderHelper;

/**
 * @author jwells
 */
public class ProviderModule implements TestModule {

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Module#configure(org.glassfish.hk2.api.Configuration)
     */
    @Override
    public void configure(DynamicConfiguration configurator) {
        configurator.bind(BuilderHelper.link(InstantiationChecker.class).build());
        configurator.bind(BuilderHelper.link(ProviderInjected.class).build());
        
        // These are for the iterable provider tests
        configurator.bind(BuilderHelper.link(EliManning.class).
                to(Character.class).
                to(FootballCharacter.class).
                named(ProviderTest.ELI).
                qualifiedBy(Giants.class.getName()).build());
        
        configurator.bind(BuilderHelper.link(ShadyMcCoy.class).
                to(Character.class).
                to(FootballCharacter.class).
                named(ProviderTest.SHADY).
                qualifiedBy(Eagles.class.getName()).build());
        
        configurator.bind(BuilderHelper.link(Ishmael.class).
                to(Character.class).
                to(BookCharacter.class).
                named(ProviderTest.ISHMAEL).
                build());
        
        configurator.bind(BuilderHelper.link(QueeQueg.class).
                to(Character.class).
                to(BookCharacter.class).
                named(ProviderTest.QUEEQUEG).
                build());
        
        configurator.bind(BuilderHelper.link(Menagerie.class).build());
        
        configurator.addActiveDescriptor(PerLookupService.class);
        configurator.addActiveDescriptor(ProviderInjectedPerLookup.class);
        
    }

}
