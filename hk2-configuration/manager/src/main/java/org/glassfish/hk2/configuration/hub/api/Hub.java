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

package org.glassfish.hk2.configuration.hub.api;

import org.jvnet.hk2.annotations.Contract;

/**
 * The central repository of configuration objects in the system.
 * Configuration objects are organized by type, and within
 * that type keyed instances of the configuration object.
 * <p>
 * A configuration object can be a java bean following
 * the java bean standard.  A configuration object
 * can also be a bean-like Map, which is a Map&lt;String,Object&gt;
 * that has as keys the names of the properties and as
 * values value that property should take
 * 
 * @author jwells
 *
 */
@Contract
public interface Hub {
    /**
     * Gets the current database running in the system
     * 
     * @return The current database known to the Hub
     */
    public BeanDatabase getCurrentDatabase();
    
    /**
     * Creates a writeable copy of the currently running
     * database.  If the {@link WriteableBeanDatabase#commit()}
     * method is called (and no other {@link WriteableBeanDatabase#commit()}
     * method has been called) then a read-only copye of the
     * {@link WriteableBeanDatabase} will become the current database.
     * There is no requirement to eventually call the
     * {@link WriteableBeanDatabase#commit()} method
     * 
     * @return A writeable copy of the current database
     */
    public WriteableBeanDatabase getWriteableDatabaseCopy();
}
