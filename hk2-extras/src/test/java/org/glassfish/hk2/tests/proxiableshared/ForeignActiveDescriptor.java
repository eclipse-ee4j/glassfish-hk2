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

package org.glassfish.hk2.tests.proxiableshared;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.extras.operation.OperationHandle;
import org.glassfish.hk2.extras.operation.OperationManager;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;
import org.glassfish.hk2.utilities.DescriptorImpl;

/**
 * @author jwells
 *
 */
public class ForeignActiveDescriptor<T> extends AbstractActiveDescriptor<T> {
    private final OperationManager operationManager;
    
    private final Class<?> implClass;
    private final Descriptor baseDescriptor;
    
    private final Map<ServiceLocator, ActiveDescriptor<T>> multiplexor = new HashMap<ServiceLocator, ActiveDescriptor<T>>();
    
    public ForeignActiveDescriptor(OperationManager manager, ServiceLocator childLocator, ActiveDescriptor<T> foreignDescriptor) {
        super(
                foreignDescriptor.getContractTypes(),
                foreignDescriptor.getScopeAnnotation(),
                foreignDescriptor.getName(),
                foreignDescriptor.getQualifierAnnotations(),
                foreignDescriptor.getDescriptorType(),
                DescriptorVisibility.LOCAL,
                foreignDescriptor.getRanking(),
                foreignDescriptor.isProxiable(),
                foreignDescriptor.isProxyForSameScope(),
                foreignDescriptor.getClassAnalysisName(),
                foreignDescriptor.getMetadata());
        
        setScopeAsAnnotation(foreignDescriptor.getScopeAsAnnotation());
        setReified(true);
        
        implClass = foreignDescriptor.getImplementationClass();
        baseDescriptor = new DescriptorImpl(foreignDescriptor);
        
        this.operationManager = manager;
        
        multiplexor.put(childLocator, foreignDescriptor);
    }
    
    public synchronized void addSimilarChild(ServiceLocator locator, ActiveDescriptor<T> foreignDescriptor) {
        if (multiplexor.containsKey(locator)) {
            throw new IllegalStateException("We already have this descriptor for locator " + locator);
        }
        
        Descriptor d = new DescriptorImpl(foreignDescriptor);
        if (!d.equals(baseDescriptor)) {
            throw new IllegalArgumentException("The descriptor " + foreignDescriptor + " does not match the base descriptor " + baseDescriptor);
        }
        
        multiplexor.put(locator, foreignDescriptor);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ActiveDescriptor#getImplementationClass()
     */
    @Override
    public Class<?> getImplementationClass() {
        return implClass;
    }

    @Override
    public Type getImplementationType() {
        return implClass;
    }
    
    @Override
    public String getImplementation() {
        return baseDescriptor.getImplementation();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ActiveDescriptor#create(org.glassfish.hk2.api.ServiceHandle)
     */
    @Override
    public synchronized T create(ServiceHandle<?> root) {
        OperationHandle<ReqScoped> handle = operationManager.getCurrentOperation(ReqScopedImpl.REQ_SCOPED);
        if (handle == null) {
            throw new IllegalStateException("no current operation on the thread");
        }
        
        ServiceLocator childLocator = (ServiceLocator) handle.getOperationData();
        ActiveDescriptor<T> foreignDescriptor = multiplexor.get(childLocator);
        
        ServiceHandle<T> serviceHandle = childLocator.getServiceHandle(foreignDescriptor);
        return serviceHandle.getService();
    }
}
