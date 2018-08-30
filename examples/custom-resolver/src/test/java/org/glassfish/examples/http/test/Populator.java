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

package org.glassfish.examples.http.test;

import javax.inject.Singleton;

import org.glassfish.examples.http.AlternateInjectResolver;
import org.glassfish.examples.http.HttpEventReceiver;
import org.glassfish.examples.http.HttpRequest;
import org.glassfish.examples.http.HttpServer;
import org.glassfish.examples.http.Logger;
import org.glassfish.examples.http.RequestContext;
import org.glassfish.examples.http.RequestProcessor;
import org.glassfish.examples.http.RequestScope;
import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.BuilderHelper;

/**
 * TODO:  Once &#64;Service works this class should go
 * away in favor of just automatically registering the
 * services.  Until then we must do the registration
 * manually
 * 
 * @author jwells
 */
public class Populator {
    
    /**
     * TODO:  This should be removed once we have &#64;Service all hooked up
     * 
     * @param locator The locator to populate
     */
    public static void populate(ServiceLocator locator) {
        DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
        DynamicConfiguration config = dcs.createDynamicConfiguration();
        
        // The InjectionResolver we are showcasing
        config.bind(BuilderHelper.link(AlternateInjectResolver.class).
                to(InjectionResolver.class).
                in(Singleton.class.getName()).
                build());
        
        // The HttpEventReciever is in the default scope @PerLookup
        config.bind(BuilderHelper.link(HttpEventReceiver.class).
                build());
        
        // The HttpRequest is in the RequestScope
        config.bind(BuilderHelper.link(HttpRequest.class).
                in(RequestScope.class.getName()).
                build());
        
        // The HttpServer is a singleton
        config.bind(BuilderHelper.link(HttpServer.class).
                in(Singleton.class.getName()).
                build());
        
        // The RequestContext is the context implementation for RequestScope
        config.bind(BuilderHelper.link(RequestContext.class).
                to(Context.class).
                in(Singleton.class.getName()).
                build());
        
        // RequestProcessor processes a request from an HttpServer
        config.bind(BuilderHelper.link(RequestProcessor.class).
                build());
        
        // The logger is just another service to be injected
        config.bind(BuilderHelper.link(Logger.class).
                in(Singleton.class.getName()).
                build());
        
        // And commit
        config.commit();           
    }

}
