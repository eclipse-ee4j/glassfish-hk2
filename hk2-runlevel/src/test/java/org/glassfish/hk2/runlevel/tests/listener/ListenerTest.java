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

import junit.framework.Assert;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.runlevel.RunLevelController;
import org.glassfish.hk2.runlevel.RunLevelController.ThreadingPolicy;
import org.glassfish.hk2.runlevel.tests.utilities.Utilities;
import org.junit.Test;

/**
 * This tests that proceedTo and proceedToAsync work properly
 * from listeners
 * 
 * @author jwells
 *
 */
public class ListenerTest {
    public final static int NO_LEVEL = -3;
    
    private static void setupChanger(ServiceLocator locator, int changeAt, int changeTo) {
        setupChanger(locator, changeAt, changeTo, NO_LEVEL);
    }
    
    private static void setupChanger(ServiceLocator locator, int changeAt, int changeTo, int sleepAt) {
        locator.getService(OnProgressLevelChangerListener.class).setLevels(changeAt, changeTo, sleepAt);
    }
    
    /**
     * Tests that we can change the proceeding from the proposedLevel
     * callback
     */
    @Test 
    public void testProceedToFurtherUpFromEndOfRunWillKeepGoingUp() {
        ServiceLocator locator = Utilities.getServiceLocator(
                OnProgressLevelChangerListener.class);
        
        setupChanger(locator, 5, 10);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        
        controller.proceedTo(5);
        
        // But really, it should end up being 10
        Assert.assertEquals(10, controller.getCurrentRunLevel());
        
    }
    
    /**
     * Tests that the level can be changed from the middle of
     * the proposedLevel run
     */
    @Test 
    public void testProceedToFurtherUpFromMiddleOfRunWillKeepGoingUp() {
        ServiceLocator locator = Utilities.getServiceLocator(
                OnProgressLevelChangerListener.class);
        
        setupChanger(locator, 2, 10);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        
        controller.proceedTo(5);
        
        // But really, it should end up being 10
        Assert.assertEquals(10, controller.getCurrentRunLevel());
        
    }
    
    /**
     * Tests that the level can be changed from the middle of
     * the proposedLevel run
     */
    @Test 
    public void testProceedToFurtherUpFromMiddleOfRunWillKeepGoingDown() {
        ServiceLocator locator = Utilities.getServiceLocator(
                OnProgressLevelChangerListener.class);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        controller.proceedTo(10);
        
        setupChanger(locator, 7, 1);
        
        controller.proceedTo(5);
        
        // But really, it should end up being 1
        Assert.assertEquals(1, controller.getCurrentRunLevel());
        
    }
    
    /**
     * Tests that the level can be changed from the end of
     * the proposedLevel run
     */
    @Test 
    public void testProceedToFurtherUpFromEndOfRunWillKeepGoingDown() {
        ServiceLocator locator = Utilities.getServiceLocator(
                OnProgressLevelChangerListener.class);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        controller.proceedTo(10);
        
        setupChanger(locator, 5, 1);
        
        controller.proceedTo(5);
        
        // But really, it should end up being 1
        Assert.assertEquals(1, controller.getCurrentRunLevel());
    }
    
    /**
     * Tests going from an up direction to a down direction
     */
    @Test 
    public void testGoingFromUpToDown() {
        ServiceLocator locator = Utilities.getServiceLocator(
                OnProgressLevelChangerListener.class);
        
        setupChanger(locator, 7, 3, 1);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        controller.proceedTo(10);
        
        // But really, it should end up being 1
        Assert.assertEquals(3, controller.getCurrentRunLevel());
    }
    
    /**
     * Tests going from an down direction to a up direction
     */
    @Test 
    public void testGoingFromDownToUp() {
        ServiceLocator locator = Utilities.getServiceLocator(
                OnProgressLevelChangerListener.class);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        controller.proceedTo(10);
        
        setupChanger(locator, 2, 5, 9);
        
        controller.proceedTo(0);
        
        // But really, it should end up being 5
        Assert.assertEquals(5, controller.getCurrentRunLevel());
    }
    
    private static void noThreads(ServiceLocator locator) {
        locator.getService(RunLevelController.class).setThreadingPolicy(ThreadingPolicy.USE_NO_THREADS);
    }
    
    /**
     * Tests that we can change the proceeding from the proposedLevel
     * callback
     */
    @Test 
    public void testProceedToFurtherUpFromEndOfRunWillKeepGoingUpNoThreads() {
        ServiceLocator locator = Utilities.getServiceLocator(
                OnProgressLevelChangerListener.class);
        noThreads(locator);
        
        setupChanger(locator, 5, 10);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        
        controller.proceedTo(5);
        
        // But really, it should end up being 10
        Assert.assertEquals(10, controller.getCurrentRunLevel());
        
    }
    
    /**
     * Tests that the level can be changed from the middle of
     * the proposedLevel run
     */
    @Test 
    public void testProceedToFurtherUpFromMiddleOfRunWillKeepGoingUpNoThreads() {
        ServiceLocator locator = Utilities.getServiceLocator(
                OnProgressLevelChangerListener.class);
        noThreads(locator);
        
        setupChanger(locator, 2, 10);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        
        controller.proceedTo(5);
        
        // But really, it should end up being 10
        Assert.assertEquals(10, controller.getCurrentRunLevel());
        
    }
    
    /**
     * Tests that the level can be changed from the middle of
     * the proposedLevel run
     */
    @Test 
    public void testProceedToFurtherUpFromMiddleOfRunWillKeepGoingDownNoThreads() {
        ServiceLocator locator = Utilities.getServiceLocator(
                OnProgressLevelChangerListener.class);
        noThreads(locator);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        controller.proceedTo(10);
        
        setupChanger(locator, 7, 1);
        
        controller.proceedTo(5);
        
        // But really, it should end up being 1
        Assert.assertEquals(1, controller.getCurrentRunLevel());
        
    }
    
    /**
     * Tests that the level can be changed from the end of
     * the proposedLevel run
     */
    @Test 
    public void testProceedToFurtherUpFromEndOfRunWillKeepGoingDownNoThreads() {
        ServiceLocator locator = Utilities.getServiceLocator(
                OnProgressLevelChangerListener.class);
        noThreads(locator);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        controller.proceedTo(10);
        
        setupChanger(locator, 5, 1);
        
        controller.proceedTo(5);
        
        // But really, it should end up being 1
        Assert.assertEquals(1, controller.getCurrentRunLevel());
    }
    
    /**
     * Tests going from an up direction to a down direction
     */
    @Test
    public void testGoingFromUpToDownNoThreads() {
        ServiceLocator locator = Utilities.getServiceLocator(
                OnProgressLevelChangerListener.class);
        noThreads(locator);
        
        setupChanger(locator, 7, 3);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        controller.proceedTo(10);
        
        // But really, it should end up being 1
        Assert.assertEquals(3, controller.getCurrentRunLevel());
    }
    
    /**
     * Tests going from an down direction to a up direction
     */
    @Test
    public void testGoingFromDownToUpNoThreads() {
        ServiceLocator locator = Utilities.getServiceLocator(
                OnProgressLevelChangerListener.class);
        noThreads(locator);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        controller.proceedTo(10);
        
        setupChanger(locator, 2, 5);
        
        controller.proceedTo(0);
        
        // But really, it should end up being 5
        Assert.assertEquals(5, controller.getCurrentRunLevel());
    }


}
