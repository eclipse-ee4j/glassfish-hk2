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

package org.glassfish.hk2.runlevel;

/**
 * This exception is thrown by proceedTo if there is currently a
 * job being run when proceedTo is called
 * 
 * @author jwells
 *
 */
public class CurrentlyRunningException extends RuntimeException {
    /**
     * For serialization
     */
    private static final long serialVersionUID = -1712057070339111837L;
    
    private transient RunLevelFuture currentJob;
    
    /**
     * Basic no-arg constructor
     */
    public CurrentlyRunningException() {
    }
    
    /**
     * Constructor with job that is in progress
     * 
     * @param runLevelFuture The job currently in progress
     */
    public CurrentlyRunningException(RunLevelFuture runLevelFuture) {
        currentJob = runLevelFuture;
    }
    
    /**
     * Gets the job current in progress
     * @return The job currently in progress
     */
    public RunLevelFuture getCurrentJob() {
        return currentJob;
    }

}
