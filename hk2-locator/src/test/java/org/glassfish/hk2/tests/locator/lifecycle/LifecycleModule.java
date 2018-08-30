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

import javax.inject.Singleton;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.InstanceLifecycleListener;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.tests.locator.utilities.TestModule;
import org.glassfish.hk2.utilities.BuilderHelper;

/**
 * @author jwells
 *
 */
public class LifecycleModule implements TestModule {

    /* (non-Javadoc)
     * @see org.glassfish.hk2.tests.locator.utilities.TestModule#configure(org.glassfish.hk2.api.DynamicConfiguration)
     */
    @Override
    public void configure(DynamicConfiguration config) {
        // This is the LifecycleListener itself
        config.bind(BuilderHelper.link(
                InstanceLifecycleListenerImpl.class).
                to(InstanceLifecycleListener.class).
                in(Singleton.class.getName()).
                build());
        
        // This is the default one, actually created by the system
        config.bind(BuilderHelper.link(
                Notifier.class).
                named(Notifier.DEFAULT_NAME).
                in(Singleton.class.getName()).
                build());
        
        
        // This is the receiver that gets it from the system created guy
        config.bind(BuilderHelper.link(
                KnownInjecteeNotifyee.class).
                to(Notifyee.class).
                in(PerLookup.class.getName()).
                build());
        
        // The earth wind and fire ordered service, to check that ordering of the PRE_PRODUCTION is ok
        config.bind(BuilderHelper.link(OrderedLifecycleListener.class.getName()).
                to(InstanceLifecycleListener.class.getName()).
                in(Singleton.class.getName()).
                build());
        
        // This is earth, wind and fire as basic descriptors
        config.bind(BuilderHelper.link(Earth.class.getName()).
                to(EarthWindAndFire.class.getName()).
                in(Singleton.class.getName()).
                build());
        
        config.bind(BuilderHelper.link(Wind.class.getName()).
                to(EarthWindAndFire.class.getName()).
                build());
        
        config.bind(BuilderHelper.link(Fire.class.getName()).
                to(EarthWindAndFire.class.getName()).
                build());
        
        // This is water, sand and space done as active descriptors (but as a class)
        config.addActiveDescriptor(Water.class);
        config.addActiveDescriptor(Sand.class);
        config.addActiveDescriptor(Space.class);
        
    }

}
