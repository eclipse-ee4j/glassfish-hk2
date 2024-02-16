/*
 * Copyright (c) 2015, 2024 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2018 Payara Foundation
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

package org.glassfish.hk2.extras.operation.internal;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.extras.operation.OperationHandle;
import org.glassfish.hk2.extras.operation.OperationIdentifier;
import org.glassfish.hk2.extras.operation.OperationState;

/**
 * @author jwells
 *
 */
public class OperationHandleImpl<T extends Annotation> implements OperationHandle<T> {
    private final SingleOperationManager<T> parent;
    private final OperationIdentifier<T> identifier;
    private final ReentrantLock lock = new ReentrantLock();
    private final ReentrantLock operationLock;
    private OperationState state;
    private final HashSet<Long> activeThreads = new HashSet<Long>();
    
    // Not controlled by operationLock
    private Object userData;
    
    /* package */ OperationHandleImpl(
            SingleOperationManager<T> parent,
            OperationIdentifier<T> identifier,
            ReentrantLock operationLock,
            ServiceLocator locator) {
        this.parent = parent;
        this.identifier = identifier;
        this.operationLock = operationLock;
        this.state = OperationState.SUSPENDED;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.extras.operation.OperationHandle#getIdentifier()
     */
    @Override
    public OperationIdentifier<T> getIdentifier() {
        return identifier;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.extras.operation.OperationHandle#getState()
     */
    @Override
    public OperationState getState() {
        operationLock.lock();
        try {
            return state;
        } finally {
            operationLock.unlock();
        }
    }
    
    /**
     * operationLock must be held
     */
    /* package */ void shutdownByFiat() {
        state = OperationState.CLOSED;
    }
    
    private void checkState() {
        operationLock.lock();
        try {
            if (OperationState.CLOSED.equals(state)) {
                throw new IllegalStateException(this + " is closed");
            }
        } finally {
            operationLock.unlock();
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.extras.operation.OperationHandle#getActiveThreads()
     */
    @Override
    public Set<Long> getActiveThreads() {
        operationLock.lock();
        try {
            return Collections.unmodifiableSet(activeThreads);
        } finally {
            operationLock.unlock();
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.extras.operation.OperationHandle#suspend(long)
     */
    @Override
    public void suspend(long threadId) {
        operationLock.lock();
        try {
            if (OperationState.CLOSED.equals(state)) return;
            
            parent.disassociateThread(threadId, this);
            
            if (activeThreads.remove(threadId)) {
                if (activeThreads.isEmpty()) {
                    state = OperationState.SUSPENDED;
                }
            }
        } finally {
            operationLock.unlock();
        }
        
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.extras.operation.OperationHandle#suspend()
     */
    @Override
    public void suspend() {
        suspend(Thread.currentThread().getId());
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.extras.operation.OperationHandle#resume(long)
     */
    @Override
    public void resume(long threadId) throws IllegalStateException {
        operationLock.lock();
        try {
            checkState();
            
            if (activeThreads.contains(threadId)) return;
            
            // Check parent
            OperationHandleImpl<T> existing = parent.getCurrentOperationOnThisThread(threadId);
            if (existing != null) {
                throw new IllegalStateException("The operation " + existing + " is active on " + threadId);
            }
            
            if (activeThreads.isEmpty()) {
                state = OperationState.ACTIVE;
            }
            activeThreads.add(threadId);
            
            parent.associateWithThread(threadId, this);
        } finally {
            operationLock.unlock();
        }
        
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.extras.operation.OperationHandle#resume()
     */
    @Override
    public void resume() throws IllegalStateException {
        resume(Thread.currentThread().getId());
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.extras.operation.OperationHandle#closeOperation()
     */
    @Override
    public void close() {
        // outside the lock
        parent.disposeAllOperationServices(this);
        
        operationLock.lock();
        try {
            for (long threadId : activeThreads) {
                parent.disassociateThread(threadId, this);
            }
            
            activeThreads.clear();
            state = OperationState.CLOSED;
            parent.closeOperation(this);
        } finally {
            operationLock.unlock();
        }
        
        
    }
    
    @Override
    public void closeOperation() {
        close();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.extras.operation.OperationHandle#getOperationData()
     */
    @Override
    public Object getOperationData() {
        lock.lock();
        try {
            return userData;
        } finally {
            lock.unlock();
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.extras.operation.OperationHandle#setOperationData(java.lang.Object)
     */
    @Override
    public void setOperationData(Object data) {
        lock.lock();
        try {
            userData = data;
        } finally {
            lock.unlock();
        }
    }
    
    @Override
    public int hashCode() {
        return identifier.hashCode();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof OperationHandleImpl)) return false;
        
        return identifier.equals(((OperationHandleImpl<T>) o).identifier);
    }
    
    @Override
    public String toString() {
        return "OperationHandleImpl(" + identifier + "," + System.identityHashCode(this) + ")";
    }

}
