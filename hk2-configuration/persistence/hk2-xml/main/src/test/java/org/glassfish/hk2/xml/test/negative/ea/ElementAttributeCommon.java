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

package org.glassfish.hk2.xml.test.negative.ea;

import java.net.URI;
import java.net.URL;
import java.util.List;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.xml.api.XmlRootHandle;
import org.glassfish.hk2.xml.api.XmlService;
import org.junit.Assert;

/**
 * @author jwells
 *
 */
public class ElementAttributeCommon {
    private final static String GOOD_FILE = "ea/ea1.xml";
    private final static String WHOLLY_MIXED_FILE = "ea/ea2.xml";
    private final static String BOTH_ATTRIBUTES_FILE = "ea/ea3.xml";
    private final static String BOTH_ELEMENTS_FILE = "ea/ea4.xml";
    
    private final static String ATTRIBUTE_VALUE = "attribute";
    private final static String ELEMENT_VALUE = "element";
    
    public static void testReadGoodFile(ServiceLocator locator, ClassLoader cl) throws Exception {
        XmlService xmlService = locator.getService(XmlService.class);
        
        URL url = cl.getResource(GOOD_FILE);
        URI uri = url.toURI();
        
        XmlRootHandle<ElementAttributeRoot> handle = xmlService.unmarshal(uri, ElementAttributeRoot.class, false, false);
        ElementAttributeRoot root = handle.getRoot();
        Assert.assertNotNull(root);
        
        List<ElementAttributeLeaf> leaves = root.getLeaves();
        Assert.assertEquals(1, leaves.size());
        
        ElementAttributeLeaf leaf = leaves.get(0);
        
        Assert.assertEquals(ATTRIBUTE_VALUE, leaf.getAttribute());
        Assert.assertEquals(ELEMENT_VALUE, leaf.getElement());
    }
    
    public static void testReadWhollyMixedFile(ServiceLocator locator, ClassLoader cl) throws Exception {
        XmlService xmlService = locator.getService(XmlService.class);
        
        URL url = cl.getResource(WHOLLY_MIXED_FILE);
        URI uri = url.toURI();
        
        XmlRootHandle<ElementAttributeRoot> handle = xmlService.unmarshal(uri, ElementAttributeRoot.class, false, false);
        ElementAttributeRoot root = handle.getRoot();
        Assert.assertNotNull(root);
        
        List<ElementAttributeLeaf> leaves = root.getLeaves();
        Assert.assertEquals(1, leaves.size());
        
        ElementAttributeLeaf leaf = leaves.get(0);
        
        Assert.assertNull(leaf.getAttribute());
        Assert.assertNull(leaf.getElement());
    }
    
    public static void testReadBothAttributes(ServiceLocator locator, ClassLoader cl) throws Exception {
        XmlService xmlService = locator.getService(XmlService.class);
        
        URL url = cl.getResource(BOTH_ATTRIBUTES_FILE);
        URI uri = url.toURI();
        
        XmlRootHandle<ElementAttributeRoot> handle = xmlService.unmarshal(uri, ElementAttributeRoot.class, false, false);
        ElementAttributeRoot root = handle.getRoot();
        Assert.assertNotNull(root);
        
        List<ElementAttributeLeaf> leaves = root.getLeaves();
        Assert.assertEquals(1, leaves.size());
        
        ElementAttributeLeaf leaf = leaves.get(0);
        
        Assert.assertEquals(ATTRIBUTE_VALUE, leaf.getAttribute());
        Assert.assertNull(leaf.getElement());
    }
    
    public static void testReadBothElements(ServiceLocator locator, ClassLoader cl) throws Exception {
        XmlService xmlService = locator.getService(XmlService.class);
        
        URL url = cl.getResource(BOTH_ELEMENTS_FILE);
        URI uri = url.toURI();
        
        XmlRootHandle<ElementAttributeRoot> handle = xmlService.unmarshal(uri, ElementAttributeRoot.class, false, false);
        ElementAttributeRoot root = handle.getRoot();
        Assert.assertNotNull(root);
        
        List<ElementAttributeLeaf> leaves = root.getLeaves();
        Assert.assertEquals(1, leaves.size());
        
        ElementAttributeLeaf leaf = leaves.get(0);
        
        Assert.assertNull(leaf.getAttribute());
        Assert.assertEquals(ELEMENT_VALUE, leaf.getElement());
    }

}
