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

package org.glassfish.examples.operations.tests;

import org.glassfish.examples.operations.application.BankingService;
import org.glassfish.examples.operations.application.DepositorService;
import org.glassfish.examples.operations.application.TransferService;
import org.glassfish.examples.operations.application.WithdrawalService;
import org.glassfish.examples.operations.application.internal.BankingServiceImpl;
import org.glassfish.examples.operations.scopes.DepositScopeContext;
import org.glassfish.examples.operations.scopes.WithdrawalScopeContext;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.extras.ExtrasUtilities;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * These test cases demonstrate the use of Operation scopes
 * 
 * @author jwells
 *
 */
public class OperationsTest {
    public static final ServiceLocatorFactory FACTORY = ServiceLocatorFactory.getInstance();
    
    private final static String CHASE_BANK = "Chase";
    private final static String BOA_BANK = "Bank of America";
    private final static String SJFCU_BANK = "South Jersey Federal Credit Union";
    
    private final static long ALICE_ACCOUNT = 1000;
    private final static long BOB_ACCOUNT = 2000;
    private final static long CAROL_ACCOUNT = 3000;
    
    private ServiceLocator getServiceLocator() {
        ServiceLocator retVal = FACTORY.create(null);
        
        ExtrasUtilities.enableOperations(retVal);
        ServiceLocatorUtilities.addClasses(retVal,
                DepositScopeContext.class,
                WithdrawalScopeContext.class,
                BankingServiceImpl.class,
                DepositorService.class,
                WithdrawalService.class,
                TransferService.class);
        
        return retVal;
    }
    
    /**
     * Tests that we can transfer funds between ALICE and BOB
     */
    @Test
    public void testTransferBetweenBanks() {
        ServiceLocator locator = getServiceLocator();
        
        BankingService bankingService = locator.getService(BankingService.class);
        
        // First, initialize the accounts of ALICE, BOB and CAROL with 100 funds
        int aliceBalance = bankingService.getWithdrawalBalance(CHASE_BANK, ALICE_ACCOUNT);
        int bobBalance = bankingService.getWithdrawalBalance(BOA_BANK, BOB_ACCOUNT);
        int carolBalance = bankingService.getWithdrawalBalance(SJFCU_BANK, CAROL_ACCOUNT);
        
        Assert.assertEquals(100, aliceBalance);
        Assert.assertEquals(100, bobBalance);
        Assert.assertEquals(100, carolBalance);
        
        // OK, lets transfer that 100 from alice to bob
        int amtTransferred = bankingService.transferFunds(CHASE_BANK, ALICE_ACCOUNT, BOA_BANK, BOB_ACCOUNT, 100);
        
        Assert.assertEquals(100, amtTransferred);
        
        // And lets check the withdrawl funds again, alice should have zero, bob should have 100, carol should still have 100
        aliceBalance = bankingService.getWithdrawalBalance(CHASE_BANK, ALICE_ACCOUNT);
        bobBalance = bankingService.getWithdrawalBalance(BOA_BANK, BOB_ACCOUNT);
        carolBalance = bankingService.getWithdrawalBalance(SJFCU_BANK, CAROL_ACCOUNT);
        
        Assert.assertEquals(0, aliceBalance);
        Assert.assertEquals(100, bobBalance);
        Assert.assertEquals(100, carolBalance);
        
        // But now bob should have 100 in his deposit account
        int toAlice = bankingService.getDepositedBalance(CHASE_BANK, ALICE_ACCOUNT);
        int toBob = bankingService.getDepositedBalance(BOA_BANK, BOB_ACCOUNT);
        int toCarol = bankingService.getDepositedBalance(SJFCU_BANK, CAROL_ACCOUNT);
        
        Assert.assertEquals(0, toAlice);
        Assert.assertEquals(100, toBob);
        Assert.assertEquals(0, toCarol);
        
        // Now lets have Carol transfer to Alice
        amtTransferred = bankingService.transferFunds(SJFCU_BANK, CAROL_ACCOUNT, CHASE_BANK, ALICE_ACCOUNT, 100);
        
        // Now Alice and Carol should have nothing left, while Bob still has his original 100
        aliceBalance = bankingService.getWithdrawalBalance(CHASE_BANK, ALICE_ACCOUNT);
        bobBalance = bankingService.getWithdrawalBalance(BOA_BANK, BOB_ACCOUNT);
        carolBalance = bankingService.getWithdrawalBalance(SJFCU_BANK, CAROL_ACCOUNT);
        
        Assert.assertEquals(0, aliceBalance);
        Assert.assertEquals(100, bobBalance);
        Assert.assertEquals(0, carolBalance);
        
        // At this point Alice and Carol should each have 100 in there deposit account
        toAlice = bankingService.getDepositedBalance(CHASE_BANK, ALICE_ACCOUNT);
        toBob = bankingService.getDepositedBalance(BOA_BANK, BOB_ACCOUNT);
        toCarol = bankingService.getDepositedBalance(SJFCU_BANK, CAROL_ACCOUNT);
        
        Assert.assertEquals(100, toAlice);
        Assert.assertEquals(100, toBob);
        Assert.assertEquals(0, toCarol);
        
        // Yay, the Operations worked properly!
    }
}
