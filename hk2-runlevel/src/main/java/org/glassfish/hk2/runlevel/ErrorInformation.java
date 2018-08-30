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

import org.glassfish.hk2.api.Descriptor;

/**
 * @author jwells
 *
 */
public interface ErrorInformation {
    /**
     * The set of actions that the system can perform
     * when an error is detected
     * 
     * @author jwells
     */
    public enum ErrorAction {
        /**
         * Tells the RunLevelController to halt progress
         * in the level and proceed to the next lowest
         * level and stop the proceeding at that level.
         * This is the default action when an error is
         * encountered while the system is proceeding
         * upward.  The error (or errors) will be thrown
         * by the {@link RunLevelFuture}
         */
        GO_TO_NEXT_LOWER_LEVEL_AND_STOP,
        
        /**
         * Tells the RunLevelController to disregard
         * the error and continue its progress as if
         * the error never happened.  This is the default
         * action when an error is encountered while
         * the system is proceeding downward.  The error
         * (or errors) will NOT be thrown by the
         * {@link RunLevelFuture}
         */
        IGNORE
    }
    
    /**
     * Returns the throwable that caused the error
     * @return The non-null throwable that caused
     * the error to occur
     */
    public Throwable getError();
    
    /**
     * Returns the action the system will take
     * 
     * @return The action the system will take
     * once the onError method has returned
     */
    public ErrorAction getAction();
    
    /**
     * Sets the action the system should take
     * 
     * @param action The action the system will take
     * once the onError method has returned
     */
    public void setAction(ErrorAction action);
    
    /**
     * Returns the descriptor associated with this failure,
     * or null if the descriptor could not be determined
     * 
     * @return The failed descriptor, or null if the
     * descriptor could not be determined
     */
    public Descriptor getFailedDescriptor();

}
