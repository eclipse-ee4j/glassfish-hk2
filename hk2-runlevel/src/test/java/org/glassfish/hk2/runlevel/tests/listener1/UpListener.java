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

package org.glassfish.hk2.runlevel.tests.listener1;

import jakarta.inject.Singleton;

import org.glassfish.hk2.runlevel.ChangeableRunLevelFuture;
import org.glassfish.hk2.runlevel.ErrorInformation;
import org.glassfish.hk2.runlevel.ProgressStartedListener;
import org.glassfish.hk2.runlevel.RunLevelFuture;
import org.glassfish.hk2.runlevel.RunLevelListener;

/**
 * @author jwells
 *
 */
@Singleton
public class UpListener implements RunLevelListener, ProgressStartedListener {
    private boolean up = false;
    private boolean down = false;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.runlevel.RunLevelListener#onProgress(org.glassfish.hk2.runlevel.ChangeableRunLevelFuture, int)
     */
    @Override
    public void onProgress(ChangeableRunLevelFuture currentJob,
            int levelAchieved) {
        if (currentJob.isUp()) up = true;
        if (currentJob.isDown()) down = true;
    }
    
    public void done() {
        up = false;
        down = false;
    }
    
    public boolean isUp() {
        return up;
    }
    
    public boolean isDown() {
        return down;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.runlevel.RunLevelListener#onCancelled(org.glassfish.hk2.runlevel.RunLevelFuture, int)
     */
    @Override
    public void onCancelled(RunLevelFuture currentJob, int levelAchieved) {
        // Do nothing
        
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.runlevel.RunLevelListener#onError(org.glassfish.hk2.runlevel.RunLevelFuture, org.glassfish.hk2.runlevel.ErrorInformation)
     */
    @Override
    public void onError(RunLevelFuture currentJob,
            ErrorInformation errorInformation) {
        // Do nothing
        
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.runlevel.ProgressStartedListener#onProgressStarting(org.glassfish.hk2.runlevel.ChangeableRunLevelFuture, int)
     */
    @Override
    public void onProgressStarting(ChangeableRunLevelFuture currentJob,
            int currentLevel) {
        onProgress(currentJob, currentLevel);
        
    }

}
