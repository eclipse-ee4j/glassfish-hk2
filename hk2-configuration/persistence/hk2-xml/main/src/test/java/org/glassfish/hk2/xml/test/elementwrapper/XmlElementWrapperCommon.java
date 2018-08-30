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

package org.glassfish.hk2.xml.test.elementwrapper;

import java.net.URI;
import java.net.URL;
import java.util.List;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.xml.api.XmlHk2ConfigurationBean;
import org.glassfish.hk2.xml.api.XmlRootHandle;
import org.glassfish.hk2.xml.api.XmlService;
import org.glassfish.hk2.xml.test.elementwrapper.beans.WrapperLeafBean;
import org.glassfish.hk2.xml.test.elementwrapper.beans.WrapperMiddleBean;
import org.glassfish.hk2.xml.test.elementwrapper.beans.WrapperRootBean;
import org.junit.Assert;

/**
 * @author jwells
 *
 */
public class XmlElementWrapperCommon {
    private final static String WRAPPER_FILE1 = "elementwrapper/ElementWrapper1.xml";
    
    private final static String ALICE = "Alice";
    private final static String BOB = "Bob";
    
    public static void testReadOneOfEachElement(ServiceLocator locator, ClassLoader cl) throws Exception {
        XmlService xmlService = locator.getService(XmlService.class);
        
        URL url = cl.getResource(WRAPPER_FILE1);
        URI uri = url.toURI();
        
        XmlRootHandle<WrapperRootBean> handle = xmlService.unmarshal(uri, WrapperRootBean.class, true, true);
        WrapperRootBean root = handle.getRoot();
        Assert.assertNotNull(root);
        
        List<WrapperMiddleBean> middles = root.getMiddle();
        Assert.assertEquals(1, middles.size());
        
        WrapperMiddleBean middle = middles.get(0);
        
        Assert.assertEquals(ALICE, middle.getName());
        XmlHk2ConfigurationBean middleConfigBean = (XmlHk2ConfigurationBean) middle;
        
        Assert.assertEquals("/wrapper-root/middles/middle", middleConfigBean._getXmlPath());
        Assert.assertEquals("wrapper-root.middles.Alice", middleConfigBean._getInstanceName());
        Assert.assertEquals(ALICE, middleConfigBean._getKeyValue());
        
        List<WrapperLeafBean> leaves = middle.getLeaves();
        Assert.assertEquals(1, leaves.size());
        
        WrapperLeafBean leaf = leaves.get(0);
        
        Assert.assertEquals(BOB, leaf.getData());
        XmlHk2ConfigurationBean leafConfigBean = (XmlHk2ConfigurationBean) leaf;
        
        Assert.assertEquals("/wrapper-root/middles/middle/leaves/leaf", leafConfigBean._getXmlPath());
        String instanceName = leafConfigBean._getInstanceName();
        
        Assert.assertNull(leafConfigBean._getKeyPropertyName());
        String keyValue = leafConfigBean._getKeyValue();
        Assert.assertNotNull(keyValue);
        
        String fullKeyName = "wrapper-root.middles.Alice.leaves." + keyValue;
        Assert.assertEquals(fullKeyName, instanceName);
    }
}
