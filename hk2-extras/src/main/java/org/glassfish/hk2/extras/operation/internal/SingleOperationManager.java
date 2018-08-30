/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.extras.operation.OperationContext;
import org.glassfish.hk2.extras.operation.OperationHandle;
import org.glassfish.hk2.extras.operation.OperationIdentifier;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

/**
 * @author jwells
 *
 */
public class SingleOperationManager<T extends Annotation> {
    private final static String ID_PREAMBLE = "OperationIdentifier(";
    
    private final Object operationLock = new Object();
    private final T scope;
    private final HashMap<OperationIdentifier<T>, OperationHandleImpl<T>> openScopes = new HashMap<OperationIdentifier<T>, OperationHandleImpl<T>>();
    private final HashMap<Long, OperationHandleImpl<T>> threadToHandleMap = new HashMap<Long, OperationHandleImpl<T>>();
    private final ServiceLocator locator;
    private final OperationContext<T> context;
    private long scopedIdentifier;
    private final ActiveDescriptor<?> operationDescriptor;
    private boolean closed = false;
    
    /* package */ @SuppressWarnings("unchecked")
    SingleOperationManager(T scope,
            ServiceLocator locator) {
        this.scope = scope;
        this.locator = locator;
        
        OperationContext<T> found = null;
        for (OperationContext<T> context : locator.getAllServices(OperationContext.class)) {
            if (context.getScope().equals(scope.annotationType())) {
                found = context;
                break;
            }
        }
        
        if (found == null) {
            throw new IllegalStateException("Could not find the OperationContext for scope " + scope);
        }
        context = found;
        
        context.setOperationManager(this);
        
        OperationDescriptor<T> opDesc = new OperationDescriptor<T>(scope, this);
        
        operationDescriptor = ServiceLocatorUtilities.addOneDescriptor(locator, opDesc);
    }
    
    private OperationIdentifierImpl<T> allocateNewIdentifier() {
        return new OperationIdentifierImpl<T>(
                ID_PREAMBLE + scopedIdentifier++ + "," + scope.annotationType().getName() + ")",
                scope);
    }
    
    public OperationHandleImpl<T> createOperation() {
        
        synchronized (operationLock) {
            if (closed) {
                throw new IllegalStateException("This manager has been closed");
            }
            
            OperationIdentifierImpl<T> id = allocateNewIdentifier();
            OperationHandleImpl<T> created = new OperationHandleImpl<T>(this, id, operationLock, locator);
            
            openScopes.put(id, created);
            
            return created;
        }
    }

    /**
     * Called with the operationLock held
     * 
     * @param closeMe The non-null operation to close
     */
    /* package */ void closeOperation(OperationHandleImpl<T> closeMe) {
        openScopes.remove(closeMe.getIdentifier());
    }
    
    /**
     * Explicitly called WITHOUT the operationLock held to avoid any deadlock
     * with the context lock
     * 
     * @param closeMe The non-null operation to close
     */
    /* package */ void disposeAllOperationServices(OperationHandleImpl<T> closeMe) {
        context.closeOperation(closeMe);
    }
    
    /**
     * OperationLock must be held
     * 
     * @param threadId The threadId to associate with this handle
     * @param handle The handle to be associated with this thread
     */
    /* package */ void associateWithThread(long threadId, OperationHandleImpl<T> handle) {
        threadToHandleMap.put(threadId, handle);
    }
    
    /**
     * OperationLock must be held
     * 
     * @param threadId The threadId to disassociate with this handle
     */
    /* package */ void disassociateThread(long threadId, OperationHandleImpl<T> toRemove) {
        OperationHandleImpl<T> activeOnThread = threadToHandleMap.get(threadId);
        if (activeOnThread == null || !activeOnThread.equals(toRemove)) return;
        
        threadToHandleMap.remove(threadId);
    }
    
    /**
     * OperationLock must be held
     * 
     * @return The operation associated with the given thread
     */
    /* package */ OperationHandleImpl<T> getCurrentOperationOnThisThread(long threadId) {
        return threadToHandleMap.get(threadId);
    }
    
    /**
     * OperationLock need NOT be held
     * 
     * @return The operation associated with the current thread
     */
    public OperationHandleImpl<T> getCurrentOperationOnThisThread() {
        long threadId = Thread.currentThread().getId();
        
        synchronized (operationLock) {
            if (closed) return null;
            return getCurrentOperationOnThisThread(threadId);
        }
    }
    
    /* package */ Set<OperationHandle<T>> getAllOperations() {
        HashSet<OperationHandle<T>> retVal = new HashSet<OperationHandle<T>>();
        
        synchronized (operationLock) {
            if (closed) return Collections.emptySet();
            
            retVal.addAll(openScopes.values());
            
            return Collections.unmodifiableSet(retVal);
        }
    }
    
    /* package */ void shutdown() {
        synchronized (operationLock) {
            if (closed) return;
            closed = true;
            
            for (OperationHandleImpl<T> closeMe : openScopes.values()) {
                closeMe.shutdownByFiat();
            }
            
            openScopes.clear();
            threadToHandleMap.clear();
            
            ServiceLocatorUtilities.removeOneDescriptor(locator, operationDescriptor);
        }
    }
    
    @Override
    public String toString() {
        return "SingleOperationManager(" + scope.annotationType().getName() + ",closed=" + closed + "," + System.identityHashCode(this) + ")";
    }
}
