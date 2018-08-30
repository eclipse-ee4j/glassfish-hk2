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

package org.glassfish.hk2.xml.test.dynamic.transaction;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Map;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.configuration.hub.api.Hub;
import org.glassfish.hk2.configuration.hub.api.Instance;
import org.glassfish.hk2.xml.api.XmlHandleTransaction;
import org.glassfish.hk2.xml.api.XmlRootHandle;
import org.glassfish.hk2.xml.api.XmlService;
import org.glassfish.hk2.xml.test.beans.DomainBean;
import org.glassfish.hk2.xml.test.beans.JMSServerBean;
import org.glassfish.hk2.xml.test.beans.MachineBean;
import org.glassfish.hk2.xml.test.beans.ServerBean;
import org.glassfish.hk2.xml.test.dynamic.merge.MergeTest;
import org.glassfish.hk2.xml.test.utilities.Utilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 */
public class TransactionTest {
    private final static String ALT_SUBNET = "0.0.255.255";
    private final static String MIXED_METAPHOR = "Mixed Metaphor";
    
    /**
     * Modifies two properties in one transaction
     * 
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Test 
    // @org.junit.Ignore
    public void testModifyTwoPropertiesInOneTransaction() throws Exception {
        ServiceLocator locator = Utilities.createLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        Hub hub = locator.getService(Hub.class);
        
        URL url = getClass().getClassLoader().getResource(MergeTest.DOMAIN1_FILE);
        
        XmlRootHandle<DomainBean> rootHandle = xmlService.unmarshal(url.toURI(), DomainBean.class);
        
        MergeTest.verifyDomain1Xml(rootHandle, hub, locator);
        
        DomainBean domain = rootHandle.getRoot();
        
        XmlHandleTransaction<DomainBean> transaction = rootHandle.lockForTransaction();
        try {
            Assert.assertEquals(rootHandle, transaction.getRootHandle());
            
            domain.setSubnetwork(ALT_SUBNET);
            domain.setTaxonomy(MIXED_METAPHOR);
        }
        finally {
            transaction.commit();
        }
        
        Assert.assertEquals(ALT_SUBNET, domain.getSubnetwork());
        Assert.assertEquals(MIXED_METAPHOR, domain.getTaxonomy());
        
        {
            Instance domainInstance = hub.getCurrentDatabase().getInstance(MergeTest.DOMAIN_TYPE, MergeTest.DOMAIN_INSTANCE);
            Assert.assertNotNull(domainInstance);
        
            Map<String, Object> domainMap = (Map<String, Object>) domainInstance.getBean();
            Assert.assertEquals(ALT_SUBNET, domainMap.get(MergeTest.SUBNET_TAG));
            Assert.assertEquals(MIXED_METAPHOR, domainMap.get(MergeTest.TAXONOMY_TAG));
        }
    }
    
    /**
     * Modifies two properties in one transaction but abandons the changes
     * 
     * @throws Exception
     */
    @Test 
    // @org.junit.Ignore
    public void testModifyTwoPropertiesInOneTransactionAbandon() throws Exception {
        ServiceLocator locator = Utilities.createLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        Hub hub = locator.getService(Hub.class);
        
        URL url = getClass().getClassLoader().getResource(MergeTest.DOMAIN1_FILE);
        
        XmlRootHandle<DomainBean> rootHandle = xmlService.unmarshal(url.toURI(), DomainBean.class);
        
        MergeTest.verifyDomain1Xml(rootHandle, hub, locator);
        
        DomainBean domain = rootHandle.getRoot();
        
        XmlHandleTransaction<DomainBean> transaction = rootHandle.lockForTransaction();
        try {
            Assert.assertEquals(rootHandle, transaction.getRootHandle());
            
            domain.setSubnetwork(ALT_SUBNET);
            domain.setTaxonomy(MIXED_METAPHOR);
        }
        finally {
            transaction.abandon();
        }
        
        // Nothing should have changed at all
        MergeTest.verifyDomain1Xml(rootHandle, hub, locator);
    }
    
    /**
     * Adds beans, removes beans and modifies the properties of
     * a few beans, ensures they all get done
     * 
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Test 
    // @org.junit.Ignore
    public void testAddRemoveModifySuccess() throws Exception {
        ServiceLocator locator = Utilities.createLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        Hub hub = locator.getService(Hub.class);
        
        URL url = getClass().getClassLoader().getResource(MergeTest.DOMAIN1_FILE);
        
        XmlRootHandle<DomainBean> rootHandle = xmlService.unmarshal(url.toURI(), DomainBean.class);
        
        MergeTest.verifyDomain1Xml(rootHandle, hub, locator);
        
        DomainBean domain = rootHandle.getRoot();
        
        XmlHandleTransaction<DomainBean> transaction = rootHandle.lockForTransaction();
        try {
            addRemoveAndModify(xmlService, domain);
        }
        finally {
            transaction.commit();
        }
        
        Assert.assertNull(domain.lookupJMSServer(MergeTest.DAVE_NAME));
        
        MachineBean eddie = domain.lookupMachine(MergeTest.EDDIE_NAME);
        Assert.assertNotNull(eddie);
        Assert.assertEquals(MergeTest.EDDIE_NAME, eddie.getName());
        
        ServerBean server1 = eddie.lookupServer(MergeTest.SERVER1_NAME);
        Assert.assertEquals(MergeTest.SERVER1_NAME, server1.getName());
        
        // First modify
        Assert.assertEquals(ALT_SUBNET, domain.getSubnetwork());
        
        JMSServerBean carol = domain.lookupJMSServer(MergeTest.CAROL_NAME);
        
        // This is another modify, not on the same bean
        Assert.assertEquals(MergeTest.LZ_COMPRESSION, carol.getCompressionAlgorithm());
        
        {
            Instance domainInstance = hub.getCurrentDatabase().getInstance(MergeTest.DOMAIN_TYPE, MergeTest.DOMAIN_INSTANCE);
            Assert.assertNotNull(domainInstance);
        
            Map<String, Object> domainMap = (Map<String, Object>) domainInstance.getBean();
            Assert.assertEquals(ALT_SUBNET, domainMap.get(MergeTest.SUBNET_TAG));
            Assert.assertNull(domainMap.get(MergeTest.TAXONOMY_TAG));
        }
        
        {
            Instance daveInstance = hub.getCurrentDatabase().getInstance(MergeTest.JMS_SERVER_TYPE, MergeTest.DAVE_INSTANCE);
            Assert.assertNull(daveInstance);
        }
        
        MergeTest.assertNameOnlyBean(hub, MergeTest.MACHINE_TYPE, MergeTest.EDDIE_INSTANCE, MergeTest.EDDIE_NAME);
        MergeTest.assertNameOnlyBean(hub, MergeTest.SERVER_TYPE, MergeTest.SERVER1_INSTANCE, MergeTest.SERVER1_NAME);
        
        {
            Instance carolInstance = hub.getCurrentDatabase().getInstance(MergeTest.JMS_SERVER_TYPE, MergeTest.JMS_SERVER_CAROL_INSTANCE);
            Assert.assertNotNull(carolInstance);
            
            Map<String, Object> carolMap = (Map<String, Object>) carolInstance.getBean();
            Assert.assertEquals(MergeTest.LZ_COMPRESSION, carolMap.get(MergeTest.COMPRESSION_TAG));
        }
        
        Assert.assertNull(locator.getService(JMSServerBean.class, MergeTest.DAVE_NAME));
        Assert.assertNotNull(locator.getService(MachineBean.class, MergeTest.EDDIE_NAME));
        Assert.assertNotNull(locator.getService(ServerBean.class, MergeTest.SERVER1_NAME));
    }
    
    /**
     * Adds beans, removes beans and modifies the properties of
     * a few beans, ensures none of them get done
     * 
     * @throws Exception
     */
    @Test 
    // @org.junit.Ignore
    public void testAddRemoveModifyAbandon() throws Exception {
        ServiceLocator locator = Utilities.createLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        Hub hub = locator.getService(Hub.class);
        
        URL url = getClass().getClassLoader().getResource(MergeTest.DOMAIN1_FILE);
        
        XmlRootHandle<DomainBean> rootHandle = xmlService.unmarshal(url.toURI(), DomainBean.class);
        
        MergeTest.verifyDomain1Xml(rootHandle, hub, locator);
        
        DomainBean domain = rootHandle.getRoot();
        
        XmlHandleTransaction<DomainBean> transaction = rootHandle.lockForTransaction();
        try {
            addRemoveAndModify(xmlService, domain);
        }
        finally {
            transaction.abandon();
        }
        
        // Make sure nothing actually happened
        MergeTest.verifyDomain1Xml(rootHandle, hub, locator);
    }
    
    /**
     * Adds beans, removes beans and modifies the properties of
     * a few beans, ensures none of them get done
     * 
     * @throws Exception
     */
    @Test 
    // @org.junit.Ignore
    public void testMarshalInsideATransaction() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        Hub hub = locator.getService(Hub.class);
        
        URL url = getClass().getClassLoader().getResource(MergeTest.DOMAIN1_FILE);
        
        XmlRootHandle<DomainBean> rootHandle = xmlService.unmarshal(url.toURI(), DomainBean.class);
        
        MergeTest.verifyDomain1Xml(rootHandle, hub, locator);
        
        DomainBean domain = rootHandle.getRoot();
        
        XmlHandleTransaction<DomainBean> transaction = rootHandle.lockForTransaction();
        boolean success = false;
        try {
            addRemoveAndModify(xmlService, domain);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                rootHandle.marshal(baos);
            }
            finally {
                baos.close();
            }
            
            String asString = baos.toString();
            
            // Make sure the updated tree is written to the output stream
            Assert.assertFalse(asString.contains(MergeTest.DAVE_NAME));
            Assert.assertTrue(asString.contains(MergeTest.EDDIE_NAME));
            Assert.assertTrue(asString.contains(MergeTest.SERVER1_NAME));
            Assert.assertFalse(asString.contains(MergeTest.DEFAULT_SUBNET));
            Assert.assertTrue(asString.contains(ALT_SUBNET));
            
            success = true;
        }
        finally {
            if (success) {
                transaction.commit();
            }
            else {
                transaction.abandon();
            }
        }
    }
    
    /**
     * Does this from the original state
     * 
     * @param domain
     */
    private static void addRemoveAndModify(XmlService xmlService, DomainBean domain) {
        // This is the remove
        JMSServerBean dave = domain.removeJMSServer(MergeTest.DAVE_NAME);
        Assert.assertNotNull(dave);
        
        MachineBean eddie = xmlService.createBean(MachineBean.class);
        eddie.setName(MergeTest.EDDIE_NAME);
        
        ServerBean server1 = xmlService.createBean(ServerBean.class);
        server1.setName(MergeTest.SERVER1_NAME);
        
        eddie.addServer(server1);
        
        // This is the add
        domain.addMachine(eddie);
        
        // This is the modify
        domain.setSubnetwork(ALT_SUBNET);
        
        JMSServerBean carol = domain.lookupJMSServer(MergeTest.CAROL_NAME);
        
        // This is another modify, not on the same bean
        carol.setCompressionAlgorithm(MergeTest.LZ_COMPRESSION);
    }

}
