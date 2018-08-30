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

package org.glassfish.hk2.configuration.hub.internal;

import java.util.Collections;
import java.util.Map;

import org.glassfish.hk2.configuration.hub.api.Instance;
import org.glassfish.hk2.configuration.hub.api.Type;
import org.glassfish.hk2.utilities.reflection.ClassReflectionHelper;

/**
 * @author jwells
 *
 */
public class TypeImpl implements Type {
    private final String name;
    private final Map<String, Instance> instances;
    private final ClassReflectionHelper helper;
    private Object metadata;
    
    /* package */ TypeImpl(Type baseType, ClassReflectionHelper helper) {
        name = baseType.getName();
        instances = Collections.unmodifiableMap(baseType.getInstances());
        this.helper = helper;
        this.metadata = baseType.getMetadata();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.Type#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.Type#getInstances()
     */
    @Override
    public Map<String, Instance> getInstances() {
        return instances;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.Type#getInstance(java.lang.Object)
     */
    @Override
    public Instance getInstance(String key) {
        return instances.get(key);
    }
    
    /* package */ ClassReflectionHelper getHelper() {
        return helper;
    }
    
    

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.Type#getMetadata()
     */
    @Override
    public synchronized Object getMetadata() {
        return metadata;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.Type#setMetadata(java.lang.Object)
     */
    @Override
    public synchronized void setMetadata(Object metadata) {
        this.metadata = metadata;
        
    }
    
    @Override
    public String toString() {
        return "TypeImpl(" + name + "," + System.identityHashCode(this) + ")";
    }
}
