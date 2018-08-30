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

package org.glassfish.hk2.extras.operation;

import java.lang.annotation.Annotation;
import java.util.Set;

import org.jvnet.hk2.annotations.Contract;

/**
 * Manages HK2 operations, allowing the user to create new operations of a particular
 * type or discover the current set of active operations or find the current operation
 * on the current thread
 * <p>
 * Operations are categorized by the Annotation type.  The annotation type corresponds
 * to the annotation class used in the extension of the {@link OperationContext} which
 * defines the scope of the Operation.  Most of the methods for this service take an
 * implementation of that annotation class, which is usually implemented by extending
 * {@link org.glassfish.hk2.api.AnnotationLiteral}
 * 
 * @author jwells
 *
 */
@Contract
public interface OperationManager {
    /**
     * Creates an OperationHandle.  The returned
     * handle will not be associated with any threads.
     * The scope parameter is normally created with
     * {@link org.glassfish.hk2.api.AnnotationLiteral}
     * 
     * @param scope The scope annotation for this operation type
     * @return A non-null OperationHandle that can
     * be used to associate threads with the Operation
     */
    public <T extends Annotation> OperationHandle<T> createOperation(T scope);
    
    /**
     * Creates an OperationHandle that will be associated
     * with the thread calling this method.
     * The scope parameter is normally created with
     * {@link org.glassfish.hk2.api.AnnotationLiteral}
     * 
     * @param scope The scope annotation for this operation type
     * @return A non-null OperationHandle that can
     * be used to associate threads with the Operation
     * @throws IllegalStateException  if the current thread is
     * associated with a different Operation of the same type
     */
    public <T extends  Annotation> OperationHandle<T> createAndStartOperation(T scope);
    
    /**
     * Gets a set of all Operations that are in state
     * {@link OperationState#ACTIVE} or {@link OperationState#SUSPENDED}.
     * Operations that are in the {@link OperationState#CLOSED} state
     * are no longer tracked by the Manager.
     * The scope parameter is normally created with
     * {@link org.glassfish.hk2.api.AnnotationLiteral}
     * 
     * @param scope The scope annotation for this operation type
     * @return A non-null but possibly empty set of OperationHandles
     * that have not been closed
     */
    public <T extends Annotation> Set<OperationHandle<T>> getCurrentOperations(T scope);
    
    /**
     * Gets the current operation of scope type on the current thread.
     * The scope parameter is normally created with 
     * {@link org.glassfish.hk2.api.AnnotationLiteral}
     * 
     * @param scope The scope annotation for this operation type
     * @return The current operation of the given type on this thread.
     * May be null if there is no active operation on this thread of
     * this type
     */
    public <T extends Annotation> OperationHandle<T> getCurrentOperation(T scope);
    
    /**
     * This method will suspend all currently open operations on all threads and
     * then close them.  This will also remove all entities associated with
     * this operation type, including the OperationHandle associated with
     * this scope from the HK2 locator service registry.  Therefore this
     * mechanism of shutting down the operations should be used with care,
     * and would normally only be used when the Operation type can never
     * be used again.
     * <p>
     * The scope parameter is normally created with
     * {@link org.glassfish.hk2.api.AnnotationLiteral}
     * 
     * @param scope The scope annotation for this operation type
     */
    public void shutdownAllOperations(Annotation scope);
}
