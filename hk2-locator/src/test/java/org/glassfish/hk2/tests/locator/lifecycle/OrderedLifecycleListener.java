/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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

import java.util.LinkedList;
import java.util.List;

import javax.inject.Singleton;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.InstanceLifecycleEvent;
import org.glassfish.hk2.api.InstanceLifecycleEventType;
import org.glassfish.hk2.api.InstanceLifecycleListener;
import org.glassfish.hk2.utilities.BuilderHelper;

/**
 * @author jwells
 *
 */
@Singleton
public class OrderedLifecycleListener implements InstanceLifecycleListener {
    private final LinkedList<ActiveDescriptor<?>> ordered = new LinkedList<ActiveDescriptor<?>>();

    @Override
    public Filter getFilter() {
        return BuilderHelper.createContractFilter(EarthWindAndFire.class.getName());
    }

    @Override
    public void lifecycleEvent(InstanceLifecycleEvent lifecycleEvent) {
        if (!lifecycleEvent.getEventType().equals(InstanceLifecycleEventType.PRE_PRODUCTION)) {
            return;
        }
        
        ordered.add(lifecycleEvent.getActiveDescriptor());
    }
    
    public List<ActiveDescriptor<?>> getOrderedList() {
        return ordered;
    }
    
    public void clear() {
        ordered.clear();
    }

}
