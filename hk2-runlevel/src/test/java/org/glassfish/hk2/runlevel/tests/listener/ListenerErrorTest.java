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

import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.runlevel.ErrorInformation;
import org.glassfish.hk2.runlevel.RunLevelController;
import org.glassfish.hk2.runlevel.RunLevelController.ThreadingPolicy;
import org.glassfish.hk2.runlevel.tests.utilities.Utilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class ListenerErrorTest {
    private static void setupErrorChanger(ServiceLocator locator, ErrorInformation.ErrorAction action) {
        locator.getService(OnProgressLevelChangerListener.class).setErrorAction(action);
    }
    
    /**
     * Ensures we can ignore failures when going up
     */
    @Test
    public void testKeepGoingUpWithIgnoreAction() {
        ServiceLocator locator = Utilities.getServiceLocator(LevelFiveErrorService.class,
                LevelFiveUpService.class,
                OnProgressLevelChangerListener.class);
        
        LevelFiveUpService.postConstructCalled = false;
        
        setupErrorChanger(locator, ErrorInformation.ErrorAction.IGNORE);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        
        controller.proceedTo(10);
        
        Assert.assertTrue(LevelFiveUpService.postConstructCalled);
        
        // Should go all the way up because we ignored the error
        Assert.assertEquals(10, controller.getCurrentRunLevel());
    }
    
    /**
     * Ensures we can ignore failures when going up
     */
    @Test
    public void testKeepGoingUpWithIgnoreActionSingleThread() {
        ServiceLocator locator = Utilities.getServiceLocator(LevelFiveErrorService.class,
                LevelFiveUpService.class,
                OnProgressLevelChangerListener.class);
        
        LevelFiveUpService.postConstructCalled = false;
        
        setupErrorChanger(locator, ErrorInformation.ErrorAction.IGNORE);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        controller.setMaximumUseableThreads(1);
        
        controller.proceedTo(10);
        
        Assert.assertTrue(LevelFiveUpService.postConstructCalled);
        
        // Should go all the way up because we ignored the error
        Assert.assertEquals(10, controller.getCurrentRunLevel());
    }
    
    /**
     * Ensures we can ignore failures when going up
     */
    @Test
    public void testComingDownDoesNotCallOtherServices() {
        ServiceLocator locator = Utilities.getServiceLocator(LevelFiveErrorService.class,
                LevelFiveUpService.class,
                OnProgressLevelChangerListener.class);
        
        LevelFiveUpService.postConstructCalled = false;
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        controller.setMaximumUseableThreads(1);
        
        try {
            controller.proceedTo(10);
            Assert.fail("Should have failed at level 5");
        }
        catch (MultiException me) {
            // Expected exception
        }
        
        Assert.assertFalse(LevelFiveUpService.postConstructCalled);
        
        // Should go all the way up because we ignored the error
        Assert.assertEquals(4, controller.getCurrentRunLevel());
    }
    
    /**
     * Ensures we can ignore failures when going up
     */
    @Test
    public void testKeepGoingUpWithIgnoreActionNoThreads() {
        ServiceLocator locator = Utilities.getServiceLocator(LevelFiveErrorService.class,
                LevelFiveUpService.class,
                OnProgressLevelChangerListener.class);
        
        LevelFiveUpService.postConstructCalled = false;
        
        setupErrorChanger(locator, ErrorInformation.ErrorAction.IGNORE);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        controller.setThreadingPolicy(ThreadingPolicy.USE_NO_THREADS);
        
        controller.proceedTo(10);
        
        Assert.assertTrue(LevelFiveUpService.postConstructCalled);
        
        // Should go all the way up because we ignored the error
        Assert.assertEquals(10, controller.getCurrentRunLevel());
    }
    
    /**
     * Ensures we can ignore failures when going up
     */
    @Test
    public void testComingDownDoesNotCallOtherServicesNoThreads() {
        ServiceLocator locator = Utilities.getServiceLocator(LevelFiveErrorService.class,
                LevelFiveUpService.class,
                OnProgressLevelChangerListener.class);
        
        LevelFiveUpService.postConstructCalled = false;
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        controller.setThreadingPolicy(ThreadingPolicy.USE_NO_THREADS);
        
        try {
            controller.proceedTo(10);
            Assert.fail("Should have failed at level 5");
        }
        catch (MultiException me) {
            // Expected exception
        }
        
        Assert.assertFalse(LevelFiveUpService.postConstructCalled);
        
        // Should go all the way up because we ignored the error
        Assert.assertEquals(4, controller.getCurrentRunLevel());
    }
    
    /**
     * Ensures the user can halt the downward level progression if a service
     * failed when going down
     */
    @Test
    public void testHaltLevelRegressionOnError() {
        ServiceLocator locator = Utilities.getServiceLocator(LevelFiveDownErrorService.class,
                LevelFiveService.class,
                OnProgressLevelChangerListener.class);
        
        setupErrorChanger(locator, ErrorInformation.ErrorAction.GO_TO_NEXT_LOWER_LEVEL_AND_STOP);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        
        controller.proceedTo(10);
        
        // Should go all the way up because we ignored the error
        Assert.assertEquals(10, controller.getCurrentRunLevel());
        
        LevelFiveService levelFiveService = locator.getService(LevelFiveService.class);
        Assert.assertFalse(levelFiveService.isPreDestroyCalled());
        
        controller.proceedTo(0);
        
        // Should get halted
        Assert.assertEquals(4, controller.getCurrentRunLevel());
        
        OnProgressLevelChangerListener listener = locator.getService(OnProgressLevelChangerListener.class);
        
        Assert.assertEquals(4, listener.getLatestOnProgress());
        
        Assert.assertTrue(levelFiveService.isPreDestroyCalled());
    }
    
    /**
     * Ensures the user can halt the downward level progression if a service
     * failed when going down
     */
    @Test
    public void testHaltLevelRegressionOnErrorNoThreads() {
        ServiceLocator locator = Utilities.getServiceLocator(LevelFiveDownErrorService.class,
                LevelFiveService.class,
                OnProgressLevelChangerListener.class);
        
        setupErrorChanger(locator, ErrorInformation.ErrorAction.GO_TO_NEXT_LOWER_LEVEL_AND_STOP);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        controller.setThreadingPolicy(ThreadingPolicy.USE_NO_THREADS);
        
        controller.proceedTo(10);
        
        // Should go all the way up because we ignored the error
        Assert.assertEquals(10, controller.getCurrentRunLevel());
        
        LevelFiveService levelFiveService = locator.getService(LevelFiveService.class);
        Assert.assertFalse(levelFiveService.isPreDestroyCalled());
        
        controller.proceedTo(0);
        
        // Should get halted
        Assert.assertEquals(4, controller.getCurrentRunLevel());
        
        OnProgressLevelChangerListener listener = locator.getService(OnProgressLevelChangerListener.class);
        
        Assert.assertEquals(4, listener.getLatestOnProgress());
        
        Assert.assertTrue(levelFiveService.isPreDestroyCalled());
    }
    
    /**
     * Ensures that the expected exception was given to the error handler
     */
    @Test
    public void testCheckDescriptorFromErrorInformationOnWayUp() {
        ServiceLocator locator = Utilities.getServiceLocator(
                LevelFiveService.class,
                LevelFiveErrorService.class,
                OnProgressLevelChangerListener.class);
        
        OnProgressLevelChangerListener listener = locator.getService(OnProgressLevelChangerListener.class);
        listener.reset();
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        controller.setThreadingPolicy(ThreadingPolicy.USE_NO_THREADS);
        
        try {
          controller.proceedTo(5);
          Assert.fail("Should have failed at level 5");
        }
        catch (MultiException me) {
            
        }
        
        // Sanity check
        Assert.assertEquals(4, controller.getCurrentRunLevel());
        
        ErrorInformation errorInfo = listener.getLastErrorInformation();
        Assert.assertNotNull(errorInfo);
        
        Descriptor failedDescriptor = errorInfo.getFailedDescriptor();
        Assert.assertNotNull(failedDescriptor);
        
        Assert.assertEquals(failedDescriptor.getImplementation(), LevelFiveErrorService.class.getName());
    }
    
    /**
     * Tests that the error handler is only called once when going up
     */
    @Test
    public void testErrorHandlerOnlyCalledOnceUp() {
        ServiceLocator locator = Utilities.getServiceLocator(
                LevelFiveErrorService.class,
                OnProgressLevelChangerListener.class);
        
        OnProgressLevelChangerListener listener = locator.getService(OnProgressLevelChangerListener.class);
        listener.reset();
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        controller.setThreadingPolicy(ThreadingPolicy.USE_NO_THREADS);
        
        try {
          controller.proceedTo(5);
          Assert.fail("Should have failed at level 5");
        }
        catch (MultiException me) {
            
        }
        
        // Sanity check
        Assert.assertEquals(4, controller.getCurrentRunLevel());
        
        Assert.assertEquals(1, listener.getNumOnErrorCalled());
    }
    
    /**
     * Tests that the error handler is only called once when going down
     */
    @Test
    public void testErrorHandlerOnlyCalledOnceDown() {
        ServiceLocator locator = Utilities.getServiceLocator(
                LevelFiveDownErrorService.class,
                LevelFiveService.class,
                OnProgressLevelChangerListener.class);
        
        OnProgressLevelChangerListener listener = locator.getService(OnProgressLevelChangerListener.class);
        listener.reset();
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        controller.setThreadingPolicy(ThreadingPolicy.USE_NO_THREADS);
        
        controller.proceedTo(6);
        
        Assert.assertEquals(0, listener.getNumOnErrorCalled());
        
        controller.proceedTo(4);
        
        // Sanity check
        Assert.assertEquals(4, controller.getCurrentRunLevel());
        
        Assert.assertEquals(1, listener.getNumOnErrorCalled());
    }
    
    /**
     * Ensures that the expected exception was given to the error handler
     * on the way down
     */
    @Test
    public void testCheckDescriptorFromErrorInformationOnWayDown() {
        ServiceLocator locator = Utilities.getServiceLocator(
                LevelFiveService.class,
                LevelFiveDownErrorService.class,
                OnProgressLevelChangerListener.class);
        
        OnProgressLevelChangerListener listener = locator.getService(OnProgressLevelChangerListener.class);
        listener.reset();
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        controller.setThreadingPolicy(ThreadingPolicy.USE_NO_THREADS);
        
        controller.proceedTo(6);
        controller.proceedTo(4);
        
        // Sanity check
        Assert.assertEquals(4, controller.getCurrentRunLevel());
        
        ErrorInformation errorInfo = listener.getLastErrorInformation();
        Assert.assertNotNull(errorInfo);
        
        Descriptor failedDescriptor = errorInfo.getFailedDescriptor();
        Assert.assertNotNull(failedDescriptor);
        
        Assert.assertEquals(failedDescriptor.getImplementation(), LevelFiveDownErrorService.class.getName());
    }

}
