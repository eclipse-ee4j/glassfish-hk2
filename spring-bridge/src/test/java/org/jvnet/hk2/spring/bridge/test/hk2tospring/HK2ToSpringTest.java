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

package org.jvnet.hk2.spring.bridge.test.hk2tospring;

import org.glassfish.hk2.api.ServiceLocator;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.jvnet.hk2.spring.bridge.api.SpringScopeImpl;
import org.jvnet.hk2.spring.bridge.test.utilities.LocatorAndContext;
import org.jvnet.hk2.spring.bridge.test.utilities.Utilities;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author jwells
 *
 */
public class HK2ToSpringTest {
    /**
     * Tests injecting a bean from hk2 into spring
     */
    @Test
    public void testHK2IntoSpring() {
        LocatorAndContext locatorAndContext = Utilities.createSpringTestLocator(
                "hk2-into-spring.xml",
                "HK2ToSpringTest",
                HK2Service.class);
        
        ServiceLocator locator = locatorAndContext.getServiceLocator();
        ApplicationContext context = locatorAndContext.getApplicationContext();
        
        SpringService sService = (SpringService) context.getBean("SpringService");
        Assert.assertNotNull(sService);
        
        HK2Service hk2Service = sService.getHK2Service();
        Assert.assertNotNull(hk2Service);
        
        HK2Service locatorHK2Service = locator.getService(HK2Service.class);
        Assert.assertNotNull(locatorHK2Service);
        
        Assert.assertEquals(hk2Service, locatorHK2Service);
    }

}
