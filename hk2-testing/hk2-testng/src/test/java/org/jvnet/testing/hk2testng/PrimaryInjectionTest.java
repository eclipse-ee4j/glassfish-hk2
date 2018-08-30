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

package org.jvnet.testing.hk2testng;

import java.util.List;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.jvnet.testing.hk2testng.service.PrimaryService;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

/**
 *
 * @author saden
 */
@HK2(PrimaryInjectionTest.SAME_LOCATOR_NAME)
public class PrimaryInjectionTest {
    public final static String SAME_LOCATOR_NAME = "Same";

    @Inject
    PrimaryService primaryService;
    
    @Inject
    ServiceLocator serviceLocator;

    @Test
    public void assertPrimaryServiceInjecton() {
        assertThat(primaryService).isNotNull();
    }

    @Test
    public void assertSecondaryService() {
        assertThat(primaryService.getSecondaryService()).isNotNull();
    }
    
    /**
     * The exact same test is found in ConfigurationMethodInjectionTest.  The
     * intent of the test is to ensure that there are not multiple
     * descriptors for PrimaryService due to the fact that multiple
     * test classes are using the same service locator.  If the
     * service locator was being populated by both test classes then
     * one of these two identical tests would see multiple instances
     * of the primaryService in the IterableProvider
     */
    @Test
    public void assertNoMultiplePrimaries() {
        List<ActiveDescriptor<?>> allPrimaryServices = serviceLocator.getDescriptors(
                BuilderHelper.createContractFilter(PrimaryService.class.getName()));
        
        assertThat(allPrimaryServices.size()).isEqualTo(1);
    }
}
