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

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.IndexedFilter;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.configuration.api.ConfiguredBy;

/**
 * Matches only things with scope ConfiguredBy and which have no name
 * 
 * @author jwells
 *
 */
class NoNameTypeFilter implements IndexedFilter {
    private final ServiceLocator locator;
    private final String typeName;
    private final String instanceName;
    
    NoNameTypeFilter(ServiceLocator locator, String typeName, String instanceName) {
        this.locator = locator;
        this.typeName = typeName;
        this.instanceName = instanceName;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Filter#matches(org.glassfish.hk2.api.Descriptor)
     */
    @Override
    public boolean matches(Descriptor d) {
        if (instanceName == null) {
            if (typeName == null) {
                return (d.getName() == null);
            }
            
            if (d.getName() != null) return false;
        }
        else {
            if (d.getName() == null) return false;
            if (!instanceName.equals(d.getName())) return false;
        }
        
        ActiveDescriptor<?> reified;
        try {
            reified = locator.reifyDescriptor(d);
        }
        catch (MultiException me) {
            return false;
        }
        
        Class<?> implClass = reified.getImplementationClass();
        ConfiguredBy configuredBy = implClass.getAnnotation(ConfiguredBy.class);
        if (configuredBy == null) return false;
        
        return configuredBy.value().equals(typeName);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.IndexedFilter#getAdvertisedContract()
     */
    @Override
    public String getAdvertisedContract() {
        return ConfiguredBy.class.getName();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.IndexedFilter#getName()
     */
    @Override
    public String getName() {
        return null;
    }
    
    @Override
    public String toString() {
        return "NoNameTypeFilter(" + typeName + "," + instanceName + "," + System.identityHashCode(this) + ")";
    }
    
}
