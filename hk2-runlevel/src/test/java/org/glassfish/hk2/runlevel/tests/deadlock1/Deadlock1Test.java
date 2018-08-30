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

package org.glassfish.hk2.runlevel.tests.deadlock1;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.runlevel.RunLevelController;
import org.glassfish.hk2.runlevel.tests.utilities.Utilities;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class Deadlock1Test {
    /**
     * This test ensures that spending forever in an onProgress does not hold
     * up other threads from calling cancel on the RLC
     */
	@Test
	public void testOnProgressCancelDeadlock() {
		ServiceLocator locator = Utilities.getServiceLocator(
		        DeadLock1Listener.class);
		
		RunLevelController rlc = locator.getService(RunLevelController.class);
		rlc.proceedTo(1);
	}
}
