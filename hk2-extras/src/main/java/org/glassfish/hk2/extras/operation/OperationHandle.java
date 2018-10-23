/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.extras.operation;

import java.io.Closeable;
import java.lang.annotation.Annotation;
import java.util.Set;

import org.jvnet.hk2.annotations.Contract;

/**
 * This handle is used to associate or dis-associate threads with
 * Operations.  It can also be used to close this Operation.
 * Every OperationHandle will be added as an HK2 service in
 * the Operations scope, and hence can be injected into other
 * HK2 services
 * 
 * @author jwells
 *
 */
@Contract
public interface OperationHandle<T extends Annotation> extends Closeable {
    /**
     * Returns a unique identifier for this operation
     * 
     * @return A non-null unique identifier for this
     * operation
     */
    public OperationIdentifier<T> getIdentifier();
    
    /**
     * Gets the current state of this operation
     * 
     * @return The current state of this operation
     */
    public OperationState getState();
    
    /**
     * Gets a set of threads upon which this Operation is active
     * 
     * @return The set of threads upon which this Operation is active
     */
    public Set<Long> getActiveThreads();
    
    /**
     * Suspends this operation on the given thread id.  If this Operation
     * is not associated with the given threadId this method does nothing
     * 
     * @param threadId The thread on which to suspend this operation
     */
    public void suspend(long threadId);
    
    /**
     * Suspends this operation on the current thread.    If this Operation
     * is not associated with the current threadId this method does nothing
     */
    public void suspend();
    
    /**
     * Resumes this operation on the given thread id.  If this Operation
     * is already associated with the given threadId this method does
     * nothing
     * 
     * @param threadId The thread on which to resume this operation
     * @throws IllegalStateException if the Operation is closed or
     * if the given thread is associated with a different Operation
     * of the same type
     */
    public void resume(long threadId) throws IllegalStateException;
    
    /**
     * Resumes this operation on the current thread.  If this Operation
     * is already associated with the current thread this method does
     * nothing
     * 
     * @throws IllegalStateException if the Operation is closed or
     * if the current thread is associated with a different Operation
     * of the same type
     */
    public void resume() throws IllegalStateException;
    
    /**
     * Suspends this Operation on all threads where it is associated
     * and closes the operation.  All resume calls on this handle after
     * this is called will throw IllegalStateException.  If this handle
     * is already closed this method does nothing
     * @see #close()
     * @deprecated replaced by close()
     */
    default public void closeOperation() { close(); };
    
    /**
     * Suspends this Operation on all threads where it is associated
     * and closes the operation.  All resume calls on this handle after
     * this is called will throw IllegalStateException.  If this handle
     * is already closed this method does nothing
     */
    @Override
    public void close();
    
    /**
     * Gets arbitrary Operation data to be associated
     * with this Operation
     * 
     * @return Arbitrary (possibly null) data that
     * is associated with this Operation
     */
    public Object getOperationData();
    
    /**
     * Sets arbitrary Operation data to be associated
     * with this Operation
     * 
     * @param data (possibly null) data that
     * is associated with this Operation
     */
    public void setOperationData(Object data);

}
