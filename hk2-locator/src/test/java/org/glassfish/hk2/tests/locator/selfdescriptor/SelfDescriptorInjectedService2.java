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

package org.glassfish.hk2.tests.locator.selfdescriptor;

import java.util.List;

import javax.inject.Inject;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.Self;
import org.junit.Assert;

/**
 * @author jwells
 *
 */
public class SelfDescriptorInjectedService2 {
    private final ActiveDescriptor<?> viaConstructor;
    
    @Inject @Self
    private ActiveDescriptor<SelfDescriptorInjectedService2> viaField;
    
    private ActiveDescriptor<SelfDescriptorInjectedService2> viaMethod;
    
    @Inject
    private SelfDescriptorInjectedService2(@Self ActiveDescriptor<?> viaConstructor) {
        this.viaConstructor = viaConstructor;
    }
    
    /**
     * Called by HK2
     * 
     * @param viaMethod with its own descriptor
     */
    @Inject
    public void injectMe(@Self ActiveDescriptor<SelfDescriptorInjectedService2> viaMethod) {
        this.viaMethod = viaMethod;
    }
    
    /**
     * Used by the test
     */
    public void checkAllDescriptorsEqual() {
        Assert.assertNotNull(viaConstructor);
        
        Assert.assertEquals(viaConstructor, viaField);
        Assert.assertEquals(viaConstructor, viaMethod);
        Assert.assertEquals(viaField, viaMethod);
        
        List<Injectee> injectees = viaConstructor.getInjectees();
        Assert.assertTrue(3 == injectees.size());
        
        for (Injectee injectee : injectees) {
            Assert.assertTrue(injectee.isSelf());
        }
    }

}
