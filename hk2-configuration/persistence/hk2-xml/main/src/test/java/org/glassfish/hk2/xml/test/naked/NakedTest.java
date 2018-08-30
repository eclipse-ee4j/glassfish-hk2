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

package org.glassfish.hk2.xml.test.naked;

import java.net.URL;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.xml.api.XmlRootHandle;
import org.glassfish.hk2.xml.api.XmlService;
import org.glassfish.hk2.xml.test.basic.beans.Commons;
import org.glassfish.hk2.xml.test.utilities.Utilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class NakedTest {
    private final static String NAKED_FILE = "naked.xml";
    
    /**
     * Tests that children with no XmlElement
     * attribute will work properly
     */
    @Test // @org.junit.Ignore
    public void testChildrenWithNoAnnotation() throws Exception {
        ServiceLocator locator = Utilities.createLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        
        URL url = getClass().getClassLoader().getResource(NAKED_FILE);
        
        XmlRootHandle<ParentBean> rootHandle = xmlService.unmarshal(url.toURI(), ParentBean.class);
        
        ParentBean parentBean = rootHandle.getRoot();
        Assert.assertNotNull(parentBean);
        
        // Lets check the data
        for (int lcv = 0; lcv < 2; lcv++) {
            ChildOne child = parentBean.getOne().get(lcv);
            
            switch(lcv) {
            case 0 : 
                Assert.assertEquals("d1", child.getData());
                break;
            case 1:
                Assert.assertEquals("d2", child.getData());
                break;
            default:
                Assert.fail();
            }
                    
        }
        
        for (int lcv = 0; lcv < 3; lcv++) {
            ChildTwo child = parentBean.getTwo().get(lcv);
            
            switch(lcv) {
            case 0 : 
                Assert.assertEquals(Commons.ALICE, child.getName());
                break;
            case 1:
                Assert.assertEquals(Commons.BOB, child.getName());
                break;
            case 2:
                Assert.assertEquals(Commons.CAROL, child.getName());
                break;
            default:
                Assert.fail();
            }
                    
        }
        
        for (int lcv = 0; lcv < 1; lcv++) {
            ChildOne child3 = parentBean.getThree()[lcv];
            ChildTwo child4 = parentBean.getFour()[lcv];
            
            switch(lcv) {
            case 0 : 
                Assert.assertEquals("d3", child3.getData());
                Assert.assertEquals(Commons.DAVE, child4.getName());
                break;
            default:
                Assert.fail();
            }
                    
        }
        
        Assert.assertEquals("d4", parentBean.getFive().getData());
        Assert.assertEquals(Commons.ENGLEBERT, parentBean.getFive().getName());
        
        Assert.assertEquals("d5", parentBean.getSix().getData());
        Assert.assertEquals(Commons.FRANK, parentBean.getSix().getName());
    }

}
