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

package org.glassfish.hk2.xml.test.negative.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.xml.api.XmlRootHandle;
import org.glassfish.hk2.xml.api.XmlService;
import org.glassfish.hk2.xml.spi.XmlServiceParser;
import org.glassfish.hk2.xml.test.beans.DomainBean;
import org.glassfish.hk2.xml.test.dynamic.merge.MergeTest;
import org.glassfish.hk2.xml.test.utilities.Utilities;
import org.junit.Test;

/**
 * Tests for bad input and the like
 * @author jwells
 *
 */
public class NegativeAPITest {
    private final static File OUTPUT_FILE = new File("negative-output.xml");
    private final static Filter PARSER_REMOVE_FILTER = BuilderHelper.createContractFilter(XmlServiceParser.class.getName());
    
    private final URL DOMAIN_URL = getClass().getClassLoader().getResource(MergeTest.DOMAIN1_FILE);
    private final XMLInputFactory xif = XMLInputFactory.newInstance();
    
    
    private XMLStreamReader openDomainReader() throws Exception {
        InputStream is = DOMAIN_URL.openStream();
        return xif.createXMLStreamReader(is);
    }
    
    private InputStream openDomainInputStream() throws Exception {
        return DOMAIN_URL.openStream();
    }
    
    /**
     * XmlService.unmarshal with null URI
     */
    @Test(expected=IllegalArgumentException.class)
    public void testNullURIXmlServiceUnmarshal() {
        ServiceLocator locator = Utilities.createDomLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        
        xmlService.unmarshal((URI) null, DomainBean.class);
    }
    
    /**
     * XmlService.unmarshal with null bean
     */
    @Test(expected=IllegalArgumentException.class)
    public void testNullBeanXmlServiceUnmarshal() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        
        URI uri = DOMAIN_URL.toURI();
        
        xmlService.unmarshal(uri, null);
    }
    
    /**
     * XmlService.unmarshal with null bean
     */
    @Test(expected=IllegalArgumentException.class)
    public void testBeanIsClassXmlServiceUnmarshal() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        
        URI uri = DOMAIN_URL.toURI();
        
        xmlService.unmarshal(uri, NegativeAPITest.class);
    }
    
    /**
     * XmlService.unmarshal with null URI
     */
    @Test(expected=IllegalArgumentException.class)
    public void testNullReaderXmlServiceUnmarshal() {
        ServiceLocator locator = Utilities.createDomLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        
        xmlService.unmarshal((XMLStreamReader) null, DomainBean.class, false, true);
    }
    
    /**
     * XmlService.unmarshal with null bean
     */
    @Test(expected=IllegalArgumentException.class)
    public void testNullBeanXmlServiceUnmarshalReader() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        
        XMLStreamReader reader = openDomainReader();
        try {
            xmlService.unmarshal(reader, null, false, false);
        }
        finally {
            reader.close();
        }
    }
    
    /**
     * XmlService.unmarshal with null bean
     */
    @Test(expected=IllegalArgumentException.class)
    public void testBeanIsClassXmlServiceUnmarshalReader() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        
        XMLStreamReader reader = openDomainReader();
        try {
            xmlService.unmarshal(reader, NegativeAPITest.class, true, false);
        }
        finally {
            reader.close();
        }
    }
    
    /**
     * XmlService.unmarshal with null URI
     */
    @Test(expected=IllegalArgumentException.class)
    public void testNullInputStreamXmlServiceUnmarshal() {
        ServiceLocator locator = Utilities.createDomLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        
        xmlService.unmarshal((InputStream) null, DomainBean.class);
    }
    
    /**
     * XmlService.unmarshal with null bean
     */
    @Test(expected=IllegalArgumentException.class)
    public void testNullBeanXmlServiceUnmarshalInputStream() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        
        InputStream reader = openDomainInputStream();
        try {
            xmlService.unmarshal(reader, null, false, false);
        }
        finally {
            reader.close();
        }
    }
    
    /**
     * XmlService.unmarshal with null bean
     */
    @Test(expected=IllegalArgumentException.class)
    public void testBeanIsClassXmlServiceUnmarshalInputStream() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        
        InputStream reader = openDomainInputStream();
        try {
            xmlService.unmarshal(reader, NegativeAPITest.class, true, false);
        }
        finally {
            reader.close();
        }
    }
    
    /**
     * If the parser is gone an exception is thrown (URI version)
     * @throws Exception
     */
    @Test(expected=IllegalStateException.class)
    public void testNoParserXmlServiceUnmarshalURI() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        
        removeParser(locator);
        
        xmlService.unmarshal(DOMAIN_URL.toURI(), DomainBean.class);
    }
    
    /**
     * If the parser is gone an exception is thrown (InputStream version)
     * @throws Exception
     */
    @Test(expected=IllegalStateException.class)
    public void testNoParserXmlServiceUnmarshalInputStream() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        
        removeParser(locator);
        
        InputStream reader = openDomainInputStream();
        try {
          xmlService.unmarshal(reader, DomainBean.class, false, false);
        }
        finally {
            reader.close();
        }
    }
    
    /**
     * If the parser is gone an exception is thrown (InputStream version)
     * @throws Exception
     */
    @Test(expected=IllegalStateException.class)
    public void testNoParserXmlServiceMarshal() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        
        XmlRootHandle<DomainBean> handle = xmlService.unmarshal(DOMAIN_URL.toURI(), DomainBean.class);
        
        removeParser(locator);
        
        FileOutputStream fos = new FileOutputStream(OUTPUT_FILE);
        try {
          xmlService.marshal(fos, handle);
        }
        finally {
            fos.close();
        }
    }
    
    /**
     * If the parser is gone an exception is thrown (InputStream version)
     * @throws Exception
     */
    @Test(expected=IllegalArgumentException.class)
    public void testXmlServiceEmptyHandleBadInput() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        
        xmlService.createEmptyHandle(NegativeAPITest.class);
    }
    
    /**
     * If the parser is gone an exception is thrown (InputStream version)
     * @throws Exception
     */
    @Test(expected=IllegalArgumentException.class)
    public void testXmlServiceCreateBeanBadInput() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        
        xmlService.createBean(NegativeAPITest.class);
    }
    
    private static void removeParser(ServiceLocator locator) {
        ServiceLocatorUtilities.removeFilter(locator, PARSER_REMOVE_FILTER);
    }

}
