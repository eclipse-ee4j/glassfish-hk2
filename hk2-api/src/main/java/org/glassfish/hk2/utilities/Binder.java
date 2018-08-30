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

package org.glassfish.hk2.utilities;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.jvnet.hk2.annotations.Contract;

/**
 * The binder is used in conjunction with the {@link ServiceLocatorUtilities#bind(org.glassfish.hk2.api.ServiceLocator, Binder...)}
 * method in order to add (or remove) services to a ServiceLocator.  This is useful when you have sets of related services to
 * add into the locator
 * 
 * @author jwells
 *
 */
@Contract
public interface Binder {
    
    /**
     * This method will be called by the
     * {@link ServiceLocatorUtilities#bind(org.glassfish.hk2.api.ServiceLocator, Binder...)} method for each
     * binder given.  All of the updates will be committed as one commit operation.
     * 
     * @param config The non-null config to bind service references into
     */
    public void bind(DynamicConfiguration config);

}
