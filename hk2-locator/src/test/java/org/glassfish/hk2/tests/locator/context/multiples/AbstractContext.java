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

package org.glassfish.hk2.tests.locator.context.multiples;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.api.ServiceHandle;

/**
 * We do not care about multi-threaded stuff with this implementation
 * 
 * @author jwells
 *
 */
public abstract class AbstractContext implements Context<MultiScope> {
    private final HashMap<ActiveDescriptor<?>, Object> instances = new HashMap<ActiveDescriptor<?>, Object>();
    private boolean active = false;
    
    public abstract AbstractContext getNextContext();

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#getScope()
     */
    @Override
    public Class<? extends Annotation> getScope() {
        return MultiScope.class;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#findOrCreate(org.glassfish.hk2.api.ActiveDescriptor, org.glassfish.hk2.api.ServiceHandle)
     */
    @SuppressWarnings("unchecked")
    @Override
    public synchronized <U> U findOrCreate(ActiveDescriptor<U> activeDescriptor,
            ServiceHandle<?> root) {
        U retVal = (U) instances.get(activeDescriptor);
        if (retVal != null) return retVal;
        
        retVal = activeDescriptor.create(root);
        instances.put(activeDescriptor, retVal);
        
        // Everytime we create something we move it on
        // to the next guy in line
        deactivate();
        
        return retVal;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#containsKey(org.glassfish.hk2.api.ActiveDescriptor)
     */
    @Override
    public synchronized boolean containsKey(ActiveDescriptor<?> descriptor) {
        return instances.containsKey(descriptor);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#destroyOne(org.glassfish.hk2.api.ActiveDescriptor)
     */
    @Override
    public void destroyOne(ActiveDescriptor<?> descriptor) {
        // Do nothing
        
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#supportsNullCreation()
     */
    @Override
    public boolean supportsNullCreation() {
        return false;
    }
    
    protected void activate() {
        active = true;
    }
    
    private void deactivate() {
        AbstractContext next = getNextContext();
        active = false;
        next.activate();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#isActive()
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#shutdown()
     */
    @Override
    public synchronized void shutdown() {
        instances.clear();
    }
    
    public synchronized Map<ActiveDescriptor<?>, Object> getInstances() {
        return Collections.unmodifiableMap(instances);
        
    }

}
