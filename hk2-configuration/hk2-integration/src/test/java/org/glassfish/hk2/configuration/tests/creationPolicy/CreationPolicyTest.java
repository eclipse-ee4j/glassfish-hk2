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

package org.glassfish.hk2.configuration.tests.creationPolicy;

import org.glassfish.hk2.configuration.api.ConfigurationUtilities;
import org.glassfish.hk2.configuration.hub.api.Hub;
import org.glassfish.hk2.configuration.hub.api.WriteableBeanDatabase;
import org.glassfish.hk2.configuration.hub.api.WriteableType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvnet.hk2.testing.junit.HK2Runner;

/**
 * @author jwells
 *
 */
public class CreationPolicyTest extends HK2Runner {
    /* package */ static final String ON_DEMAND_TEST = "OnDemandType";
    /* package */ static final String EAGER_TEST = "EagerType";
    
    /* package */ static final String ONE = "One";
    /* package */ static final String TWO = "Two";
    
    private Hub hub;
    
    @Before
    public void before() {
        super.before();
        
        ConfigurationUtilities.enableConfigurationSystem(testLocator);
        
        hub = testLocator.getService(Hub.class);
    }
    
    private void createType(String typeName) {
        WriteableBeanDatabase database = hub.getWriteableDatabaseCopy();
        
        database.findOrAddWriteableType(typeName);
        
        database.commit();
    }
    
    private void removeType(String typeName) {
        WriteableBeanDatabase database = hub.getWriteableDatabaseCopy();
        
        database.removeType(typeName);
        
        database.commit();
    }
    
    private void addInstance(String typeName, String instanceName, CreationBean bean) {
        WriteableBeanDatabase database = hub.getWriteableDatabaseCopy();
        
        WriteableType type = database.getWriteableType(typeName);
        
        type.addInstance(instanceName, bean);
        
        database.commit();
    }
    
    /**
     * Tests that an on_demand service is not created until someone... uh... demands it!
     */
    @Test
    public void testOnDemandCreation() {
        try {
            Assert.assertNull(OnDemandConfiguredService.getInstance());
            
            createType(ON_DEMAND_TEST);
            
            Assert.assertNull(OnDemandConfiguredService.getInstance());
            
            addInstance(ON_DEMAND_TEST, ONE, new CreationBean(1));
            
            // Still null because this is on-demand, and no demand has yet been made
            Assert.assertNull(OnDemandConfiguredService.getInstance());
            
            OnDemandConfiguredService service = testLocator.getService(OnDemandConfiguredService.class);
            Assert.assertNotNull(service);
            
            Assert.assertEquals(1, service.getCreationNumber());
            
            Assert.assertEquals(service, OnDemandConfiguredService.getInstance());
        }
        finally {
            removeType(ON_DEMAND_TEST);
        }
        
        
    }
    
    /**
     * Tests that an eager service is created eagerly
     */
    @Test
    public void testEagerCreation() {
        try {
            Assert.assertNull(EagerConfiguredService.getInstance());
            
            createType(EAGER_TEST);
            
            // No instances yet, there cannot be a service yet
            Assert.assertNull(EagerConfiguredService.getInstance());
            
            addInstance(EAGER_TEST, TWO, new CreationBean(2));
            
            // Still null because this is on-demand, and no demand has yet been made
            Assert.assertNotNull(EagerConfiguredService.getInstance());
            
            Assert.assertEquals(2, EagerConfiguredService.getInstance().getCreationNumber());
            
            EagerConfiguredService service = testLocator.getService(EagerConfiguredService.class);
            Assert.assertNotNull(service);
            
            Assert.assertEquals(2, service.getCreationNumber());
            
            Assert.assertEquals(service, EagerConfiguredService.getInstance());
        }
        finally {
            removeType(ON_DEMAND_TEST);
        }
        
        
    }

}
