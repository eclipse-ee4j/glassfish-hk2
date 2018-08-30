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

package org.glassfish.hk2.runlevel.tests.sorter;

import java.util.List;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.runlevel.RunLevelController;
import org.glassfish.hk2.runlevel.tests.utilities.Utilities;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class SorterTest {
    /* package */ final static String FOO = "foo";
    /* package */ final static String BAR = "bar";
    /* package */ final static String BAZ = "baz";
    
    /**
     * Tests that a sorter changes the order
     */
    @Test
    public void testBasicSort() {
        ServiceLocator locator = Utilities.getServiceLocator(Foo.class,
                Bar.class,
                Baz.class,
                RecorderService.class);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        controller.setMaximumUseableThreads(1);
        
        // Ensure the order without the sorter is as expected
        controller.proceedTo(1);
        
        RecorderService recorder = locator.getService(RecorderService.class);
        
        {
            List<String> withoutSorter = recorder.getRecord();
            Assert.assertEquals(3, withoutSorter.size());
        
            Assert.assertEquals(FOO, withoutSorter.get(0));
            Assert.assertEquals(BAR, withoutSorter.get(1));
            Assert.assertEquals(BAZ, withoutSorter.get(2));
        }
        
        ServiceLocatorUtilities.addClasses(locator, BazBarFooSorter.class);
        
        controller.proceedTo(0);
        
        recorder.clear();
        
        controller.proceedTo(1);
        
        {
            List<String> withSorter = recorder.getRecord();
            Assert.assertEquals(3, withSorter.size());
        
            Assert.assertEquals(BAZ, withSorter.get(0));
            Assert.assertEquals(BAR, withSorter.get(1));
            Assert.assertEquals(FOO, withSorter.get(2));
        }
        
    }
    
    /**
     * Tests that a sorter changes the order
     */
    @Test
    public void testMultipleSorters() {
        ServiceLocator locator = Utilities.getServiceLocator(Foo.class,
                Bar.class,
                Baz.class,
                BazBarFooSorter.class,
                BarFooBazSorter.class,
                RecorderService.class);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        controller.setMaximumUseableThreads(1);
        
        // Ensure the order without the sorter is as expected
        controller.proceedTo(1);
        
        RecorderService recorder = locator.getService(RecorderService.class);
        
        {
            List<String> withoutSorter = recorder.getRecord();
            Assert.assertEquals(3, withoutSorter.size());
        
            Assert.assertEquals(BAR, withoutSorter.get(0));
            Assert.assertEquals(FOO, withoutSorter.get(1));
            Assert.assertEquals(BAZ, withoutSorter.get(2));
        }
    }

}
