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

package org.glassfish.hk2.configuration.hub.test;

import java.util.List;

import org.glassfish.hk2.configuration.hub.api.BeanDatabase;
import org.glassfish.hk2.configuration.hub.api.BeanDatabaseUpdateListener;
import org.glassfish.hk2.configuration.hub.api.Change;

/**
 * @author jwells
 *
 */
public class GenericBeanDatabaseUpdateListener implements
        BeanDatabaseUpdateListener {
    private BeanDatabase originalDatabase;
    private BeanDatabase lastNewDatabase;
    private List<Change> lastSetOfChanges;
    private Object lastCommitMessage;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.BeanDatabaseUpdateListener#databaseHasChanged(org.glassfish.hk2.configuration.hub.api.BeanDatabase, java.util.List)
     */
    @Override
    public void prepareDatabaseChange(
            BeanDatabase originalDatabase,
            BeanDatabase newDatabase,
            Object commitMessage,
            List<Change> changes) {
        this.originalDatabase = originalDatabase;
        lastNewDatabase = newDatabase;
        lastSetOfChanges = changes;
        lastCommitMessage = commitMessage;
    }
    
    public BeanDatabase getOriginalDatabase() {
        return originalDatabase;
    }
    
    public BeanDatabase getLastNewDatabase() {
        return lastNewDatabase;
    }
    
    public List<Change> getLastSetOfChanges() {
        return lastSetOfChanges;
    }
    
    public Object getLastCommitMessage() {
        return lastCommitMessage;
    }
    
    public void clear() {
        lastNewDatabase = null;
        lastSetOfChanges = null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.BeanDatabaseUpdateListener#commitDatabaseChange(org.glassfish.hk2.configuration.hub.api.BeanDatabase, org.glassfish.hk2.configuration.hub.api.BeanDatabase, java.lang.Object, java.util.List)
     */
    @Override
    public void commitDatabaseChange(BeanDatabase oldDatabase,
            BeanDatabase currentDatabase, Object commitMessage,
            List<Change> changes) {
        // Do nothing
        
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.BeanDatabaseUpdateListener#rollbackDatabaseChange(org.glassfish.hk2.configuration.hub.api.BeanDatabase, org.glassfish.hk2.configuration.hub.api.BeanDatabase, java.lang.Object, java.util.List)
     */
    @Override
    public void rollbackDatabaseChange(BeanDatabase currentDatabase,
            BeanDatabase proposedDatabase, Object commitMessage,
            List<Change> changes) {
        // Do nothing
        
    }

}
