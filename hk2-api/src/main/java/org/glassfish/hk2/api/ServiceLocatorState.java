/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.api;

/**
 * The possible states in which a service locator can be in.  Portable code
 * should assume that future states will be added
 * 
 * @author jwells
 *
 */
public enum ServiceLocatorState {
    /**
     * In this state the ServiceLocator is able to service requests, do injections and
     * generally operate in a normal manner
     */
    RUNNING,
    
    /**
     * In this state the ServiceLocator has been shutdown, and only a few selected operations
     * will not throw an IllegalStateException
     */
    SHUTDOWN

}
