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

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.extras.operation.OperationHandle;
import org.glassfish.hk2.extras.operation.OperationManager;

/**
 * Request context to manage request scoped components.
 * A single threaded client is assumed for the sake of simplicity.
  *
 * @author Jakub Podlesak (jakub.podlesak at oracle.com)
 */
@Singleton
public class ReqContext implements Context<ReqScoped> {
    @Inject
    private OperationManager parentOperationManager;
    
    @Inject
    private ServiceLocator whoAmIFor;
    
    private OperationHandle<ReqScoped> currentRequest;

    private Map<ActiveDescriptor<?>, Object> context = null;

    /**
     * Make room for new request scoped data.
     */
    public void startRequest() {
        if (currentRequest != null) {
            currentRequest.close();
            currentRequest = null;
        }
        
        currentRequest = parentOperationManager.createAndStartOperation(ReqScopedImpl.REQ_SCOPED);
        currentRequest.setOperationData(whoAmIFor);
        
        context = new HashMap<ActiveDescriptor<?>, Object>();
    }

    /**
     * Forget all request data.
     */
    public void stopRequest() {
        this.context = null;
        if (currentRequest != null) {
            currentRequest.close();
            currentRequest = null;
        }
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return ReqScoped.class;
    }

    @Override
    public <U> U findOrCreate(ActiveDescriptor<U> activeDescriptor, ServiceHandle<?> root) {

        ensureActiveRequest();

        Object result = context.get(activeDescriptor);
        if (result == null) {
            result = activeDescriptor.create(root);
            context.put(activeDescriptor, result);
        }
        return (U) result;
    }

    private void ensureActiveRequest() {
        if (context == null) {
            throw new IllegalStateException("Not inside an active request scope");
        }
    }

    @Override
    public boolean containsKey(ActiveDescriptor<?> descriptor) {
        ensureActiveRequest();
        return context.containsKey(descriptor);
    }

    @Override
    public void destroyOne(ActiveDescriptor<?> descriptor) {
        ensureActiveRequest();
        context.remove(descriptor);
    }

    @Override
    public boolean supportsNullCreation() {
        return false;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void shutdown() {
        context.clear();
    }
}
