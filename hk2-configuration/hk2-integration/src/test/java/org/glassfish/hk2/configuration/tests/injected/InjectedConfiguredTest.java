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

package org.glassfish.hk2.configuration.tests.injected;

import java.util.HashMap;

import org.glassfish.hk2.api.MultiException;
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
public class InjectedConfiguredTest extends HK2Runner {
    /* package */ static final String CTEST_ONE_TYPE = "InjectedConfiguredTestType1";
    /* package */ static final String CTEST_TWO_TYPE = "InjectedConfiguredTestType2";
    
    /* package */ static final String ALICE = "Alice";
    /* package */ static final String BOB = "Bob";
    
    private final static String NAME_KEY = "name";
    private final static String CONFIGURED_VALUE_KEY = "configuredValue";
    private final static String DEFAULT_KEY = "default";
    
    private Hub hub;
    
    @Before
    public void before() {
        super.before();
        
        ConfigurationUtilities.enableConfigurationSystem(testLocator);
        
        hub = testLocator.getService(Hub.class);
    }
    
    private void addConfiguredValueBean(String typeName, long value) {
        WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
        
        WriteableType wt = wbd.findOrAddWriteableType(typeName);
        
        HashMap<String, Object> namedBean = new HashMap<String, Object>();
        namedBean.put(CONFIGURED_VALUE_KEY, new Long(value));
        
        wt.addInstance(DEFAULT_KEY, namedBean);
        
        wbd.commit();
    }
    
    private void addNamedBean(String typeName, String name) {
        WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
        
        WriteableType wt = wbd.findOrAddWriteableType(typeName);
        
        HashMap<String, Object> namedBean = new HashMap<String, Object>();
        namedBean.put(NAME_KEY, name);
        
        wt.addInstance(name, namedBean);
        
        wbd.commit();
    }
    
    private void removeType(String typeName) {
        WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
        
        wbd.removeType(typeName);
        
        wbd.commit();
    }
    
    private void removeNamedBean(String typeName, String name) {
        WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
        
        WriteableType wt = wbd.findOrAddWriteableType(typeName);
        
        wt.removeInstance(name);
        
        wbd.commit();
    }
    
    /**
     * Tests that a service specifically injected with a named
     * configured service works when the service is there and
     * does not work when it is not there
     */
    @Test
    public void testSpecificInjectionOfConfiguredService() {
        try {
            testLocator.getService(AliceInjectedService.class);
            Assert.fail("There is no ConfiguredContract with name alice, should have failed");
        }
        catch (MultiException me) {
            // expected
        }
        
        try {
            addNamedBean(CTEST_ONE_TYPE, BOB);
            
            try {
                testLocator.getService(AliceInjectedService.class);
                Assert.fail("Should still fail, only Bob is available");
            }
            catch (MultiException me) {
                // expected
            }
            
            addNamedBean(CTEST_ONE_TYPE, ALICE);
            
            {
                AliceInjectedService aliceService = testLocator.getService(AliceInjectedService.class);
                Assert.assertNotNull(aliceService);
                
                ConfiguredContract aliceContract = aliceService.getAlice();
                Assert.assertNotNull(aliceContract);
                Assert.assertEquals(ALICE, aliceContract.getName());
            }
            
            removeNamedBean(CTEST_ONE_TYPE, BOB);
            
            {
                AliceInjectedService aliceService = testLocator.getService(AliceInjectedService.class);
                Assert.assertNotNull(aliceService);
                
                ConfiguredContract aliceContract = aliceService.getAlice();
                Assert.assertNotNull(aliceContract);
                Assert.assertEquals(ALICE, aliceContract.getName());
            }
            
            removeNamedBean(CTEST_ONE_TYPE, ALICE);
            
            try {
                testLocator.getService(AliceInjectedService.class);
                Assert.fail("Alice was removed, new instance should not get created");
            }
            catch (MultiException me) {
                // expected
            }
            
        }
        finally {
            removeType(CTEST_ONE_TYPE);
        }
        
    }
    
    /**
     * Tests that a service injected with an IterableProvider
     * can be used to get specific instances
     */
    @Test
    public void testIterableProviderOfConfiguredService() {
        VariableInjectedService vis = testLocator.getService(VariableInjectedService.class);
        
        Assert.assertNull(vis.getNamedContract(ALICE));
        Assert.assertNull(vis.getNamedContract(BOB));
        
        try {
            addNamedBean(CTEST_ONE_TYPE, ALICE);
            
            Assert.assertEquals(ALICE, vis.getNamedContract(ALICE).getName());
            Assert.assertNull(vis.getNamedContract(BOB));
            
            removeNamedBean(CTEST_ONE_TYPE, ALICE);
            
            Assert.assertNull(vis.getNamedContract(ALICE));
            Assert.assertNull(vis.getNamedContract(BOB));
            
            addNamedBean(CTEST_ONE_TYPE, BOB);
            addNamedBean(CTEST_ONE_TYPE, ALICE);
            
            Assert.assertEquals(ALICE, vis.getNamedContract(ALICE).getName());
            Assert.assertEquals(BOB, vis.getNamedContract(BOB).getName());
            
        }
        finally {
            removeType(CTEST_ONE_TYPE);
        }
        
    }
    
    /**
     * Tests that a configured service can inject another
     * configured service
     */
    @Test
    public void testStackedConfiguredService() {
        try {
            addNamedBean(CTEST_ONE_TYPE, BOB);
            addConfiguredValueBean(CTEST_TWO_TYPE, 100L);
            
            StackedConfiguredService scs = testLocator.getService(StackedConfiguredService.class);
            Assert.assertNotNull(scs);
            
            Assert.assertEquals(100L, scs.getConfiguredValue());
            Assert.assertEquals(BOB, scs.getConfiguredContract().getName());
        }
        finally {
            removeType(CTEST_ONE_TYPE);
            removeType(CTEST_TWO_TYPE);
        }
        
    }

}
