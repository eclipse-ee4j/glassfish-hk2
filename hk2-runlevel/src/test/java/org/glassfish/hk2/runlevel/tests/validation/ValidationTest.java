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

package org.glassfish.hk2.runlevel.tests.validation;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.runlevel.RunLevel;
import org.glassfish.hk2.runlevel.RunLevelController;
import org.glassfish.hk2.runlevel.RunLevelFuture;
import org.glassfish.hk2.runlevel.tests.utilities.Utilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class ValidationTest {
    public final static int ZERO = 0;
    public final static int THREE = 3;
    public final static int FIVE = 5;
    
    /**
     * This test ensures a validating service will
     * fail if it created (with no outstanding work)
     */
    @Test
    public void testValidatingService() {
        ServiceLocator locator = Utilities.getServiceLocator(
                DependsOnValidatingLevelFiveService.class,
                LevelFiveService.class);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        
        controller.proceedTo(4);  // Not five
        
        try {
            locator.getService(DependsOnValidatingLevelFiveService.class);
            Assert.fail("Should have failed as we are not at level 5");
        }
        catch (MultiException me) {
            Throwable topException = me.getErrors().get(0);
            Assert.assertTrue(topException instanceof IllegalStateException);
            
            Assert.assertTrue(topException.getMessage().contains(" but it has a run level of "));
        }
        
    }
    
    /**
     * This test ensures a non-validating service will
     * get properly created even if it is not the proper
     * level yet
     */
    @Test
    public void testNonValidatingService() {
        ServiceLocator locator = Utilities.getServiceLocator(
                DependsOnNonValidatingLevelFiveService.class,
                NonValidatingLevelFiveService.class);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        
        controller.proceedTo(4);  // Not five
        
        locator.getService(DependsOnNonValidatingLevelFiveService.class);
    }
    
    /**
     * This test ensures a validating service will
     * fail if it is created (when going up)
     * @throws TimeoutException 
     * @throws InterruptedException 
     */
    @Test
    public void testValidatingServiceInProgress() throws InterruptedException, TimeoutException {
        ServiceLocator locator = Utilities.getServiceLocator(
                LevelThreeService.class,
                LevelFiveService.class);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        
        RunLevelFuture future = controller.proceedToAsync(FIVE);
        
        try {
            future.get(20, TimeUnit.SECONDS);
            Assert.fail("Should have failed as a service at level three depends on a service at level 5");
        }
        catch (ExecutionException ee) {
            Throwable cause = ee.getCause();
            Assert.assertTrue (cause instanceof MultiException);
            
            MultiException me = (MultiException) cause;
            Throwable th1 = me.getErrors().get(0);
            
            Assert.assertTrue(th1 instanceof MultiException);
            MultiException me1 = (MultiException) th1;
            Throwable th2 = me1.getErrors().get(0);
            
            Assert.assertTrue(th2 instanceof IllegalStateException);
            Assert.assertTrue(th2.getMessage().contains(" but it has a run level of "));
        }
        
    }
    
    /**
     * This test ensures a validating service will
     * fail if it is created (when going up)
     * @throws TimeoutException 
     * @throws InterruptedException 
     */
    @Test
    public void testNonValidatingServiceInProgress() throws InterruptedException, TimeoutException {
        ServiceLocator locator = Utilities.getServiceLocator(
                LevelThreeDependsOnLevelFiveNonValidating.class,
                NonValidatingLevelFiveService.class);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        
        controller.proceedTo(FIVE);
        
        LevelThreeDependsOnLevelFiveNonValidating l3 = locator.getService(LevelThreeDependsOnLevelFiveNonValidating.class);
        NonValidatingLevelFiveService l5 = locator.getService(NonValidatingLevelFiveService.class);
        
        Assert.assertTrue(l3.isUp());
        Assert.assertTrue(l5.isUp());
        
        controller.proceedTo(THREE);
        
        // Even though we have gone below level 5, this level 5 service should STILL be up because
        // a level 3 service depended on it...
        Assert.assertTrue(l3.isUp());
        Assert.assertTrue(l5.isUp());
        
        controller.proceedTo(ZERO);
        
        // But now they should both come down
        Assert.assertFalse(l3.isUp());
        Assert.assertFalse(l5.isUp());
    }
    
    @Test
    public void testDirectlyGettingInvalidService() {
        ServiceLocator locator = Utilities.getServiceLocator(
                LevelFiveService.class);
        
        try {
            locator.getService(LevelFiveService.class);
        }
        catch (MultiException me) {
            Throwable th1 = me.getErrors().get(0);
            
            Assert.assertTrue(th1 instanceof IllegalStateException);
            Assert.assertTrue(th1.getMessage().contains(" but it has a run level of "));
        }
        
    }
    
    /**
     * Tests that a validating service can be overridden to non-validating
     */
    @Test // @org.junit.Ignore
    public void testOverrideValidationValidatingToNonValidating() {
        ServiceLocator locator = Utilities.getServiceLocator(
                LevelFiveService.class);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        
        controller.setValidationOverride(RunLevel.RUNLEVEL_MODE_NON_VALIDATING);
        
        LevelFiveService lfs = locator.getService(LevelFiveService.class);
        
        // Got it, even though it is validating!
        Assert.assertNotNull(lfs);
    }
    
    /**
     * Tests that a non-validating service can be overridden to validating
     */
    @Test // @org.junit.Ignore
    public void testOverrideValidationNonValidatingToValidating() {
        ServiceLocator locator = Utilities.getServiceLocator(
                NonValidatingLevelFiveService.class);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        
        controller.setValidationOverride(RunLevel.RUNLEVEL_MODE_VALIDATING);
        
        try {
            locator.getService(NonValidatingLevelFiveService.class);
            Assert.fail("Should have failed, service is now validating");
        }
        catch (MultiException me) {
            // Expected failure, the non-validating service is now validating!
        }
    }

}
