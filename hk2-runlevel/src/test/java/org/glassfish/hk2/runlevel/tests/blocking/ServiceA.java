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

package org.glassfish.hk2.runlevel.tests.blocking;

import javax.inject.Inject;

import org.glassfish.hk2.api.Rank;
import org.glassfish.hk2.runlevel.RunLevel;

/**
 * This service has an unfortunate dependency on ServiceB,
 * which takes a long time to come up.  As both ServiceA
 * and ServiceB have a higher run level than ServiceC,
 * they should get farmed out to the two threads in the
 * run level controller.  One of them should be reported
 * as blocking, allowing ServiceC to remove the blocking
 * condition
 * 
 * @author jwells
 *
 */
@RunLevel(5)
@Rank(100)
public class ServiceA {
    @Inject
    ServiceB b;

}
