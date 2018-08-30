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

package org.glassfish.hk2.runlevel.tests.async;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.glassfish.hk2.runlevel.ChangeableRunLevelFuture;
import org.glassfish.hk2.runlevel.ErrorInformation;
import org.glassfish.hk2.runlevel.ProgressStartedListener;
import org.glassfish.hk2.runlevel.RunLevelFuture;
import org.glassfish.hk2.runlevel.RunLevelListener;

/**
 * @author jwells
 *
 */
public abstract class AbstractRunLevelListener implements RunLevelListener, ProgressStartedListener {
    private List<Integer> progressedLevels;
    private List<Integer> cancelledLevels;
    private List<Throwable> reportedErrors;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.runlevel.RunLevelListener#onCancelled(org.glassfish.hk2.runlevel.RunLevelController, int, boolean)
     */
    @Override
    public void onCancelled(RunLevelFuture currentJob,
            int levelAchieved) {
        synchronized (this) {
            if (cancelledLevels == null) {
                cancelledLevels = new LinkedList<Integer>(); 
            }
            
            cancelledLevels.add(levelAchieved);
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.runlevel.RunLevelListener#onError(org.glassfish.hk2.runlevel.RunLevelController, java.lang.Throwable, boolean)
     */
    @Override
    public void onError(RunLevelFuture currentJob, ErrorInformation info) {
        synchronized (this) {
            if (reportedErrors == null) {
                reportedErrors = new LinkedList<Throwable>();
            }
            
            reportedErrors.add(info.getError());
        }

    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.runlevel.RunLevelListener#onProgress(org.glassfish.hk2.runlevel.RunLevelFuture, int)
     */
    @Override
    public void onProgress(ChangeableRunLevelFuture currentJob, int levelAchieved) {
        synchronized (this) {
            if (progressedLevels == null) {
                progressedLevels = new LinkedList<Integer>(); 
            }
            
            progressedLevels.add(levelAchieved);
        }

    }
    
    @Override
    public void onProgressStarting(ChangeableRunLevelFuture currentJob, int currentLevel) {
        onProgress(currentJob, currentLevel);
    }
    
    public List<Integer> getAndPurgeProgressedLevels() {
        synchronized (this) {
            if (progressedLevels == null) return Collections.emptyList();
            List<Integer> retVal =  progressedLevels;
            progressedLevels = null;
            return retVal;
        }
    }
    
    public List<Integer> getAndPurgeCancelledLevels() {
        synchronized (this) {
            if (cancelledLevels == null) return Collections.emptyList();
            List<Integer> retVal =  cancelledLevels;
            cancelledLevels = null;
            return retVal;
        }
    }
    
    public List<Throwable> getAndPurgeReportedErrors() {
        synchronized (this) {
            if (reportedErrors == null) return Collections.emptyList();
            List<Throwable> retVal = reportedErrors;
            reportedErrors = null;
            return retVal;
        }
    }

}
