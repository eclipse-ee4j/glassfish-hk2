/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.runlevel.tests.ghost;

import java.util.List;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.runlevel.RunLevel;
import org.glassfish.hk2.runlevel.RunLevelController;
import org.glassfish.hk2.runlevel.RunLevelServiceUtilities;
import org.glassfish.hk2.runlevel.tests.utilities.Utilities;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the ability to ghost run levelsvalue
 * @author jwells
 *
 */
public class GhostRunLevelTest {
    
    /**
     * Ensures we can ghost the RunLevel annotation
     */
    @Test // @org.junit.Ignore
    public void testAddWildcardBetweenFiveAndTen() {
        ServiceLocator locator = Utilities.getServiceLocator(Registrar.class,
                ServiceFive.class,
                ServiceTen.class);
        
        ActiveDescriptor<?> desc = BuilderHelper.activeLink(ServiceWildcard.class).
                to(ServiceWildcard.class).
                in(RunLevelServiceUtilities.getRunLevelAnnotation(7)).
                build();
        
        ServiceLocatorUtilities.addOneDescriptor(locator, desc);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        
        Registrar registrar = locator.getService(Registrar.class);
        for (int lcv = 0; lcv < 3; lcv++) {
            registrar.clear();
            
            controller.proceedTo(10);
            
            {
                List<Object> services = registrar.get();
        
                Assert.assertEquals(3, services.size());
                Assert.assertEquals(ServiceFive.class, services.get(0).getClass());
                Assert.assertEquals(ServiceWildcard.class, services.get(1).getClass());
                Assert.assertEquals(ServiceTen.class, services.get(2).getClass());
            }
            
            controller.proceedTo(0);
            
            {
                List<Object> downers = registrar.getDown();
            
                Assert.assertEquals(3, downers.size());
                Assert.assertEquals(ServiceTen.class, downers.get(0).getClass());
                Assert.assertEquals(ServiceWildcard.class, downers.get(1).getClass());
                Assert.assertEquals(ServiceFive.class, downers.get(2).getClass());
            }
        }
    }
    
    /**
     * Ensures we can change the existing annotations
     */
    @Test // @org.junit.Ignore
    public void testChangeExistingAnnotations() {
        ServiceLocator locator = Utilities.getServiceLocator(Registrar.class,
                ServiceTen.class);
        
        ActiveDescriptor<?> desc15 = BuilderHelper.activeLink(ServiceFive.class).
                to(ServiceFive.class).
                in(RunLevelServiceUtilities.getRunLevelAnnotation(15)).
                build();
        
        ActiveDescriptor<?> desc5 = BuilderHelper.activeLink(ServiceFifteen.class).
                to(ServiceFifteen.class).
                in(RunLevelServiceUtilities.getRunLevelAnnotation(5)).
                build();
        
        ServiceLocatorUtilities.addOneDescriptor(locator, desc15);
        ServiceLocatorUtilities.addOneDescriptor(locator, desc5);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        
        Registrar registrar = locator.getService(Registrar.class);
        for (int lcv = 0; lcv < 3; lcv++) {
            registrar.clear();
            
            controller.proceedTo(20);
            
            {
                List<Object> services = registrar.get();
        
                Assert.assertEquals(3, services.size());
                Assert.assertEquals(ServiceFifteen.class, services.get(0).getClass());
                Assert.assertEquals(ServiceTen.class, services.get(1).getClass());
                Assert.assertEquals(ServiceFive.class, services.get(2).getClass());
            }
            
            controller.proceedTo(0);
            
            {
                List<Object> downers = registrar.getDown();
            
                Assert.assertEquals(3, downers.size());
                Assert.assertEquals(ServiceFive.class, downers.get(0).getClass());
                Assert.assertEquals(ServiceTen.class, downers.get(1).getClass());
                Assert.assertEquals(ServiceFifteen.class, downers.get(2).getClass());
            }
        }
    }
    
    /**
     * Ensures we can change the default mode of RunLevel
     */
    @Test // @org.junit.Ignore
    public void testChangeDefaultMode() {
        ServiceLocator locator = Utilities.getServiceLocator(Registrar.class);
        
        ActiveDescriptor<?> desc5 = BuilderHelper.activeLink(ServiceFive.class).
                to(ServiceFive.class).
                in(RunLevelServiceUtilities.getRunLevelAnnotation(5, RunLevel.RUNLEVEL_MODE_NON_VALIDATING)).
                build();
        
        ServiceLocatorUtilities.addOneDescriptor(locator, desc5);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        controller.proceedTo(0);
        
        // Since ServiceFive is now NOT validating, we should be able to look it up
        // without error even though the level is only zero
        
        Assert.assertNotNull(locator.getService(ServiceFive.class));
    }
    
    /**
     * Ensures we can change the default mode of RunLevel
     */
    @Test // @org.junit.Ignore
    public void testChangeNonDefaultMode() {
        ServiceLocator locator = Utilities.getServiceLocator(Registrar.class);
        
        ActiveDescriptor<?> desc10 = BuilderHelper.activeLink(ServiceTen.class).
                to(ServiceTen.class).
                in(RunLevelServiceUtilities.getRunLevelAnnotation(10, RunLevel.RUNLEVEL_MODE_VALIDATING)).
                build();
        
        ServiceLocatorUtilities.addOneDescriptor(locator, desc10);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        controller.proceedTo(0);
        
        // Since ServiceTen is now *validating*, we should be NOT be able to look it up
        
        try {
            locator.getService(ServiceTen.class);
            Assert.fail("Should have failed since it is now validating and the run level is not high enough");
        }
        catch (MultiException me) {
            // expected!
        }
    }

}
