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

package org.jvnet.hk2.guice.bridge.test.bidirectional;

import org.glassfish.hk2.api.ServiceLocator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;
import org.jvnet.hk2.guice.bridge.api.HK2IntoGuiceBridge;
import org.jvnet.hk2.guice.bridge.test.utilities.Utilities;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * @author jwells
 *
 */
public class BiDirectionalBridgeTest {
    private final static ServiceLocator testLocator = Utilities.createLocator("BiDirectionalBridgeTest", new BiDirectionalBridgeModule());
    
    private Injector injector;
    
    private static Injector createBiDirectionalGuiceBridge(ServiceLocator serviceLocator,
            Module... applicationModules) {
        Module allModules[] = new Module[applicationModules.length + 1];
        
        allModules[0] = new HK2IntoGuiceBridge(serviceLocator);
        for (int lcv = 0; lcv < applicationModules.length; lcv++) {
            allModules[lcv + 1] = applicationModules[lcv];
        }
        
        Injector injector = Guice.createInjector(allModules);
        
        GuiceIntoHK2Bridge g2h = serviceLocator.getService(GuiceIntoHK2Bridge.class);
        g2h.bridgeGuiceInjector(injector);
        
        return injector;
    }
    
    @Before
    public void before() {
        // Setup the bidirection bridge
        injector = createBiDirectionalGuiceBridge(testLocator,
                new AbstractModule() {

                    @Override
                    protected void configure() {
                        bind(GuiceService1_1.class);
                        bind(GuiceService1_3.class);
                    }
            
        });
    }
    
    /**
     * In this test we get a guice service
     * that injects an hk2 service that
     * injects a guice service that injects
     * an hk2 service
     */
    @Test
    public void testGuiceHK2GuiceHK2() {
        GuiceService1_3 guice3 = injector.getInstance(GuiceService1_3.class);
        Assert.assertNotNull(guice3);
        
        guice3.check();
    }

}
