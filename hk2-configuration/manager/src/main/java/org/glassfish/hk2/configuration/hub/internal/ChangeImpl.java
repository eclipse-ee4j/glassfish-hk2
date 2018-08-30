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

import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.List;

import org.glassfish.hk2.configuration.hub.api.Change;
import org.glassfish.hk2.configuration.hub.api.Instance;
import org.glassfish.hk2.configuration.hub.api.Type;

/**
 * @author jwells
 *
 */
public class ChangeImpl implements Change {
    private final ChangeCategory changeCategory;
    private final Type changeType;
    private final String instanceKey;
    private final Instance instanceValue;
    private final Instance originalInstanceValue;
    private final List<PropertyChangeEvent> propertyChanges;
    
    /* package */ ChangeImpl(ChangeCategory changeCategory,
                             Type changeType,
                             String instanceKey,
                             Instance instanceValue,
                             Instance originalInstanceValue,
                             List<PropertyChangeEvent> propertyChanges) {
        this.changeCategory = changeCategory;
        this.changeType = changeType;
        this.instanceKey = instanceKey;
        this.instanceValue = instanceValue;
        this.originalInstanceValue = originalInstanceValue;
        this.propertyChanges = propertyChanges;
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.Change#getChangeCategory()
     */
    @Override
    public ChangeCategory getChangeCategory() {
        return changeCategory;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.Change#getChangeType()
     */
    @Override
    public Type getChangeType() {
        return changeType;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.Change#getInstanceKey()
     */
    @Override
    public String getInstanceKey() {
        return instanceKey;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.Change#getInstanceValue()
     */
    @Override
    public Instance getInstanceValue() {
        return instanceValue;
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.Change#getOriginalInstanceValue()
     */
    @Override
    public Instance getOriginalInstanceValue() {
        return originalInstanceValue;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.Change#getModifiedProperties()
     */
    @Override
    public List<PropertyChangeEvent> getModifiedProperties() {
        if (propertyChanges == null) return null;
        
        return Collections.unmodifiableList(propertyChanges);
    }

    @Override
    public String toString() {
        StringBuffer propChanges= new StringBuffer(",propChanges=[");
        
        if (propertyChanges != null) {
            boolean firstTime = true;
            for (PropertyChangeEvent pce : propertyChanges) {
                if (firstTime) {
                    propChanges.append(pce.getPropertyName());
                    firstTime = false;
                }
                else {
                    propChanges.append("," + pce.getPropertyName());
                }
            }
        }
        propChanges.append("]");
        
        return "ChangeImpl(" + changeCategory +
                ",type=" + changeType +
                ",instanceKey=" + instanceKey +
                propChanges.toString() +
                ",sid=" + System.identityHashCode(this) + ")";
    }
}
