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

package org.glassfish.hk2.xml.test.basic;

import java.io.InputStream;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.xml.test.basic.beans.Commons;
import org.glassfish.hk2.xml.test.utilities.Utilities;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class UnmarshallXMLStreamTest {
    private final XMLInputFactory xif = XMLInputFactory.newInstance();
    
    private InputStream getStream(String fileName) throws Exception {
        URL url = getClass().getClassLoader().getResource(fileName);
        InputStream is = url.openStream();
        
        return is;
    }
    
    /**
     * Tests the most basic of xml files can be unmarshalled with an interface
     * annotated with jaxb annotations
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testInterfaceJaxbUnmarshalling() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        
        InputStream is = getStream(Commons.MUSEUM1_FILE);
        try {
            XMLStreamReader reader = xif.createXMLStreamReader(is);
            try {
                Commons.testInterfaceJaxbUnmarshalling(locator, reader);
            }
            finally {
                reader.close();
            }
        }
        finally {
            is.close();
        }
    }
    
    /**
     * Tests the most basic of xml files can be unmarshalled with an interface
     * annotated with jaxb annotations
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testInterfaceJaxbUnmarshallingStream() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        
        InputStream is = getStream(Commons.MUSEUM1_FILE);
        try {
            Commons.testInterfaceJaxbUnmarshalling(locator, is);
        }
        finally {
            is.close();
        }
    }
    
    /**
     * Tests the most basic of xml files can be unmarshalled with an interface
     * annotated with jaxb annotations
     * 
     * @throws Exception
     */
    @Test 
    // @org.junit.Ignore
    public void testBeanLikeMapOfInterface() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        
        InputStream is = getStream(Commons.ACME1_FILE);
        try {
            XMLStreamReader reader = xif.createXMLStreamReader(is);
            try {
                Commons.testBeanLikeMapOfInterface(locator, reader);;
            }
            finally {
                reader.close();
            }
        }
        finally {
            is.close();
        }
    }
    
    /**
     * Tests the most basic of xml files can be unmarshalled with an interface
     * annotated with jaxb annotations
     * 
     * @throws Exception
     */
    @Test 
    // @org.junit.Ignore
    public void testInterfaceJaxbUnmarshallingWithChildren() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        
        InputStream is = getStream(Commons.ACME1_FILE);
        try {
            XMLStreamReader reader = xif.createXMLStreamReader(is);
            try {
                Commons.testInterfaceJaxbUnmarshallingWithChildren(locator, reader);;
            }
            finally {
                reader.close();
            }
        }
        finally {
            is.close();
        }
    }
    
    /**
     * Tests a more complex XML format.  This test will ensure
     * all elements are in the Hub with expected names
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testComplexUnmarshalling() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        
        InputStream is = getStream(Commons.SAMPLE_CONFIG_FILE);
        try {
            XMLStreamReader reader = xif.createXMLStreamReader(is);
            try {
                Commons.testComplexUnmarshalling(locator, reader);;
            }
            finally {
                reader.close();
            }
        }
        finally {
            is.close();
        }
    }
    
    /**
     * Associations has unkeyed children of type Association.  We
     * get them and make sure they have unique keys generated
     * by the system
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testUnkeyedChildren() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        
        InputStream is = getStream(Commons.SAMPLE_CONFIG_FILE);
        try {
            XMLStreamReader reader = xif.createXMLStreamReader(is);
            try {
                Commons.testUnkeyedChildren(locator, reader);;
            }
            finally {
                reader.close();
            }
        }
        finally {
            is.close();
        }
    }
    
    /**
     * Foobar has two children, foo and bar, both of which are of type DataBean
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testSameClassTwoChildren() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        
        InputStream is = getStream(Commons.FOOBAR_FILE);
        try {
            XMLStreamReader reader = xif.createXMLStreamReader(is);
            try {
                Commons.testSameClassTwoChildren(locator, reader);;
            }
            finally {
                reader.close();
            }
        }
        finally {
            is.close();
        }
    }
    
    /**
     * Tests that an xml hierarchy with a cycle can be unmarshalled
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testBeanCycle() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        
        InputStream is = getStream(Commons.CYCLE_FILE);
        try {
            XMLStreamReader reader = xif.createXMLStreamReader(is);
            try {
                Commons.testBeanCycle(locator, reader);;
            }
            finally {
                reader.close();
            }
        }
        finally {
            is.close();
        }
    }
    
    /**
     * Tests every scalar type that can be read
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testEveryType() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        
        InputStream is = getStream(Commons.TYPE1_FILE);
        try {
            XMLStreamReader reader = xif.createXMLStreamReader(is);
            try {
                Commons.testEveryType(locator, reader);;
            }
            finally {
                reader.close();
            }
        }
        finally {
            is.close();
        }
    }
    
    /**
     * Tests that the annotation is fully copied over on the method
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testAnnotationWithEverythingCopied() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        
        InputStream is = getStream(Commons.ACME1_FILE);
        try {
            XMLStreamReader reader = xif.createXMLStreamReader(is);
            try {
                Commons.testAnnotationWithEverythingCopied(locator, reader);
            }
            finally {
                reader.close();
            }
        }
        finally {
            is.close();
        }
    }
    
    /**
     * Tests that a list child with no elements returns an empty list (not null)
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testEmptyListChildReturnsEmptyList() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        
        InputStream is = getStream(Commons.ACME1_FILE);
        try {
            XMLStreamReader reader = xif.createXMLStreamReader(is);
            try {
                Commons.testEmptyListChildReturnsEmptyList(locator, reader);
            }
            finally {
                reader.close();
            }
        }
        finally {
            is.close();
        }
    }
    
    /**
     * Tests that a list child with no elements returns an empty array (not null)
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testEmptyArrayChildReturnsEmptyArray() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        
        InputStream is = getStream(Commons.ACME1_FILE);
        try {
            XMLStreamReader reader = xif.createXMLStreamReader(is);
            try {
                Commons.testEmptyArrayChildReturnsEmptyArray(locator, reader);
            }
            finally {
                reader.close();
            }
        }
        finally {
            is.close();
        }
    }
    
    /**
     * Tests that a byte[] child gets properly translated
     * (into itself, for now)
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testByteArrayNonChild() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        
        InputStream is = getStream(Commons.ACME2_FILE);
        try {
            XMLStreamReader reader = xif.createXMLStreamReader(is);
            try {
                Commons.testByteArrayNonChild(locator, reader);
            }
            finally {
                reader.close();
            }
        }
        finally {
            is.close();
        }
    }
    
    /**
     * Tests that JaxB style references work.
     * These are references that use XmlID and XmlIDREF
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testJaxbStyleReference() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        
        InputStream is = getStream(Commons.SAMPLE_CONFIG_FILE);
        try {
            XMLStreamReader reader = xif.createXMLStreamReader(is);
            try {
                Commons.testJaxbStyleReference(locator, reader);;
            }
            finally {
                reader.close();
            }
        }
        finally {
            is.close();
        }
    }
    
    /**
     * Tests that JaxB style references work
     * even if the referenced object is AFTER the stanza
     * being referenced
     * 
     * These are references that use XmlID and XmlIDREF
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testJaxbStyleForwardReference() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        
        InputStream is = getStream(Commons.REFERENCE1_FILE);
        try {
            XMLStreamReader reader = xif.createXMLStreamReader(is);
            try {
                Commons.testJaxbStyleForwardReference(locator, reader);
            }
            finally {
                reader.close();
            }
        }
        finally {
            is.close();
        }
    }
    
    /**
     * Tests that JaxB style references work
     * even if the referenced object is AFTER the stanza
     * being referenced
     * 
     * These are references that use XmlID and XmlIDREF
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testXmlJavaTypeAdapter() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        
        InputStream is = getStream(Commons.ROOT_BEAN_WITH_PROPERTIES);
        try {
            XMLStreamReader reader = xif.createXMLStreamReader(is);
            try {
                Commons.testXmlJavaTypeAdapter(locator, reader);
            }
            finally {
                reader.close();
            }
        }
        finally {
            is.close();
        }
    }

}
