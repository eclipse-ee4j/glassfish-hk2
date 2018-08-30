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

package org.glassfish.hk2.api;

import org.jvnet.hk2.annotations.Contract;

/**
 * This processor is called for certain events in the lifecycle of instances
 * of services.
 * <p>
 * This listener is concerned with instances of services, whereas the
 * {@link ValidationService} is concerned with the descriptors for services.
 * <p>
 * An implementation of InstanceLifecycleListener must be in the Singleton scope.
 * Implementations of InstanceLifecycleListener will be instantiated as soon as
 * they are added to HK2 in order to avoid deadlocks and circular references.
 * Therefore it is recommended that implementations of InstanceLifecycleListener
 * make liberal use of {@link javax.inject.Provider} or {@link IterableProvider}
 * when injecting dependent services so that these services are not instantiated
 * when the InstanceLifecycleListener is created
 * 
 * @author jwells
 */
@Contract
public interface InstanceLifecycleListener {
    /**
     * This returns a filter that tells the system whether a particular descriptor should be handled by this lifecycle
     * listener.  The filter can be called at any time
     * 
     * @return The filter that tells the system if this listener applies to this descriptor.  If this returns null then
     * this Listener will apply to ALL descriptors.
     */
    public Filter getFilter();
    
    /**
     * This method will be called when any lifecycle event occurs.  The currently supported
     * lifecycle events are PRE_PRODUCTION, POST_PRODUCTION and PRE_DESTRUCTION.  Code should be written to
     * allow for future events to be generated.  This method should not throw exceptions
     * 
     * @param lifecycleEvent The event that has occurred, will not be null
     */
    public void lifecycleEvent(InstanceLifecycleEvent lifecycleEvent);
}
