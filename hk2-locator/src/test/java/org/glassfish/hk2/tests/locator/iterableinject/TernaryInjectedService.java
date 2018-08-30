/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.locator.iterableinject;

import javax.inject.Inject;

/**
 * @author jwells
 *
 */
public class TernaryInjectedService {
    @Inject @TernaryQualifiers(Ternary.TRUE)
    private Iterable<TernaryServices> trues;
    
    @Inject @TernaryQualifiers(Ternary.FALSE)
    private Iterable<TernaryServices> falses;
    
    @Inject @TernaryQualifiers(Ternary.NEITHER)
    private Iterable<TernaryServices> neithers;
    
    @Inject
    private Iterable<TernaryServices> alls;
    
    private int getCount(Iterable<?> it) {
        int lcv = 0;
        for (@SuppressWarnings("unused") Object i : it) {
            lcv++;
        }
        return lcv;
    }
    
    public int getNumTrues() {
        return getCount(trues);
    }
    
    public int getNumFalses() {
        return getCount(falses);
    }
    
    public int getNumNeithers() {
        return getCount(neithers);
    }
    
    public int getNumAlls() {
        return getCount(alls);
    }

}
