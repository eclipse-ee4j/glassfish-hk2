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

package org.glassfish.hk2.configuration.internal;

import java.lang.reflect.Type;

import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.IndexedFilter;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;

class ChildFilter implements IndexedFilter {
    private final String requiredType;
    private final String requiredPrefix;
    private final String requiredSuffix;
    
    ChildFilter(Type type, String prefix, String suffix) {
        Class<?> requiredTypeClass = ReflectionHelper.getRawClass(type);
        
        requiredType = requiredTypeClass.getName();
        requiredPrefix = prefix;
        requiredSuffix = suffix;
    }
    
    ChildFilter(Type type, String prefix) {
        this(type, prefix, null);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Filter#matches(org.glassfish.hk2.api.Descriptor)
     */
    @Override
    public boolean matches(Descriptor d) {
        if (d.getName() == null) return false;
        
        if (!d.getName().startsWith(requiredPrefix)) return false;
        
        if (requiredSuffix == null) return true;
        
        return d.getName().endsWith(requiredSuffix);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.IndexedFilter#getAdvertisedContract()
     */
    @Override
    public String getAdvertisedContract() {
        return requiredType;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.IndexedFilter#getName()
     */
    @Override
    public String getName() {
        return null;
    }
    
}
