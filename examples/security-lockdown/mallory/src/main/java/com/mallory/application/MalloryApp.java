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

package com.mallory.application;

import javax.inject.Inject;

import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.jvnet.hk2.annotations.Service;

import com.alice.application.AliceApp;

/**
 * This naughty service will attempt to do many things it is not allowed to do
 * 
 * @author jwells
 *
 */
@Service
public class MalloryApp {
    @Inject
    private AliceApp alice;
    
    @Inject
    private ServiceLocator locator;
    
    /**
     * Mallory is allowed to call the AliceApp
     */
    public void doAnApprovedOperation() {
        alice.doAuditedService("Mallory");
    }
    
    /**
     * Mallory cannot however lookup the AuditService himself!
     */
    public void tryToGetTheAuditServiceMyself() {
        Object auditService = locator.getBestDescriptor(BuilderHelper.createContractFilter("org.acme.security.AuditService"));
        auditService.toString();  // This should NPE!
    }
    
    /**
     * Mallory cannot advertise the EvilService
     */
    public void tryToAdvertiseAService() {
        DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
        DynamicConfiguration config = dcs.createDynamicConfiguration();
        
        config.addActiveDescriptor(EvilService.class);
        
        config.commit();  // This will throw a MultiException
    }
    
    /**
     * Mallory cannot un-advertise ServiceLocator!
     */
    public void tryToUnAdvertiseAService() {
        final Descriptor locatorService = locator.getBestDescriptor(BuilderHelper.createContractFilter(ServiceLocator.class.getName()));
        
        // This filter matches ServiceLocator itself!
        Filter unbindFilter = new Filter() {

            @Override
            public boolean matches(Descriptor d) {
                if (d.getServiceId().equals(locatorService.getServiceId())) {
                    if (d.getLocatorId().equals(locator.getLocatorId())) {
                        return true;
                    }
                }
                
                return false;
            }
            
        };
        
        DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
        DynamicConfiguration config = dcs.createDynamicConfiguration();
        
        config.addUnbindFilter(unbindFilter);
        
        config.commit();  // This will throw a MultiException
    }
    
    /**
     * Tries to instantiate a service with an illegal injection point
     */
    public void tryToInstantiateAServiceWithABadInjectionPoint() {
        locator.getService(EvilInjectedService.class);  // Will throw MultiException
    }
}
