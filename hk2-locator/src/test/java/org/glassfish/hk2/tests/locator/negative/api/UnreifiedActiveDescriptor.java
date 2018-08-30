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

package org.glassfish.hk2.tests.locator.negative.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;

import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;

/**
 * @author jwells
 *
 */
public class UnreifiedActiveDescriptor extends AbstractActiveDescriptor<Object> {
    /**
     * This is blank, just used for testing
     */
    public UnreifiedActiveDescriptor() {
        super(
                new HashSet<Type>(),
                PerLookup.class,
                null,
                new HashSet<Annotation>(),
                DescriptorType.CLASS,
                DescriptorVisibility.NORMAL,
                0,
                null,
                null,
                null,
                null);
        
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ActiveDescriptor#isReified()
     */
    @Override
    public boolean isReified() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ActiveDescriptor#getImplementationClass()
     */
    @Override
    public Class<?> getImplementationClass() {
        throw new AssertionError("not called");
    }
    
    @Override
    public Type getImplementationType() {
        throw new AssertionError("not called");
    }
    
    @Override
    public void setImplementationType(Type t) {
        throw new AssertionError("not called");
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ActiveDescriptor#create(org.glassfish.hk2.api.ServiceHandle)
     */
    @Override
    public Object create(ServiceHandle<?> root) {
        throw new AssertionError("not called");
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Descriptor#getImplementation()
     */
    @Override
    public String getImplementation() {
        return this.getClass().getName();
    }

}
