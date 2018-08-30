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

package org.glassfish.hk2.xml.integration.test.tests;

import java.util.List;
import java.util.Map;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.xml.api.XmlRootHandle;
import org.glassfish.hk2.xml.api.XmlService;
import org.glassfish.hk2.xml.integration.test.LeafBean;
import org.glassfish.hk2.xml.integration.test.RootBean;
import org.glassfish.hk2.xml.integration.test.utilities.IntegrationTestUtilities;
import org.junit.Assert;
import org.junit.Test;

public class XmlIntegrationTest {
    private static final String NAME_KEY = "name";
    
    private static final String ALICE = "Alice";
    private static final String HATTER = "Hatter";
    
    /**
     * Tests integrating a child using a bean like map
     */
    @Test
    public void testHk2IntegrationMap() {
        ServiceLocator locator = IntegrationTestUtilities.createDomLocator(LeafMapService.class);
        
        createBenchmarkRoot(locator);
        
        List<LeafMapService> leaves = locator.getAllServices(LeafMapService.class);
        Assert.assertEquals(2, leaves.size());
        
        {
            LeafMapService aliceLeaf = leaves.get(0);
            Map<String, Object> aliceBean = aliceLeaf.getLeafAsMap();
            Assert.assertNotNull(aliceBean);
            Assert.assertEquals(ALICE, aliceBean.get(NAME_KEY));
        }
        
        {
            LeafMapService hatterLeaf = leaves.get(1);
            Map<String, Object> hatterBean = hatterLeaf.getLeafAsMap();
            Assert.assertNotNull(hatterBean);
            Assert.assertEquals(HATTER, hatterBean.get(NAME_KEY));
        }
        
    }
    
    /**
     * Tests integrating a child using a bean
     */
    @Test
    // @org.junit.Ignore
    public void testHk2IntegrationBean() {
        ServiceLocator locator = IntegrationTestUtilities.createLocator(LeafBeanService.class);
        
        createBenchmarkRoot(locator);
        
        List<LeafBeanService> leaves = locator.getAllServices(LeafBeanService.class);
        Assert.assertEquals(2, leaves.size());
        
        {
            LeafBeanService aliceLeaf = leaves.get(0);
            LeafBean aliceBean = aliceLeaf.getBeanAsBean();
            Assert.assertNotNull(aliceBean);
            Assert.assertEquals(ALICE, aliceBean.getName());
        }
        
        {
            LeafBeanService hatterLeaf = leaves.get(1);
            LeafBean hatterBean = hatterLeaf.getBeanAsBean();
            Assert.assertNotNull(hatterBean);
            Assert.assertEquals(HATTER, hatterBean.getName());
        }
        
    }
    
    private XmlRootHandle<RootBean> createBenchmarkRoot(ServiceLocator locator) {
        XmlService xmlService = locator.getService(XmlService.class);
        XmlRootHandle<RootBean> retVal = xmlService.createEmptyHandle(RootBean.class);
        
        retVal.addRoot();
        RootBean root = retVal.getRoot();
        
        root.addLeave(ALICE);
        root.addLeave(HATTER);
        
        return retVal;
    }
}
