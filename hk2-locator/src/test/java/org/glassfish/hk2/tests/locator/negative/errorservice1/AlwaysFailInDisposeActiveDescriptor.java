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

package org.glassfish.hk2.tests.locator.negative.errorservice1;

import java.lang.reflect.Type;

import javax.inject.Singleton;

import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;

/**
 * @author jwells
 *
 */
public class AlwaysFailInDisposeActiveDescriptor extends
    AbstractActiveDescriptor<SimpleService> {
    public AlwaysFailInDisposeActiveDescriptor() {
        super();

        super.addContractType(SimpleService.class);
        super.setScopeAnnotation(Singleton.class);
        super.setImplementation(SimpleService.class.getName());
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ActiveDescriptor#getImplementationClass()
     */
    @Override
    public Class<?> getImplementationClass() {
        return SimpleService.class;
    }
    
    @Override
    public Type getImplementationType() {
        return SimpleService.class;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ActiveDescriptor#create(org.glassfish.hk2.api.ServiceHandle)
     */
    @Override
    public SimpleService create(ServiceHandle<?> root) {
        return new SimpleService();
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ActiveDescriptor#dispose(java.lang.Object, org.glassfish.hk2.api.ServiceHandle)
     */
    @Override
    public void dispose(SimpleService instance) {
        throw new IllegalStateException(ErrorService1Test.ERROR_STRING);
    }
    
    
}

