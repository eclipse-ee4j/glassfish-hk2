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

package org.glassfish.examples.operations.application;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * This service is a singleton service but uses two Operation scoped services in order to transfer
 * funds from one bank account to another.  It is up to the controller to properly set the depositor
 * and withdrawler Operations properly before calling the doTransfer operation
 * 
 * @author jwells
 *
 */
@Singleton
public class TransferService {
    @Inject
    private DepositorService depositor;
    
    @Inject
    private WithdrawalService withdrawer;
    
    public int doTransfer(long depositAccount, long withdrawlAccount, int funds) {
        int recieved = withdrawer.withdrawlFunds(withdrawlAccount, funds);
        depositor.depositFunds(depositAccount, recieved);
        
        return recieved;
    }

}
