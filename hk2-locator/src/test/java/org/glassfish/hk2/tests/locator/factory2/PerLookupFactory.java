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

package org.glassfish.hk2.tests.locator.factory2;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InstantiationService;
import org.glassfish.hk2.api.PerLookup;

/**
 * @author jwells
 *
 */
@Singleton
public class PerLookupFactory implements Factory<PerLookupService> {
    @Inject
    private InstantiationService instantiationService;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Factory#provide()
     */
    @Override @PerLookup
    public PerLookupService provide() {
        final Injectee parent = instantiationService.getInstantiationData().getParentInjectee();
        
        return new PerLookupService() {

            @Override
            public Injectee getInjectee() {
                return parent;
            }
            
        };
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Factory#dispose(java.lang.Object)
     */
    @Override
    public void dispose(PerLookupService instance) {

    }

}
