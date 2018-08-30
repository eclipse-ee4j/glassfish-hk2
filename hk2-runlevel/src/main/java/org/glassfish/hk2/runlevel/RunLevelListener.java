/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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


import org.jvnet.hk2.annotations.Contract;


/**
 * Instances of classes implementing this contract can be registered with HK2
 * to be informed of events of RunLevelControllers.
 * <p>
 * Lengthy operations should not be performed in the listener since
 * that may impact the performance of the RunLevelController calling the
 * listener.
 *
 * @author jtrent, tbeerbower
 */
@Contract
public interface RunLevelListener {
    /**
     * Called when the RunLevelController advances to the next level
     * <p>
     * Neither {@link RunLevelController#proceedTo(int)} nor
     * {@link RunLevelController#proceedToAsync(int)} may be called from this method.  However,
     * {@link ChangeableRunLevelFuture#changeProposedLevel(int)} may be called
     * <p>
     * Any exception thrown from this method is ignored
     * <p>
     *
     * @param currentJob the job currently running
     * @param levelAchieved the level just achieved by the currentJob.  Note
     * that if the currentJob is going up then the levelAchieved will
     * be the level for which all the services in that level were just started.
     * When going down the levelAchieved will be the level for which
     * all the services ABOVE that level have been shutdown. In all cases the
     * levelAchieved represents the current level of the system.
     */
    void onProgress(ChangeableRunLevelFuture currentJob, int levelAchieved);
    
    /**
     * Called when an RunLevelController implementation's proceedTo() operation
     * has been canceled for some reason.
     * <p>
     * Neither {@link RunLevelController#proceedTo(int)} nor
     * {@link RunLevelController#proceedToAsync(int)} may be called from this method
     * <p>
     * Any exception thrown from this method is ignored
     *
     * @param currentJob the job currently running
     * @param levelAchieved the level just achieved by the currentJob.  Note
     * that if the currentJob is currently going up then the levelAchieved will
     * be the level for which all the services in that level were just started
     * while when going down the levelAchieved will be the level for which
     * all the services ABOVE that level have been shutdown.  In both cases
     * the levelAchieved represents the current level of the system
     */
    void onCancelled(RunLevelFuture currentJob, int levelAchieved);

    /**
     * Called when a service throws an exception during a proceedTo
     * operation
     * <p>
     * Neither {@link RunLevelController#proceedTo(int)} nor
     * {@link RunLevelController#proceedToAsync(int)} may be called from this method
     * <p>
     * Any exception thrown from this method is ignored
     * 
     * @param currentJob    the run level controller
     * @param errorInformation information about the error that had been caught
     */
    void onError(RunLevelFuture currentJob, ErrorInformation errorInformation);

    
}
