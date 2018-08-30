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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.glassfish.hk2.api.UseProxy;

/**
 * @author jwells
 *
 */
@Singleton @UseProxy(true)
public class InterfaceProxiedService implements Foo, Bar, Control {
    private int fooCalls;
    private int barCalls;
    
    /**
     * This constructor is here to ensure that there is no zero-arg
     * constructor for this class, making it impossible to proxy
     * with the cglib proxying scheme
     * 
     * @param simple A dummy service, used to ensure there is no
     * zero-arg constructor for this class
     */
    @Inject
    private InterfaceProxiedService(SimpleService simple) {
        
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.tests.locator.proxiable3.Control#getFooInvocations()
     */
    @Override
    public int getFooInvocations() {
        return fooCalls;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.tests.locator.proxiable3.Control#getBarInvocations()
     */
    @Override
    public int getBarInvocations() {
        return barCalls;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.tests.locator.proxiable3.Bar#bar()
     */
    @Override
    public void bar() {
        barCalls++;

    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.tests.locator.proxiable3.Foo#foo()
     */
    @Override
    public void foo() {
        fooCalls++;

    }

    @Override
    public void resetInvocations() {
        barCalls = 0;
        fooCalls = 0;
        
    }

}
