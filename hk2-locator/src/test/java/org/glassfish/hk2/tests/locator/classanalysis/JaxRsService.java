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

import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.ServiceLocator;
import org.junit.Assert;

/**
 * @author jwells
 *
 */
public class JaxRsService {
    private boolean calledProperConstructor = false;
    
    public JaxRsService() {
        
    }
    
    public JaxRsService(Double d) {
        
    }
    
    public JaxRsService(Double d, ServiceLocator sl) {
        
    }
    
    /**
     * One of these two constructors will be called (which one is random)
     * @param d
     * @param sl
     * @param dcs
     * @param sl1
     */
    public JaxRsService(Double d, ServiceLocator sl, DynamicConfigurationService dcs, ServiceLocator sl1) {
        calledProperConstructor = true;
        
    }
    
    /**
     * One of these two constructors will be called (which one is random)
     * @param d
     * @param sl
     * @param dcs
     * @param sl1
     */
    public JaxRsService(DynamicConfigurationService dcs, ServiceLocator sl, ServiceLocator sl1, Double d) {
        calledProperConstructor = true;
        
    }
    
    public JaxRsService(String hello) {
        
    }
    
    public void checkProperConstructor() {
        Assert.assertTrue(calledProperConstructor);
    }

}
