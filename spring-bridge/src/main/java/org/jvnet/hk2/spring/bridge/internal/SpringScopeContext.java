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

package org.jvnet.hk2.spring.bridge.internal;

import java.lang.annotation.Annotation;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.Visibility;
import org.jvnet.hk2.annotations.Service;
import org.jvnet.hk2.spring.bridge.api.SpringScope;

/**
 * @author jwells
 *
 */
@Service
@Visibility(DescriptorVisibility.LOCAL)
public class SpringScopeContext implements Context<SpringScope> {

    @Override
    public Class<? extends Annotation> getScope() {
        return SpringScope.class;
    }

    @Override
    public <U> U findOrCreate(ActiveDescriptor<U> activeDescriptor,
            ServiceHandle<?> root) {
        return activeDescriptor.create(root);
    }

    @Override
    public boolean containsKey(ActiveDescriptor<?> descriptor) {
        return false;
    }

    @Override
    public void destroyOne(ActiveDescriptor<?> descriptor) {
        // TODO:  Not sure how spring destroys instances
        
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
        // Do nothing
        
    }

}
