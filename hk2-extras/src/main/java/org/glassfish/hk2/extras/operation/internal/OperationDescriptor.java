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
import java.lang.reflect.Type;

import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.extras.operation.OperationHandle;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;
import org.glassfish.hk2.utilities.reflection.ParameterizedTypeImpl;

/**
 * @author jwells
 *
 */
public class OperationDescriptor<T extends Annotation> extends AbstractActiveDescriptor<OperationHandle<T>> {
    private final SingleOperationManager<T> parent;
    
    public OperationDescriptor(T scope, SingleOperationManager<T> parent) {
        this.parent = parent;
        
        setImplementation(OperationHandleImpl.class.getName());
        addContractType(new ParameterizedTypeImpl(OperationHandle.class, scope.annotationType()));
        
        setScopeAsAnnotation(scope);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ActiveDescriptor#getImplementationClass()
     */
    @Override
    public Class<?> getImplementationClass() {
        return OperationHandleImpl.class;
    }

    @Override
    public Type getImplementationType() {
        return OperationHandleImpl.class;
    }

    @Override
    public void setImplementationType(Type t) {
        throw new AssertionError("Cannot set type of OperationHandle");
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ActiveDescriptor#create(org.glassfish.hk2.api.ServiceHandle)
     */
    @Override
    public OperationHandle<T> create(ServiceHandle<?> root) {
        OperationHandleImpl<T> retVal = parent.getCurrentOperationOnThisThread();
        if (retVal == null) {
            throw new IllegalStateException("There is no active operation in scope " +
                getScopeAnnotation().getName() + " on thread " + Thread.currentThread().getId());
        }
        
        return retVal;
    }

}
