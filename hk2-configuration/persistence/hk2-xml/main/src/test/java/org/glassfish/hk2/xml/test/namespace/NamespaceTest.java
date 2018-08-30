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

package org.glassfish.hk2.xml.test.namespace;

import java.net.URI;
import java.net.URL;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.xml.test.utilities.Utilities;
import org.junit.Test;

/**
 * These tests use Dom and JAXB
 * 
 * @author jwells
 *
 */
public class NamespaceTest {
    /**
     * Tests a very basic XmlAnyAttribute with the native XmlService
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testExtraAttributesNative() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        URL url = getClass().getClassLoader().getResource(NamespaceCommon.XTRA_ATTRIBUTES_FILE);
        URI uri = url.toURI();
        
        NamespaceCommon.testExtraAttributes(locator, uri);
    }
    
    /**
     * Tests a very basic XmlAnyAttribute with the JAXB XmlService
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testExtraAttributesJAXB() throws Exception {
        ServiceLocator locator = Utilities.createLocator();
        URL url = getClass().getClassLoader().getResource(NamespaceCommon.XTRA_ATTRIBUTES_FILE);
        URI uri = url.toURI();
        
        NamespaceCommon.testExtraAttributes(locator, uri);
    }
    
    /**
     * Tests that documents that use namespaces can work,
     * even if they have the same xml tags
     * 
     * @throws Exception
     */
    @Test
    @org.junit.Ignore
    public void testNamespaceClashJAXB() throws Exception {
        ServiceLocator locator = Utilities.createLocator();
        URL url = getClass().getClassLoader().getResource(NamespaceCommon.NAMESPACE_CLASH_FILE);
        URI uri = url.toURI();
        
        NamespaceCommon.testNamespaceClash(locator, uri);
    }
    
    /**
     * Tests that documents that use namespaces can work,
     * even if they have the same xml tags
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testNamespaceClashNative() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        URL url = getClass().getClassLoader().getResource(NamespaceCommon.NAMESPACE_CLASH_FILE);
        URI uri = url.toURI();
        
        NamespaceCommon.testNamespaceClash(locator, uri);
    }

}
