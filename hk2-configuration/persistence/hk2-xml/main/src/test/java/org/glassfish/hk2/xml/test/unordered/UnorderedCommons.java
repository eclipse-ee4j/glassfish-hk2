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

package org.glassfish.hk2.xml.test.unordered;

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
public class UnorderedCommons {
    private final static String UNORDERED1_FILE = "unordered/unordered1.xml";
    /**
     * Tests an unordered file can be unmarshalled
     * 
     * @param locator
     */
    public static void testUnorderedUnmarshal(ServiceLocator locator, ClassLoader cl) throws Exception {
        XmlService xmlService = locator.getService(XmlService.class);
        
        URL url = cl.getResource(UNORDERED1_FILE);
        URI uri = url.toURI();
        
        XmlRootHandle<UnorderedRootBean> rootHandle = xmlService.unmarshal(uri, UnorderedRootBean.class);
        UnorderedRootBean root = rootHandle.getRoot();
        
        List<ABean> as = root.getA();
        BBean bs[] = root.getB();
        
        Assert.assertEquals(4, as.size());
        Assert.assertEquals(3, bs.length);
    }

}
