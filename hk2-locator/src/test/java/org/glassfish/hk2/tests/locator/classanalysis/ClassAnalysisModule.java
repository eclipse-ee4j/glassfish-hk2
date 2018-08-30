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

package org.glassfish.hk2.tests.locator.classanalysis;

import javax.inject.Singleton;

import org.glassfish.hk2.api.ClassAnalyzer;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.tests.locator.utilities.TestModule;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

/**
 * @author jwells
 *
 */
public class ClassAnalysisModule implements TestModule {

    /* (non-Javadoc)
     * @see org.glassfish.hk2.tests.locator.utilities.TestModule#configure(org.glassfish.hk2.api.DynamicConfiguration)
     */
    @Override
    public void configure(DynamicConfiguration config) {
        config.addActiveDescriptor(JaxRsClassAnalyzer.class);
        
        config.bind(BuilderHelper.link(DoubleFactory.class).
                to(Double.class.getName()).
                in(PerLookup.class.getName()).
                buildFactory(Singleton.class.getName()));
        
        config.bind(BuilderHelper.link(DoubleClassAnalyzer.class.getName()).
                to(ClassAnalyzer.class.getName()).
                in(Singleton.class.getName()).
                named(DoubleClassAnalyzer.DOUBLE_ANALYZER).
                build());
        
        config.bind(BuilderHelper.link(ServiceWithManyDoubles.class.getName()).
                to(ServiceWithManyDoubles.class.getName()).
                in(PerLookup.class.getName()).
                analyzeWith(DoubleClassAnalyzer.DOUBLE_ANALYZER).
                build());
        
        config.bind(BuilderHelper.link(JaxRsService.class.getName()).
                analyzeWith(JaxRsClassAnalyzer.PREFER_LARGEST_CONSTRUCTOR).
                build());
        
        config.bind(BuilderHelper.link(SimpleService1.class.getName()).
                build());
        config.bind(BuilderHelper.link(SimpleService2.class.getName()).
                build());
        
        config.bind(BuilderHelper.link(AlternateDefaultAnalyzer.class.getName()).
                to(ClassAnalyzer.class.getName()).
                in(Singleton.class.getName()).
                named(ClassAnalysisTest.ALTERNATE_DEFAULT_ANALYZER).
                analyzeWith(ClassAnalyzer.DEFAULT_IMPLEMENTATION_NAME).
                build());
    }

}
