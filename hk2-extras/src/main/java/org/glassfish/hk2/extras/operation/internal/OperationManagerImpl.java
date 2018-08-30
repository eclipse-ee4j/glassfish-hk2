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
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.extras.operation.OperationHandle;
import org.glassfish.hk2.extras.operation.OperationManager;

/**
 * @author jwells
 *
 */
@Singleton
public class OperationManagerImpl implements OperationManager {
    private final HashMap<Class<? extends Annotation>, SingleOperationManager<?>> children =
            new HashMap<Class<? extends Annotation>, SingleOperationManager<?>>();
    
    @Inject
    private ServiceLocator locator;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.extras.operation.OperationManager#createOperation()
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends Annotation> OperationHandle<T> createOperation(T scope) {
        SingleOperationManager<T> manager;
        synchronized (this) {
            manager = (SingleOperationManager<T>) children.get(scope.annotationType());
        
            if (manager == null) {
                manager = new SingleOperationManager<T>(scope, locator);
                children.put(scope.annotationType(), manager);
            }
        }
        
        return manager.createOperation();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.extras.operation.OperationManager#createAndStartOperation()
     */
    @Override
    public <T extends Annotation> OperationHandle<T> createAndStartOperation(T scope) {
        OperationHandle<T> retVal = createOperation(scope);
        retVal.resume();
        
        return retVal;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.extras.operation.OperationManager#getCurrentOperations()
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends Annotation> Set<OperationHandle<T>> getCurrentOperations(T scope) {
        SingleOperationManager<T> manager;
        synchronized (this) {
            manager = (SingleOperationManager<T>) children.get(scope.annotationType());
            if (manager == null) return Collections.emptySet();
        }
        
        return manager.getAllOperations();
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.extras.operation.OperationManager#getCurrentOperation(java.lang.annotation.Annotation)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends Annotation> OperationHandle<T> getCurrentOperation(T scope) {
        SingleOperationManager<T> manager;
        synchronized (this) {
            manager = (SingleOperationManager<T>) children.get(scope.annotationType());
            if (manager == null) return null;  
        }
        
        return manager.getCurrentOperationOnThisThread();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.extras.operation.OperationManager#shutdownAllOperations()
     */
    @Override
    public void shutdownAllOperations(Annotation scope) {
        SingleOperationManager<?> manager;
        synchronized (this) {
            manager = children.remove(scope.annotationType());
            if (manager == null) return;
            
            manager.shutdown();
        }
        
    }

    

}
