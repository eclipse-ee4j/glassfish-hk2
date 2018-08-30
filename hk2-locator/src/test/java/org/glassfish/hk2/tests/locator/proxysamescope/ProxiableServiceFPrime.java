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

import org.glassfish.hk2.api.ProxyForSameScope;

/**
 * A little strange in that it sets the ProxyForSameScope value
 * to the same as the default one for the scope, but still
 * a valuable test!
 * 
 * @author jwells
 *
 */
@ProxiableSingleton @ProxyForSameScope(true)
public class ProxiableServiceFPrime {

}
