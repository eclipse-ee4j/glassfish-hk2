/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.locator.servicelocatorutilities;

/**
 * This service cannot be directly created by hk2
 * 
 * @author jwells
 *
 */
public class UncreateableContractOneImpl implements ContractOne {
    public UncreateableContractOneImpl() {
        throw new AssertionError("HK2 should NOT try to create this");
    }
    
    /**
     * This constructor ensures hk2 cannot create this
     * service itself
     * 
     * @param i Just a parameter
     */
    public UncreateableContractOneImpl(int i) {
    }

}
