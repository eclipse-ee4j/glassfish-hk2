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

package org.glassfish.hk2.xml.test.defaulting;

import java.net.URL;

import javax.xml.namespace.QName;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.configuration.hub.api.Hub;
import org.glassfish.hk2.xml.api.XmlHk2ConfigurationBean;
import org.glassfish.hk2.xml.api.XmlRootHandle;
import org.glassfish.hk2.xml.api.XmlService;
import org.glassfish.hk2.xml.spi.XmlServiceParser;
import org.glassfish.hk2.xml.test.beans.DomainBean;
import org.glassfish.hk2.xml.test.beans.SSLManagerBean;
import org.glassfish.hk2.xml.test.beans.SSLManagerBeanCustomizer;
import org.glassfish.hk2.xml.test.beans.SecurityManagerBean;
import org.glassfish.hk2.xml.test.dynamic.merge.MergeTest;
import org.glassfish.hk2.xml.test.utilities.Utilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class DefaultingTest {
    private final static String DEFAULTING_FILE = "defaulted.xml";
    
    private final DefaultingCommon commons = new DefaultingCommon();
    
    /**
     * Tests that we can default values with JAXB 
     * @throws Exception
     */
    @Test // @org.junit.Ignore
    public void testDefaultedValues() throws Exception {
        ServiceLocator locator = Utilities.createLocator();
        XmlService xmlService = locator.getService(XmlService.class, XmlServiceParser.DEFAULT_PARSING_SERVICE);
        
        commons.testDefaultedValues(xmlService);
    }
    
    /**
     * Tests that we can default values with JAXB 
     * @throws Exception
     */
    @Test // @org.junit.Ignore
    public void testDefaultedValuesStream() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        XmlService xmlService = locator.getService(XmlService.class, XmlServiceParser.STREAM_PARSING_SERVICE);
        
        commons.testDefaultedValues(xmlService);
    }
    
    /**
     * Tests that we can default values with JAXB 
     * @throws Exception
     */
    @Test // @org.junit.Ignore
    public void testDefaultDefaultedValues() throws Exception {
        ServiceLocator locator = Utilities.createLocator();
        XmlService xmlService = locator.getService(XmlService.class, XmlServiceParser.DEFAULT_PARSING_SERVICE);
        
        commons.testDefaultDefaultedValues(xmlService);
    }
    
    /**
     * Tests that we can default values with JAXB 
     * @throws Exception
     */
    @Test // @org.junit.Ignore
    public void testDefaultDefaultedValuesString() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        XmlService xmlService = locator.getService(XmlService.class, XmlServiceParser.STREAM_PARSING_SERVICE);
        
        commons.testDefaultDefaultedValues(xmlService);
    }
    
    /**
     * Tests that defaults work in a dynamically created bean
     */
    @Test // @org.junit.Ignore
    public void testCanGetValuesFromDynamicallyCreatedBean() {
        ServiceLocator locator = Utilities.createLocator();
        XmlService xmlService = locator.getService(XmlService.class, XmlServiceParser.DEFAULT_PARSING_SERVICE);
        
        commons.testCanGetValuesFromDynamicallyCreatedBean(xmlService);
    }
    
    /**
     * Tests that defaults work in a dynamically created bean
     */
    @Test // @org.junit.Ignore
    public void testCanGetValuesFromDynamicallyCreatedBeanStream() {
        ServiceLocator locator = Utilities.createDomLocator();
        XmlService xmlService = locator.getService(XmlService.class, XmlServiceParser.STREAM_PARSING_SERVICE);
        
        commons.testCanGetValuesFromDynamicallyCreatedBean(xmlService);
    }
    
    /**
     * Ensures that we can default beans via an InstanceLifecycleListener
     * 
     * @throws Exception
     */
    @Test
    public void testDefaultingViaServiceWorks() throws Exception {
        ServiceLocator locator = Utilities.createLocator(
                SSLManagerBeanCustomizer.class,
                SecurityManagerBeanDefaulter.class);
        
        XmlService xmlService = locator.getService(XmlService.class, XmlServiceParser.DEFAULT_PARSING_SERVICE);
        
        commons.testDefaultingViaServiceWorks(locator, xmlService);
    }
    
    /**
     * Ensures that we can default beans via an InstanceLifecycleListener
     * 
     * @throws Exception
     */
    @Test
    public void testDefaultingViaServiceWorksStream() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator(
                SSLManagerBeanCustomizer.class,
                SecurityManagerBeanDefaulter.class);
        
        XmlService xmlService = locator.getService(XmlService.class, XmlServiceParser.STREAM_PARSING_SERVICE);
        
        commons.testDefaultingViaServiceWorks(locator, xmlService);
    }
    
    /**
     * Ensures that we can default beans via an InstanceLifecycleListener
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testDefaultingViaAddWorks() throws Exception {
        ServiceLocator locator = Utilities.createLocator(
                SSLManagerBeanCustomizer.class,
                SecurityManagerBeanDefaulter.class);
        
        XmlService xmlService = locator.getService(XmlService.class, XmlServiceParser.DEFAULT_PARSING_SERVICE);
        
        commons.testDefaultingViaAddWorks(locator, xmlService);
        
    }
    
    /**
     * Ensures that we can default beans via an InstanceLifecycleListener
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testDefaultingViaAddWorksStream() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator(
                SSLManagerBeanCustomizer.class,
                SecurityManagerBeanDefaulter.class);
        
        XmlService xmlService = locator.getService(XmlService.class, XmlServiceParser.STREAM_PARSING_SERVICE);
        
        commons.testDefaultingViaAddWorks(locator, xmlService);
        
    }

}
