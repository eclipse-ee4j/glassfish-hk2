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

package org.glassfish.hk2.xml.test.pbuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.pbuf.api.PBufUtilities;
import org.glassfish.hk2.xml.api.XmlRootHandle;
import org.glassfish.hk2.xml.api.XmlService;
import org.glassfish.hk2.xml.test.pbuf.pc1.PBufPrecompileChild;
import org.glassfish.hk2.xml.test.pbuf.pc1.PBufPrecompileChildOuterClass;
import org.glassfish.hk2.xml.test.pbuf.pc1.PBufPrecompileRootBean;
import org.glassfish.hk2.xml.test.pbuf.pc1.PBufPrecompileRootBeanOuterClass;
import org.glassfish.hk2.xml.test.pbuf.pc1.ThingBean;
import org.glassfish.hk2.xml.test.pbuf.pc1.ThingBeanOuterClass;
import org.glassfish.hk2.xml.test.pbuf.pc1.ThingTwoBean;
import org.glassfish.hk2.xml.test.pbuf.pc1.ThingTwoBeanOuterClass;
import org.glassfish.hk2.xml.test.pbuf.pc2.PBufPrecompileChild2;
import org.glassfish.hk2.xml.test.pbuf.pc2.PBufPrecompileChild2OuterClass;
import org.glassfish.hk2.xml.test1.utilities.Utilities;
import org.junit.Assert;
import org.junit.Test;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

/**
 * @author jwells
 *
 */
public class PBufPreCompileTest {
    private final static String ALICE = "Alice";
    
    private final static String[] PROTO_RESOURCES = {
            "org/glassfish/hk2/xml/test/pbuf/pc1/PBufPrecompileChild.proto"
            , "org/glassfish/hk2/xml/test/pbuf/pc1/PBufPrecompileRootBean.proto"
            , "org/glassfish/hk2/xml/test/pbuf/pc2/PBufPrecompileChild2.proto"
            , "org/glassfish/hk2/xml/test/pbuf/pc2/TypeBean.proto"
            , "org/glassfish/hk2/xml/test/pbuf/pc1/ThingBean.proto"
            , "org/glassfish/hk2/xml/test/pbuf/pc1/ThingOneBean.proto"
            , "org/glassfish/hk2/xml/test/pbuf/pc1/ThingTwoBean.proto"
    };
    
    private final static String PRECOMPILED_PREFIX = "protos/";
    
    /**
     * Tests that the expected files are generate and put into the resulting jar file
     */
    @Test
    // @org.junit.Ignore
    public void testPrecompileHappens() throws Exception {
        ClassLoader loader = getClass().getClassLoader();
        
        for (String protoResource : PROTO_RESOURCES) {
            URL compiledURL = loader.getResource(protoResource);
            Assert.assertNotNull("Could not find " + protoResource, compiledURL);
            
            URL precompiledURL = loader.getResource(PRECOMPILED_PREFIX + protoResource);
            Assert.assertNotNull("Could not find " + protoResource, precompiledURL);
            
            byte compiledProto[] = Utilities.readBytesFromURL(compiledURL);
            byte precompiledProto[] = Utilities.readBytesFromURL(precompiledURL);
            
            Assert.assertTrue(Arrays.equals(precompiledProto, compiledProto));
        }
    }
    
    /**
     * Tests that we can interoperate with true protocol buffers
     * generated from java files
     */
    @Test
    public void testCanInteropWithTrueProtos() throws Exception {
        ServiceLocator locator = Utilities.createInteropLocator();
        
        PBufPrecompileRootBeanOuterClass.PBufPrecompileRootBean root = createBenchmarkProto();
        validateBenchmarkProto(root);
        
        int serializedSize;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            serializedSize = root.getSerializedSize();
            
            CodedOutputStream cos = CodedOutputStream.newInstance(baos);
            
            try {
                cos.writeInt32NoTag(serializedSize);
            
                root.writeTo(cos);
            }
            finally {
                cos.flush();
            }
        }
        finally {
            baos.close();
        }
        
        byte fromProto[] = baos.toByteArray();
        
        XmlService xmlService = locator.getService(XmlService.class, PBufUtilities.PBUF_SERVICE_NAME);
        
        XmlRootHandle<PBufPrecompileRootBean> writeHandle = createBenchmarkHk2(xmlService);
        validateBenchmarkHk2(writeHandle.getRoot());
        
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        try {
            writeHandle.marshal(baos2);
        }
        finally {
            baos2.close();
        }
        
        byte fromHk2[] = baos2.toByteArray();
        
        // The byte arrays difference from hk2 vs protocol buffers is because of a bug in protocol buffers
        // in that they always encode a false boolean by not sending it, but that's not correct
        // as it does not account for the settedness of the field.  In protocol buffers a false
        // boolean will always appear as being unset
        
        // Assert.assertTrue(getByteArraysDifferentMessage(fromProto, fromHk2), Arrays.equals(fromProto, fromHk2));
        
        // Protocol Buffer into hk2
        PBufPrecompileRootBean hk2Root = null;
        ByteArrayInputStream bais = new ByteArrayInputStream(fromProto);
        try {
            XmlRootHandle<PBufPrecompileRootBean> handle = xmlService.unmarshal(bais, PBufPrecompileRootBean.class);
            
            hk2Root = handle.getRoot();
        }
        finally {
            bais.close();
        }
        
        validateBenchmarkHk2(hk2Root);
        
        // hk2 into ProtocolBuffer
        ByteArrayInputStream bais2 = new ByteArrayInputStream(fromHk2);
        try {
            CodedInputStream cis = CodedInputStream.newInstance(bais2);
            
            // hk2 protocol puts the lenght here
            cis.readInt32();
            
            PBufPrecompileRootBeanOuterClass.PBufPrecompileRootBean parsedRoot = PBufPrecompileRootBeanOuterClass.PBufPrecompileRootBean.parseFrom(cis);
            
            validateBenchmarkProto(parsedRoot);
        }
        finally {
            bais2.close();
        }
    }
    
    private static PBufPrecompileRootBeanOuterClass.PBufPrecompileRootBean createBenchmarkProto() {
        PBufPrecompileChildOuterClass.PBufPrecompileChild.Builder childBuilder0 = PBufPrecompileChildOuterClass.PBufPrecompileChild.newBuilder();
        PBufPrecompileChildOuterClass.PBufPrecompileChild child0 = childBuilder0.
                setJtype(13).
                setSType((short) 13).
                setZType(true).
                setZType2(false).
                build();
        
        PBufPrecompileChildOuterClass.PBufPrecompileChild.Builder childBuilder1 = PBufPrecompileChildOuterClass.PBufPrecompileChild.newBuilder();
        PBufPrecompileChildOuterClass.PBufPrecompileChild child1 = childBuilder1.
                setJtype(14).
                setSType((short) 14).
                setZType(false).
                setZType2(true).
                build();
        
        PBufPrecompileChild2OuterClass.PBufPrecompileChild2.Builder child2Builder0 = PBufPrecompileChild2OuterClass.PBufPrecompileChild2.newBuilder();
        PBufPrecompileChild2OuterClass.PBufPrecompileChild2 child2_0 = child2Builder0.
                setBtype(15).
                setZType(false).
                build();
        
        PBufPrecompileChild2OuterClass.PBufPrecompileChild2.Builder child2Builder1 = PBufPrecompileChild2OuterClass.PBufPrecompileChild2.newBuilder();
        PBufPrecompileChild2OuterClass.PBufPrecompileChild2 child2_1 = child2Builder1.
                setBtype(16).
                setZType(true).
                build();
        
        ThingBeanOuterClass.ThingBean.Builder thingBuilder = ThingBeanOuterClass.ThingBean.newBuilder();
        ThingBeanOuterClass.ThingBean thing0 = thingBuilder.build();
        
        ThingTwoBeanOuterClass.ThingTwoBean.Builder thingTwoBuilder = ThingTwoBeanOuterClass.ThingTwoBean.newBuilder();
        ThingTwoBeanOuterClass.ThingTwoBean thing2 = thingTwoBuilder.build();
        
        PBufPrecompileRootBeanOuterClass.PBufPrecompileRootBean.Builder rootBuilder = PBufPrecompileRootBeanOuterClass.PBufPrecompileRootBean.newBuilder();
        PBufPrecompileRootBeanOuterClass.PBufPrecompileRootBean root = rootBuilder.setName(ALICE).
                setItype(12).
                addRemoteTypes(child2_0).
                addRemoteTypes(child2_1).
                addLocalTypes(child0).
                addLocalTypes(child1).
                setSecondThing(thing0).
                setFourthThing(thing2).
                build();
        
        return root;
    }
    
    private static XmlRootHandle<PBufPrecompileRootBean> createBenchmarkHk2(XmlService xmlService) {
        PBufPrecompileChild child0 = xmlService.createBean(PBufPrecompileChild.class);
        child0.setJType(13L);
        child0.setSType((short) 13);
        child0.setZType(true);
        child0.setZType2(false);
        
        PBufPrecompileChild child1 = xmlService.createBean(PBufPrecompileChild.class);
        child1.setJType(14L);
        child1.setSType((short) 14);
        child1.setZType(false);
        child1.setZType2(true);
        
        PBufPrecompileChild2 child2_0 = xmlService.createBean(PBufPrecompileChild2.class);
        child2_0.setBType(15);
        child2_0.setZType(false);
        
        PBufPrecompileChild2 child2_1 = xmlService.createBean(PBufPrecompileChild2.class);
        child2_1.setBType(16);
        child2_1.setZType(true);
        
        ThingBean thing0 = xmlService.createBean(ThingBean.class);
        ThingTwoBean thing2 = xmlService.createBean(ThingTwoBean.class);
        
        XmlRootHandle<PBufPrecompileRootBean> retVal = xmlService.createEmptyHandle(PBufPrecompileRootBean.class);
        retVal.addRoot();
        
        PBufPrecompileRootBean root = retVal.getRoot();
        
        root.setName(ALICE);
        root.setIType(12);
        root.addLocalType(child0);
        root.addLocalType(child1);
        root.addRemoteType(child2_0);
        root.addRemoteType(child2_1);
        root.setSecondThing(thing0);
        root.setFourthThing(thing2);
        
        return retVal;
    }
    
    private static void validateBenchmarkHk2(PBufPrecompileRootBean rootBean) {
        Assert.assertNotNull(rootBean);
        
        Assert.assertEquals(ALICE, rootBean.getName());
        Assert.assertEquals(12, rootBean.getIType());
        
        List<PBufPrecompileChild> localTypes = rootBean.getLocalTypes();
        Assert.assertNotNull(localTypes);
        Assert.assertEquals(2, localTypes.size());
        
        {
            PBufPrecompileChild child0 = localTypes.get(0);
            Assert.assertEquals(13L, child0.getJType());
            Assert.assertEquals((short) 13, child0.getSType());
            Assert.assertTrue(child0.isZType());
            Assert.assertFalse(child0.isZType2());
        }
        
        {
            PBufPrecompileChild child1 = localTypes.get(1);
            Assert.assertEquals(14L, child1.getJType());
            Assert.assertEquals((short) 14, child1.getSType());
            Assert.assertFalse(child1.isZType());
            Assert.assertTrue(child1.isZType2());
        }
        
        PBufPrecompileChild2 child2[] = rootBean.getRemoteTypes();
        Assert.assertNotNull(child2);
        Assert.assertEquals(2, child2.length);
        
        {
            PBufPrecompileChild2 child2_0 = child2[0];
            Assert.assertEquals(15, child2_0.getBType());
            Assert.assertFalse(child2_0.getZType());
        }
        
        {
            PBufPrecompileChild2 child2_1 = child2[1];
            Assert.assertEquals(16, child2_1.getBType());
            Assert.assertTrue(child2_1.getZType());
        }
        
        Assert.assertNull(rootBean.getFirstThing());
        Assert.assertNotNull(rootBean.getSecondThing());
        
        Assert.assertNull(rootBean.getThirdThing());
        Assert.assertNotNull(rootBean.getFourthThing());
        Assert.assertNull(rootBean.getFifthThing());
    }
    
    private static void validateBenchmarkProto(PBufPrecompileRootBeanOuterClass.PBufPrecompileRootBean rootBean) {
        Assert.assertNotNull(rootBean);
        
        Assert.assertEquals(ALICE, rootBean.getName());
        Assert.assertEquals(12, rootBean.getItype());
        
        List<PBufPrecompileChildOuterClass.PBufPrecompileChild> localTypes = rootBean.getLocalTypesList();
        Assert.assertNotNull(localTypes);
        Assert.assertEquals(2, localTypes.size());
        
        {
            PBufPrecompileChildOuterClass.PBufPrecompileChild child0 = localTypes.get(0);
            Assert.assertEquals(13L, child0.getJtype());
            Assert.assertEquals(13, child0.getSType());
            Assert.assertTrue(child0.getZType());
            Assert.assertFalse(child0.getZType2());
            
        }
        
        {
            PBufPrecompileChildOuterClass.PBufPrecompileChild child1 = localTypes.get(1);
            Assert.assertEquals(14L, child1.getJtype());
            Assert.assertEquals(14, child1.getSType());
            Assert.assertFalse(child1.getZType());
            Assert.assertTrue(child1.getZType2());
        }
        
        List<PBufPrecompileChild2OuterClass.PBufPrecompileChild2> child2 = rootBean.getRemoteTypesList();
        Assert.assertNotNull(child2);
        Assert.assertEquals(2, child2.size());
        
        {
            PBufPrecompileChild2OuterClass.PBufPrecompileChild2 child2_0 = child2.get(0);
            Assert.assertEquals(15, child2_0.getBtype());
            Assert.assertFalse(child2_0.getZType());
        }
        
        {
            PBufPrecompileChild2OuterClass.PBufPrecompileChild2 child2_1 = child2.get(1);
            Assert.assertEquals(16, child2_1.getBtype());
            Assert.assertTrue(child2_1.getZType());
        }
        
        
        Assert.assertEquals(PBufPrecompileRootBeanOuterClass.PBufPrecompileRootBean.FirstOneOfCase.SECOND_THING,
                rootBean.getFirstOneOfCase());
        
        Assert.assertEquals(PBufPrecompileRootBeanOuterClass.PBufPrecompileRootBean.SecondOneOfCase.FOURTH_THING,
                rootBean.getSecondOneOfCase());
    }
    
    @SuppressWarnings("unused")
    private static String getByteArraysDifferentMessage(byte a[], byte b[]) {
        StringBuffer sb = new StringBuffer("The byte arrays were different.  Size of a=" + a.length + " Size of b=" + b.length + "\n");
        int largest = (a.length > b.length) ? a.length : b.length ;
        
        for (int lcv = 0; lcv < largest; lcv++) {
            String aValue = (lcv < a.length) ? "" + a[lcv] : "n" ;
            String bValue = (lcv < b.length) ? "" + b[lcv] : "n" ;
            
            sb.append(lcv + ". " + aValue + "(" + bValue + ")\n");
        }
        
        return sb.toString();
    }

}
