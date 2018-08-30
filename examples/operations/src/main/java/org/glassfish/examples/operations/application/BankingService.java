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

import org.jvnet.hk2.annotations.Contract;

/**
 * This service transfers funds between accounts using the {@link TransferService} by
 * setting up the proper Operation for the Depositorer and Transferer.
 * 
 * @author jwells
 *
 */
@Contract
public interface BankingService {
    /**
     * Transfers funds between the withdrawal account and the deposit account.  If there
     * is not enough funds in the withdrawal account it may transfer less than the requested
     * amount
     * 
     * @param withdrawlBank The bank from which the withdrawal is being made
     * @param withdrawlAccount The account number to withdrawal funds from
     * @param depositorBank The bank to which the deposit is being done
     * @param depositAccount The account number to deposit funds to
     * @param funds The number of funds to transfer
     * @return The actual funds transferred
     */
    public int transferFunds(String withdrawlBank, long withdrawlAccount, String depositorBank, long depositAccount, int funds);
    
    /**
     * Tells how much money has been deposited in the given account in the
     * given bank
     * 
     * @param bank the name of the bank of the account to check
     * @param account the account number to get the deposited amount from
     * @return the amount of money deposited in this account
     */
    public int getDepositedBalance(String bank, long account);
    
    /**
     * Tells how much money that is available for withdrawal from the
     * given account number at the given bank.  Note that if the
     * account number has never been seen before the amount given
     * at the start is 100 funds
     * 
     * @param bank the name of the bank of the account to check
     * @param account the account number to check
     * @return the amount of money deposited in this account
     */
    public int getWithdrawalBalance(String bank, long account);

}
