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

package org.glassfish.hk2.configuration.hub.internal;

import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.TwoPhaseResource;
import org.glassfish.hk2.api.TwoPhaseTransactionData;
import org.glassfish.hk2.configuration.hub.api.BeanDatabaseUpdateListener;
import org.glassfish.hk2.configuration.hub.api.Change;
import org.glassfish.hk2.configuration.hub.api.Instance;
import org.glassfish.hk2.configuration.hub.api.Type;
import org.glassfish.hk2.configuration.hub.api.WriteableBeanDatabase;
import org.glassfish.hk2.configuration.hub.api.WriteableType;

/**
 * @author jwells
 *
 */
public class WriteableBeanDatabaseImpl implements WriteableBeanDatabase {
    private final long baseRevision;
    private final HashMap<String, WriteableTypeImpl> types = new HashMap<String, WriteableTypeImpl>();
    private final HubImpl hub;
    private final TwoPhaseResourceImpl resource = new TwoPhaseResourceImpl();

    private final ReentrantLock lock = new ReentrantLock();
    private final LinkedList<Change> changes = new LinkedList<Change>();
    private final LinkedList<WriteableTypeImpl> removedTypes = new LinkedList<WriteableTypeImpl>();
    private boolean committed = false;
    private Object commitMessage = null;
    
    /* package */ WriteableBeanDatabaseImpl(HubImpl hub, BeanDatabaseImpl currentDatabase) {
        this.hub = hub;
        baseRevision = currentDatabase.getRevision();
        
        for (Type type : currentDatabase.getAllTypes()) {
            types.put(type.getName(), new WriteableTypeImpl(this, (TypeImpl) type));
        }
        
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.BeanDatabase#getAllTypes()
     */
    @Override
    public Set<Type> getAllTypes() {
        lock.lock();
        try {
            return Collections.unmodifiableSet(new HashSet<Type>(types.values()));
        } finally {
            lock.unlock();
        }
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.WriteableBeanDatabase#getAllWriteableTypes()
     */
    @Override
    public Set<WriteableType> getAllWriteableTypes() {
        return Collections.unmodifiableSet(new HashSet<WriteableType>(types.values()));
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.BeanDatabase#getType(java.lang.String)
     */
    @Override
    public Type getType(String type) {
        lock.lock();
        try {
            return types.get(type);
        } finally {
            lock.unlock();
        }
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.BeanDatabase#getInstance(java.lang.String, java.lang.Object)
     */
    @Override
    public Instance getInstance(String type, String instanceKey) {
        lock.lock();
        try {
            Type t = getType(type);
            if (t == null) return null;
            
            return t.getInstance(instanceKey);
        } finally {
            lock.unlock();
        }
    }
    
    private void checkState() {
        if (committed) throw new IllegalStateException("This database has already been committed");
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.WriteableBeanDatabase#addType(java.lang.String)
     */
    @Override
    public WriteableType addType(String typeName) {
        lock.lock();
        try {
            if (typeName == null) throw new IllegalArgumentException();
            checkState();
            
            WriteableTypeImpl wti = new WriteableTypeImpl(this, typeName);
            
            changes.add(new ChangeImpl(Change.ChangeCategory.ADD_TYPE,
                                       wti,
                                       null,
                                       null,
                                       null,
                                       null));
            
            types.put(typeName, wti);
            
            return wti;
        } finally {
            lock.unlock();
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.WriteableBeanDatabase#removeType(java.lang.String)
     */
    @Override
    public Type removeType(String typeName) {
        lock.lock();
        try {
            if (typeName == null) throw new IllegalArgumentException();
            checkState();
            
            WriteableTypeImpl retVal = types.remove(typeName);
            if (retVal == null) return null;
            
            Map<String, Instance> instances = retVal.getInstances();
            for (String key : new HashSet<String>(instances.keySet())) {
                retVal.removeInstance(key);
            }
            
            changes.add(new ChangeImpl(Change.ChangeCategory.REMOVE_TYPE,
                    retVal,
                    null,
                    null,
                    null,
                    null));
            
            removedTypes.add(retVal);
            
            return retVal;
        } finally {
            lock.unlock();
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.WriteableBeanDatabase#getWriteableType(java.lang.String)
     */
    @Override
    public WriteableType getWriteableType(String typeName) {
        lock.lock();
        try {
            checkState();
            return types.get(typeName);
        } finally {
            lock.unlock();
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.WriteableBeanDatabase#findOrAddWriteableType(java.lang.String)
     */
    @Override
    public WriteableType findOrAddWriteableType(String typeName) {
        lock.lock();
        try {
            if (typeName == null) throw new IllegalArgumentException();
            checkState();
            
            WriteableTypeImpl wti = types.get(typeName);
            if (wti == null) {
                return addType(typeName);
            }
            
            return wti;
        } finally {
            lock.unlock();
        }
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.WriteableBeanDatabase#commit()
     */
    @Override
    public void commit() {
        Object defaultCommit;
        lock.lock();
        try {
            defaultCommit = commitMessage;
        } finally {
            lock.unlock();
        }
        
        commit(defaultCommit);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.WriteableBeanDatabase#commit()
     */
    @Override
    public void commit(Object commitMessage) {
        lock.lock();
        try {
            checkState();
        
            committed = true;
        } finally {
            lock.unlock();
        }
        
        // Outside of lock
        hub.setCurrentDatabase(this, commitMessage, changes);
        
        for (WriteableTypeImpl removedType : removedTypes) {
            removedType.getHelper().dispose();
        }
        
        removedTypes.clear();
    }
    
    /* package */ long getBaseRevision() {
        return baseRevision;
    }
    
    /* package */ void addChange(Change change) {
        lock.lock();
        try {
            changes.add(change);
        } finally {
            lock.unlock();
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.BeanDatabase#dumpDatabase()
     */
    @Override
    public void dumpDatabase() {
        dumpDatabase(System.err);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.BeanDatabase#dumpDatabase(java.io.PrintStream)
     */
    @Override
    public void dumpDatabase(PrintStream output) {
        lock.lock();
        try {
            Utilities.dumpDatabase(this, output);
        } finally {
            lock.unlock();
        }
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.BeanDatabase#dumpDatabaseAsString()
     */
    @Override
    public String dumpDatabaseAsString() {
        return Utilities.dumpDatabaseAsString(this);
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.WriteableBeanDatabase#getTwoPhaseResource()
     */
    @Override
    public TwoPhaseResource getTwoPhaseResource() {
        return resource;
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.WriteableBeanDatabase#getCommitMessage()
     */
    @Override
    public Object getCommitMessage() {
        lock.lock();
        try {
            return commitMessage;
        } finally {
            lock.unlock();
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.WriteableBeanDatabase#setCommitMessage(java.lang.Object)
     */
    @Override
    public void setCommitMessage(Object commitMessage) {
        lock.lock();
        try {
            this.commitMessage = commitMessage;
        } finally {
            lock.unlock();
        }
    }

    

    private class TwoPhaseResourceImpl implements TwoPhaseResource {
        private LinkedList<BeanDatabaseUpdateListener> completedListeners;

        /* (non-Javadoc)
         * @see org.glassfish.hk2.api.TwoPhaseResource#prepareDynamicConfiguration(org.glassfish.hk2.api.TwoPhaseTransactionData)
         */
        @Override
        public void prepareDynamicConfiguration(
                TwoPhaseTransactionData dynamicConfiguration)
                throws MultiException {
            Object defaultCommit;
            WriteableBeanDatabaseImpl.this.lock.lock();
            try {
                checkState();
                
                committed = true;
                
                defaultCommit = commitMessage;
            } finally {
                WriteableBeanDatabaseImpl.this.lock.unlock();
            }
            
            // Outside of lock
            completedListeners = hub.prepareCurrentDatabase(WriteableBeanDatabaseImpl.this, defaultCommit, changes);
        }

        /* (non-Javadoc)
         * @see org.glassfish.hk2.api.TwoPhaseResource#activateDynamicConfiguration(org.glassfish.hk2.api.TwoPhaseTransactionData)
         */
        @Override
        public void activateDynamicConfiguration(
                TwoPhaseTransactionData dynamicConfiguration) {
            LinkedList<BeanDatabaseUpdateListener> completedListeners = this.completedListeners;
            this.completedListeners = null;
            
            Object defaultCommit;
            WriteableBeanDatabaseImpl.this.lock.lock();
            try {
                defaultCommit = commitMessage;
            } finally {
                WriteableBeanDatabaseImpl.this.lock.unlock();
            }
            
            hub.activateCurrentDatabase(WriteableBeanDatabaseImpl.this, defaultCommit, changes, completedListeners);
            
            for (WriteableTypeImpl removedType : removedTypes) {
                removedType.getHelper().dispose();
            }
            
            removedTypes.clear();
            
        }

        /* (non-Javadoc)
         * @see org.glassfish.hk2.api.TwoPhaseResource#rollbackDynamicConfiguration(org.glassfish.hk2.api.TwoPhaseTransactionData)
         */
        @Override
        public void rollbackDynamicConfiguration(
                TwoPhaseTransactionData dynamicConfiguration) {
            LinkedList<BeanDatabaseUpdateListener> completedListeners = this.completedListeners;
            this.completedListeners = null;
            
            Object defaultCommit;
            WriteableBeanDatabaseImpl.this.lock.lock();
            try {
                defaultCommit = commitMessage;
            } finally {
                WriteableBeanDatabaseImpl.this.lock.unlock();
            }

            hub.rollbackCurrentDatabase(WriteableBeanDatabaseImpl.this, defaultCommit, changes, completedListeners);
            
            for (WriteableTypeImpl removedType : removedTypes) {
                removedType.getHelper().dispose();
            }
            
            removedTypes.clear();
        }
        
    }
    
    private String getChanges() {
        int lcv = 1;
        StringBuffer sb = new StringBuffer("\n");
        for (Change change : changes) {
            sb.append(lcv + ". " + change + "\n");
            lcv++;
        }
        
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return "WriteableBeanDatabaseImpl(" + baseRevision + ",changes=" + getChanges() + "," + System.identityHashCode(this) + ")";
    }

    
}
