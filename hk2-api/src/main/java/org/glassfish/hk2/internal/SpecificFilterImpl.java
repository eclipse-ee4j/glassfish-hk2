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

package org.glassfish.hk2.internal;

import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.IndexedFilter;

/**
 * This is a filter that matches an exact descriptor
 * 
 * @author jwells
 */
public class SpecificFilterImpl implements IndexedFilter {
    private final String contract;
    private final String name;
    private final long id;
    private final long locatorId;
    
    /**
     * For matching an exact descriptor
     * 
     * @param contract
     * @param name
     * @param id
     * @param locatorId
     */
    public SpecificFilterImpl(String contract,
            String name,
            long id,
            long locatorId) {
       this.contract = contract;
       this.name = name;
       this.id = id;
       this.locatorId = locatorId;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Filter#matches(org.glassfish.hk2.api.Descriptor)
     */
    @Override
    public boolean matches(Descriptor d) {
        if ((d.getServiceId().longValue() == id) &&
                (d.getLocatorId().longValue() == locatorId)) {
            return true;
        }
        
        return false;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.IndexedFilter#getAdvertisedContract()
     */
    @Override
    public String getAdvertisedContract() {
        return contract;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.IndexedFilter#getName()
     */
    @Override
    public String getName() {
        return name;
    }

}
