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

import java.util.Set;

import javax.inject.Singleton;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.tests.locator.utilities.TestModule;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.DescriptorImpl;

/**
 * @author jwells
 *
 */
public class LocatorModule implements TestModule {

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Module#configure(org.glassfish.hk2.api.Configuration)
     */
    @Override
    public void configure(DynamicConfiguration configurator) {
        configurator.bind(BuilderHelper.link(BootCommand.class).
                to(AdminCommand.class).
                named("BootCommand").
                build());
        
        configurator.bind(BuilderHelper.link(GetStatisticsCommand.class).
                to(AdminCommand.class).
                named("GetStatisticsCommand").
                build());
        
        configurator.bind(BuilderHelper.link(ShutdownCommand.class).
                to(AdminCommand.class).
                named("ShutdownCommand").
                build());
        
        // This is part of the test, to use a non-BuilderHelper descriptor
        ForeignDescriptor fd = new ForeignDescriptor();
        fd.setImplementation(FrenchService.class.getName());
        
        Set<String> contracts = fd.getAdvertisedContracts();
        contracts.add(FrenchService.class.getName());
        
        configurator.bind(fd);
        
        DescriptorImpl latin = new DescriptorImpl();
        latin.setImplementation(LatinService.class.getName());
        latin.addAdvertisedContract(LatinService.class.getName());
        latin.addQualifier(Dead.class.getName());
        latin.setScope(Singleton.class.getName());
        
        configurator.bind(latin);
        
        DescriptorImpl thracian = new DescriptorImpl();
        thracian.setImplementation(ThracianService.class.getName());
        thracian.addAdvertisedContract(ThracianService.class.getName());
        thracian.addQualifier(Dead.class.getName());
        
        configurator.bind(thracian);
        
        // These are for the TypeLiteral tests
        configurator.bind(BuilderHelper.link(COBOL.class).
                                        to(ComputerLanguage.class).
                                        in(Singleton.class.getName()).
                                        build());
        
        configurator.bind(BuilderHelper.link(Fortran.class).
                to(ComputerLanguage.class).
                in(Singleton.class.getName()).
                build());
        
        configurator.bind(BuilderHelper.link(Java.class).
                to(ComputerLanguage.class).
                named(LocatorTest.JAVA_NAME).
                in(Singleton.class.getName()).
                build());
        
        // These are for a class with no scope annotation, and hence
        // should be allowed to take on any scope
        configurator.bind(BuilderHelper.link(NoScopeService.class.getName()).
                in(Singleton.class.getName()).build());
        
        configurator.bind(BuilderHelper.link(NoScopeService.class.getName()).
                in(PerLookup.class.getName()).build());
        
        // For the performance check of the cache
        configurator.bind(BuilderHelper.link(PerformanceService.class).
                in(Singleton.class.getName()).build());
        
        
    }

}
