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

package org.jvnet.hk2.internal;

import java.util.Collections;
import java.util.Map;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InstanceLifecycleEvent;
import org.glassfish.hk2.api.InstanceLifecycleEventType;

/**
 * @author jwells
 *
 */
public class InstanceLifecycleEventImpl implements InstanceLifecycleEvent {
    private final InstanceLifecycleEventType eventType;
    private final ActiveDescriptor<?> descriptor;
    private final Object lifecycleObject;
    private final Map<Injectee, Object> knownInjectees;
    
    /* package */ InstanceLifecycleEventImpl(InstanceLifecycleEventType eventType,
            Object lifecycleObject, Map<Injectee,Object> knownInjectees, ActiveDescriptor<?> descriptor) {
        this.eventType = eventType;
        this.lifecycleObject = lifecycleObject;
        if (knownInjectees == null) {
            this.knownInjectees = null;
        }
        else {
            this.knownInjectees = Collections.unmodifiableMap(knownInjectees);
        }
        this.descriptor = descriptor;
    }
    
    /* package */ InstanceLifecycleEventImpl(InstanceLifecycleEventType eventType,
            Object lifecycleObject, ActiveDescriptor<?> descriptor) {
        this(eventType, lifecycleObject, null, descriptor);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InstanceLifecycleEvent#getEventType()
     */
    @Override
    public InstanceLifecycleEventType getEventType() {
        return eventType;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InstanceLifecycleEvent#getActiveDescriptor()
     */
    @Override
    public ActiveDescriptor<?> getActiveDescriptor() {
        return descriptor;
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InstanceLifecycleEvent#getLifecycleObject()
     */
    @Override
    public Object getLifecycleObject() {
        return lifecycleObject;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InstanceLifecycleEvent#getKnownInjectees()
     */
    @Override
    public Map<Injectee, Object> getKnownInjectees() {
        return knownInjectees;
    }

    public String toString() {
        String descName = (descriptor == null) ? "null" : descriptor.getImplementation() ;
        
        return "InstanceLifecycleEventImpl(" + eventType + "," + descName + "," + System.identityHashCode(this) + ")";
    }
}
