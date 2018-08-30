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

package org.glassfish.examples.operations.application.internal;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.glassfish.examples.operations.application.BankingService;
import org.glassfish.examples.operations.application.DepositorService;
import org.glassfish.examples.operations.application.TransferService;
import org.glassfish.examples.operations.application.WithdrawalService;
import org.glassfish.examples.operations.scopes.DepositScope;
import org.glassfish.examples.operations.scopes.WithdrawalScope;
import org.glassfish.hk2.extras.operation.OperationHandle;
import org.glassfish.hk2.extras.operation.OperationManager;

/**
 * Implementation of the Banking Service that uses Operations
 * to switch between banks
 * 
 * @author jwells
 *
 */
@Singleton
public class BankingServiceImpl implements BankingService {
    @Inject
    private OperationManager manager;
    
    @Inject
    private TransferService transferAgent;
    
    @Inject
    private DepositorService depositorAgent;
    
    @Inject
    private WithdrawalService withdrawerAgent;
    
    private final Map<String, OperationHandle<DepositScope>> depositors = new HashMap<String, OperationHandle<DepositScope>>();
    private final Map<String, OperationHandle<WithdrawalScope>> withdrawers = new HashMap<String, OperationHandle<WithdrawalScope>>();
    
    private synchronized OperationHandle<DepositScope> getDepositBankHandle(String bank) {
        OperationHandle<DepositScope> depositor = depositors.get(bank);
        if (depositor == null) {
            // create and start it
            depositor = manager.createOperation(DepositScopeImpl.INSTANCE);
            depositors.put(bank, depositor);
        }
        
        return depositor;
    }
    
    private synchronized OperationHandle<WithdrawalScope> getWithdrawerBankHandle(String bank) {
        OperationHandle<WithdrawalScope> withdrawer = withdrawers.get(bank);
        if (withdrawer == null) {
            // create and start it
            withdrawer = manager.createOperation(WithdrawalScopeImpl.INSTANCE);
            withdrawers.put(bank, withdrawer);
        }
        
        return withdrawer;
    }

    /* (non-Javadoc)
     * @see org.glassfish.examples.operations.application.BankingService#transferFunds(java.lang.String, long, java.lang.String, long, int)
     */
    @Override
    public synchronized int transferFunds(String withdrawlBank, long withdrawlAccount,
            String depositorBank, long depositAccount, int funds) {
        OperationHandle<DepositScope> depositor = getDepositBankHandle(depositorBank);
        OperationHandle<WithdrawalScope> withdrawer = getWithdrawerBankHandle(withdrawlBank);
        
        // Set the context for the transfer
        depositor.resume();
        withdrawer.resume();
        
        // At this point the scopes are set properly, we can just call the service!
        try {
            return transferAgent.doTransfer(depositAccount, withdrawlAccount, funds);
        }
        finally {
            // Turn off the two scopes
            withdrawer.suspend();
            depositor.suspend();
        }
        
    }

    /* (non-Javadoc)
     * @see org.glassfish.examples.operations.application.BankingService#getDepositedBalance(java.lang.String, long)
     */
    @Override
    public int getDepositedBalance(String bank, long account) {
        OperationHandle<DepositScope> depositor = getDepositBankHandle(bank);
        
        // Set the context for the deposit balance check
        depositor.resume();
        
        try {
            return depositorAgent.getBalance(account);
        }
        finally {
            // Suspend the operation
            depositor.suspend();
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.examples.operations.application.BankingService#getWithdrawlBalance(java.lang.String, long)
     */
    @Override
    public int getWithdrawalBalance(String bank, long account) {
        OperationHandle<WithdrawalScope> withdrawer = getWithdrawerBankHandle(bank);
        
        // Set the context for the withdrawal balance check
        withdrawer.resume();
        
        try {
            return withdrawerAgent.getBalance(account);
        }
        finally {
            // suspend the operation
            withdrawer.suspend();
        }
    }

}
