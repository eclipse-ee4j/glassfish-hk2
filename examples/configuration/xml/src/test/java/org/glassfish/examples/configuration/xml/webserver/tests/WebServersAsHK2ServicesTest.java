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

package org.glassfish.examples.configuration.xml.webserver.tests;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Provider;

import org.glassfish.examples.configuration.xml.webserver.ApplicationBean;
import org.glassfish.examples.configuration.xml.webserver.WebServerBean;
import org.glassfish.examples.configuration.xml.webserver.internal.WebServerManager;
import org.glassfish.hk2.xml.api.XmlRootHandle;
import org.glassfish.hk2.xml.api.XmlService;
import org.glassfish.hk2.xml.api.XmlServiceUtilities;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvnet.hk2.testing.junit.HK2Runner;

/**
 * @author jwells
 *
 */
public class WebServersAsHK2ServicesTest extends HK2Runner {
    private final static String EXAMPLE1_FILENAME = "webserverExample1.xml";
    
    @Inject
    private Provider<XmlService> xmlServiceProvider;
    
    @Before
    public void before() {
        initialize();
        
        XmlServiceUtilities.enableXmlService(testLocator);
    }
    
    /**
     * Tests that the XmlService puts the WebServerBeans into
     * the HK2 service registry with names defined by the
     * XmlIdentifier attribute (name)
     * 
     * @throws Exception
     */
    @Test
    public void testWebServerBeansAreHK2Services() throws Exception {
        XmlService xmlService = xmlServiceProvider.get();
        
        URI webserverFile = getClass().getClassLoader().getResource(EXAMPLE1_FILENAME).toURI();
        
        XmlRootHandle<ApplicationBean> applicationRootHandle =
                xmlService.unmarshal(webserverFile, ApplicationBean.class);
        
        WebServerManager manager = testLocator.getService(WebServerManager.class);
        Assert.assertNotNull(manager);
        
        {
            WebServerBean developmentServer = manager.getWebServer("Development Server");
            Assert.assertEquals("Development Server", developmentServer.getName());
            Assert.assertEquals(8001, developmentServer.getAdminPort());
            Assert.assertEquals(8002, developmentServer.getPort());
            Assert.assertEquals(8003, developmentServer.getSSLPort());
        }
        
        {
            WebServerBean qaServer = manager.getWebServer("QA Server");
            Assert.assertEquals("QA Server", qaServer.getName());
            Assert.assertEquals(9001, qaServer.getAdminPort());
            Assert.assertEquals(9002, qaServer.getPort());
            Assert.assertEquals(9003, qaServer.getSSLPort());
        }
        
        {
            WebServerBean externalServer = manager.getWebServer("External Server");
            Assert.assertEquals("External Server", externalServer.getName());
            Assert.assertEquals(10001, externalServer.getAdminPort());
            Assert.assertEquals(80, externalServer.getPort());
            Assert.assertEquals(81, externalServer.getSSLPort());
        }
    }

}
