/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.api;

import org.jvnet.hk2.annotations.Contract;

/**
 * This service is used to get information about
 * the creation of a service from an
 * implementation of {@link Factory}.  The
 * system creates this service, and while
 * it can be injected into any service or
 * even looked up, it only has data
 * when called from inside the {@link Factory#provide()}
 * method of a {@link Factory}.
 * 
 * @author jwells
 *
 */
@Contract
public interface InstantiationService {
    /**
     * This method may be called from inside the
     * implementation of {@link Factory#provide()}
     * method to get more information about the
     * reason for instantiation.  If this method
     * is called outside the scope of a
     * {@link Factory#provide()} method the results
     * are indeterminate
     * 
     * @return A non-null InstantiationData object
     * containing information about the caller of
     * the {@link Factory#provide()} method. May
     * return null if no information is known or
     * if called from outside of a {@link Factory#provide()}
     * method
     */
    public InstantiationData getInstantiationData();
}
