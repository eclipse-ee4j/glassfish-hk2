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

package org.glassfish.hk2.tests.locator.descriptornamed;

import javax.inject.Singleton;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.tests.locator.utilities.TestModule;
import org.glassfish.hk2.utilities.BuilderHelper;

/**
 * @author jwells
 *
 */
public class DescriptorNamedModule implements TestModule {

    /**
     * This guy will put in the same service three times, twice with a name and once without a name.
     * The one without a name will get a higher rank to prefer it to the others
     * 
     * @see org.glassfish.hk2.tests.locator.utilities.TestModule#configure(org.glassfish.hk2.api.DynamicConfiguration)
     */
    @Override
    public void configure(DynamicConfiguration config) {
        // No name, but higher rank
        config.bind(BuilderHelper.link(DescriptorNamedService.class).
                in(Singleton.class.getName()).
                ofRank(1).build());
        
        // named alice
        config.bind(BuilderHelper.link(DescriptorNamedService.class).
                in(Singleton.class.getName()).
                named(DescriptorNamedTest.ALICE).
                build());
        
        // named bob
        config.bind(BuilderHelper.link(DescriptorNamedService.class).
                in(Singleton.class.getName()).
                named(DescriptorNamedTest.BOB).
                build());
    }

}
