/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.jvnet.hk2.internal;

import javax.inject.Inject;
import javax.inject.Named;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.UnsatisfiedDependencyException;

/**
 * @author jwells
 *
 */
@Named(InjectionResolver.SYSTEM_RESOLVER_NAME)
public class ThreeThirtyResolver implements InjectionResolver<Inject> {
    private final ServiceLocatorImpl locator;
    
    /* package */ ThreeThirtyResolver(ServiceLocatorImpl locator) {
        this.locator = locator;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InjectionResolver#resolve(org.glassfish.hk2.api.Injectee, org.glassfish.hk2.api.ServiceHandle)
     */
    @Override
    public Object resolve(Injectee injectee, ServiceHandle<?> root) {
        ActiveDescriptor<?> ad = locator.getInjecteeDescriptor(injectee);
        
        if (ad == null) {
            if (injectee.isOptional()) return null;
            
            throw new MultiException(new UnsatisfiedDependencyException(injectee));
        }
        
        return locator.getService(ad, root, injectee);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InjectionResolver#isConstructorParameterIndicator()
     */
    @Override
    public boolean isConstructorParameterIndicator() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InjectionResolver#isMethodParameterIndicator()
     */
    @Override
    public boolean isMethodParameterIndicator() {
        return false;
    }
    
    @Override
    public String toString() {
        return "ThreeThirtyResolver(" + locator + "," + System.identityHashCode(this) + ")";
    }

}
