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

package org.glassfish.hk2.tests.locator.negative.classanalysis;

import org.glassfish.hk2.api.ClassAnalyzer;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.tests.locator.utilities.TestModule;
import org.glassfish.hk2.utilities.BuilderHelper;

/**
 * @author jwells
 *
 */
public class NegativeClassAnalysisModule implements TestModule {

    /* (non-Javadoc)
     * @see org.glassfish.hk2.tests.locator.utilities.TestModule#configure(org.glassfish.hk2.api.DynamicConfiguration)
     */
    @Override
    public void configure(DynamicConfiguration config) {
        config.addActiveDescriptor(ConfigurablyBadClassAnalyzer.class);
        
        config.bind(BuilderHelper.link(SelfAnalyzer.class.getName()).
                to(ClassAnalyzer.class.getName()).
                analyzeWith(NegativeClassAnalysisTest.SELF_ANALYZER).
                named(NegativeClassAnalysisTest.SELF_ANALYZER).
                build());
    }

}
