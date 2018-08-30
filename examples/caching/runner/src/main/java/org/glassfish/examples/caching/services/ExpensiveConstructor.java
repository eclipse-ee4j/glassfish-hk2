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

import javax.inject.Inject;

import org.glassfish.examples.caching.hk2.Cache;
import org.glassfish.hk2.api.PerLookup;
import org.jvnet.hk2.annotations.Service;

/**
 * This is a service that has a very expensive constructor.  Luckily
 * this service can be cached with the constructor input
 * parameter as a key, and so we will use constructor interception with
 * it
 * 
 * @author jwells
 *
 */
@Service @PerLookup
public class ExpensiveConstructor {
    private static int numTimesConstructed;
    private final int multiplier;
    
    /**
     * This is the extremely expensive constructor
     * 
     * @param multiplier The number to multiply by
     */
    @Inject @Cache
    public ExpensiveConstructor(int multiplier) {
        // Very expensive operation
        this.multiplier = multiplier * 2;
        numTimesConstructed++;
    }
    
    /**
     * This method ensures that we can get at the
     * results of the expensive operation performed
     * in the constructor
     * 
     * @return The result of the expensive operation
     * done in the constructor
     */
    public int getComputation() {
        return multiplier;
    }
    
    /**
     * Clears the number of times this was constructed
     */
    public static void clear() {
        numTimesConstructed = 0;
    }
    
    /**
     * Gets the number of times this class has been
     * constructed since the last call to {@link #clear()}
     * 
     * @return The number of times this class has been constructed
     */
    public static int getNumTimesConstructed() {
        return numTimesConstructed;
    }
}
