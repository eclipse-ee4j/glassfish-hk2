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

import java.util.HashMap;
import java.util.Map;

import org.glassfish.examples.operations.scopes.WithdrawalScope;

/**
 * This simple service just keeps a record of withdrawls made to the accounts in a single bank.
 * There is a different instance of this service per bank name.
 * 
 * This is a nice service, because if you don't have an account already it'll
 * create one for you and put 100 funds in it!
 * 
 * @author jwells
 *
 */
@WithdrawalScope
public class WithdrawalService {
    private Map<Long, Integer> accounts = new HashMap<Long, Integer>();
    
    public int withdrawlFunds(long account, int funds) {
        Integer balance = accounts.get(account);
        if (balance == null) {
            // How nice, 100 for me!
            balance = new Integer(100);
        }
        
        int current = balance;
        if (funds > current) {
            funds = current;
        }
        
        current = current - funds;
        accounts.put(account, current);
        
        return funds;
    }
    
    public int getBalance(long account) {
        Integer balance = accounts.get(account);
        if (balance == null) {
            accounts.put(account, 100);
        }
        
        return accounts.get(account);
    }
    
    @Override
    public String toString() {
        return "WithdrawlService(" + System.identityHashCode(this) + ")";
    }

}
