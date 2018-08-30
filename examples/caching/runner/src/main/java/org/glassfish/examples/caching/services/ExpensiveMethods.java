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

package org.glassfish.examples.caching.services;

import org.glassfish.examples.caching.hk2.Cache;
import org.jvnet.hk2.annotations.Service;

/**
 * This class has very expensive method calls
 * whose answers can be cached.  We will cache
 * them by using interception and using the
 * {@link Cache} annotation
 * 
 * @author jwells
 *
 */
@Service
public class ExpensiveMethods {
    private int timesCalled = 0;
    
    /**
     * This method is extremely expensive.  Extremely.
     * Adding one takes a lot 
     * @param input An input parameter
     * @return The results of an expensive calculation
     */
    @Cache
    public int veryExpensiveCalculation(int input) {
        timesCalled++;
        return input + 1;
    }
    
    /**
     * Returns the number of times the expensive method
     * was called
     * 
     * @return The number of times the expensive method was called
     */
    public int getNumTimesCalled() {
        return timesCalled;
    }
    
    /**
     * Sets the number of times the expensive method was called to zero
     */
    public void clear() {
        timesCalled = 0;
    }
    
}
