/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.runlevel;

import java.lang.annotation.Annotation;

import javax.inject.Inject;
import javax.inject.Named;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.Visibility;
import org.glassfish.hk2.runlevel.internal.AsyncRunLevelContext;
import org.jvnet.hk2.annotations.Service;

/**
 * This is the implementation of the {@link Context}
 * for the {@link RunLevel} scope
 * 
 * @author jwells
 *
 */
@Service @Named(RunLevelContext.CONTEXT_NAME) @Visibility(DescriptorVisibility.LOCAL)
public class RunLevelContext implements Context<RunLevel> {
    /** The name under which this context will be advertised */
    public static final String CONTEXT_NAME = "DefaultRunLevelContext";
    
    private final AsyncRunLevelContext asyncRunLevelContext;
    
    @Inject
    private RunLevelContext(AsyncRunLevelContext asyncRunLevelContext) {
        this.asyncRunLevelContext = asyncRunLevelContext;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#getScope()
     */
    @Override
    public Class<? extends Annotation> getScope() {
        return RunLevel.class;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#findOrCreate(org.glassfish.hk2.api.ActiveDescriptor, org.glassfish.hk2.api.ServiceHandle)
     */
    @Override
    public <U> U findOrCreate(ActiveDescriptor<U> activeDescriptor,
            ServiceHandle<?> root) {
        return asyncRunLevelContext.findOrCreate(activeDescriptor, root);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#containsKey(org.glassfish.hk2.api.ActiveDescriptor)
     */
    @Override
    public boolean containsKey(ActiveDescriptor<?> descriptor) {
        return asyncRunLevelContext.containsKey(descriptor);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Context#destroyOne(org.glassfish.hk2.api.ActiveDescriptor)
     */
    @Override
    public void destroyOne(ActiveDescriptor<?> descriptor) {
        asyncRunLevelContext.destroyOne(descriptor);

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
        // Do nothing

    }

}
