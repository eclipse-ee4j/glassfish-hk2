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

package org.jvnet.hk2.guice.bridge.test;

import org.junit.Assert;
import org.jvnet.hk2.guice.bridge.api.HK2Inject;

/**
 * This guy is injected with an HK2 service
 * 
 * @author jwells
 *
 */
public class GuiceService2 {
    @HK2Inject
    private HK2Service2 hk2Service;
    
    public void verifyHK2Service() {
        Assert.assertFalse(hk2Service.wasCalled());
        
        hk2Service.callMe();
        
        Assert.assertTrue(hk2Service.wasCalled());
    }

}
