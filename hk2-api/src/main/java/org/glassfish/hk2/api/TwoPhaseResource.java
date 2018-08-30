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

package org.glassfish.hk2.api;

/**
 * Implementations of this interface can be added to a {@link DynamicConfiguration}
 * in order to atomically participate in the changes being made to the
 * {@link ServiceLocator}.  No changes to the ServiceLocator can be made from
 * any method of this interface, otherwise the {@link ServiceLocator} can be
 * left in an inconsistent state
 * 
 * @author jwells
 *
 */
public interface TwoPhaseResource {
    /**
     * This method is called prior to any changes being made to the {@link ServiceLocator}
     * but after the IdempotentFilters are called.  If this method throws any exception the
     * entire transaction will not go forward and the thrown exception will be thrown back
     * to the caller.  If this method completes successfully then either the commit or rollback
     * methods will be called eventually once the final outcome of the transaction has been
     * established.  This method is called with the write lock of the ServiceLocator held
     * 
     * @param dynamicConfiguration Information about the dynamic configuration for which this resource
     * was registered
     * @throws MultiException If for some reason the transaction can not go through the expected
     * exception is a MultiException with enclosed exceptions detailing the reasons why the
     * transaction cannot complete.  No subsequent TwoPhaseResource listeners will be invoked
     * once any TwoPhaseResource throws any exception
     */
    public void prepareDynamicConfiguration(TwoPhaseTransactionData dynamicConfiguration) throws MultiException;
    
    /**
     * Once all TwoPhaseResource prepare methods have completed successfully the activate method
     * will be called on all registered TwoPhaseResource implementations.  Any exception from
     * this method will be ignored (though they will be logged if debug logging is turned on).
     * This method is called after the write lock has been released and all other listeners
     * have been called
     * 
     * @param dynamicConfiguration Information about the dynamic configuration for which this resource
     * was registered
     */
    public void activateDynamicConfiguration(TwoPhaseTransactionData dynamicConfiguration);
    
    /**
     * If any TwoPhaseResource fails then all TwoPhaseResources that successfully completed their
     * prepare method will get this method invoked.  Any exceptions from this method will be ignored
     * (though they will be logged if debugging is turned on). This method is called with the write
     * lock of the ServiceLocator held
     * 
     * @param dynamicConfiguration Information about the dynamic configuration for which this resource
     * was registered
     */
    public void rollbackDynamicConfiguration(TwoPhaseTransactionData dynamicConfiguration);
}
