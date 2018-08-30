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

package org.glassfish.hk2.utilities.cache;

/**
 * This exception should be thrown from the {@link Computable#compute(Object)} method
 * if the returned computation should NOT be kept in the cache.  The actual value
 * returned by the {@link #getComputation()} should be returned to the caller, but
 * that value should be considered volatile and should NOT be kept in the cache
 * 
 * @author jwells
 *
 */
public class ComputationErrorException extends RuntimeException {
    private static final long serialVersionUID = 1186268368624844657L;
    public Object computation;
    
    public ComputationErrorException() {
        super();
    }
    
    public ComputationErrorException(Object computation) {
        this.computation = computation;
    }
    
    public Object getComputation() {
        return computation;
    }
}
