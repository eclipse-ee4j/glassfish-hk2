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

package org.glassfish.hk2.runlevel;

import org.jvnet.hk2.annotations.Contract;

/**
 * Instances of classes implementing this contract can be registered with HK2
 * to be informed when the system starts progressing to a new level, either
 * upward or downward
 * <p>
 * Lengthy operations should not be performed in the listener since
 * that may impact the performance of the RunLevelController calling the
 * listener
 *
 * @author jwells
 */
@Contract
public interface ProgressStartedListener {
    /**
     * Called when the RunLevelController starts progressing to a new
     * level but before any work has been done yet
     * <p>
     * Neither {@link RunLevelController#proceedTo(int)} nor
     * {@link RunLevelController#proceedToAsync(int)} may be called from this method.  However,
     * {@link ChangeableRunLevelFuture#changeProposedLevel(int)} may be called
     * <p>
     * Any exception thrown from this method is ignored
     * <p>
     *
     * @param currentJob the job currently running
     * @param currentLevel the level that the system is currently at before
     * any work has been done to move the system up or down
     */
    public void onProgressStarting(ChangeableRunLevelFuture currentJob, int currentLevel);

}
