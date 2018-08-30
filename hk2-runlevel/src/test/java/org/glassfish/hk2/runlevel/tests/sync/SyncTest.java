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

import java.util.List;

import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.runlevel.RunLevelContext;
import org.glassfish.hk2.runlevel.RunLevelController;
import org.glassfish.hk2.runlevel.tests.utilities.Utilities;
import org.glassfish.hk2.runlevel.tests.utilities.Utilities.InitType;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class SyncTest {
    public final static String SERVICE_ONE = "One";
    public final static String SERVICE_TEN = "Ten";
    public final static String SERVICE_TWENTY = "Twenty";
    
    @Test
    public void testNamedContextViaDynamicAddition() {
        ServiceLocator locator = Utilities.getServiceLocator(InitType.DYNAMIC);
        Assert.assertNotNull(locator.getService(Context.class, RunLevelContext.CONTEXT_NAME));
    }
    
    @Test
    public void testNamedContextViaUtilities() {
        ServiceLocator locator = Utilities.getServiceLocator(InitType.UTILITIES);
        Assert.assertNotNull(locator.getService(Context.class, RunLevelContext.CONTEXT_NAME));
    }
    
    @Test
    public void testNamedContextViaEDSL() {
        ServiceLocator locator = Utilities.getServiceLocator(InitType.MODULE);
        Assert.assertNotNull(locator.getService(Context.class, RunLevelContext.CONTEXT_NAME));
    }
    
    /**
     * This tests that things truly happen on the thread passed in
     */
    @Test
    public void testUseNoThreadsPolicy() {
        ServiceLocator locator = Utilities.getServiceLocator(
                ServiceWithThreadLocal.class,
                ThreadSensitiveService.class,
                ListenerService.class);
        
        ServiceWithThreadLocal threadLocal = locator.getService(ServiceWithThreadLocal.class);
        Assert.assertFalse(threadLocal.wasUpToggled());
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        controller.setThreadingPolicy(RunLevelController.ThreadingPolicy.USE_NO_THREADS);
        
        controller.proceedTo(1);
        
        // If no other thread was used then the thread local variable will have been toggled
        Assert.assertTrue(threadLocal.wasUpToggled());
        
        Assert.assertFalse(threadLocal.wasDownToggled());
        
        controller.proceedTo(0);
        
        Assert.assertTrue(threadLocal.wasDownToggled());
    }
    
    private void checkList(List<String> list, String onlyEvent) {
        Assert.assertEquals(1, list.size());
        
        Assert.assertEquals(list.get(0), onlyEvent);
    }
    
    /**
     * Makes sure the proper events are fired for multiple levels
     */
    @Test
    public void testMultipleServicesUpAndDown() {
        ServiceLocator locator = Utilities.getServiceLocator(
                LevelOneService.class,
                LevelTenService.class,
                LevelTwentyService.class,
                RunLevelListenerRecorder.class);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        controller.setThreadingPolicy(RunLevelController.ThreadingPolicy.USE_NO_THREADS);
        
        RunLevelListenerRecorder recorder = locator.getService(RunLevelListenerRecorder.class);
        
        controller.proceedTo(20);
        recorder.goingDown();
        controller.proceedTo(0);
        
        for (int lcv = 0; lcv < 21; lcv++) {
            if (lcv == 0) {
                checkList(recorder.getUpEventsForLevel(lcv), SERVICE_ONE);
                
                Assert.assertTrue(recorder.getDownEventsForLevel(lcv).isEmpty());
            }
            else if (lcv == 1) {
                Assert.assertTrue(recorder.getUpEventsForLevel(lcv).isEmpty());
                
                checkList(recorder.getDownEventsForLevel(lcv), SERVICE_ONE);
            }
            else if (lcv == 9) {
                checkList(recorder.getUpEventsForLevel(lcv), SERVICE_TEN);
                
                Assert.assertTrue(recorder.getDownEventsForLevel(lcv).isEmpty());
            }
            else if (lcv == 10) {
                Assert.assertTrue(recorder.getUpEventsForLevel(lcv).isEmpty());
                
                checkList(recorder.getDownEventsForLevel(lcv), SERVICE_TEN);
            }
            else if (lcv == 19) {
                checkList(recorder.getUpEventsForLevel(lcv), SERVICE_TWENTY);
                
                Assert.assertTrue(recorder.getDownEventsForLevel(lcv).isEmpty());
            }
            else if (lcv == 20) {
                Assert.assertTrue(recorder.getUpEventsForLevel(lcv).isEmpty());
                
                checkList(recorder.getDownEventsForLevel(lcv), SERVICE_TWENTY);
            }
            else {
                Assert.assertTrue(recorder.getUpEventsForLevel(lcv).isEmpty());
                
                Assert.assertTrue(recorder.getDownEventsForLevel(lcv).isEmpty());
            }
        }
     }

}
