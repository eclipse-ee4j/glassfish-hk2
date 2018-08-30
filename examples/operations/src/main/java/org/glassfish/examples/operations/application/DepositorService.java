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

import org.glassfish.examples.operations.scopes.DepositScope;

/**
 * This simple service just keeps a record of deposits made to the accounts in a single bank.
 * There is a different instance of this service per bank name. 
 * 
 * @author jwells
 *
 */
@DepositScope
public class DepositorService {
    private Map<Long, Integer> accounts = new HashMap<Long, Integer>();
    
    public void depositFunds(long account, int funds) {
        Integer balance = accounts.get(account);
        if (balance == null) {
            accounts.put(account, funds);
        }
        else {
            int original = balance;
            original += funds;
            accounts.put(account, original);
        }
    }
    
    public int getBalance(long account) {
        Integer balance = accounts.get(account);
        if (balance == null) return 0;
        return balance;
        
    }

}
