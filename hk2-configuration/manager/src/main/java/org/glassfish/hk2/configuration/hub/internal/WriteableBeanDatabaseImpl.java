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

import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

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
    public synchronized Set<Type> getAllTypes() {
        return Collections.unmodifiableSet(new HashSet<Type>(types.values()));
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
    public synchronized Type getType(String type) {
        return types.get(type);
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.BeanDatabase#getInstance(java.lang.String, java.lang.Object)
     */
    @Override
    public synchronized Instance getInstance(String type, String instanceKey) {
        Type t = getType(type);
        if (t == null) return null;
        
        return t.getInstance(instanceKey);
    }
    
    private void checkState() {
        if (committed) throw new IllegalStateException("This database has already been committed");
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.WriteableBeanDatabase#addType(java.lang.String)
     */
    @Override
    public synchronized WriteableType addType(String typeName) {
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
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.WriteableBeanDatabase#removeType(java.lang.String)
     */
    @Override
    public synchronized Type removeType(String typeName) {
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
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.WriteableBeanDatabase#getWriteableType(java.lang.String)
     */
    @Override
    public synchronized WriteableType getWriteableType(String typeName) {
        checkState();
        return types.get(typeName);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.WriteableBeanDatabase#findOrAddWriteableType(java.lang.String)
     */
    @Override
    public synchronized WriteableType findOrAddWriteableType(String typeName) {
        if (typeName == null) throw new IllegalArgumentException();
        checkState();
        
        WriteableTypeImpl wti = types.get(typeName);
        if (wti == null) {
            return addType(typeName);
        }
        
        return wti;
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.WriteableBeanDatabase#commit()
     */
    @Override
    public void commit() {
        Object defaultCommit;
        synchronized (this) {
            defaultCommit = commitMessage;
        }
        
        commit(defaultCommit);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.WriteableBeanDatabase#commit()
     */
    @Override
    public void commit(Object commitMessage) {
        synchronized (this) {
            checkState();
        
            committed = true;
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
    
    /* package */ synchronized void addChange(Change change) {
        changes.add(change);
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
    public synchronized void dumpDatabase(PrintStream output) {
        Utilities.dumpDatabase(this, output);        
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
    public synchronized Object getCommitMessage() {
        return commitMessage;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.WriteableBeanDatabase#setCommitMessage(java.lang.Object)
     */
    @Override
    public synchronized void setCommitMessage(Object commitMessage) {
        this.commitMessage = commitMessage;
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
            synchronized (WriteableBeanDatabaseImpl.this) {
                checkState();
                
                committed = true;
                
                defaultCommit = commitMessage;
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
            synchronized (WriteableBeanDatabaseImpl.this) {
                defaultCommit = commitMessage;
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
            synchronized (WriteableBeanDatabaseImpl.this) {
                defaultCommit = commitMessage;
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
