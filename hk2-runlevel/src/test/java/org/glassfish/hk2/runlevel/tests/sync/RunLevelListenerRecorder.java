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

package org.glassfish.hk2.runlevel.tests.sync;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import junit.framework.Assert;

import org.glassfish.hk2.runlevel.ChangeableRunLevelFuture;
import org.glassfish.hk2.runlevel.ErrorInformation;
import org.glassfish.hk2.runlevel.RunLevelController;
import org.glassfish.hk2.runlevel.RunLevelFuture;
import org.glassfish.hk2.runlevel.RunLevelListener;
import org.jvnet.hk2.annotations.Service;

/**
 * @author jwells
 *
 */
@Service
public class RunLevelListenerRecorder implements RunLevelListener {
    private boolean up = true;
    private int currentLevel;
    private final HashMap<Integer, List<String>> ups = new HashMap<Integer, List<String>>();
    private final HashMap<Integer, List<String>> downs = new HashMap<Integer, List<String>>();
    
    @Inject
    private RunLevelController controller;

    @Override
    public void onCancelled(RunLevelFuture controller, int levelAchieved) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onError(RunLevelFuture currentJob, ErrorInformation info) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onProgress(ChangeableRunLevelFuture currentJob, int levelAchieved) {
        currentLevel = levelAchieved;
        up = currentJob.isUp();
    }
    
    /**
     * Tells the recorder it is going down
     */
    public void goingDown() {
        up = false;
    }
    
    /**
     * Records the given event
     * @param event The event to record
     */
    public void recordEvent(String event) {
        // no locks
        if (up) {
            Assert.assertTrue(controller.getCurrentRunLevel() == currentLevel);
            
            List<String> upList = ups.get(currentLevel);
            if (upList == null) {
                upList = new LinkedList<String>();
                
                ups.put(currentLevel, upList);
            }
            
            upList.add(event);
        }
        else {
            List<String> downList = downs.get(currentLevel);
            if (downList == null) {
                downList = new LinkedList<String>();
                
                downs.put(currentLevel, downList);
            }
            
            downList.add(event);
        }
    }
    
    /**
     * Gets the up events for the given level
     * 
     * @param level The level to get events for
     * @return The list of events
     */
    public List<String> getUpEventsForLevel(int level) {
        List<String> retVal = ups.get(level);
        if (retVal == null) return Collections.emptyList();
        return retVal;
    }
    
    /**
     * Gets the down events for the given level
     * 
     * @param level The level to get events for
     * @return The list of events
     */
    public List<String> getDownEventsForLevel(int level) {
        List<String> retVal = downs.get(level);
        if (retVal == null) return Collections.emptyList();
        return retVal;
    }

}
