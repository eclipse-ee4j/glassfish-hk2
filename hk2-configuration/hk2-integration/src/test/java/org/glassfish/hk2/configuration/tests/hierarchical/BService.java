/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.configuration.tests.hierarchical;

import org.glassfish.hk2.configuration.api.ChildInject;
import org.glassfish.hk2.configuration.api.ChildIterable;
import org.glassfish.hk2.configuration.api.ConfiguredBy;
import org.jvnet.hk2.annotations.Optional;

/**
 * @author jwells
 *
 */
@ConfiguredBy(HierarchicalTest.BBEAN_XPATH)
public class BService extends NamedService {
    @ChildInject(".c-beans.")
    private ChildIterable<CService> cServices;
    
    private final ChildIterable<DService> dServices;
    
    private BService(@ChildInject ChildIterable<DService> dServices) {
        this.dServices = dServices;
    }
    
    // Set by method
    private ChildIterable<DService> dServicesAsHandles;
    private DService dave;
    
    @SuppressWarnings("unused")
    private void myInitializer(@ChildInject ChildIterable<DService> dServicesAsHandles,
                               @ChildInject @Optional DService dave) {
        this.dServicesAsHandles = dServicesAsHandles;
        this.dave = dave;
    }
    
    public ChildIterable<CService> getCServices() {
        return cServices;
    }
    
    public ChildIterable<DService> getDServices() {
        return dServices;
    }
    
    public ChildIterable<DService> getDServicesAsHandles() {
        return dServicesAsHandles;
    }
    
    public DService getDave() {
        return dave;
    }
}
