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

package org.glassfish.hk2.xml.test.defaulting;

import java.net.URL;

import javax.xml.namespace.QName;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.configuration.hub.api.Hub;
import org.glassfish.hk2.xml.api.XmlHk2ConfigurationBean;
import org.glassfish.hk2.xml.api.XmlRootHandle;
import org.glassfish.hk2.xml.api.XmlService;
import org.glassfish.hk2.xml.test.beans.DomainBean;
import org.glassfish.hk2.xml.test.beans.SSLManagerBean;
import org.glassfish.hk2.xml.test.beans.SecurityManagerBean;
import org.glassfish.hk2.xml.test.dynamic.merge.MergeTest;
import org.junit.Assert;

/**
 * @author jwells
 *
 */
public class DefaultingCommon {
private final static String DEFAULTING_FILE = "defaulted.xml";
    
    /**
     * Tests that we can default values with JAXB 
     * @throws Exception
     */
    public void testDefaultedValues(XmlService xmlService) throws Exception {
        URL url = getClass().getClassLoader().getResource(DEFAULTING_FILE);
        
        XmlRootHandle<DefaultedBean> rootHandle = xmlService.unmarshal(url.toURI(), DefaultedBean.class);
        DefaultedBean db = rootHandle.getRoot();
        
        XmlHk2ConfigurationBean asBean = (XmlHk2ConfigurationBean) db;
        Assert.assertFalse(asBean._isSet("int-prop"));
        Assert.assertFalse(asBean._isSet("long-prop"));
        Assert.assertFalse(asBean._isSet("short-prop"));
        Assert.assertFalse(asBean._isSet("byte-prop"));
        Assert.assertFalse(asBean._isSet("boolean-prop"));
        Assert.assertFalse(asBean._isSet("char-prop"));
        Assert.assertFalse(asBean._isSet("float-prop"));
        Assert.assertFalse(asBean._isSet("double-prop"));
        Assert.assertFalse(asBean._isSet("string-prop"));
        Assert.assertFalse(asBean._isSet("qname-prop"));
        Assert.assertFalse(asBean._isSet("color"));
        
        Assert.assertEquals(13, db.getIntProp());
        Assert.assertEquals(13L, db.getLongProp());
        Assert.assertEquals((byte) 13, db.getByteProp());
        Assert.assertEquals(true, db.isBooleanProp());
        Assert.assertEquals((short) 13, db.getShortProp());
        Assert.assertEquals('f', db.getCharProp());
        Assert.assertEquals(0, Float.compare((float) 13.00, db.getFloatProp()));
        Assert.assertEquals(0, Double.compare(13.00, db.getDoubleProp()));
        Assert.assertEquals("13", db.getStringProp());
        Assert.assertEquals(new QName("http://qwerty.com/qwerty", "foo", "xyz"), db.getQNameProp());
        Assert.assertEquals(Colors.GREEN, db.getColor());
        
        // Now set them all to the default values and make sure "isSet" works properly
        // db.setIntProp(13); we will have to trust that this one works
        db.setLongProp(13L);
        db.setByteProp((byte) 13);
        db.setBooleanProp(true);
        db.setShortProp((short) 13); 
        db.setCharProp('f');
        db.setFloatProp((float) 13.00);
        db.setDoubleProp(13.00);
        db.setStringProp("13");
        db.setQNameProp(new QName("http://qwerty.com/qwerty", "foo", "xyz"));
        db.setColor(Colors.GREEN);
        
        // Check the SET default values (for completeness)
        Assert.assertEquals(13, db.getIntProp());
        Assert.assertEquals(13L, db.getLongProp());
        Assert.assertEquals((byte) 13, db.getByteProp());
        Assert.assertEquals(true, db.isBooleanProp());
        Assert.assertEquals((short) 13, db.getShortProp());
        Assert.assertEquals('f', db.getCharProp());
        Assert.assertEquals(0, Float.compare((float) 13.00, db.getFloatProp()));
        Assert.assertEquals(0, Double.compare(13.00, db.getDoubleProp()));
        Assert.assertEquals("13", db.getStringProp());
        Assert.assertEquals(new QName("http://qwerty.com/qwerty", "foo", "xyz"), db.getQNameProp());
        Assert.assertEquals(Colors.GREEN, db.getColor());
        
        // First one still false, need to test this without a setter
        Assert.assertFalse(asBean._isSet("int-prop"));
        
        // But all the rest should be true now
        Assert.assertTrue(asBean._isSet("long-prop"));
        Assert.assertTrue(asBean._isSet("short-prop"));
        Assert.assertTrue(asBean._isSet("byte-prop"));
        Assert.assertTrue(asBean._isSet("boolean-prop"));
        Assert.assertTrue(asBean._isSet("char-prop"));
        Assert.assertTrue(asBean._isSet("float-prop"));
        Assert.assertTrue(asBean._isSet("double-prop"));
        Assert.assertTrue(asBean._isSet("string-prop"));
        Assert.assertTrue(asBean._isSet("qname-prop"));
        Assert.assertTrue(asBean._isSet("color"));
    }
    
    /**
     * Tests that we can default values with JAXB 
     * @throws Exception
     */
    public void testDefaultDefaultedValues(XmlService xmlService) throws Exception {
        URL url = getClass().getClassLoader().getResource(DEFAULTING_FILE);
        
        XmlRootHandle<DefaultedBean> rootHandle = xmlService.unmarshal(url.toURI(), DefaultedBean.class);
        DefaultedBean db = rootHandle.getRoot();
        
        Assert.assertEquals(0, db.getDefaultIntProp());
        Assert.assertEquals(0L, db.getDefaultLongProp());
        Assert.assertEquals((byte) 0, db.getDefaultByteProp());
        Assert.assertEquals(false, db.isDefaultBooleanProp());
        Assert.assertEquals((short) 0, db.getDefaultShortProp());
        Assert.assertEquals((char) 0, db.getDefaultCharProp());
        Assert.assertEquals(0, Float.compare((float) 0.00, db.getDefaultFloatProp()));
        Assert.assertEquals(0, Double.compare(0.00, db.getDefaultDoubleProp()));
        Assert.assertEquals(null, db.getDefaultStringProp());
        Assert.assertNull(db.getDefaultQNameProp());
        Assert.assertNull(db.getDefaultColor());
    }
    
    /**
     * Tests that defaults work in a dynamically created bean
     */
    public void testCanGetValuesFromDynamicallyCreatedBean(XmlService xmlService) {
        DefaultedBean db = xmlService.createBean(DefaultedBean.class);
        
        Assert.assertEquals(13, db.getIntProp());
        Assert.assertEquals(13L, db.getLongProp());
        Assert.assertEquals((byte) 13, db.getByteProp());
        Assert.assertEquals(true, db.isBooleanProp());
        Assert.assertEquals((short) 13, db.getShortProp());
        Assert.assertEquals('f', db.getCharProp());
        Assert.assertEquals(0, Float.compare((float) 13.00, db.getFloatProp()));
        Assert.assertEquals(0, Double.compare(13.00, db.getDoubleProp()));
        Assert.assertEquals("13", db.getStringProp());
        Assert.assertEquals(Colors.GREEN, db.getColor());
        
        Assert.assertEquals(0, db.getDefaultIntProp());
        Assert.assertEquals(0L, db.getDefaultLongProp());
        Assert.assertEquals((byte) 0, db.getDefaultByteProp());
        Assert.assertEquals(false, db.isDefaultBooleanProp());
        Assert.assertEquals((short) 0, db.getDefaultShortProp());
        Assert.assertEquals((char) 0, db.getDefaultCharProp());
        Assert.assertEquals(0, Float.compare((float) 0.00, db.getDefaultFloatProp()));
        Assert.assertEquals(0, Double.compare(0.00, db.getDefaultDoubleProp()));
        Assert.assertEquals(null, db.getDefaultStringProp());
        Assert.assertNull(db.getDefaultColor());
        
    }
    
    /**
     * Ensures that we can default beans via an InstanceLifecycleListener
     * 
     * @throws Exception
     */
    public void testDefaultingViaServiceWorks(ServiceLocator locator, XmlService xmlService) throws Exception {
        Hub hub = locator.getService(Hub.class);
        
        URL url = getClass().getClassLoader().getResource(MergeTest.DOMAIN1_FILE);
        
        XmlRootHandle<DomainBean> rootHandle = xmlService.unmarshal(url.toURI(), DomainBean.class);
        
        MergeTest.verifyDomain1Xml(rootHandle, hub, locator, true);
        
        SecurityManagerBean smb = locator.getService(SecurityManagerBean.class);
        Assert.assertNotNull(smb);
        
        Assert.assertNotNull(smb.getSSLManager());
        
        Assert.assertNotNull(locator.getService(SSLManagerBean.class));
    }
    
    /**
     * Ensures that we can default beans via an InstanceLifecycleListener
     * 
     * @throws Exception
     */
    public void testDefaultingViaAddWorks(ServiceLocator locator, XmlService xmlService) throws Exception {
        Hub hub = locator.getService(Hub.class);
        
        URL url = getClass().getClassLoader().getResource(MergeTest.DOMAIN1_FILE);
        
        XmlRootHandle<DomainBean> rootHandle = xmlService.unmarshal(url.toURI(), DomainBean.class);
        
        MergeTest.verifyDomain1Xml(rootHandle, hub, locator, true);
        
        DomainBean domain = rootHandle.getRoot();
        
        // Removes the securityManager
        domain.setSecurityManager(null);
        
        // Just makes sure the SSLManager added as a default is gone
        Assert.assertNull(locator.getService(SSLManagerBean.class));
        
        SecurityManagerBean smb = xmlService.createBean(SecurityManagerBean.class);
        
        domain.setSecurityManager(smb);
        
        smb = domain.getSecurityManager();
        Assert.assertNotNull(smb);
        
        // For this test to fail properly this MUST be before the lookup in the locator
        Assert.assertNotNull(smb.getSSLManager());
        Assert.assertNotNull(locator.getService(SecurityManagerBean.class));
        
        Assert.assertNotNull(smb.getSSLManager());
        Assert.assertNotNull(locator.getService(SSLManagerBean.class));
    }

}
