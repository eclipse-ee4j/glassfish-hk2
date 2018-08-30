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

package org.glassfish.hk2.xml.test.childlocators;

import java.net.URL;
import java.util.List;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.configuration.hub.api.Hub;
import org.glassfish.hk2.xml.api.XmlRootHandle;
import org.glassfish.hk2.xml.api.XmlService;
import org.glassfish.hk2.xml.spi.XmlServiceParser;
import org.glassfish.hk2.xml.test.beans.DomainBean;
import org.glassfish.hk2.xml.test.dynamic.merge.MergeTest;
import org.glassfish.hk2.xml.test.utilities.Utilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class ChildLocatorTest {
    /**
     * Makes sure children do not see parent XmlService and hub
     */
    @Test
    public void testChildrenDoNotSeeParentServices() {
        ServiceLocator parentLocator = Utilities.createDomLocator();
        ServiceLocator childALocator = Utilities.createDomLocator(parentLocator);
        ServiceLocator childBLocator = Utilities.createDomLocator(parentLocator);
        ServiceLocator grandChildALocator = Utilities.createDomLocator(childALocator);
        
        List<XmlService> allXmlServices = grandChildALocator.getAllServices(XmlService.class);
        Assert.assertEquals(1, allXmlServices.size());
        XmlService grandChildAXmlService = allXmlServices.get(0);
        
        allXmlServices = childALocator.getAllServices(XmlService.class);
        Assert.assertEquals(1, allXmlServices.size());
        XmlService childAXmlService = allXmlServices.get(0);
        
        allXmlServices = parentLocator.getAllServices(XmlService.class);
        Assert.assertEquals(1, allXmlServices.size());
        XmlService parentXmlService = allXmlServices.get(0);
        
        allXmlServices = childBLocator.getAllServices(XmlService.class);
        Assert.assertEquals(1, allXmlServices.size());
        XmlService childBXmlService = allXmlServices.get(0);
        
        Assert.assertNotEquals(grandChildAXmlService, childAXmlService);
        Assert.assertNotEquals(grandChildAXmlService, parentXmlService);
        Assert.assertNotEquals(grandChildAXmlService, childBXmlService);
        Assert.assertNotEquals(childAXmlService, parentXmlService);
        Assert.assertNotEquals(childAXmlService, childBXmlService);
        Assert.assertNotEquals(parentXmlService, childBXmlService);
        
        // Now check the Hub
        List<Hub> allHubServices = grandChildALocator.getAllServices(Hub.class);
        Assert.assertEquals(1, allHubServices.size());
        Hub grandChildAHub = allHubServices.get(0);
        
        allHubServices = childALocator.getAllServices(Hub.class);
        Assert.assertEquals(1, allHubServices.size());
        Hub childAHub = allHubServices.get(0);
        
        allHubServices = parentLocator.getAllServices(Hub.class);
        Assert.assertEquals(1, allHubServices.size());
        Hub parentHub = allHubServices.get(0);
        
        allHubServices = childBLocator.getAllServices(Hub.class);
        Assert.assertEquals(1, allHubServices.size());
        Hub childBHub = allHubServices.get(0);
        
        Assert.assertNotEquals(grandChildAHub, childAHub);
        Assert.assertNotEquals(grandChildAHub, parentHub);
        Assert.assertNotEquals(grandChildAHub, childBHub);
        Assert.assertNotEquals(childAHub, parentHub);
        Assert.assertNotEquals(childAHub, childBHub);
        Assert.assertNotEquals(parentHub, childBHub);
    }
    
    /**
     * One parent, one child, both with XmlService started.
     * Only read document in child, ensure it is there in
     * child but not there in parent
     */
    @Test
    public void testReadingInChildOnlyWorks() throws Exception {
        ServiceLocator parentLocator = Utilities.createDomLocator();
        ServiceLocator childLocator = Utilities.createDomLocator(parentLocator);
        
        XmlService childXmlService = childLocator.getService(XmlService.class);
        Hub childHub = childLocator.getService(Hub.class);
        
        URL url = getClass().getClassLoader().getResource(MergeTest.DOMAIN1_FILE);
        
        XmlRootHandle<DomainBean> rootHandle = childXmlService.unmarshal(url.toURI(), DomainBean.class);
        
        Hub parentHub = parentLocator.getService(Hub.class);
        
        MergeTest.verifyDomain1Xml(rootHandle, childHub, childLocator);
        MergeTest.verifyDomain1XmlDomainNotThere(parentHub, parentLocator);
    }
    
    /**
     * One parent, one child, both with XmlService started.
     * Read in both child and parent, verify expected
     * services were found
     */
    @Test
    public void testReadingInChildAndParentWorks() throws Exception {
        ServiceLocator parentLocator = Utilities.createDomLocator();
        ServiceLocator childLocator = Utilities.createDomLocator(parentLocator);
        
        XmlService childXmlService = childLocator.getService(XmlService.class);
        XmlService parentXmlService = parentLocator.getService(XmlService.class);
        
        Hub childHub = childLocator.getService(Hub.class);
        Hub parentHub = parentLocator.getService(Hub.class);
        
        URL url = getClass().getClassLoader().getResource(MergeTest.DOMAIN1_FILE);
        
        XmlRootHandle<DomainBean> childHandle = childXmlService.unmarshal(url.toURI(), DomainBean.class);
        XmlRootHandle<DomainBean> parentHandle = parentXmlService.unmarshal(url.toURI(), DomainBean.class);
        
        MergeTest.verifyDomain1Xml(parentHandle, parentHub, parentLocator);
        MergeTest.verifyDomain1Xml(childHandle, childHub, childLocator);
    }
    
    /**
     * One parent, one child, both with XmlService started.
     * Only read document in parent, ensure the services are
     * in the child, but nothing else
     */
    @Test
    public void testReadingInParentOnlyWorks() throws Exception {
        ServiceLocator parentLocator = Utilities.createDomLocator();
        ServiceLocator childLocator = Utilities.createDomLocator(parentLocator);
        
        XmlService parentXmlService = parentLocator.getService(XmlService.class);
        
        Hub childHub = childLocator.getService(Hub.class);
        Hub parentHub = parentLocator.getService(Hub.class);
        
        URL url = getClass().getClassLoader().getResource(MergeTest.DOMAIN1_FILE);
        
        XmlRootHandle<DomainBean> parentHandle = parentXmlService.unmarshal(url.toURI(), DomainBean.class);
        
        MergeTest.verifyDomain1Xml(parentHandle, parentHub, parentLocator);
        MergeTest.assertDomain1Services(childLocator, parentLocator, false);
        
        MergeTest.verifyDomain1XmlDomainNotThere(childHub, childLocator);
    }

}
