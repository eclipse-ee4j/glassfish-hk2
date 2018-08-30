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

package org.glassfish.hk2.runlevel.tests.listener2;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Singleton;

import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.runlevel.ChangeableRunLevelFuture;
import org.glassfish.hk2.runlevel.ErrorInformation;
import org.glassfish.hk2.runlevel.ErrorInformation.ErrorAction;
import org.glassfish.hk2.runlevel.ProgressStartedListener;
import org.glassfish.hk2.runlevel.RunLevelController;
import org.glassfish.hk2.runlevel.RunLevelFuture;
import org.glassfish.hk2.runlevel.RunLevelListener;
import org.glassfish.hk2.runlevel.tests.utilities.Utilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class ListenerLevelsTest {
    /**
     * Ensures the different listener types get the proper set of events
     * even when there is a failure going up or down
     */
    @Test
    public void testDifferentRunLevelListenerTypes() {
        ServiceLocator locator = Utilities.getServiceLocator(TestStartingListener.class,
                TestProgressListener.class,
                ServiceAtTen.class, SometimesFailsAtFiveService.class);
        
        TestStartingListener listener = locator.getService(TestStartingListener.class);
        TestProgressListener progressListener = locator.getService(TestProgressListener.class);
        progressListener.clear();
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        
        SometimesFailsAtFiveService.setBombAtFive(true);
        
        try {
            controller.proceedTo(10);
            Assert.fail("Should fail at five");
        }
        catch (MultiException me) {
            // Expected
        }
        
        Assert.assertFalse(ServiceAtTen.isActive());
        
        Assert.assertEquals(-2, listener.getLastLevel());
        
        List<Integer> levels = progressListener.getLevels();
        
        Assert.assertEquals(6, levels.size());
        
        Assert.assertEquals(-1, levels.get(0).intValue());
        Assert.assertEquals(0, levels.get(1).intValue());
        Assert.assertEquals(1, levels.get(2).intValue());
        Assert.assertEquals(2, levels.get(3).intValue());
        Assert.assertEquals(3, levels.get(4).intValue());
        Assert.assertEquals(4, levels.get(5).intValue());
        
        // Now let it proceed
        SometimesFailsAtFiveService.setBombAtFive(false);
        
        progressListener.clear();
        controller.proceedTo(10);
        
        Assert.assertTrue(ServiceAtTen.isActive());
        
        Assert.assertEquals(4, listener.getLastLevel());
        
        levels = progressListener.getLevels();
        
        Assert.assertEquals(6, levels.size());
        
        Assert.assertEquals(5, levels.get(0).intValue());
        Assert.assertEquals(6, levels.get(1).intValue());
        Assert.assertEquals(7, levels.get(2).intValue());
        Assert.assertEquals(8, levels.get(3).intValue());
        Assert.assertEquals(9, levels.get(4).intValue());
        Assert.assertEquals(10, levels.get(5).intValue());
        
        // Now the downward direction
        SometimesFailsAtFiveService.setBombAtFive(true);
        
        progressListener.clear();
        
        // Will halt at four due to value returned from progressListener
        controller.proceedTo(0);
        
        Assert.assertFalse(ServiceAtTen.isActive());
        
        Assert.assertEquals(10, listener.getLastLevel());
        Assert.assertEquals(4, controller.getCurrentRunLevel());
        
        levels = progressListener.getLevels();
        
        Assert.assertEquals(6, levels.size());
        
        Assert.assertEquals(9, levels.get(0).intValue());
        Assert.assertEquals(8, levels.get(1).intValue());
        Assert.assertEquals(7, levels.get(2).intValue());
        Assert.assertEquals(6, levels.get(3).intValue());
        Assert.assertEquals(5, levels.get(4).intValue());
        Assert.assertEquals(4, levels.get(5).intValue());
        
        // And all the way down now
        progressListener.clear();
        controller.proceedTo(0);
        
        Assert.assertFalse(ServiceAtTen.isActive());
        
        Assert.assertEquals(4, listener.getLastLevel());
        Assert.assertEquals(0, controller.getCurrentRunLevel());
        
        levels = progressListener.getLevels();
        
        Assert.assertEquals(4, levels.size());
        
        Assert.assertEquals(3, levels.get(0).intValue());
        Assert.assertEquals(2, levels.get(1).intValue());
        Assert.assertEquals(1, levels.get(2).intValue());
        Assert.assertEquals(0, levels.get(3).intValue());
    }
    
    @Singleton
    private static class TestStartingListener implements ProgressStartedListener {
        private int lastLevel = -3;

        @Override
        public void onProgressStarting(ChangeableRunLevelFuture currentJob,
                int currentLevel) {
            lastLevel = currentLevel;
        }
        
        public int getLastLevel() {
            return lastLevel;
        }
    }
    
    @Singleton
    private static class TestProgressListener implements RunLevelListener {
        private final List<Integer> levels = new LinkedList<Integer>();
        
        public void clear() {
            levels.clear();
        }
        
        public List<Integer> getLevels() {
            return levels;
        }

        /* (non-Javadoc)
         * @see org.glassfish.hk2.runlevel.RunLevelListener#onProgress(org.glassfish.hk2.runlevel.ChangeableRunLevelFuture, int)
         */
        @Override
        public void onProgress(ChangeableRunLevelFuture currentJob,
                int levelAchieved) {
            levels.add(levelAchieved);
            
        }

        /* (non-Javadoc)
         * @see org.glassfish.hk2.runlevel.RunLevelListener#onCancelled(org.glassfish.hk2.runlevel.RunLevelFuture, int)
         */
        @Override
        public void onCancelled(RunLevelFuture currentJob, int levelAchieved) {
            
        }

        /* (non-Javadoc)
         * @see org.glassfish.hk2.runlevel.RunLevelListener#onError(org.glassfish.hk2.runlevel.RunLevelFuture, org.glassfish.hk2.runlevel.ErrorInformation)
         */
        @Override
        public void onError(RunLevelFuture currentJob,
                ErrorInformation errorInformation) {
            // Make it stop when going down
            errorInformation.setAction(ErrorAction.GO_TO_NEXT_LOWER_LEVEL_AND_STOP);
        }

        
    }
    
    

}
