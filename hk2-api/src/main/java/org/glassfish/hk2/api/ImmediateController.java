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

package org.glassfish.hk2.api;

import java.util.concurrent.Executor;

import org.jvnet.hk2.annotations.Contract;

/**
 * This service is advertised when the Immediate service is put into the
 * registry.  The immediate service by default starts in the SUSPENDED
 * state so that the Executor and other parameters can be set prior to
 * the first Immediate service being started
 * 
 * @author jwells
 *
 */
@Contract
public interface ImmediateController {
    /**
     * Returns the executor that is currently in use by the Immediate subsystem
     * 
     * @return The current executor in use by the controller.  Will not return null
     * since a default executor is used and returned by this call even if setExecutor
     * is called with null
     */
    public Executor getExecutor();
    
    /**
     * Sets the executor to be used by the Immediate subsystem.  If set to
     * null a default executor will be used.  This may only be called when
     * the Immediate service is suspended
     * 
     * @param executor The executor to be used when scheduling work.  If null
     * a default executor implementation will be used
     * @throws IllegalStateException if this is called when the Immediate service
     * is not in suspended state
     */
    public void setExecutor(Executor executor) throws IllegalStateException;
    
    /**
     * Returns the time in milliseconds a thread will wait for new Immediate
     * services before dying
     * 
     * @return The time in milliseconds a thread will wait for new
     * Immediate service before dying
     */
    public long getThreadInactivityTimeout();
    
    /**
     * Sets the time in milliseconds a thread will wait for new Immediate
     * services before dying
     * 
     * @param timeInMillis The time in milliseconds a thread will wait for new
     * Immediate service before dying
     * @throws IllegalArgumentException if timeInMillis is less than zero
     */
    public void setThreadInactivityTimeout(long timeInMillis) throws IllegalArgumentException;
    
    /**
     * Returns the state the system is currently running under
     * 
     * @return The current state of the ImmediateService
     */
    public ImmediateServiceState getImmediateState();
    
    /**
     * Sets the state the system is currently running under
     * 
     * @param state The new state of the ImmediateService
     */
    public void setImmediateState(ImmediateServiceState state);
    
    public enum ImmediateServiceState {
        /**
         * The system will not create new Immediate services when in SUSPENDED state.
         * Suspended state does not imply that Immediate services will be shutdown, but
         * rather that any new Immediate services that come along will not be started
         */
        SUSPENDED,
        
        /**
         * The system will create new Immediate services as soon as they are found
         */
        RUNNING
    }

}
