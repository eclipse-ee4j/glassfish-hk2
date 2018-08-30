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

package org.jvnet.hk2.guice.bridge.api;

import org.glassfish.hk2.api.ServiceLocator;
import org.jvnet.hk2.guice.bridge.internal.HK2ToGuiceTypeListenerImpl;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

/**
 * This is an implementation of com.google.inject.Module that should
 * be used if one wishes to inject HK2 services into Guice services
 * 
 * @author jwells
 *
 */
public class HK2IntoGuiceBridge extends AbstractModule {
    private final ServiceLocator locator;
    
    /**
     * Creates the {@link HK2IntoGuiceBridge} TypeLocator that must
     * be bound into the Module with a call to bindListener.  The
     * ServiceLocator will be consulted at this time for any types
     * Guice cannot find.  If this type is found in the ServiceLocator
     * then that service will be instantiated by hk2
     * 
     * @param locator The non-null locator that should be used to discover
     * services
     */
    public HK2IntoGuiceBridge(ServiceLocator locator) {
        this.locator = locator;
    }

    @Override
    protected void configure() {
        bindListener(Matchers.any(), new HK2ToGuiceTypeListenerImpl(locator));
    }
}
