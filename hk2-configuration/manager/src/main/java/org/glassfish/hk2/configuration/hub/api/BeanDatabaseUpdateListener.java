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

import java.util.List;

import org.jvnet.hk2.annotations.Contract;

/**
 * This is a listener that is notified when changes are made to
 * the current {@link BeanDatabase}
 * 
 * @author jwells
 */
@Contract
public interface BeanDatabaseUpdateListener {
    /**
     * This method will be called prior to the bean database being updated.
     * If this method throws an exception subsequent listeners prepare methods
     * will not be called and the rollback method of any listeners that had
     * run previously will be called and the proposedDatabase will not become
     * the current database.  If all the registered bean update listeners
     * prepare methods return normally then the proposedDatabase will
     * become the current database
     * 
     * @param currentDatabase The bean database that is current in effect
     * @param proposedDatabase The bean database that will go into effect
     * @param commitMessage An object passed to the commit method in a dynamic change
     * @param changes The changes that were made to the current database
     */
    public void prepareDatabaseChange(BeanDatabase currentDatabase, BeanDatabase proposedDatabase, Object commitMessage, List<Change> changes);
    
    /**
     * This method is called after the change of database has already happened.
     * If this method throws an exception subsequent listeners commit methods
     * will be called, but the {@link WriteableBeanDatabase#commit()} method
     * will throw an exception, indicating a possibly inconsistent state
     * 
     * @param oldDatabase The database from which the current database was derived
     * @param currentDatabase The current bean database
     * @param commitMessage An object passed to the commit method in a dynamic change
     * @param changes The changes that were made to arrive at the current database
     */
    public void commitDatabaseChange(BeanDatabase oldDatabase, BeanDatabase currentDatabase, Object commitMessage, List<Change> changes);
    
    /**
     * If any {@link #prepareDatabaseChange(BeanDatabase, BeanDatabase, Object, List)}
     * throws an exception this method will be called on all listeners whose
     * {@link #prepareDatabaseChange(BeanDatabase, BeanDatabase, Object, List)} had already
     * been succesfully called.  If this method throws an exception subsequent listeners
     * rollback methods will be called and the exception will be returned in the exception
     * thrown to the caller of {@link WriteableBeanDatabase#commit()} method
     * 
     * @param currentDatabase The bean database that is current in effect
     * @param proposedDatabase The bean database that was to go into effect (but which will not)
     * @param commitMessage An object passed to the commit method in a dynamic change
     * @param changes The changes that were proposed to be made to the current database
     */
    public void rollbackDatabaseChange(BeanDatabase currentDatabase, BeanDatabase proposedDatabase, Object commitMessage, List<Change> changes);
}
