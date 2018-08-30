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

package org.glassfish.hk2.tests.locator.lifecycle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.InstanceLifecycleEvent;
import org.glassfish.hk2.api.InstanceLifecycleListener;
import org.glassfish.hk2.utilities.BuilderHelper;

/**
 * @author jwells
 *
 */
@Singleton
public class InstanceLifecycleListenerImpl implements InstanceLifecycleListener {
    private final Filter notifyeeFilter = BuilderHelper.createContractFilter(Notifyee.class.getName());
    private final Map<Notifyee, Set<Notifier>> disposePath = new HashMap<Notifyee, Set<Notifier>>();

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InstanceLifecycleListener#getFilter()
     */
    @Override
    public Filter getFilter() {
        return notifyeeFilter;
    }
    
    private void preDestruction(InstanceLifecycleEvent lifecycleEvent) {
        Notifyee notifyee = (Notifyee) lifecycleEvent.getLifecycleObject();
        Set<Notifier> forDisposal = disposePath.get(notifyee);
        if (forDisposal == null) return;
        
        for (Notifier notifier : forDisposal) {
            notifier.removeNotifyee(notifyee);
            
        }
    }
    
    private void postProduction(InstanceLifecycleEvent lifecycleEvent) {
        if (lifecycleEvent.getKnownInjectees() == null) {
            return;
        }
        
        for (Object injectedInstance : lifecycleEvent.getKnownInjectees().values()) {
            if (!(injectedInstance instanceof Notifier)) {
                continue;
            }
            
            Notifier notifier = (Notifier) injectedInstance;
            Notifyee notifyee = (Notifyee) lifecycleEvent.getLifecycleObject();
            
            Set<Notifier> forDisposal = disposePath.get(notifyee);
            if (forDisposal == null) {
                forDisposal = new HashSet<Notifier>();
                disposePath.put(notifyee, forDisposal);
            }
            forDisposal.add(notifier);
            
            notifier.addNotifyee(notifyee);
        }
        
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.InstanceLifecycleListener#lifecycleEvent(org.glassfish.hk2.api.InstanceLifecycleEvent)
     */
    @Override
    public void lifecycleEvent(InstanceLifecycleEvent lifecycleEvent) {
        switch (lifecycleEvent.getEventType()) {
        case POST_PRODUCTION:
            postProduction(lifecycleEvent);
            return;
        case PRE_DESTRUCTION:
            preDestruction(lifecycleEvent);
            return;
         default:
            return;
        }
    }

}
