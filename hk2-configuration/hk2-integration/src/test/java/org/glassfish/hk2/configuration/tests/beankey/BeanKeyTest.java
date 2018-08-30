/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.configuration.tests.beankey;

import java.util.List;
import java.util.Map;

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
public class BeanKeyTest extends HK2Runner {
    public final static String TYPE = "/looks-like/xml/path";
    
    private final static String ALICE = "Alice";
    private final static String BOB = "Bob";
    private final static String CAROL = "Carol";
    
    private Hub hub;
    
    @Before
    public void before() {
        super.before();
        
        ConfigurationUtilities.enableConfigurationSystem(testLocator);
        
        hub = testLocator.getService(Hub.class);
    }
    
    /**
     * Tests that beans can be injected with BEAN_KEY two different ways
     */
    @Test
    // @org.junit.Ignore
    public void testDifferentBeanKeyTypesGetInjectedProperly() {
        addBeanLikeMapWithBeanMetadataToHub(ALICE, 0);
        addBeanLikeMapWithBeanMetadataToHub(BOB, 1);
        addBeanLikeMapWithBeanMetadataToHub(CAROL, 2);
        
        List<BeanKeyService> allServices = testLocator.getAllServices(BeanKeyService.class);
        Assert.assertEquals(3, allServices.size());
        
        checkService(allServices.get(0), ALICE, 0);
        checkService(allServices.get(1), BOB, 1);
        checkService(allServices.get(2), CAROL, 2);
    }
    
    private static void checkService(BeanKeyService service, String name, int data) {
        MultiBean mb = service.getAsBean();
        Assert.assertEquals(name, mb.getName());
        Assert.assertEquals(data, mb.getData());
        
        Map<String, Object> asMap = service.getAsMap();
        Assert.assertEquals(name, asMap.get("name"));
        Assert.assertEquals(new Integer(data), asMap.get("data"));
    }
    
    private void addBeanLikeMapWithBeanMetadataToHub(String name, int data) {
        WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
        
        WriteableType type = wbd.findOrAddWriteableType(TYPE);
        
        MultiBean mb = new MultiBean();
        mb.setName(name);
        mb.setData(data);
        
        Map<String, Object> beanLikeMap = mb.getBeanAsMap();
        
        type.addInstance(name, beanLikeMap, mb);
        
        wbd.commit();
        
    }
    

}
