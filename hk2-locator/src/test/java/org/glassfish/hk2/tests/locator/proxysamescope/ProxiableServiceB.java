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

package org.glassfish.hk2.tests.locator.proxysamescope;

import javax.inject.Inject;

/**
 * @author jwells
 *
 */
@ProxiableSingletonNoLazy
public class ProxiableServiceB {
    @Inject
    private ProxiableServiceA viaField;
    
    private ProxiableServiceA viaMethod;
    
    private final ProxiableServiceA viaConstructor;
    
    /**
     * Needed so that this service can be proxied
     */
    public ProxiableServiceB() {
        viaConstructor = null;
    }
    
    @SuppressWarnings("unused")
    @Inject
    private ProxiableServiceB(ProxiableServiceA viaConstructor) {
        this.viaConstructor = viaConstructor;
    }
    
    @SuppressWarnings("unused")
    @Inject
    private void setViaMethod(ProxiableServiceA viaMethod) {
        this.viaMethod = viaMethod;
    }
    
    public ProxiableServiceA getViaField() { return viaField; }
    public ProxiableServiceA getViaMethod() { return viaMethod; }
    public ProxiableServiceA getViaConstructor() { return viaConstructor; }
}
