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

package org.glassfish.hk2.utilities;

import java.util.ArrayList;

import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.IndexedFilter;
import org.glassfish.hk2.utilities.general.GeneralUtilities;

/**
 * Creates a filter that matches if at least one
 * of the sub-filters is a match.  Respects the
 * rules of {@link IndexedFilter} as well
 * 
 * @author jwells
 *
 */
public class OrFilter implements Filter {
    private final ArrayList<Filter> allFilters;
    
    /**
     * Creates an OrFilter whose matches methods returns
     * true if at least one of the filters given returns
     * true.  If filters is zero length then the matches
     * method will always return false because none of
     * the filters returned true!
     * 
     * @param filters other filters to be considered in the
     * Or expression
     */
    public OrFilter(Filter...filters) {
        
        allFilters = new ArrayList<Filter>(filters.length);
        
        for (Filter f : filters) {
            if (f != null) {
                allFilters.add(f);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Filter#matches(org.glassfish.hk2.api.Descriptor)
     */
    @Override
    public boolean matches(Descriptor d) {
        for (Filter filter : allFilters) {
            if (filter instanceof IndexedFilter) {
                IndexedFilter iFilter = (IndexedFilter) filter;
                
                String name = iFilter.getName();
                if (name != null) {
                  if (!GeneralUtilities.safeEquals(name, d.getName())) continue;
                }
                
                String contract = iFilter.getAdvertisedContract();
                if (contract != null) {
                    if (!d.getAdvertisedContracts().contains(contract)) continue;
                }
            }
            
            if (filter.matches(d)) return true;
        }
        
        return false;
    }

}
