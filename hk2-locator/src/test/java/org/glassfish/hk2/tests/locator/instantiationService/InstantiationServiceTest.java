/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.locator.instantiationService;

import java.lang.reflect.Field;

import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class InstantiationServiceTest {
    /**
     * Test that nested Factories injecting InstantiationService get the proper fields
     */
    @Test // @org.junit.Ignore
    public void testNestedFactoriesWithInstantiationService() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(RootService.class,
                SonServiceFactory.class,
                GrandsonServiceFactory.class,
                GrandDaughterServiceFactory.class);
        
        RootService rootService = locator.getService(RootService.class);
        SonService sonService = rootService.getSonService();
        
        Injectee sonInjectee = sonService.getMyInjectee();
        Field rootField = (Field) sonInjectee.getParent();
        Assert.assertEquals(RootService.class, rootField.getDeclaringClass());
        Assert.assertEquals(SonService.class, sonInjectee.getRequiredType());
        
        GrandsonService grandsonService = sonService.getGrandson();
        
        Injectee grandsonInjectee = grandsonService.getMyInjectee();
        Field sonServiceFactoryField = (Field) grandsonInjectee.getParent();
        Assert.assertEquals(SonServiceFactory.class, sonServiceFactoryField.getDeclaringClass());
        Assert.assertEquals(GrandsonService.class, grandsonInjectee.getRequiredType());
        
        GrandDaughterService grandDaughterService = sonService.getGrandDaughter();
        
        Injectee grandDaughterInjectee = grandDaughterService.getMyInjectee();
        Field grandDaughterServiceFactoryField = (Field) grandDaughterInjectee.getParent();
        Assert.assertEquals(SonServiceFactory.class, grandDaughterServiceFactoryField.getDeclaringClass());
        Assert.assertEquals(GrandDaughterService.class, grandDaughterInjectee.getRequiredType());
        
    }

}
