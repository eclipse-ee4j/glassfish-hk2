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

package org.glassfish.hk2.configuration.properties.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import org.glassfish.hk2.configuration.hub.api.Hub;
import org.glassfish.hk2.configuration.hub.api.Instance;
import org.glassfish.hk2.configuration.hub.api.WriteableBeanDatabase;
import org.glassfish.hk2.configuration.hub.api.WriteableType;
import org.glassfish.hk2.configuration.persistence.properties.PropertyFileBean;
import org.glassfish.hk2.configuration.persistence.properties.PropertyFileHandle;
import org.glassfish.hk2.configuration.persistence.properties.PropertyFileService;
import org.glassfish.hk2.configuration.persistence.properties.PropertyFileUtilities;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvnet.hk2.testing.junit.HK2Runner;

/**
 * @author jwells
 *
 */
public class PropertiesTest extends HK2Runner {
    private final static String TYPE1 = "T1";
    private final static String TYPE2 = "T2";
    private final static String TYPE3 = "T3";
    private final static String TYPE4 = "T4";
    
    private final static String INSTANCE1 = "I1";
    private final static String INSTANCE2 = "I2";
    
    private final static String DEFAULT_INSTANCE_NAME = "DEFAULT_INSTANCE_NAME";
    
    private final static String NAME = "name";
    private final static String OTHER = "other";
    
    private final static String ALICE = "alice";
    private final static String BOB = "bob";
    private final static String CAROL = "carol";
    
    private final static String OTHER_VALUE1 = "V1";
    private final static String OTHER_VALUE2 = "V2";
    
    private Hub hub;
    
    @Before
    public void before() {
        super.before();
        
        PropertyFileUtilities.enablePropertyFileService(testLocator);
        
        hub = testLocator.getService(Hub.class);
    }
    
    private void removeType(String typeName) {
        WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
        
        wbd.removeType(typeName);
        
        wbd.commit();
    }
    
    private void removeInstance(String typeName, String instanceName) {
        WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
        
        WriteableType wt = wbd.getWriteableType(typeName);
        
        wt.removeInstance(instanceName);
        
        wbd.commit();
    }
    
    private InputStream getInputResource(String resource) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource(resource);
        return url.openStream();
    }
    
    /**
     * Tests reading a property file
     * 
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    @Test // @org.junit.Ignore
    public void testBasicPropertyFile() throws IOException {
        removeType(TYPE1);
        removeType(TYPE2);
        
        PropertyFileService pfs = testLocator.getService(PropertyFileService.class);
        PropertyFileHandle pfh = pfs.createPropertyHandleOfAnyType();
        
        InputStream is = getInputResource("multiPropV1.txt");
        try {
            Properties p = new Properties();
        
            p.load(is);
            
            pfh.readProperties(p);
            
            {
                Map<String, Object> instance1 = (Map<String, Object>) hub.getCurrentDatabase().getInstance(TYPE1, INSTANCE1).getBean();
                Assert.assertEquals("I1", instance1.get("name"));
                Assert.assertEquals("300 Packer Blvd", instance1.get("address"));
                Assert.assertEquals("Philadelphia", instance1.get("city"));
                Assert.assertEquals("PA", instance1.get("state"));
                Assert.assertEquals("07936", instance1.get("zip"));
            }
            
            {
                Map<String, Object> instance2 = (Map<String, Object>) hub.getCurrentDatabase().getInstance(TYPE1, INSTANCE2).getBean();
                Assert.assertEquals("I2", instance2.get("name"));
                Assert.assertEquals("310 Packer Blvd", instance2.get("address"));
                Assert.assertEquals("Philadelphia", instance2.get("city"));
                Assert.assertEquals("PA", instance2.get("state"));
                Assert.assertEquals("08008", instance2.get("zip"));
            }
            
            {
                Map<String, Object> instance1 = (Map<String, Object>) hub.getCurrentDatabase().getInstance(TYPE2, INSTANCE1).getBean();
                Assert.assertEquals("I1", instance1.get("name"));
                Assert.assertEquals("Taylor Avenue", instance1.get("address"));
                Assert.assertEquals("Beach Haven", instance1.get("city"));
                Assert.assertEquals("NJ", instance1.get("state"));
                Assert.assertEquals("08007", instance1.get("zip"));
            }
        }
        finally {
            is.close();
            pfh.dispose();
            
            removeType(TYPE1);
            removeType(TYPE2);
        }
    }
    
    /**
     * Tests adding a bean via properties that is backed by a java bean rather than a map
     */
    @Test // @org.junit.Ignore
    public void addJavaBeanBackedPropertyMap() {
        removeType(FooBean.TYPE_NAME);
        
        PropertyFileBean propertyFileBean = new PropertyFileBean();
        propertyFileBean.addTypeMapping(FooBean.TYPE_NAME, FooBean.class);
        
        PropertyFileService pfs = testLocator.getService(PropertyFileService.class);
        pfs.addPropertyFileBean(propertyFileBean);
        
        pfs.addPropertyFileBean(propertyFileBean);
        PropertyFileHandle pfh = pfs.createPropertyHandleOfSpecificType(FooBean.TYPE_NAME,
                DEFAULT_INSTANCE_NAME);
        
        try {
            Properties p = new Properties();
            
            p.put("fooBool", "true");
            p.put("fooShort", "13");
            p.put("fooInt", "14");
            p.put("fooLong", "-15");
            p.put("fooFloat", "16.0");
            p.put("fooDouble", "17.00");
            p.put("fooChar", "e");
            p.put("fooString", "Eagles");
            p.put("fooByte", "18");
            p.put("fooFile", ".");
            p.put("snmpValue", "19");
            
            pfh.readProperties(p);
            
            Object o = hub.getCurrentDatabase().getInstance(FooBean.TYPE_NAME, DEFAULT_INSTANCE_NAME).getBean();
            Assert.assertNotNull(o);
            Assert.assertTrue(o instanceof FooBean);
            
            FooBean fooBean = (FooBean) o;
            
            Assert.assertEquals(true, fooBean.isFooBool());
            Assert.assertEquals(13, fooBean.getFooShort());
            Assert.assertEquals(14, fooBean.getFooInt());
            Assert.assertEquals(-15L, fooBean.getFooLong());
            Assert.assertEquals((float) 16.00, fooBean.getFooFloat(), 1);
            Assert.assertEquals((double) 17.00, fooBean.getFooDouble(), 1);
            Assert.assertEquals('e', fooBean.getFooChar());
            Assert.assertEquals("Eagles", fooBean.getFooString());
            Assert.assertEquals((byte) 18, fooBean.getFooByte());
            Assert.assertEquals(new File("."), fooBean.getFooFile());
            Assert.assertEquals("19", fooBean.getSNMPValue());
        }
        finally {
            pfs.removePropertyFileBean();
            
            removeType(FooBean.TYPE_NAME);
        }
        
    }
    
    private static String instanceAndParamKey(String instance, String param) {
        return instance + "." + param;
    }
    
    @SuppressWarnings("unchecked")
    private String getHubValue(String type, String instance, String param) {
        Instance i = hub.getCurrentDatabase().getInstance(type, instance);
        if (i == null) return null;
        Map<String, Object> blm = (Map<String,Object>) i.getBean();
        if (blm == null) return null;
        
        return (String) blm.get(param);
    }
    
    /**
     * Tests removing instances
     * 
     * @throws IOException
     */
    @Test
    public void testInstanceRemoval() throws IOException {
        removeType(TYPE3);
        
        PropertyFileService pfs = testLocator.getService(PropertyFileService.class);
        PropertyFileHandle pfh = pfs.createPropertyHandleOfSpecificType(TYPE3);
        
        try {
            Properties p = new Properties();
            
            p.put(instanceAndParamKey(ALICE, NAME), ALICE);
            p.put(instanceAndParamKey(BOB, NAME), BOB);
            p.put(instanceAndParamKey(CAROL, NAME), CAROL);
            
            pfh.readProperties(p);
            
            Assert.assertEquals(ALICE, getHubValue(TYPE3, ALICE, NAME));
            Assert.assertEquals(BOB, getHubValue(TYPE3, BOB, NAME));
            Assert.assertEquals(CAROL, getHubValue(TYPE3, CAROL, NAME));
            
            p.remove(instanceAndParamKey(BOB,NAME));
            
            pfh.readProperties(p);
            
            Assert.assertEquals(ALICE, getHubValue(TYPE3, ALICE, NAME));
            Assert.assertNull(getHubValue(TYPE3, BOB, NAME));
            Assert.assertEquals(CAROL, getHubValue(TYPE3, CAROL, NAME));
            
            p.remove(instanceAndParamKey(ALICE,NAME));
            
            pfh.readProperties(p);
            
            Assert.assertNull(getHubValue(TYPE3, ALICE, NAME));
            Assert.assertNull(getHubValue(TYPE3, BOB, NAME));
            Assert.assertEquals(CAROL, getHubValue(TYPE3, CAROL, NAME));
            
            p.remove(instanceAndParamKey(CAROL, NAME));
            
            pfh.readProperties(p);
            
            Assert.assertNull(getHubValue(TYPE3, ALICE, NAME));
            Assert.assertNull(getHubValue(TYPE3, BOB, NAME));
            Assert.assertNull(getHubValue(TYPE3, CAROL, NAME));
            
        }
        finally {
            pfh.dispose();
            
            removeType(TYPE3);
        }
    }
    
    /**
     * Tests modifying an instance
     * 
     * @throws IOException
     */
    @Test
    public void testInstanceModification() throws IOException {
        removeType(TYPE4);
        
        PropertyFileService pfs = testLocator.getService(PropertyFileService.class);
        PropertyFileHandle pfh = pfs.createPropertyHandleOfAnyType(TYPE4, CAROL);
        
        try {
            Properties p = new Properties();
            
            p.put(instanceAndParamKey(ALICE, NAME), ALICE);
            p.put(instanceAndParamKey(ALICE, OTHER), OTHER_VALUE1);
            
            pfh.readProperties(p);
            
            Assert.assertEquals(ALICE, getHubValue(TYPE4, ALICE, NAME));
            Assert.assertEquals(OTHER_VALUE1, getHubValue(TYPE4, ALICE, OTHER));
            
            p.put(instanceAndParamKey(ALICE, OTHER), OTHER_VALUE2);
            
            pfh.readProperties(p);
            
            Assert.assertEquals(ALICE, getHubValue(TYPE4, ALICE, NAME));
            Assert.assertEquals(OTHER_VALUE2, getHubValue(TYPE4, ALICE, OTHER));
        }
        finally {
            pfh.dispose();
            
            removeType(TYPE4);
        }
    }
    
    /**
     * Tests modifying and instance that was deleted outside the control of the
     * PropertyFileHandle (should act like an add)
     * 
     * @throws IOException
     */
    @Test
    public void testModifyDeletedInstance() throws IOException {
        removeType(TYPE4);
        
        PropertyFileService pfs = testLocator.getService(PropertyFileService.class);
        PropertyFileHandle pfh = pfs.createPropertyHandleOfAnyType(TYPE4, CAROL);
        
        try {
            Properties p = new Properties();
            
            p.put(instanceAndParamKey(ALICE, NAME), ALICE);
            p.put(instanceAndParamKey(ALICE, OTHER), OTHER_VALUE1);
            
            pfh.readProperties(p);
            
            Assert.assertEquals(ALICE, getHubValue(TYPE4, ALICE, NAME));
            Assert.assertEquals(OTHER_VALUE1, getHubValue(TYPE4, ALICE, OTHER));
            
            removeInstance(TYPE4, ALICE);
            
            Assert.assertNull(getHubValue(TYPE4, ALICE, NAME));
            Assert.assertNull(getHubValue(TYPE4, ALICE, OTHER));
            
            p.put(instanceAndParamKey(ALICE, OTHER), OTHER_VALUE2);
            
            pfh.readProperties(p);
            
            Assert.assertEquals(ALICE, getHubValue(TYPE4, ALICE, NAME));
            Assert.assertEquals(OTHER_VALUE2, getHubValue(TYPE4, ALICE, OTHER));
        }
        finally {
            pfh.dispose();
            
            removeType(TYPE4);
        }
    }
    
    /**
     * Tests adding and instance that was previously already added.
     * Should act like a modify
     * 
     * @throws IOException
     */
    @Test
    public void testAddAlreadyExistingInstance() throws IOException {
        removeType(TYPE4);
        
        PropertyFileService pfs = testLocator.getService(PropertyFileService.class);
        PropertyFileHandle pfh1 = pfs.createPropertyHandleOfAnyType(TYPE4, CAROL);
        PropertyFileHandle pfh2 = pfs.createPropertyHandleOfAnyType(TYPE4, CAROL);
        
        try {
            Properties p = new Properties();
            
            p.put(instanceAndParamKey(ALICE, NAME), ALICE);
            p.put(instanceAndParamKey(ALICE, OTHER), OTHER_VALUE1);
            
            pfh1.readProperties(p);
            
            Assert.assertEquals(ALICE, getHubValue(TYPE4, ALICE, NAME));
            Assert.assertEquals(OTHER_VALUE1, getHubValue(TYPE4, ALICE, OTHER));
            
            p.put(instanceAndParamKey(ALICE, OTHER), OTHER_VALUE2);
            
            pfh2.readProperties(p);
            
            Assert.assertEquals(ALICE, getHubValue(TYPE4, ALICE, NAME));
            Assert.assertEquals(OTHER_VALUE2, getHubValue(TYPE4, ALICE, OTHER));
        }
        finally {
            pfh1.dispose();
            pfh2.dispose();
            
            removeType(TYPE4);
        }
    }

}
