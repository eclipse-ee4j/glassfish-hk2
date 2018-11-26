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

package org.glassfish.hk2.pbuf.test.basic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.pbuf.api.PBufUtilities;
import org.glassfish.hk2.pbuf.test.beans.AnotherRootBean;
import org.glassfish.hk2.pbuf.test.beans.CustomerBean;
import org.glassfish.hk2.pbuf.test.beans.NFCEast;
import org.glassfish.hk2.pbuf.test.beans.OneOfRootBean;
import org.glassfish.hk2.pbuf.test.beans.RootOnlyBean;
import org.glassfish.hk2.pbuf.test.beans.ServiceRecordBean;
import org.glassfish.hk2.pbuf.test.beans.ServiceRecordBean.NFCWest;
import org.glassfish.hk2.pbuf.test.beans.ServiceRecordBlockBean;
import org.glassfish.hk2.pbuf.test.beans.TypeBean;
import org.glassfish.hk2.pbuf.test.utilities.Utilities;
import org.glassfish.hk2.xml.api.XmlHk2ConfigurationBean;
import org.glassfish.hk2.xml.api.XmlRootHandle;
import org.glassfish.hk2.xml.api.XmlService;
import org.junit.Assert;
import org.junit.Test;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @author jwells
 *
 */
public class PBufParserTest {
    private final static String ALICE = "Alice";
    private final static String ALICE_ADDRESS = "Milky Way";
    
    private final static String BOB = "Bob";
    
    private final static String ACME = "Acme";
    private final static long ACME_ID = 3000;
    private final static String ACME_HASH = "aaabbbccc";
    
    private final static String BJS = "BJs";
    private final static long BJS_ID = 4000;
    private final static String BJS_HASH = "dddeeefff";
    
    private final static String COSTCO = "Costco";
    private final static long COSTCO_ID = 5000;
    private final static String COSTCO_HASH = "ggghhhiii";
    
    /**
     * Tests very basic marshaling
     */
    @Test
    // @org.junit.Ignore
    public void testMarshalFlatBean() throws Exception {
        ServiceLocator locator = Utilities.enableLocator();
        
        XmlService xmlService = locator.getService(XmlService.class, PBufUtilities.PBUF_SERVICE_NAME);
        Assert.assertNotNull(xmlService);
        
        XmlRootHandle<RootOnlyBean> handle = xmlService.createEmptyHandle(RootOnlyBean.class);
        handle.addRoot();
        
        RootOnlyBean rootOnlyBean = handle.getRoot();
        
        rootOnlyBean.setAddress(ALICE_ADDRESS);
        rootOnlyBean.setName(ALICE);
        rootOnlyBean.setEnumeration(NFCEast.EAGLES);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
          handle.marshal(baos);
        }
        finally {
            baos.close();
        }
        
        byte[] asBytes = baos.toByteArray();
        Assert.assertTrue(asBytes.length > 0);
    }
    
    /**
     * Tests very basic marshaling
     */
    @Test
    // @org.junit.Ignore
    public void testMarshalFlatBeanNoSize() throws Exception {
        ServiceLocator locator = Utilities.enableLocator();
        
        XmlService xmlService = locator.getService(XmlService.class, PBufUtilities.PBUF_SERVICE_NAME);
        Assert.assertNotNull(xmlService);
        
        XmlRootHandle<RootOnlyBean> handle = xmlService.createEmptyHandle(RootOnlyBean.class);
        handle.addRoot();
        
        RootOnlyBean rootOnlyBean = handle.getRoot();
        
        rootOnlyBean.setAddress(ALICE_ADDRESS);
        rootOnlyBean.setName(ALICE);
        rootOnlyBean.setEnumeration(NFCEast.EAGLES);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
          handle.marshal(baos, getOptions(false));
        }
        finally {
            baos.close();
        }
        
        byte[] asBytes = baos.toByteArray();
        Assert.assertTrue(asBytes.length > 0);
    }
    
    /**
     * marshals a complex data structure
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testMarshalStructuredBean() throws Exception {
        ServiceLocator locator = Utilities.enableLocator();
        
        XmlService xmlService = locator.getService(XmlService.class, PBufUtilities.PBUF_SERVICE_NAME);
        Assert.assertNotNull(xmlService);
        
        XmlRootHandle<ServiceRecordBlockBean> handle = getStandardTestBlock(xmlService);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
          handle.marshal(baos);
        }
        finally {
            baos.close();
        }
        
        byte[] asBytes = baos.toByteArray();
        Assert.assertTrue(asBytes.length > 0);
        
        /*
        File f = new File("jrw.pbuf");
        FileOutputStream bais = new FileOutputStream(f);
        bais.write(asBytes);
        bais.close();
        */
    }
    
    /**
     * marshals a complex data structure
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testMarshalStructuredBeanNoSize() throws Exception {
        ServiceLocator locator = Utilities.enableLocator();
        
        XmlService xmlService = locator.getService(XmlService.class, PBufUtilities.PBUF_SERVICE_NAME);
        Assert.assertNotNull(xmlService);
        
        XmlRootHandle<ServiceRecordBlockBean> handle = getStandardTestBlock(xmlService);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
          handle.marshal(baos, getOptions(false));
        }
        finally {
            baos.close();
        }
        
        byte[] asBytes = baos.toByteArray();
        Assert.assertTrue(asBytes.length > 0);
        
        /*
        File f = new File("jrwNS.pbuf");
        FileOutputStream bais = new FileOutputStream(f);
        bais.write(asBytes);
        bais.close();
        */
    }
    
    /**
     * Reads in a pre-generated binary protobuf
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testUnmarshalStructuredBean() throws Exception {
        ClassLoader cl = getClass().getClassLoader();
        URI standardPbufURI = cl.getResource("standard.pbuf").toURI();
        
        ServiceLocator locator = Utilities.enableLocator();
        
        XmlService xmlService = locator.getService(XmlService.class, PBufUtilities.PBUF_SERVICE_NAME);
        Assert.assertNotNull(xmlService);
        
        XmlRootHandle<ServiceRecordBlockBean> handle = xmlService.unmarshal(standardPbufURI, ServiceRecordBlockBean.class);
        
        validateStandardBean(handle, 0);
    }
    
    /**
     * Reads in a pre-generated binary protobuf
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testUnmarshalStructuredBeanNoSize() throws Exception {
        ClassLoader cl = getClass().getClassLoader();
        URI standardPbufURI = cl.getResource("standardNoSize.pbuf").toURI();
        
        ServiceLocator locator = Utilities.enableLocator();
        
        XmlService xmlService = locator.getService(XmlService.class, PBufUtilities.PBUF_SERVICE_NAME);
        Assert.assertNotNull(xmlService);
        
        XmlRootHandle<ServiceRecordBlockBean> handle = xmlService.unmarshal(
                standardPbufURI, ServiceRecordBlockBean.class, true, true, getOptions(false));
        
        validateStandardBean(handle, 0);
    }
    
    /**
     * Tests all the native types of pbuf
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testTypes() throws Exception {
        ServiceLocator locator = Utilities.enableLocator();
        
        XmlService xmlService = locator.getService(XmlService.class, PBufUtilities.PBUF_SERVICE_NAME);
        Assert.assertNotNull(xmlService);
        
        XmlRootHandle<TypeBean> handle = xmlService.createEmptyHandle(TypeBean.class);
        handle.addRoot();
        
        TypeBean root = handle.getRoot();
        
        root.setIType(13);
        root.setJType(14L);
        root.setZType(true);
        root.setBType((byte) 15);
        root.setCType('E');
        root.setSType((short) 16);
        root.setFType((float) 17.0);
        root.setDType(18.0);
        root.setIsType(false);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
          handle.marshal(baos);
        }
        finally {
            baos.close();
        }
        
        byte[] asBytes = baos.toByteArray();
        Assert.assertTrue(asBytes.length > 0);
        
        XmlRootHandle<TypeBean> readHandle;
        
        ByteArrayInputStream bais = new ByteArrayInputStream(asBytes);
        try {
            readHandle = xmlService.unmarshal(bais, TypeBean.class);
        }
        finally {
            bais.close();
        }
        
        TypeBean typeRoot = readHandle.getRoot();
        
        Assert.assertEquals(13, typeRoot.getIType());
        Assert.assertEquals(14L, typeRoot.getJType());
        Assert.assertEquals(true, typeRoot.getZType());
        Assert.assertEquals((byte) 15, typeRoot.getBType());
        Assert.assertEquals('E', typeRoot.getCType());
        Assert.assertEquals((short) 16, typeRoot.getSType());
        Assert.assertTrue(new Float(17.0).equals(typeRoot.getFType()));
        Assert.assertTrue(new Double(18.0).equals(typeRoot.getDType()));
        Assert.assertFalse(typeRoot.isIsType());
    }
    
    /**
     * Tests that when the simple names are the same but the
     * packages are different everything still works
     */
    @Test
    public void testPackages() throws Exception {
        ServiceLocator locator = Utilities.enableLocator();
        
        XmlService xmlService = locator.getService(XmlService.class, PBufUtilities.PBUF_SERVICE_NAME);
        Assert.assertNotNull(xmlService);
        
        XmlRootHandle<org.glassfish.hk2.pbuf.test.beans.FooBean> handle = getFooRoot(xmlService);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
          handle.marshal(baos);
        }
        finally {
            baos.close();
        }
        
        byte[] asBytes = baos.toByteArray();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(asBytes);
        XmlRootHandle<org.glassfish.hk2.pbuf.test.beans.FooBean> uHandle = null;
        try {
            uHandle = xmlService.unmarshal(bais, org.glassfish.hk2.pbuf.test.beans.FooBean.class);
        }
        finally {
            bais.close();
        }
        
        validateFooBean(uHandle);
    }
    
    /**
     * Tests marshalling a different bean with the same child as
     * other roots.  This did not work previously because of a
     * bug in the parser
     */
    @Test
    public void testAnotherBeanWithDups() throws Exception {
        ServiceLocator locator = Utilities.enableLocator();
        
        XmlService xmlService = locator.getService(XmlService.class, PBufUtilities.PBUF_SERVICE_NAME);
        Assert.assertNotNull(xmlService);
        
        XmlRootHandle<AnotherRootBean> handle = xmlService.createEmptyHandle(AnotherRootBean.class);
        handle.addRoot();
        
        AnotherRootBean arb = handle.getRoot();
        
        ServiceRecordBean child = xmlService.createBean(ServiceRecordBean.class);
        child.setServiceRecordID(BOB);
        
        arb.setSecondUsage(child);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
          handle.marshal(baos);
        }
        finally {
            baos.close();
        }
        
        byte[] asBytes = baos.toByteArray();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(asBytes);
        XmlRootHandle<AnotherRootBean> uHandle = null;
        try {
            uHandle = xmlService.unmarshal(bais, AnotherRootBean.class);
        }
        finally {
            bais.close();
        }
        
        AnotherRootBean uRoot = uHandle.getRoot();
        Assert.assertNotNull(uRoot);
        
        ServiceRecordBean uChild = uRoot.getSecondUsage();
        Assert.assertNotNull(uChild);
        Assert.assertEquals(BOB, uChild.getServiceRecordID());
    }
    
    /**
     * Tests marshalling and unmarhsalling a bean with oneOfs
     */
    @Test
    // @org.junit.Ignore
    public void testOneOf() throws Exception {
        ServiceLocator locator = Utilities.enableLocator();
        
        XmlService xmlService = locator.getService(XmlService.class, PBufUtilities.PBUF_SERVICE_NAME);
        Assert.assertNotNull(xmlService);
        
        XmlRootHandle<OneOfRootBean> handle = xmlService.createEmptyHandle(OneOfRootBean.class);
        handle.addRoot();
        
        OneOfRootBean oneOfRoot = handle.getRoot();
        
        XmlHk2ConfigurationBean asConfigBean = (XmlHk2ConfigurationBean) oneOfRoot;
        Assert.assertFalse(asConfigBean._isSet("CTO"));
        
        oneOfRoot.setMiss(ALICE);
        oneOfRoot.setCEO(13);
        oneOfRoot.setBetween(BOB);
        oneOfRoot.setCountry(ACME);
        
        Assert.assertFalse(asConfigBean._isSet("CFO"));
        Assert.assertTrue(asConfigBean._isSet("CEO"));
        Assert.assertEquals(13, oneOfRoot.getCEO());
        Assert.assertFalse(asConfigBean._isSet("CTO"));
        Assert.assertFalse(asConfigBean._isSet("employee"));
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
          handle.marshal(baos);
        }
        finally {
            baos.close();
        }
        
        byte[] asBytes = baos.toByteArray();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(asBytes);
        XmlRootHandle<OneOfRootBean> uHandle = null;
        try {
            uHandle = xmlService.unmarshal(bais, OneOfRootBean.class);
        }
        finally {
            bais.close();
        }
        
        OneOfRootBean uRoot = uHandle.getRoot();
        asConfigBean = (XmlHk2ConfigurationBean) uRoot;
        
        Assert.assertNotNull(uRoot);
        
        Assert.assertNull(oneOfRoot.getMr());
        Assert.assertEquals(ALICE, oneOfRoot.getMiss());
        Assert.assertNull(oneOfRoot.getMrs());
        
        Assert.assertFalse(asConfigBean._isSet("mr"));
        Assert.assertTrue(asConfigBean._isSet("miss"));
        Assert.assertFalse(asConfigBean._isSet("mrs"));
        
        Assert.assertFalse(asConfigBean._isSet("CFO"));
        Assert.assertTrue(asConfigBean._isSet("CEO"));
        Assert.assertEquals(13, uRoot.getCEO());
        Assert.assertFalse(asConfigBean._isSet("CTO"));
        Assert.assertFalse(asConfigBean._isSet("employee"));
        
        Assert.assertEquals(BOB, uRoot.getBetween());
        Assert.assertEquals(ACME, uRoot.getCountry());
    }
    
    /**
     * Tests that the camel case utility does what we want
     */
    @Test
    public void testCamelCaseUtility() {
        Assert.assertEquals("url_finder", org.glassfish.hk2.pbuf.internal.PBUtilities.camelCaseToUnderscore("URLFinder"));
        Assert.assertEquals("another_url_finder", org.glassfish.hk2.pbuf.internal.PBUtilities.camelCaseToUnderscore("AnotherURLFinder"));
        Assert.assertEquals("allcaps", org.glassfish.hk2.pbuf.internal.PBUtilities.camelCaseToUnderscore("ALLCAPS"));
        Assert.assertEquals("camel_case", org.glassfish.hk2.pbuf.internal.PBUtilities.camelCaseToUnderscore("camelCase"));
        Assert.assertEquals("camel_case", org.glassfish.hk2.pbuf.internal.PBUtilities.camelCaseToUnderscore("CamelCase"));
        Assert.assertEquals("wally_t", org.glassfish.hk2.pbuf.internal.PBUtilities.camelCaseToUnderscore("wallyT"));
        Assert.assertEquals("wally_tflex", org.glassfish.hk2.pbuf.internal.PBUtilities.camelCaseToUnderscore("wallyTFLEX"));
        Assert.assertEquals("", org.glassfish.hk2.pbuf.internal.PBUtilities.camelCaseToUnderscore(""));
        Assert.assertEquals("e", org.glassfish.hk2.pbuf.internal.PBUtilities.camelCaseToUnderscore("E"));
        Assert.assertEquals("e", org.glassfish.hk2.pbuf.internal.PBUtilities.camelCaseToUnderscore("e"));
        Assert.assertEquals("el", org.glassfish.hk2.pbuf.internal.PBUtilities.camelCaseToUnderscore("el"));
        Assert.assertEquals("e_l", org.glassfish.hk2.pbuf.internal.PBUtilities.camelCaseToUnderscore("eL"));
        Assert.assertEquals("el", org.glassfish.hk2.pbuf.internal.PBUtilities.camelCaseToUnderscore("El"));
        Assert.assertEquals("u_rl", org.glassfish.hk2.pbuf.internal.PBUtilities.camelCaseToUnderscore("uRL"));
        Assert.assertEquals("u_rl", org.glassfish.hk2.pbuf.internal.PBUtilities.camelCaseToUnderscore("URl"));
        Assert.assertEquals("u_rl", org.glassfish.hk2.pbuf.internal.PBUtilities.camelCaseToUnderscore("uRl"));
    }
    
    /**
     * Reads in a pre-generated binary protobuf
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testFailureUnmarshalStructuredBean() throws Exception {
        ClassLoader cl = getClass().getClassLoader();
        URL standardPbufURL = cl.getResource("standard.pbuf");
        ServiceLocator locator = Utilities.enableLocator();
        XmlService xmlService = locator.getService(XmlService.class, PBufUtilities.PBUF_SERVICE_NAME);
        Assert.assertNotNull(xmlService);
        
        byte asBytes[];
        InputStream is = standardPbufURL.openStream();
        try {
            asBytes = Utilities.readStreamFully(is);
        }
        finally {
            is.close();
        }
        
        for (int numLengthBytes = Utilities.getNumPBufLengthBytes(asBytes); numLengthBytes < asBytes.length; numLengthBytes++) {
            // Put absolute garbage in
            asBytes[numLengthBytes] = 0;
        }
        
        ByteArrayInputStream bais = new ByteArrayInputStream(asBytes);
        try {
            try {
                xmlService.unmarshal(bais, ServiceRecordBlockBean.class);
                Assert.fail("Should have failed");
            }
            catch (MultiException me) {
                checkMultiExceptionWithBadData(me);
            }
        }
        finally {
            bais.close();
        }
    }
    
    
    
    /**
     * Reads in a pre-generated binary protobuf
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testFailureUnmarshalStructuredBeanNoSize() throws Exception {
        ClassLoader cl = getClass().getClassLoader();
        URL standardPbufURL = cl.getResource("standardNoSize.pbuf");
        ServiceLocator locator = Utilities.enableLocator();
        XmlService xmlService = locator.getService(XmlService.class, PBufUtilities.PBUF_SERVICE_NAME);
        Assert.assertNotNull(xmlService);
        
        byte asBytes[];
        InputStream is = standardPbufURL.openStream();
        try {
            asBytes = Utilities.readStreamFully(is);
        }
        finally {
            is.close();
        }
        
        for (int numLengthBytes = 0; numLengthBytes < asBytes.length; numLengthBytes++) {
            // Put absolute garbage in
            asBytes[numLengthBytes] = 0;
        }
        
        ByteArrayInputStream bais = new ByteArrayInputStream(asBytes);
        try {
            try {
                xmlService.unmarshal(bais, ServiceRecordBlockBean.class, false, false, getOptions(false));
                Assert.fail("Should have failed with bad data");
            }
            catch (MultiException me) {
                checkMultiExceptionWithBadData(me);
            }
        }
        finally {
            bais.close();
        }
    }
    
    @Test
    public void testMarshalAndUnmarshalSeveralInStream() throws Exception {
        ServiceLocator locator = Utilities.enableLocator();
        XmlService xmlService = locator.getService(XmlService.class, PBufUtilities.PBUF_SERVICE_NAME);
        Assert.assertNotNull(xmlService);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        XmlRootHandle<RootOnlyBean> eaglesHandle = xmlService.createEmptyHandle(RootOnlyBean.class, false, false);
        XmlRootHandle<RootOnlyBean> giantsHandle = xmlService.createEmptyHandle(RootOnlyBean.class, false, false);

        eaglesHandle.addRoot();
        giantsHandle.addRoot();

        RootOnlyBean eagles = eaglesHandle.getRoot();
        eagles.setName("Wentz");
        eagles.setEnumeration(NFCEast.EAGLES);

        RootOnlyBean giants = giantsHandle.getRoot();
        giants.setName("Manning");
        giants.setEnumeration(NFCEast.GIANTS);

        Map<String, Object> options = new HashMap<String, Object>();
        options.put(PBufUtilities.PBUF_STREAMING_OPTION, null);
        
        xmlService.marshal(baos, eaglesHandle, options);
        AutoCloseable closeMe = (AutoCloseable) options.get(PBufUtilities.PBUF_STREAMING_OPTION);
        Assert.assertNotNull(closeMe);
        
        xmlService.marshal(baos, giantsHandle, options);
        closeMe = (AutoCloseable) options.get(PBufUtilities.PBUF_STREAMING_OPTION);
        Assert.assertNotNull(closeMe);

        baos.flush();
        baos.close();
        closeMe.close();

        byte asBytes[] = baos.toByteArray();

        options = new HashMap<String, Object>();
        options.put(PBufUtilities.PBUF_STREAMING_OPTION, null);
        
        ByteArrayInputStream bais = new ByteArrayInputStream(asBytes);
        try {
                XmlRootHandle<RootOnlyBean> inEaglesHandle = xmlService.unmarshal(bais, RootOnlyBean.class, false, false, options);
                XmlRootHandle<RootOnlyBean> inGiantsHandle = xmlService.unmarshal(bais, RootOnlyBean.class, false, false, options);

                RootOnlyBean inEagles = inEaglesHandle.getRoot();
                RootOnlyBean inGiants = inGiantsHandle.getRoot();

                Assert.assertEquals("Wentz", inEagles.getName());
                Assert.assertEquals(NFCEast.EAGLES, inEagles.getEnumeration());

                Assert.assertEquals("Manning", inGiants.getName());
                Assert.assertEquals(NFCEast.GIANTS, inGiants.getEnumeration());
        }
        finally {
        	closeMe = (AutoCloseable) options.get(PBufUtilities.PBUF_STREAMING_OPTION);
            Assert.assertNotNull(closeMe);
            
            bais.close();
            closeMe.close();
        }

    }
    
    /**
     * Specialized multi-exception validator
     * 
     * @param me The multi-exception to validate
     */
    private static void checkMultiExceptionWithBadData(MultiException me) {
        int lcv = 0;
        for (Throwable th : me.getErrors()) {
            if (lcv == 0) {
                Assert.assertTrue(th instanceof InvalidProtocolBufferException);
            }
            else if (lcv == 1) {
                Assert.assertTrue(th instanceof IllegalStateException);
                String message = th.getMessage();
                
                Assert.assertTrue(message, message.contains("00000000 "));
            }
            
            lcv++;
        }
    }
    
    private static void validateStandardBean(XmlRootHandle<ServiceRecordBlockBean> handle, long sequenceNumber) {
        ServiceRecordBlockBean root = handle.getRoot();
        Assert.assertNotNull(root);
        Assert.assertEquals(sequenceNumber, root.getSequenceNumber());
        
        Assert.assertNull(root.getNotSet());
        Assert.assertEquals(0, root.getNotSetInt());
        
        List<ServiceRecordBean> records = root.getServiceRecords();
        Assert.assertEquals(3, records.size());
        
        validateServiceRecordBean(records.get(0), ACME, ACME_ID, ACME_HASH, NFCEast.EAGLES, NFCWest.SEAHAWKS);
        validateServiceRecordBean(records.get(1), BJS, BJS_ID, BJS_HASH, NFCEast.GIANTS, NFCWest.CARDINALS);
        validateServiceRecordBean(records.get(2), COSTCO, COSTCO_ID, COSTCO_HASH, NFCEast.REDSKINS, NFCWest.RAMS);
    }
    
    private static void validateFooBean(XmlRootHandle<org.glassfish.hk2.pbuf.test.beans.FooBean> handle) {
        org.glassfish.hk2.pbuf.test.beans.FooBean root = handle.getRoot();
        Assert.assertNotNull(root);
        
        org.glassfish.hk2.pbuf.test.beans2.FooBean foo2s[] = root.getFoo2();
        Assert.assertEquals(3, foo2s.length);
        
        Assert.assertEquals(ACME, foo2s[0].getName());
        Assert.assertEquals(BJS, foo2s[1].getName());
        Assert.assertEquals(COSTCO, foo2s[2].getName());
        
        List<org.glassfish.hk2.pbuf.test.beans3.FooBean> foo3s = root.getFoo3();
        Assert.assertEquals(3, foo3s.size());
        
        Assert.assertEquals(ACME_ID, foo3s.get(0).getID());
        Assert.assertEquals(BJS_ID, foo3s.get(1).getID());
        Assert.assertEquals(COSTCO_ID, foo3s.get(2).getID());
    }
    
    private static void validateServiceRecordBean(ServiceRecordBean record, String name, long id, String hash,
            NFCEast team, NFCWest westTeam) {
        Assert.assertNotNull(record);
        
        Assert.assertEquals(hash, record.getServiceRecordID());
        Assert.assertEquals(westTeam, record.getTeam());
        
        CustomerBean customer = record.getCustomer();
        Assert.assertNotNull(customer);
        
        Assert.assertEquals(name, customer.getCustomerName());
        Assert.assertEquals(id, customer.getCustomerID());
        Assert.assertEquals(team, customer.getTeam());
    }
    
    private static CustomerBean createCustomerBean(XmlService xmlService, String companyName, long id, NFCEast team) {
        CustomerBean retVal = xmlService.createBean(CustomerBean.class);
        
        retVal.setCustomerName(companyName);
        retVal.setCustomerID(id);
        retVal.setTeam(team);
        
        return retVal;
    }
    
    private static ServiceRecordBean createServiceRecordBean(XmlService xmlService, CustomerBean customer, String hash, NFCWest team) {
        ServiceRecordBean retVal = xmlService.createBean(ServiceRecordBean.class);
        
        retVal.setServiceRecordID(hash);
        retVal.setCustomer(customer);
        retVal.setTeam(team);
        
        return retVal;
    }
    
    private static org.glassfish.hk2.pbuf.test.beans2.FooBean createFoo2(XmlService xmlService, String name) {
        org.glassfish.hk2.pbuf.test.beans2.FooBean retVal = xmlService.createBean(org.glassfish.hk2.pbuf.test.beans2.FooBean.class);
        retVal.setName(name);
        
        return retVal;
    }
    
    private static org.glassfish.hk2.pbuf.test.beans3.FooBean createFoo3(XmlService xmlService, long id) {
        org.glassfish.hk2.pbuf.test.beans3.FooBean retVal = xmlService.createBean(org.glassfish.hk2.pbuf.test.beans3.FooBean.class);
        retVal.setID(id);
        
        return retVal;
    }
    
    private static XmlRootHandle<org.glassfish.hk2.pbuf.test.beans.FooBean> getFooRoot(XmlService xmlService) {
        XmlRootHandle<org.glassfish.hk2.pbuf.test.beans.FooBean> handle =
                xmlService.createEmptyHandle(org.glassfish.hk2.pbuf.test.beans.FooBean.class);
        handle.addRoot();
        
        org.glassfish.hk2.pbuf.test.beans.FooBean root = handle.getRoot();
        
        org.glassfish.hk2.pbuf.test.beans2.FooBean acmeBean = createFoo2(xmlService, ACME);
        org.glassfish.hk2.pbuf.test.beans2.FooBean bjsBean = createFoo2(xmlService, BJS);
        org.glassfish.hk2.pbuf.test.beans2.FooBean costcoBean = createFoo2(xmlService, COSTCO);
        
        root.addFoo2(acmeBean);
        root.addFoo2(bjsBean);
        root.addFoo2(costcoBean);
        
        org.glassfish.hk2.pbuf.test.beans3.FooBean acmeIDBean = createFoo3(xmlService, ACME_ID);
        org.glassfish.hk2.pbuf.test.beans3.FooBean bjsIDBean = createFoo3(xmlService, BJS_ID);
        org.glassfish.hk2.pbuf.test.beans3.FooBean costcoIDBean = createFoo3(xmlService, COSTCO_ID);
        
        root.addFoo3(acmeIDBean);
        root.addFoo3(bjsIDBean);
        root.addFoo3(costcoIDBean);
        
        return handle;
    }
    
    private static XmlRootHandle<ServiceRecordBlockBean> getStandardTestBlock(XmlService xmlService) {
        XmlRootHandle<ServiceRecordBlockBean> handle = xmlService.createEmptyHandle(ServiceRecordBlockBean.class);
        handle.addRoot();
        
        ServiceRecordBlockBean blockBean = handle.getRoot();
        
        blockBean.setSequenceNumber(0L);
        
        CustomerBean acmeBean = createCustomerBean(xmlService, ACME, ACME_ID, NFCEast.EAGLES);
        CustomerBean bjsBean = createCustomerBean(xmlService, BJS, BJS_ID, NFCEast.GIANTS);
        CustomerBean costcoBean = createCustomerBean(xmlService, COSTCO, COSTCO_ID, NFCEast.REDSKINS);
        
        ServiceRecordBean acmeRecord = createServiceRecordBean(xmlService, acmeBean, ACME_HASH, NFCWest.SEAHAWKS);
        ServiceRecordBean bjsRecord = createServiceRecordBean(xmlService, bjsBean, BJS_HASH, NFCWest.CARDINALS);
        ServiceRecordBean costcoRecord = createServiceRecordBean(xmlService, costcoBean, COSTCO_HASH, NFCWest.RAMS);
        
        blockBean.addServiceRecord(acmeRecord);
        blockBean.addServiceRecord(bjsRecord);
        blockBean.addServiceRecord(costcoRecord);
        
        return handle;
    }
    
    private static Map<String, Object> getOptions(boolean withSize) {
        Map<String, Object> retVal = new HashMap<String, Object>();
        
        Boolean value = new Boolean(withSize);
        
        retVal.put(PBufUtilities.PBUF_OPTION_INT32_HEADER, value);
        
        return retVal;
        
    }

}
