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

import java.util.concurrent.Future;

/**
 * This is the Future object that will be returned by the
 * RunLevelController and it contains extra information about
 * the job being done
 * 
 * @author jwells
 *
 */
public interface RunLevelFuture extends Future<Object> {
    /**
     * This gets the level that this future job is attempting
     * to get to
     * 
     * @return The level that this future job is attempting
     * to go to
     */
    public int getProposedLevel();
    
    /**
     * Returns true if this job represents the system going from
     * a lower level to a higher level.  This method and isDown
     * can both be false (for the case that proceedTo asked to go
     * to the level it is already at) but they cannot both be true
     * 
     * @return true if this job was proceeding from a lower level
     * to a higher level
     */
    public boolean isUp();
    
    /**
     * Returns true if this job represents the system going from
     * a higher level to a lower level.  This method and isUp
     * can both be false (for the case that proceedTo asked to go
     * to the level it is already at) but they cannot both be true
     * 
     * @return true if this job was proceeding from a higher level
     * to a lower level
     */
    public boolean isDown();
    
    /**
     * The cancel method attempts to cancel the current running
     * job (if the job is not already completed or already cancelled).
     * The meaning of cancel is different depending on whether or
     * not the system was going up to a level or coming down to
     * a level.
     * <p>
     * If the system was going up to a level then calling cancel
     * will cause the system to stop going up, and instead proceed
     * back down to the last completed level.  For example, suppose
     * there were three services at level ten and the system was
     * going up to level ten.  As the system was proceeding up to
     * level ten the first of the three services had already been
     * started and the second service was in progress and the third
     * service had not been started.  The system will wait for the second
     * service to complete coming up and then will shut it down along
     * with the first service.  Since the last completed level was nine,
     * the system will remain at level nine and this job will be complete.
     * <p>
     * If the system was going down to a level then calling cancel
     * will cause the system to continue going down, but it will stop
     * going down at the next level.  For example, suppose there were
     * three services at level ten and the current proposed level is
     * five.  Suppose one of those three services had already been shutdown
     * and one was in the process of being shutdown and the other had
     * not yet been shutdown when the cancel arrives.  The system will
     * continue to shutdown the one in progress and then will shutdown
     * the remaining service at level ten to reach level nine.  However,
     * the job will no longer attempt to go down to level five, but will
     * instead be finished at level nine.
     * <p>
     * There is a cancel timeout value that is set.  This is the amount
     * of time the system will wait for services to complete after
     * cancel has been called.  Any services still running after this
     * timeout will be orphaned (they will not be shutdown if they
     * do eventually complete).  Further, if an attempt is made to
     * start the same service that is still running on another thread
     * that request will fail.
     * 
     * @param mayInterruptIfRunning is currently ignored
     */
    @Override
    public boolean cancel(boolean mayInterruptIfRunning);
    
    
}
