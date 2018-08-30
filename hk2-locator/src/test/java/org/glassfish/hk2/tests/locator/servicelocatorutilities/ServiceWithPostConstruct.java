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

package org.glassfish.hk2.tests.locator.servicelocatorutilities;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.junit.Assert;

/**
 * @author jwells
 *
 */
public class ServiceWithPostConstruct {
    @Inject
    private SimpleService field;
    
    private final SimpleService constructor;
    
    private SimpleService method;
    
    private boolean postCalled = false;
    
    @Inject
    private ServiceWithPostConstruct(SimpleService constructor) {
        this.constructor = constructor;
    }
    
    @SuppressWarnings("unused")
    @Inject
    private void init(SimpleService method) {
        this.method = method;
    }
    
    @PostConstruct
    private void post() {
        postCalled = true;
    }
    
    /* package */ void check() {
        Assert.assertNotNull(field);
        Assert.assertNotNull(constructor);
        Assert.assertNotNull(method);
        Assert.assertTrue(postCalled);
    }

}
