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

package org.glassfish.hk2.runlevel.tests.failmeagain;

import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.runlevel.RunLevelController;
import org.glassfish.hk2.runlevel.tests.utilities.Utilities;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class FailMeAgainTest {
    
    /**
     * Tests that the grumpy service is only invoked once.  This
     * thread is probabilistic in terms of failure.  That being said,
     * I've only ever seen it reach iteration 77 before it failed,
     * and we are running it 1000 times, so we can be pretty confident
     * this test will catch the problem
     */
    @Test
    public void testGrumpyOnlyCalledOnce() {
        ServiceLocator locator = Utilities.getServiceLocator(
                DependsOnGrumpyOne.class,
                DependsOnGrumpyTwo.class,
                GrumpyService.class);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        
        controller.setMaximumUseableThreads(2);
        
        for (int lcv = 0; lcv < 1000; lcv++) {
            GrumpyService.reset();
            
            try {
                controller.proceedTo(5);
                Assert.fail("Should have failed, grumpy throws");
            }
            catch (MultiException re) {
                // success
            }
            
            Assert.assertEquals("Failed on iteration " + lcv, 1, GrumpyService.getCalled());
            
            controller.proceedTo(4);
        }
        
        
    }

}
