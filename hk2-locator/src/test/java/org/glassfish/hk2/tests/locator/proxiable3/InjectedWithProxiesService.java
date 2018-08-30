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

package org.glassfish.hk2.tests.locator.proxiable3;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.ProxyCtl;
import org.junit.Assert;
import org.jvnet.hk2.annotations.Service;

/**
 * @author jwells
 *
 */
@Service @PerLookup
public class InjectedWithProxiesService {
    @Inject
    private Foo foo;
    
    private Bar bar;
    
    private final Control control;
    
    private boolean pass = false;
    
    @Inject
    private InjectedWithProxiesService(Control control) {
        this.control = control;
    }
    
    @SuppressWarnings("unused")
    @Inject
    private void setBar(Bar bar) {
        this.bar = bar;
    }
    
    @SuppressWarnings("unused")
    @PostConstruct
    private void postConstruct() {
        control.resetInvocations();
        
        foo.foo();
        foo.foo();
        
        bar.bar();
        
        if (control.getFooInvocations() == 2 && control.getBarInvocations() == 1) {
            pass = true;
        }
        
        control.resetInvocations();
    }
    
    public boolean didPass() {
        return pass;
    }
    
    public void areProxies() {
        Assert.assertTrue(control instanceof ProxyCtl);
        Assert.assertTrue(foo instanceof ProxyCtl);
        Assert.assertTrue(bar instanceof ProxyCtl);
    }
}
