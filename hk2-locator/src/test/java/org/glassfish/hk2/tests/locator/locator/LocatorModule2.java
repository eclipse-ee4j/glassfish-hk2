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

package org.glassfish.hk2.tests.locator.locator;

import javax.inject.Singleton;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.tests.locator.utilities.TestModule;
import org.glassfish.hk2.utilities.BuilderHelper;

/**
 * @author jwells
 *
 */
public class LocatorModule2 implements TestModule {

    /* (non-Javadoc)
     * @see org.glassfish.hk2.tests.locator.utilities.TestModule#configure(org.glassfish.hk2.api.DynamicConfiguration)
     */
    @Override
    public void configure(DynamicConfiguration config) {
        // This is for a test of getService followed by getAllServices
        // where there are two implementations of a single contract
        config.bind(BuilderHelper.link(MultiContractImpl1.class.getName()).
                in(PerLookup.class.getName()).
                andLoadWith(RecordingLoader.getInstance()).
                to(MultiContract.class.getName()).build());
        config.bind(BuilderHelper.link(MultiContractImpl2.class.getName()).
                in(Singleton.class.getName()).
                andLoadWith(RecordingLoader.getInstance()).
                to(MultiContract.class.getName()).build());

    }

}
