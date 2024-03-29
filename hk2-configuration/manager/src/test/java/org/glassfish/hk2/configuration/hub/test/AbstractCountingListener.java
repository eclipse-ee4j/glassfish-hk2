/*
 * Copyright (c) 2014, 2024 Oracle and/or its affiliates. All rights reserved.
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
import java.util.concurrent.locks.ReentrantLock;

import jakarta.inject.Singleton;

import org.glassfish.hk2.configuration.hub.api.BeanDatabase;
import org.glassfish.hk2.configuration.hub.api.BeanDatabaseUpdateListener;
import org.glassfish.hk2.configuration.hub.api.Change;

/**
 * @author jwells
 *
 */
@Singleton
public class AbstractCountingListener implements BeanDatabaseUpdateListener {
    private final ReentrantLock lock = new ReentrantLock();
    private int prepareCalled;
    private int rollbackCalled;
    private int commitCalled;
    
    public int getNumPreparesCalled() {
        lock.lock();
        try {
            return prepareCalled;
        } finally {
            lock.unlock();
        }
    }
    
    public int getNumCommitsCalled() {
        lock.lock();
        try {
            return commitCalled;
        } finally {
            lock.unlock();
        }
    }
    
    public int getNumRollbackCalled() {
        lock.lock();
        try {
            return rollbackCalled;
        } finally {
            lock.unlock();
        }
    }
    
    public void prepareAction() {
    }
    
    public void commitAction() {
    }
    
    public void rollbackAction() {
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.BeanDatabaseUpdateListener#prepareDatabaseChange(org.glassfish.hk2.configuration.hub.api.BeanDatabase, org.glassfish.hk2.configuration.hub.api.BeanDatabase, java.lang.Object, java.util.List)
     */
    @Override
    public void prepareDatabaseChange(BeanDatabase currentDatabase,
            BeanDatabase proposedDatabase, Object commitMessage,
            List<Change> changes) {
        lock.lock();
        try {
            prepareCalled++;
            prepareAction();
        } finally {
            lock.unlock();
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.BeanDatabaseUpdateListener#commitDatabaseChange(org.glassfish.hk2.configuration.hub.api.BeanDatabase, org.glassfish.hk2.configuration.hub.api.BeanDatabase, java.lang.Object, java.util.List)
     */
    @Override
    public void commitDatabaseChange(BeanDatabase oldDatabase,
            BeanDatabase currentDatabase, Object commitMessage,
            List<Change> changes) {
        commitCalled++;
        commitAction();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.BeanDatabaseUpdateListener#rollbackDatabaseChange(org.glassfish.hk2.configuration.hub.api.BeanDatabase, org.glassfish.hk2.configuration.hub.api.BeanDatabase, java.lang.Object, java.util.List)
     */
    @Override
    public void rollbackDatabaseChange(BeanDatabase currentDatabase,
            BeanDatabase proposedDatabase, Object commitMessage,
            List<Change> changes) {
        rollbackCalled++;
        rollbackAction();
    }

}
