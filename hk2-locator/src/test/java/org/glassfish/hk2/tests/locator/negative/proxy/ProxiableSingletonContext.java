/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.locator.negative.proxy;

import java.lang.annotation.Annotation;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.api.ServiceHandle;
import org.jvnet.hk2.annotations.Service;

/**
 * @author jwells
 */
@Service
public class ProxiableSingletonContext implements Context<ProxiableSingleton> {

    @Override
    public Class<? extends Annotation> getScope() {
        return ProxiableSingleton.class;
    }

    @Override
    public <U> U findOrCreate(ActiveDescriptor<U> activeDescriptor,
            ServiceHandle<?> root) {
        if (activeDescriptor.isCacheSet()) return activeDescriptor.getCache();
        
        synchronized (activeDescriptor) {
            if (activeDescriptor.isCacheSet()) return activeDescriptor.getCache();
            
            U t = activeDescriptor.create(root);
            activeDescriptor.setCache(t);
            
            return t;
        }
    }

    @Override
    public boolean containsKey(ActiveDescriptor<?> descriptor) {
        return descriptor.isCacheSet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void destroyOne(ActiveDescriptor<?> one) {
        if (!one.isCacheSet()) return;
        
        Object value = one.getCache();
        one.releaseCache();
        
        ((ActiveDescriptor<Object>) one).dispose(value);
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
    }

}
