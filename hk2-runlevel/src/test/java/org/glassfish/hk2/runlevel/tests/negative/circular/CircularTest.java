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

package org.glassfish.hk2.runlevel.tests.negative.circular;

import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.runlevel.RunLevelController;
import org.glassfish.hk2.runlevel.tests.utilities.Utilities;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class CircularTest {
    @Test(expected=MultiException.class)
    public void testCircularStartups() {
        ServiceLocator locator = Utilities.getServiceLocator(FooService.class, BarService.class);
        
        RunLevelController controller = locator.getService(RunLevelController.class);
        
        // Unfortunately the test is not deterministic with more than one thread.  With more than one thread it is possible
        // that one thread can go and be trying to create Foo, then try to create Bar.  While it tries to create Bar the
        // other thread goes ahead and starts trying to create Bar.  The first thread will go in and get stuck because it is
        // NOT the same thread trying to create Bar, and hence it'll sleep.  The second thread will sleep because Foo is
        // being attempted to be created by a different thread.  So this mechanism of cycle detection will only work in SOME cases
        controller.setMaximumUseableThreads(1);
        
        controller.proceedTo(1);
        
    }

}
