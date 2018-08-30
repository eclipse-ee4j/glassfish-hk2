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

package org.glassfish.examples.configuration.webserver.tests;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.glassfish.examples.configuration.webserver.SSLCertificateBean;
import org.glassfish.examples.configuration.webserver.WebServer;
import org.glassfish.examples.configuration.webserver.WebServerBean;
import org.glassfish.examples.configuration.webserver.internal.SSLCertificateService;
import org.glassfish.examples.configuration.webserver.internal.WebServerImpl;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.configuration.api.ConfigurationUtilities;
import org.glassfish.hk2.configuration.persistence.properties.PropertyFileBean;
import org.glassfish.hk2.configuration.persistence.properties.PropertyFileHandle;
import org.glassfish.hk2.configuration.persistence.properties.PropertyFileService;
import org.glassfish.hk2.configuration.persistence.properties.PropertyFileUtilities;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Ensures and demonstrates how the configuration drives the creation of the
 * Web Server and the associated certificates
 * 
 * @author jwells
 *
 */
public class WebServerTest {
    private ServiceLocator locator;
    
    @Before
    public void before() {
        locator = ServiceLocatorFactory.getInstance().create(null);
        
        // Enable HK2 service integration
        ConfigurationUtilities.enableConfigurationSystem(locator);
        
        // Enable Properties service, to get service properties from a Properties object
        PropertyFileUtilities.enablePropertyFileService(locator);
        
        // The propertyFileBean contains the mapping from type names to Java Beans
        PropertyFileBean propertyFileBean = new PropertyFileBean();
        propertyFileBean.addTypeMapping("WebServerBean", WebServerBean.class);
        propertyFileBean.addTypeMapping("SSLCertificateBean", SSLCertificateBean.class);
        
        // Add in the mapping from type name to bean classes
        PropertyFileService propertyFileService = locator.getService(PropertyFileService.class);
        propertyFileService.addPropertyFileBean(propertyFileBean);
        
        // Add the test services themselves
        ServiceLocatorUtilities.addClasses(locator,
                SSLCertificateService.class,
                WebServerImpl.class);
    }
    
    /**
     * This test demonstrates adding and the modifying the http and
     * ssl ports of the web server
     */
    @Test // @org.junit.Ignore
    public void testDemonstrateWebServerConfiguration() throws IOException {
        // Before we add a configuration there is no web server
        WebServer webServer = locator.getService(WebServer.class);
        Assert.assertNull(webServer);
        
        Properties configuration = new Properties();
        
        // Gets the URL of the configuration property file.  This
        // file contains one web server and two SSL certificate
        // configuration objects
        URL configURL = getClass().getClassLoader().getResource("config.prop");
        InputStream configStream = configURL.openConnection().getInputStream();
        try {
            // Read the property file
            configuration.load(configStream);
        }
        finally {
            configStream.close();
        }
        
        // In order to read the Properties object into HK2 we need to get a PropertyFileHandle
        PropertyFileService propertyFileService = locator.getService(PropertyFileService.class);
        PropertyFileHandle propertyFileHandle = propertyFileService.createPropertyHandleOfAnyType();
        
        // Now read the configuration into hk2
        propertyFileHandle.readProperties(configuration);
        
        // We should now have a web server!
        webServer = locator.getService(WebServer.class);
        Assert.assertNotNull(webServer);
        
        // Lets open all the ports, and check that they have the expected values
        // In this case the ports are:
        // adminPort = 7070
        // sslPort = 81
        // port = 80
        Assert.assertEquals((int) 7070, webServer.openAdminPort());
        Assert.assertEquals((int) 81, webServer.openSSLPort());
        Assert.assertEquals((int) 80, webServer.openPort());
        
        // Now lets check that we have two SSL certificates
        List<File> certs = webServer.getCertificates();
        
        // The two certificates should be Corporatex509.cert and HRx509.cert
        Assert.assertEquals(2, certs.size());
        
        HashSet<String> foundCerts = new HashSet<String>();
        for (File cert : certs) {
            foundCerts.add(cert.getName());
        }
        
        Assert.assertTrue(foundCerts.contains("Corporatex509.cert"));
        Assert.assertTrue(foundCerts.contains("HRx509.cert"));
        
        // OK, we have verified that all of the parameters of the
        // webserver are as expected.  We are now going to dynamically
        // change all the ports.  In the webserver however only
        // the ssl and http ports are dynamic, so after the change
        // only the ssl and http ports should have their new values,
        // while the admin port should remain with the old value
        
        // Change the ports so that they look like this in the properties file:
        // adminPort = 8082
        // sslPort = 8081
        // port = 8080
        configuration.put("WebServerBean.Acme.adminPort", "8082");
        configuration.put("WebServerBean.Acme.sslPort", "8081");
        configuration.put("WebServerBean.Acme.port", "8080");
        
        // Tell hk2 about the change
        propertyFileHandle.readProperties(configuration);
        
        // Now lets check the web server, make sure the ports have been modified
        
        // The adminPort is NOT dynamic in the back end service, so it did not change
        Assert.assertEquals(7070, webServer.getAdminPort());
        
        // But the SSL and HTTP ports have changed dynamically
        Assert.assertEquals(8081, webServer.getSSLPort());
        Assert.assertEquals(8080, webServer.openPort());
    }
}
