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

package org.glassfish.hk2.runlevel.tests.listener;

import javax.inject.Singleton;

import org.glassfish.hk2.runlevel.ChangeableRunLevelFuture;
import org.glassfish.hk2.runlevel.ErrorInformation;
import org.glassfish.hk2.runlevel.RunLevelFuture;
import org.glassfish.hk2.runlevel.RunLevelListener;

/**
 * @author jwells
 *
 */
@Singleton
public class OnProgressLevelChangerListener implements RunLevelListener {
    private int changeAtLevel = ListenerTest.NO_LEVEL;
    private int changeToLevel = ListenerTest.NO_LEVEL;
    private int sleepAtLevel = ListenerTest.NO_LEVEL;
    
    private ErrorInformation.ErrorAction changeToErrorAction = null;
    
    private int latestOnProgress = ListenerTest.NO_LEVEL;
    private ErrorInformation lastErrorInformation = null;
    private int numOnErrorCalled;
    
    /* package */ void setLevels(int changeAtLevel, int changeToLevel, int sleepAtLevel) {
        this.changeAtLevel = changeAtLevel;
        this.changeToLevel = changeToLevel;
        this.sleepAtLevel = sleepAtLevel;
    }
    
    /* package */ void setErrorAction(ErrorInformation.ErrorAction action) {
        this.changeToErrorAction = action;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.runlevel.RunLevelListener#onProgress(org.glassfish.hk2.runlevel.ChangeableRunLevelFuture, int)
     */
    @Override
    public void onProgress(ChangeableRunLevelFuture currentJob,
            int levelAchieved) {
        latestOnProgress = levelAchieved;
        
        if (levelAchieved == sleepAtLevel) {
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (levelAchieved == changeAtLevel) {
            currentJob.changeProposedLevel(changeToLevel);
        }

    }
    
    /* package */ int getLatestOnProgress() {
        return latestOnProgress;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.runlevel.RunLevelListener#onCancelled(org.glassfish.hk2.runlevel.ChangeableRunLevelFuture, int)
     */
    @Override
    public void onCancelled(RunLevelFuture currentJob,
            int levelAchieved) {

    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.runlevel.RunLevelListener#onError(org.glassfish.hk2.runlevel.ChangeableRunLevelFuture, java.lang.Throwable)
     */
    @Override
    public synchronized void onError(RunLevelFuture currentJob, ErrorInformation info) {
        numOnErrorCalled++;
        
        if (changeToErrorAction != null) {
            info.setAction(changeToErrorAction);
        }
        lastErrorInformation = info;
    }
    
    public synchronized void reset() {
        lastErrorInformation = null;
        numOnErrorCalled = 0;
    }
    
    public synchronized ErrorInformation getLastErrorInformation() {
        return lastErrorInformation;
    }
    
    public synchronized int getNumOnErrorCalled() {
        return numOnErrorCalled;
    }
    

}
