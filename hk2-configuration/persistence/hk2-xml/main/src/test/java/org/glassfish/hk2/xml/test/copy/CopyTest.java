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

package org.glassfish.hk2.xml.test.copy;

import java.net.URL;
import java.util.List;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.configuration.hub.api.Hub;
import org.glassfish.hk2.xml.api.XmlHk2ConfigurationBean;
import org.glassfish.hk2.xml.api.XmlRootCopy;
import org.glassfish.hk2.xml.api.XmlRootHandle;
import org.glassfish.hk2.xml.api.XmlService;
import org.glassfish.hk2.xml.test.beans.AuthorizationProviderBean;
import org.glassfish.hk2.xml.test.beans.DomainBean;
import org.glassfish.hk2.xml.test.beans.JMSServerBean;
import org.glassfish.hk2.xml.test.beans.MachineBean;
import org.glassfish.hk2.xml.test.beans.QueueBean;
import org.glassfish.hk2.xml.test.beans.SecurityManagerBean;
import org.glassfish.hk2.xml.test.beans.ServerBean;
import org.glassfish.hk2.xml.test.beans.TopicBean;
import org.glassfish.hk2.xml.test.dynamic.merge.MergeTest;
import org.glassfish.hk2.xml.test.dynamic.rawsets.UpdateListener;
import org.glassfish.hk2.xml.test.utilities.Utilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class CopyTest {
    
    /**
     * Tests a deep tree including all metadata
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testCopyOfDeepTree() throws Exception {
        ServiceLocator locator = Utilities.createLocator(UpdateListener.class);
        XmlService xmlService = locator.getService(XmlService.class);
        Hub hub = locator.getService(Hub.class);
        
        URL url = getClass().getClassLoader().getResource(MergeTest.DOMAIN1_FILE);
        
        XmlRootHandle<DomainBean> rootHandle = xmlService.unmarshal(url.toURI(), DomainBean.class);
        
        MergeTest.verifyDomain1Xml(rootHandle, hub, locator);
        
        // All above just verifying the pre-state
        XmlRootCopy<DomainBean> copy = rootHandle.getXmlRootCopy();
        
        MergeTest.verifyDomain1Xml(rootHandle, copy, null, null);
        
        DomainBean domainCopy = copy.getChildRoot();
        DomainBean domainOriginal = rootHandle.getRoot();
        
        verifyMetadataTheSame((XmlHk2ConfigurationBean) domainOriginal, (XmlHk2ConfigurationBean) domainCopy);
        
        SecurityManagerBean securityManagerCopy = domainCopy.getSecurityManager();
        SecurityManagerBean securityManagerOriginal = domainCopy.getSecurityManager();
        
        verifyMetadataTheSame((XmlHk2ConfigurationBean) securityManagerOriginal, (XmlHk2ConfigurationBean) securityManagerCopy);
        
        AuthorizationProviderBean atzProviderCopy = securityManagerCopy.getAuthorizationProviders().get(0);
        AuthorizationProviderBean atzProviderOriginal = securityManagerOriginal.getAuthorizationProviders().get(0);
        
        verifyMetadataTheSame((XmlHk2ConfigurationBean) atzProviderOriginal, (XmlHk2ConfigurationBean) atzProviderCopy);
        
        MachineBean machineCopy = domainCopy.getMachines().get(0);
        MachineBean machineOriginal = domainOriginal.getMachines().get(0);
        
        verifyMetadataTheSame((XmlHk2ConfigurationBean) machineOriginal, (XmlHk2ConfigurationBean) machineCopy);
        
        ServerBean serverCopy = machineCopy.getServers().get(0);
        ServerBean serverOriginal = machineOriginal.getServers().get(0);
        
        verifyMetadataTheSame((XmlHk2ConfigurationBean) serverOriginal, (XmlHk2ConfigurationBean) serverCopy);
        
        JMSServerBean jmsServersCopy[] = domainCopy.getJMSServers();
        JMSServerBean jmsServersOriginal[] = domainOriginal.getJMSServers();
        
        Assert.assertEquals(jmsServersCopy.length, jmsServersOriginal.length);
        
        for (int lcv = 0; lcv < jmsServersOriginal.length; lcv++) {
            JMSServerBean jmsServerCopy = jmsServersCopy[lcv];
            JMSServerBean jmsServerOriginal = jmsServersOriginal[lcv];
            
            verifyMetadataTheSame((XmlHk2ConfigurationBean) jmsServerOriginal, (XmlHk2ConfigurationBean) jmsServerCopy);
            
            {
                List<TopicBean> topicsCopy = jmsServerCopy.getTopics();
                List<TopicBean> topicsOriginal = jmsServerOriginal.getTopics();
            
                Assert.assertEquals(topicsCopy.size(), topicsOriginal.size());
            
                for (int lcv1 = 0; lcv1 < topicsOriginal.size(); lcv1++) {
                    TopicBean topicCopy = topicsCopy.get(lcv1);
                    TopicBean topicOriginal = topicsOriginal.get(lcv1);
                
                    verifyMetadataTheSame((XmlHk2ConfigurationBean) topicOriginal, (XmlHk2ConfigurationBean) topicCopy);
                }
            }
            
            {
                QueueBean queuesCopy[] = jmsServerCopy.getQueues();
                QueueBean queuesOriginal[] = jmsServerOriginal.getQueues();
            
                Assert.assertEquals(queuesCopy.length, queuesOriginal.length);
            
                for (int lcv1 = 0; lcv1 < queuesOriginal.length; lcv1++) {
                    QueueBean queueCopy = queuesCopy[lcv1];
                    QueueBean queueOriginal = queuesOriginal[lcv1];
                
                    verifyMetadataTheSame((XmlHk2ConfigurationBean) queueOriginal, (XmlHk2ConfigurationBean) queueCopy);
                }
            }
            
        }
    }
    
    private static void verifyMetadataTheSame(XmlHk2ConfigurationBean original, XmlHk2ConfigurationBean copy) {
        Assert.assertEquals("xmlPath does not match", original._getXmlPath(), copy._getXmlPath());
        Assert.assertEquals("instanceName does not match", original._getInstanceName(), copy._getInstanceName());
        Assert.assertEquals("keyPropertyName does not match", original._getKeyPropertyName(), copy._getKeyPropertyName());
        Assert.assertEquals("keyValue does not match", original._getKeyValue(), copy._getKeyValue());
    }

}
