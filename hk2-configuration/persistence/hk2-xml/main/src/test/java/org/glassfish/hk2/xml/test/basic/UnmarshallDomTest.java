/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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

import java.net.URI;
import java.net.URL;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.xml.test.basic.beans.Commons;
import org.glassfish.hk2.xml.test.utilities.Utilities;
import org.junit.Test;

/**
 * Tests for unmarshalling xml into the hk2 hub
 * 
 * @author jwells
 */
public class UnmarshallDomTest {
    /**
     * Tests the most basic of xml files can be unmarshalled with an interface
     * annotated with jaxb annotations
     * 
     * @throws Exception
     */
    @Test // @org.junit.Ignore
    public void testInterfaceJaxbUnmarshalling() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        URL url = getClass().getClassLoader().getResource(Commons.MUSEUM1_FILE);
        URI uri = url.toURI();
        
        Commons.testInterfaceJaxbUnmarshalling(locator, uri);
    }
    
    /**
     * Tests the most basic of xml files can be unmarshalled with an interface
     * annotated with jaxb annotations
     * 
     * @throws Exception
     */
    @Test // @org.junit.Ignore
    public void testBeanLikeMapOfInterface() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        URL url = getClass().getClassLoader().getResource(Commons.ACME1_FILE);
        URI uri = url.toURI();
        
        Commons.testBeanLikeMapOfInterface(locator, uri);
    }
    
    /**
     * Tests the most basic of xml files can be unmarshalled with an interface
     * annotated with jaxb annotations
     * 
     * @throws Exception
     */
    @Test // @org.junit.Ignore
    public void testInterfaceJaxbUnmarshallingWithChildren() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        
        URL url = getClass().getClassLoader().getResource(Commons.ACME1_FILE);
        URI uri = url.toURI();
        
        Commons.testInterfaceJaxbUnmarshallingWithChildren(locator, uri);
    }
    
    /**
     * Tests a more complex XML format.  This test will ensure
     * all elements are in the Hub with expected names
     * 
     * @throws Exception
     */
    @Test // @org.junit.Ignore
    public void testComplexUnmarshalling() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        
        URL url = getClass().getClassLoader().getResource(Commons.SAMPLE_CONFIG_FILE);
        URI uri = url.toURI();
        
        Commons.testComplexUnmarshalling(locator, uri);
    }
    
    /**
     * Associations has unkeyed children of type Association.  We
     * get them and make sure they have unique keys generated
     * by the system
     * 
     * @throws Exception
     */
    @Test // @org.junit.Ignore
    public void testUnkeyedChildren() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        
        URL url = getClass().getClassLoader().getResource(Commons.SAMPLE_CONFIG_FILE);
        URI uri = url.toURI();
        
        Commons.testUnkeyedChildren(locator, uri);
    }
    
    /**
     * Foobar has two children, foo and bar, both of which are of type DataBean
     * 
     * @throws Exception
     */
    @Test // @org.junit.Ignore
    public void testSameClassTwoChildren() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        
        URL url = getClass().getClassLoader().getResource(Commons.FOOBAR_FILE);
        URI uri = url.toURI();
        
        Commons.testSameClassTwoChildren(locator, uri);
    }
    
    /**
     * Tests that an xml hierarchy with a cycle can be unmarshalled
     * 
     * @throws Exception
     */
    @Test // @org.junit.Ignore
    public void testBeanCycle() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        
        URL url = getClass().getClassLoader().getResource(Commons.CYCLE_FILE);
        URI uri = url.toURI();
        
        Commons.testBeanCycle(locator, uri);
    }
    
    /**
     * Tests every scalar type that can be read
     * 
     * @throws Exception
     */
    @Test // @org.junit.Ignore
    public void testEveryType() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        
        URL url = getClass().getClassLoader().getResource(Commons.TYPE1_FILE);
        URI uri = url.toURI();
        
        Commons.testEveryType(locator, uri);
    }
    
    /**
     * Tests that the annotation is fully copied over on the method
     * 
     * @throws Exception
     */
    @Test // @org.junit.Ignore
    public void testAnnotationWithEverythingCopied() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        
        URL url = getClass().getClassLoader().getResource(Commons.ACME1_FILE);
        URI uri = url.toURI();
        
        Commons.testAnnotationWithEverythingCopied(locator, uri);
    }
    
    /**
     * Tests that a list child with no elements returns an empty list (not null)
     * 
     * @throws Exception
     */
    @Test // @org.junit.Ignore
    public void testEmptyListChildReturnsEmptyList() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        
        URL url = getClass().getClassLoader().getResource(Commons.ACME1_FILE);
        URI uri = url.toURI();
        
        Commons.testEmptyListChildReturnsEmptyList(locator, uri);
    }
    
    /**
     * Tests that a list child with no elements returns an empty array (not null)
     * 
     * @throws Exception
     */
    @Test // @org.junit.Ignore
    public void testEmptyArrayChildReturnsEmptyArray() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        
        URL url = getClass().getClassLoader().getResource(Commons.ACME1_FILE);
        URI uri = url.toURI();
        
        Commons.testEmptyArrayChildReturnsEmptyArray(locator, uri);
    }
    
    /**
     * Tests that a byte[] child gets properly translated
     * (into itself, for now)
     * 
     * @throws Exception
     */
    @Test // @org.junit.Ignore
    public void testByteArrayNonChild() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        
        URL url = getClass().getClassLoader().getResource(Commons.ACME2_FILE);
        URI uri = url.toURI();
        
        Commons.testByteArrayNonChild(locator, uri);
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
        
        URL url = getClass().getClassLoader().getResource(Commons.SAMPLE_CONFIG_FILE);
        URI uri = url.toURI();
        
        Commons.testJaxbStyleReference(locator, uri);
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
    @Test // @org.junit.Ignore
    public void testJaxbStyleForwardReference() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        
        URL url = getClass().getClassLoader().getResource(Commons.REFERENCE1_FILE);
        URI uri = url.toURI();
        
        Commons.testJaxbStyleForwardReference(locator, uri);
    }
    
    /**
     * Tests that XmlJavaTypeAdapter works with these beans
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testXmlJavaTypeAdapter() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        
        URL url = getClass().getClassLoader().getResource(Commons.ROOT_BEAN_WITH_PROPERTIES);
        URI uri = url.toURI();
        
        Commons.testXmlJavaTypeAdapter(locator, uri);
    }
}
