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

package org.glassfish.hk2.xml.test.childrensametype;

import java.net.URL;

import org.junit.Assert;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.xml.api.XmlRootHandle;
import org.glassfish.hk2.xml.api.XmlService;
import org.glassfish.hk2.xml.test.utilities.Utilities;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class ChildrenSameTypeTest {

    /**
     * FooBar has two children of the same type.  Ensures
     * children of same type work
     * 
     * @throws Exception
     */
    @Test // @org.junit.Ignore
    public void testNegativeTwoChildrenWithSameType() throws Exception {
        ServiceLocator locator = Utilities.createLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        
        URL url = getClass().getClassLoader().getResource("foobar.xml");
        
        XmlRootHandle<FooBarBean> handle = xmlService.unmarshal(url.toURI(), FooBarBean.class);
        
        FooBarBean fooBar = handle.getRoot();
        Assert.assertNotNull(fooBar);
        
        Assert.assertEquals(2, fooBar.getFoo().size());
        Assert.assertEquals("foo1", fooBar.getFoo().get(0).getData());
        Assert.assertEquals("foo2", fooBar.getFoo().get(1).getData());
        
        Assert.assertEquals(2, fooBar.getBar().size());
        Assert.assertEquals("bar1", fooBar.getBar().get(0).getData());
        Assert.assertEquals("bar2", fooBar.getBar().get(1).getData());
    }

}
