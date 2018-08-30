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

package org.glassfish.hk2.tests.locator.typesafety;

import javax.inject.Singleton;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.tests.locator.utilities.TestModule;
import org.glassfish.hk2.utilities.BuilderHelper;

/**
 * @author jwells
 *
 */
public class TypeSafetyModule implements TestModule {

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Module#configure(org.glassfish.hk2.api.Configuration)
     */
    @Override
    public void configure(DynamicConfiguration configurator) {
        configurator.bind(BuilderHelper.link(PSIntegerFactory.class).
                to(ParameterizedService.class).
                buildFactory());
        
        // The ranking here should make this one get chosen in ambiguous cases
        configurator.bind(BuilderHelper.link(PSStringFactory.class).
                to(ParameterizedService.class).
                buildFactory(Singleton.class.getName()));
        
        // This guy is called by default
        configurator.bind(BuilderHelper.link(PSDoubleFactory.class).
                to(ParameterizedService.class).
                ofRank(100).
                buildFactory());
        
        configurator.bind(BuilderHelper.link(TypeVariableService.class).build());
        
        configurator.bind(BuilderHelper.link(RawPSInjectee.class).build());
        configurator.bind(BuilderHelper.link(WildcardPSInjectee.class).build());
        configurator.bind(BuilderHelper.link(WildcardUpperBoundPSInjectee.class).build());
        configurator.bind(BuilderHelper.link(WildcardLowerBoundPSInjectee.class).build());
        configurator.bind(BuilderHelper.link(WildcardTVSInjectee.class).build());
        configurator.bind(BuilderHelper.link(ActualTypeTVSInjectee.class).build());
        configurator.bind(BuilderHelper.link(TypeVariableTVSInjectee.class).build());
        
    }

}
