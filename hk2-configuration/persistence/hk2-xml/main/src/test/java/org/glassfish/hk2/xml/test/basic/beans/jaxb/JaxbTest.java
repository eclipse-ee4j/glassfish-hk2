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

package org.glassfish.hk2.xml.test.basic.beans.jaxb;

import java.net.URI;
import java.net.URL;
import java.util.Map;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.xml.api.XmlRootHandle;
import org.glassfish.hk2.xml.api.XmlService;
import org.glassfish.hk2.xml.test.basic.beans.Commons;
import org.glassfish.hk2.xml.test.utilities.Utilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * This is a test that sees what JAXB does with normal classes
 * 
 * @author jwells
 *
 */
public class JaxbTest {
    /**
     * Tests the most basic of xml files can be unmarshalled with an interface
     * annotated with jaxb annotations
     * 
     * @throws Exception
     */
    @Test
    public void testXmlJavaTypeAdapter() throws Exception {
       ServiceLocator locator = Utilities.createLocator();
        
        URL url = getClass().getClassLoader().getResource(Commons.ROOT_BEAN_WITH_PROPERTIES);
        URI uri = url.toURI();
        XmlService xmlService = locator.getService(XmlService.class);
        
        XmlRootHandle<JaxbRootWithProperties> rootHandle;
        
        rootHandle = xmlService.unmarshal(uri, JaxbRootWithProperties.class);
        
        JaxbRootWithProperties rbwp = rootHandle.getRoot();
        
        Map<String, String> props = rbwp.getProperties();
        Assert.assertEquals(Commons.BOB, props.get(Commons.ALICE));
        Assert.assertEquals(Commons.DAVE, props.get(Commons.CAROL));
    }

}
