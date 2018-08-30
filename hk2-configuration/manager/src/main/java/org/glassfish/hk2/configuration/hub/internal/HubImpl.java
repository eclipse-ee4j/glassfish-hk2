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

package org.glassfish.hk2.configuration.hub.internal;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.IterableProvider;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.Visibility;
import org.glassfish.hk2.configuration.hub.api.BeanDatabase;
import org.glassfish.hk2.configuration.hub.api.BeanDatabaseUpdateListener;
import org.glassfish.hk2.configuration.hub.api.Change;
import org.glassfish.hk2.configuration.hub.api.CommitFailedException;
import org.glassfish.hk2.configuration.hub.api.Hub;
import org.glassfish.hk2.configuration.hub.api.PrepareFailedException;
import org.glassfish.hk2.configuration.hub.api.RollbackFailedException;
import org.glassfish.hk2.configuration.hub.api.WriteableBeanDatabase;
import org.jvnet.hk2.annotations.ContractsProvided;
import org.jvnet.hk2.annotations.Service;

/**
 * @author jwells
 *
 */
@Service
@ContractsProvided(Hub.class)
@Visibility(DescriptorVisibility.LOCAL)
public class HubImpl implements Hub {
    private static final AtomicLong revisionCounter = new AtomicLong(1);
    
    private final Object lock = new Object();
    private BeanDatabaseImpl currentDatabase = new BeanDatabaseImpl(revisionCounter.getAndIncrement());
    
    @Inject
    private IterableProvider<BeanDatabaseUpdateListener> listeners;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.Hub#getCurrentDatabase()
     */
    @Override
    public BeanDatabase getCurrentDatabase() {
        synchronized (lock) {
            return currentDatabase;
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.Hub#getWriteableDatabaseCopy()
     */
    @Override
    public WriteableBeanDatabase getWriteableDatabaseCopy() {
        synchronized (lock) {
            return new WriteableBeanDatabaseImpl(this, currentDatabase);
        }
    }
    
    private int inTransaction = 0;
    
    /* package */ LinkedList<BeanDatabaseUpdateListener> prepareCurrentDatabase(WriteableBeanDatabaseImpl writeableDatabase, Object commitMessage, List<Change> changes) {
        synchronized (lock) {
            if (inTransaction > 0) {
                throw new IllegalStateException("This Hub is already in a transaction");
            }
            
            long currentRevision = currentDatabase.getRevision();
            long writeRevision = writeableDatabase.getBaseRevision();
            
            if (currentRevision != writeRevision) {
                throw new IllegalStateException("commit was called on a WriteableDatabase but the current database has changed after that copy was made");
            }
            
            LinkedList<BeanDatabaseUpdateListener> completedListeners = new LinkedList<BeanDatabaseUpdateListener>();
            for (BeanDatabaseUpdateListener listener : listeners) {
                try {
                    listener.prepareDatabaseChange(currentDatabase, writeableDatabase, commitMessage, changes);
                    completedListeners.add(listener);
                }
                catch (Throwable th) {
                    // Rollback time
                    MultiException throwMe = new MultiException(new PrepareFailedException(th));
                    
                    for (BeanDatabaseUpdateListener completedListener : completedListeners) {
                        try {
                            completedListener.rollbackDatabaseChange(currentDatabase, writeableDatabase, commitMessage, changes);
                        }
                        catch (Throwable rollTh) {
                            throwMe.addError(new RollbackFailedException(rollTh));
                        }
                    }
                    
                    throw throwMe;
                }
            }
            
            inTransaction++;
            
            return completedListeners;
        }
    }
    
    /* package */ void activateCurrentDatabase(WriteableBeanDatabaseImpl writeableDatabase, Object commitMessage, List<Change> changes,
            LinkedList<BeanDatabaseUpdateListener> completedListeners) {
        synchronized (lock) {
            inTransaction--;
            if (inTransaction < 0) inTransaction = 0;
            
            List<BeanDatabaseUpdateListener> completed = completedListeners;
            completedListeners = null;
            
            if (completed == null) completed = Collections.emptyList();
            
            // success!
            BeanDatabaseImpl oldDatabase = currentDatabase;
            currentDatabase = new BeanDatabaseImpl(revisionCounter.getAndIncrement(), writeableDatabase);
            
            MultiException commitError = null;
            for (BeanDatabaseUpdateListener completedListener : completed) {
                try {
                    completedListener.commitDatabaseChange(oldDatabase, currentDatabase, commitMessage, changes);
                }
                catch (Throwable th) {
                    if (commitError == null) {
                        commitError = new MultiException(new CommitFailedException(th));
                    }
                    else {
                        commitError.addError(new CommitFailedException(th));
                    }
                }
            }
            
            if (commitError != null) throw commitError;
        }
    }
    
    /* package */ void rollbackCurrentDatabase(WriteableBeanDatabaseImpl writeableDatabase, Object commitMessage, List<Change> changes,
            LinkedList<BeanDatabaseUpdateListener> completedListeners) {
        synchronized (lock) {
            inTransaction--;
            if (inTransaction < 0) inTransaction = 0;
            
            List<BeanDatabaseUpdateListener> completed = completedListeners;
            completedListeners = null;
            
            if (completed == null) completed = Collections.emptyList();
            
            MultiException rollbackError = null;
            for (BeanDatabaseUpdateListener completedListener : completed) {
                try {
                    completedListener.rollbackDatabaseChange(currentDatabase, writeableDatabase, commitMessage, changes);
                }
                catch (Throwable th) {
                    if (rollbackError == null) {
                        rollbackError = new MultiException(new RollbackFailedException(th));
                    }
                    else {
                        rollbackError.addError(new RollbackFailedException(th));
                    }
                }
            }
            
            if (rollbackError != null) throw rollbackError;
        }
    }
    
    /* package */ void setCurrentDatabase(WriteableBeanDatabaseImpl writeableDatabase, Object commitMessage, List<Change> changes) {
        LinkedList<BeanDatabaseUpdateListener> completedListeners = prepareCurrentDatabase(writeableDatabase, commitMessage, changes);
        activateCurrentDatabase(writeableDatabase, commitMessage, changes, completedListeners);
    }
}
