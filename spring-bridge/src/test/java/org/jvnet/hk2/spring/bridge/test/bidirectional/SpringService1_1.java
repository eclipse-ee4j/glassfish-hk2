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

package org.jvnet.hk2.spring.bridge.test.bidirectional;

/**
 * @author jwells
 *
 */
public class SpringService1_1 {
    private HK2Service1_0 oneZero;
    
    /**
     * Called by Spring
     * 
     * @param oneZero The hk2 service
     */
    public void setHK2Service1_0(HK2Service1_0 oneZero) {
        this.oneZero = oneZero;
    }
    
    /**
     * Used by the test code to ensure everything is set properly
     * @return Returns the service
     */
    public HK2Service1_0 check() {
        return oneZero;
    }

}
