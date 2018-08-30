/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.configuration.hub.test;

import javax.inject.Inject;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.configuration.hub.api.Hub;
import org.glassfish.hk2.configuration.hub.api.Instance;
import org.glassfish.hk2.configuration.hub.api.ManagerUtilities;
import org.glassfish.hk2.configuration.hub.api.WriteableBeanDatabase;
import org.glassfish.hk2.configuration.hub.api.WriteableType;
import org.junit.Assert;
import org.junit.Before;
import org.jvnet.hk2.testing.junit.HK2Runner;

/**
 * @author jwells
 *
 */
public class HubTestBase extends HK2Runner {
    @Inject
    protected DynamicConfigurationService dcs;
    
    protected Hub hub;
    
    @Before
    public void before() {
        super.before();
        
        // This is necessary to make running in an IDE easier
        ManagerUtilities.enableConfigurationHub(testLocator);
        
        this.hub = testLocator.getService(Hub.class);
    }
    
    protected void addType(String typeName) {
        addType(typeName, false);
    }
    
    protected void addType(String typeName, boolean asResource) {
        WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
        
        wbd.addType(typeName);
        
        if (asResource) {
            DynamicConfiguration dc = dcs.createDynamicConfiguration();
            
            dc.registerTwoPhaseResources(wbd.getTwoPhaseResource());
            
            dc.commit();
        }
        else {
            wbd.commit();
        }
    }
    
    protected void addTypeAndInstance(String typeName, String instanceKey, Object instanceValue) {
        addTypeAndInstance(typeName, instanceKey, instanceValue, false);
 
    }
    
    protected void addTypeAndInstance(String typeName, String instanceKey, Object instanceValue, boolean asResource) {
        WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
        
        WriteableType wt = wbd.findOrAddWriteableType(typeName);
        
        Instance added = wt.addInstance(instanceKey, instanceValue);
        Assert.assertNotNull(added);
        
        if (asResource) {
            DynamicConfiguration dc = dcs.createDynamicConfiguration();
            
            dc.registerTwoPhaseResources(wbd.getTwoPhaseResource());
            
            dc.commit();
        }
        else {
            wbd.commit();
        }
    }
    
    protected void addTypeAndInstance(String typeName, String instanceKey, Object instanceValue, Object metadata) {
        addTypeAndInstance(typeName, instanceKey, instanceValue, metadata, false);
    }
    
    protected void addTypeAndInstance(String typeName, String instanceKey, Object instanceValue, Object metadata, boolean asResource) {
        WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
        
        WriteableType wt = wbd.findOrAddWriteableType(typeName);
        
        wt.addInstance(instanceKey, instanceValue, metadata);
        
        if (asResource) {
            DynamicConfiguration dc = dcs.createDynamicConfiguration();
            
            dc.registerTwoPhaseResources(wbd.getTwoPhaseResource());
            
            dc.commit();
        }
        else {
            wbd.commit();
        }
    }
    
    protected void removeType(String typeName) {
        removeType(typeName, false);
    }
    
    protected void removeType(String typeName, boolean asResource) {
        WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
        
        wbd.removeType(typeName);
        
        if (asResource) {
            DynamicConfiguration dc = dcs.createDynamicConfiguration();
            
            dc.registerTwoPhaseResources(wbd.getTwoPhaseResource());
            
            dc.commit();
        }
        else {
            wbd.commit();
        }
    }

}
