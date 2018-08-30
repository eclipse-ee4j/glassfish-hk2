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

import javax.annotation.PostConstruct;

import org.glassfish.hk2.runlevel.RunLevel;

/**
 * This service does NOT have any explicit (hk2) dependencies, though it
 * does free up ServiceB to run when its postConstruct goes.
 * <p>
 * This service is of lower rank than ServiceA or ServiceB and hence will
 * not be farmed out initially to the two worker threads.  One of those
 * worker threads should notice that it would block and hence give up and
 * go on to this service, which will break up the logjam.
 * 
 * @author jwells
 *
 */
@RunLevel(5)
public class ServiceC {
    @SuppressWarnings("unused")
    @PostConstruct
    private void postConstruct() {
        ServiceB.goAhead();
    }

}
