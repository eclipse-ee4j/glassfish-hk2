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

import org.glassfish.hk2.api.ServiceLocator;
import org.junit.Assert;
import org.jvnet.hk2.annotations.Service;

/**
 * This service has no valid hk2 constructor and will
 * also be automatically analyzed
 * 
 * @author jwells
 *
 */
@Service(analyzer=JaxRsClassAnalyzer.PREFER_LARGEST_CONSTRUCTOR)
public class ServiceWithNoValidHK2Constructor {
    private final ServiceLocator locator;
    
    /**
     * This constructor is not a valid hk2 constructor because it
     * is not annotated
     * 
     * @param locator Will contain the proper locator
     */
    private ServiceWithNoValidHK2Constructor(ServiceLocator locator) {
        this.locator = locator;
        
    }
    
    public void check() {
        Assert.assertNotNull(locator);
    }

}
