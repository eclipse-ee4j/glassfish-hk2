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

import org.glassfish.hk2.api.ProxyCtl;

import junit.framework.Assert;

/**
 * This service is in a proxiable scope with proxyForSameScope set to false
 * and injects serviceA which is in a different scope but also has
 * proxyForSameScope set to false.  This class ensures that since they
 * are in different scopes that serviceA WILL get proxied
 * @author jwells
 *
 */
@ProxiableSingletonNoLazy2
public class ProxiableServiceC {
    @Inject
    private ProxiableServiceA serviceA;
    
    public void check() {
        Assert.assertTrue(serviceA instanceof ProxyCtl);
    }

}
