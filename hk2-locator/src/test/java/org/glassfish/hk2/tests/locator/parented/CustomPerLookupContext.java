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

package org.glassfish.hk2.tests.locator.parented;

import java.lang.annotation.Annotation;

import javax.inject.Singleton;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceHandle;

/**
 * @author jwells
 *
 */
@Singleton
public class CustomPerLookupContext implements Context<CustomPerLookup> {
    private boolean shutdown = false;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#getScope()
     */
    @Override
    public Class<? extends Annotation> getScope() {
        return CustomPerLookup.class;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#findOrCreate(org.glassfish.hk2.api.ActiveDescriptor, org.glassfish.hk2.api.ServiceHandle)
     */
    @Override
    public <U> U findOrCreate(ActiveDescriptor<U> activeDescriptor,
            ServiceHandle<?> root) {
        if (shutdown) {
            throw new MultiException(new IllegalStateException("CustomPerLookupContext has been shutdown"));
        }
        
        return activeDescriptor.create(root);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#containsKey(org.glassfish.hk2.api.ActiveDescriptor)
     */
    @Override
    public boolean containsKey(ActiveDescriptor<?> descriptor) {
        return false;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#destroyOne(org.glassfish.hk2.api.ActiveDescriptor)
     */
    @Override
    public void destroyOne(ActiveDescriptor<?> descriptor) {
        // Does nothing

    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#supportsNullCreation()
     */
    @Override
    public boolean supportsNullCreation() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#isActive()
     */
    @Override
    public boolean isActive() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#shutdown()
     */
    @Override
    public void shutdown() {
        shutdown = true;

    }

}
