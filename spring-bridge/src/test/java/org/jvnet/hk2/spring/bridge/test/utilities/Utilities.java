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

package org.jvnet.hk2.spring.bridge.test.utilities;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.jvnet.hk2.spring.bridge.api.SpringBridge;
import org.jvnet.hk2.spring.bridge.api.SpringIntoHK2Bridge;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author jwells
 *
 */
public class Utilities {
    /**
     * Creates a ServiceLocator with the SpringBridge initialized
     * 
     * @param xmlFileName The name of the spring configuration file
     * @param uniqueName The name that the service locator should have
     * @param classes Classes to add to the locator
     * @return An anonymous service locator with the given classes as services
     */
    public static LocatorAndContext createSpringTestLocator(String xmlFileName, String uniqueName, Class<?>...classes) {
        ServiceLocator locator = ServiceLocatorFactory.getInstance().create(uniqueName);
        
        SpringBridge.getSpringBridge().initializeSpringBridge(locator);
        
        DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
        DynamicConfiguration config = dcs.createDynamicConfiguration();
        
        for (Class<?> clazz : classes) {
            config.addActiveDescriptor(clazz);
        }
        
        config.commit();
        
        // Now setup the bridge
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext(xmlFileName);
        SpringIntoHK2Bridge bridge = locator.getService(SpringIntoHK2Bridge.class);
        bridge.bridgeSpringBeanFactory(context);
        
        return new LocatorAndContext(locator, context);
    }

}
