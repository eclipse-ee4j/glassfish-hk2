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

package org.glassfish.hk2.internal;

import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.Immediate;

/**
 * Filter only picks up local services
 * 
 * @author jwells
 *
 */
public class ImmediateLocalLocatorFilter implements Filter {
    private final long locatorId;
    
    public ImmediateLocalLocatorFilter(long locatorId) {
        this.locatorId = locatorId;
    }

    @Override
    public boolean matches(Descriptor d) {
        String scope = d.getScope();
        if (scope == null) return false;
        if (d.getLocatorId() != locatorId) return false;
        
        return Immediate.class.getName().equals(scope);
    }
}
