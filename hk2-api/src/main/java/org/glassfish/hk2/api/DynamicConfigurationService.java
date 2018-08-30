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

package org.glassfish.hk2.api;

import org.jvnet.hk2.annotations.Contract;

/**
 * The dynamic configuration service  is the source of {@link DynamicConfiguration}
 * instances, which can be used to bind and unbind entities into the system
 * 
 * @author jwells
 */
@Contract
public interface DynamicConfigurationService {
    /**
     * Creates a dynamic configuration that can be used to add or remove values
     * to the system
     * 
     * @return A dynamic configuration to be used to add values to the system
     */
    public DynamicConfiguration createDynamicConfiguration();
    
    /**
     * Returns a populator for this service locator that can be used to
     * automatically read in hk2 inhabitant files (or some other external
     * source)
     * @return A non-null populator that can be used to fill in a {@link ServiceLocator}
     */
    public Populator getPopulator();

}
