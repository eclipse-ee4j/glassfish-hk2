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

package org.glassfish.hk2.xml.test.interop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.json.api.JsonUtilities;
import org.glassfish.hk2.pbuf.api.PBufUtilities;
import org.glassfish.hk2.xml.api.XmlRootHandle;
import org.glassfish.hk2.xml.api.XmlService;
import org.glassfish.hk2.xml.spi.XmlServiceParser;
import org.glassfish.hk2.xml.test1.utilities.Utilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class InteropTest {
    private final static String STANDARD_XML = "standard.xml";
    private final static String STANDARD_PBUF = "standard.pbuf";
    private final static String STANDARD_JSON = "standard.json";
    
    private final static String ALICE = "Alice";
    private final static int ALICE_ADDRESS = 100;
    
    private final static String BOB = "Bob";
    private final static int BOB_ADDRESS = 200;
    
    /**
     * Reads an XML file, spits out JSON
     */
    @Test
    public void testXml2Json() throws Exception {
        ServiceLocator locator = Utilities.createInteropLocator();
        
        XmlService xmlXmlService = locator.getService(XmlService.class, XmlServiceParser.STREAM_PARSING_SERVICE);
        Assert.assertNotNull(xmlXmlService);
        
        XmlService jsonXmlService = locator.getService(XmlService.class, JsonUtilities.JSON_SERVICE_NAME);
        Assert.assertNotNull(jsonXmlService);
        
        ClassLoader loader = getClass().getClassLoader();
        URL url = loader.getResource(STANDARD_XML);
        
        XmlRootHandle<InteropRootBean> xmlHandle = xmlXmlService.unmarshal(url.toURI(), InteropRootBean.class);
        validateStandardBean(xmlHandle);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            jsonXmlService.marshal(baos, xmlHandle);
        }
        finally {
            baos.close();
        }
        
        byte asBytes[] = baos.toByteArray();
        String asString = new String(asBytes);
        
        Assert.assertTrue("Incorrect String: " + asString, asString.contains("\"name\": \"Alice\""));
        Assert.assertTrue(asString.contains("\"name\": \"Bob\""));
        
        Assert.assertTrue(asString.contains("\"houseNumber\": 100"));
        Assert.assertTrue(asString.contains("\"houseNumber\": 200"));
    }
    
    /**
     * Reads an XML file, spits out pbuf
     */
    @Test
    public void testXml2PBuf() throws Exception {
        ServiceLocator locator = Utilities.createInteropLocator();
        
        XmlService xmlXmlService = locator.getService(XmlService.class, XmlServiceParser.STREAM_PARSING_SERVICE);
        Assert.assertNotNull(xmlXmlService);
        
        XmlService pbufXmlService = locator.getService(XmlService.class, PBufUtilities.PBUF_SERVICE_NAME);
        Assert.assertNotNull(pbufXmlService);
        
        ClassLoader loader = getClass().getClassLoader();
        URL url = loader.getResource(STANDARD_XML);
        
        XmlRootHandle<InteropRootBean> xmlHandle = xmlXmlService.unmarshal(url.toURI(), InteropRootBean.class);
        validateStandardBean(xmlHandle);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            pbufXmlService.marshal(baos, xmlHandle);
        }
        finally {
            baos.close();
        }
        
        byte asBytes[] = baos.toByteArray();
        
        /*
        File f = new File("jrw.pbuf");
        FileOutputStream fox = new FileOutputStream(f);
        fox.write(asBytes);
        fox.close();
        */
        
        URL pbufURL = loader.getResource(STANDARD_PBUF);
        
        byte benchmark[] = readURLCompletely(pbufURL);
        
        Assert.assertTrue(Arrays.equals(asBytes, benchmark));
    }
    
    /**
     * Reads an JSON file, spits out pbuf
     */
    @Test
    public void testJson2PBuf() throws Exception {
        ServiceLocator locator = Utilities.createInteropLocator();
        
        XmlService jsonXmlService = locator.getService(XmlService.class, JsonUtilities.JSON_SERVICE_NAME);
        Assert.assertNotNull(jsonXmlService);
        
        XmlService pbufXmlService = locator.getService(XmlService.class, PBufUtilities.PBUF_SERVICE_NAME);
        Assert.assertNotNull(pbufXmlService);
        
        ClassLoader loader = getClass().getClassLoader();
        URL url = loader.getResource(STANDARD_JSON);
        
        XmlRootHandle<InteropRootBean> xmlHandle = jsonXmlService.unmarshal(url.toURI(), InteropRootBean.class);
        validateStandardBean(xmlHandle);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            pbufXmlService.marshal(baos, xmlHandle);
        }
        finally {
            baos.close();
        }
        
        byte asBytes[] = baos.toByteArray();
        
        URL pbufURL = loader.getResource(STANDARD_PBUF);
        
        byte benchmark[] = readURLCompletely(pbufURL);
        
        Assert.assertTrue(Arrays.equals(asBytes, benchmark));
    }
    
    /**
     * Reads an JSON file, spits out Xml
     */
    @Test
    public void testJson2Xml() throws Exception {
        ServiceLocator locator = Utilities.createInteropLocator();
        
        XmlService jsonXmlService = locator.getService(XmlService.class, JsonUtilities.JSON_SERVICE_NAME);
        Assert.assertNotNull(jsonXmlService);
        
        XmlService xmlXmlService = locator.getService(XmlService.class, XmlServiceParser.STREAM_PARSING_SERVICE);
        Assert.assertNotNull(xmlXmlService);
        
        ClassLoader loader = getClass().getClassLoader();
        URL url = loader.getResource(STANDARD_JSON);
        
        XmlRootHandle<InteropRootBean> xmlHandle = jsonXmlService.unmarshal(url.toURI(), InteropRootBean.class);
        validateStandardBean(xmlHandle);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            xmlXmlService.marshal(baos, xmlHandle);
        }
        finally {
            baos.close();
        }
        
        byte asBytes[] = baos.toByteArray();
        String asString = new String(asBytes);
        
        Assert.assertTrue(asString.contains("<houseNumber>100</houseNumber>"));
        Assert.assertTrue(asString.contains("<houseNumber>200</houseNumber>"));
        
        Assert.assertTrue(asString.contains("<name>Alice</name>"));
        Assert.assertTrue(asString.contains("<name>Bob</name>"));
    }
    
    /**
     * Reads a pbuf file, spits out Xml
     */
    @Test
    public void testPbuf2Xml() throws Exception {
        ServiceLocator locator = Utilities.createInteropLocator();
        
        XmlService pbufXmlService = locator.getService(XmlService.class, PBufUtilities.PBUF_SERVICE_NAME);
        Assert.assertNotNull(pbufXmlService);
        
        XmlService xmlXmlService = locator.getService(XmlService.class, XmlServiceParser.STREAM_PARSING_SERVICE);
        Assert.assertNotNull(xmlXmlService);
        
        ClassLoader loader = getClass().getClassLoader();
        URL url = loader.getResource(STANDARD_PBUF);
        
        XmlRootHandle<InteropRootBean> xmlHandle = pbufXmlService.unmarshal(url.toURI(), InteropRootBean.class);
        validateStandardBean(xmlHandle);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            xmlXmlService.marshal(baos, xmlHandle);
        }
        finally {
            baos.close();
        }
        
        byte asBytes[] = baos.toByteArray();
        String asString = new String(asBytes);
        
        Assert.assertTrue(asString.contains("<houseNumber>100</houseNumber>"));
        Assert.assertTrue(asString.contains("<houseNumber>200</houseNumber>"));
        
        Assert.assertTrue(asString.contains("<name>Alice</name>"));
        Assert.assertTrue(asString.contains("<name>Bob</name>"));
    }
    
    /**
     * Reads a pbuf file, spits out JSON
     */
    @Test
    public void testPbuf2Json() throws Exception {
        ServiceLocator locator = Utilities.createInteropLocator();
        
        XmlService pbufXmlService = locator.getService(XmlService.class, PBufUtilities.PBUF_SERVICE_NAME);
        Assert.assertNotNull(pbufXmlService);
        
        XmlService jsonXmlService = locator.getService(XmlService.class, JsonUtilities.JSON_SERVICE_NAME);
        Assert.assertNotNull(jsonXmlService);
        
        ClassLoader loader = getClass().getClassLoader();
        URL url = loader.getResource(STANDARD_PBUF);
        
        XmlRootHandle<InteropRootBean> xmlHandle = pbufXmlService.unmarshal(url.toURI(), InteropRootBean.class);
        validateStandardBean(xmlHandle);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            jsonXmlService.marshal(baos, xmlHandle);
        }
        finally {
            baos.close();
        }
        
        byte asBytes[] = baos.toByteArray();
        String asString = new String(asBytes);
        
        Assert.assertTrue(asString.contains("\"name\": \"Alice\""));
        Assert.assertTrue(asString.contains("\"name\": \"Bob\""));
        
        Assert.assertTrue(asString.contains("\"houseNumber\": 100"));
        Assert.assertTrue(asString.contains("\"houseNumber\": 200"));
    }
    
    private static byte[] readURLCompletely(URL url) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try {
            InputStream is = url.openStream();
            
            try {
                byte buf[] = new byte[1000];
                
                int received = 0;
                while((received = is.read(buf)) >= 0) {
                    baos.write(buf, 0, received);
                }
            }
            finally {
                is.close();
            }
            
        }
        finally {
            baos.close();
        }
        
        return baos.toByteArray();
    }
    
    private static void validateStandardBean(XmlRootHandle<InteropRootBean> handle) {
        Assert.assertNotNull(handle);
        
        InteropRootBean root = handle.getRoot();
        Assert.assertNotNull(root);
        
        List<InteropChildBean> children = root.getChildren();
        Assert.assertNotNull(children);
        
        Assert.assertEquals(2, children.size());
        
        {
            InteropChildBean alice = children.get(0);
            Assert.assertEquals(ALICE, alice.getName());
            Assert.assertEquals(ALICE_ADDRESS, alice.getHouseNumber());
        }
        
        {
            InteropChildBean bob = children.get(1);
            Assert.assertEquals(BOB, bob.getName());
            Assert.assertEquals(BOB_ADDRESS, bob.getHouseNumber());
        }
    }

}
