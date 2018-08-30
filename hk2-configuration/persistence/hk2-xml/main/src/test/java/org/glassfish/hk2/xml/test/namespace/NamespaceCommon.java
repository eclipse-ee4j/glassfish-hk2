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
import java.util.Map;

import javax.xml.namespace.QName;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.xml.api.XmlRootHandle;
import org.glassfish.hk2.xml.api.XmlService;
import org.glassfish.hk2.xml.test.namespace.beans.DoubleNamespaceTroubleRootBean;
import org.glassfish.hk2.xml.test.namespace.beans.FooBean;
import org.glassfish.hk2.xml.test.namespace.beans.XtraAttributesRootBean;
import org.junit.Assert;

/**
 * 
 * @author jwells
 */
public class NamespaceCommon {
    public final static String XTRA_ATTRIBUTES_FILE = "xmlns/xtra-attributes.xml";
    public final static String NAMESPACE_CLASH_FILE = "xmlns/xml-namespace-clash.xml";
    
    private final static String ACME_NS_URI = "http://www.acme.org/jmxoverjms";
    private final static String BOX_NS_URI = "http://www.boxco.com/boxes";
    private final static String HLMS_NS_URI = "http://www.holmes.com/ac";
    
    private final static String ATTA_LOCAL = "attA";
    private final static String ATTA_PREFIX = "xos";
    private final static String ATTB_LOCAL = "attB";
    private final static String ATTB_PREFIX = "box";
    private final static String ATTC_LOCAL = "attC";
    private final static String ATTC_PREFIX = "sox";
    private final static String ATTD_LOCAL = "attD";
    
    private final static QName JOJ_ATTA_QNAME = new QName(ACME_NS_URI, ATTA_LOCAL, ATTA_PREFIX);
    private final static QName BOX_ATTB_QNAME = new QName(BOX_NS_URI, ATTB_LOCAL, ATTB_PREFIX);
    private final static QName DFL_ATTB_QNAME = new QName(ATTB_LOCAL);
    private final static QName SOX_ATTC_QNAME = new QName(HLMS_NS_URI, ATTC_LOCAL, ATTC_PREFIX);
    private final static QName DFL_ATTD_QNAME = new QName(ATTD_LOCAL);
    
    private final static String FOO = "foo";
    private final static String BAZ = "baz";
    private final static String GRAX = "grax";
    private final static String BAR = "bar";
    private final static String GMB = "gumby";
    private final static String FIG = "figaro";
    
    public final static String NAMESPACE_0 = "Namespace0";
    public final static String NAMESPACE_A = "NamespaceA";
    public final static String NAMESPACE_B = "NamespaceB";
    public final static String NAMESPACE_C = "NamespaceC";
    
    public static void testExtraAttributes(ServiceLocator locator, URI uri) {
        XmlService xmlService = locator.getService(XmlService.class);
        
        XmlRootHandle<XtraAttributesRootBean> rootHandle = xmlService.unmarshal(uri, XtraAttributesRootBean.class);
        XtraAttributesRootBean root = rootHandle.getRoot();
        FooBean fooBean = root.getFoo();
        
        Assert.assertEquals(FOO, fooBean.getAttA());
        
        Map<QName, String> others = fooBean.getOtherAttributes();
        Assert.assertNotNull(others);
        Assert.assertEquals(5, others.size());
        
        boolean foundBaz = false;
        boolean foundGrax = false;
        boolean foundBar = false;
        boolean foundGumby = false;
        boolean foundFigaro = false;
        for (Map.Entry<QName, String> entry : others.entrySet()) {
            QName qEntry = entry.getKey();
            String value = entry.getValue();
            
            if (qEntry.equals(JOJ_ATTA_QNAME)) {
                Assert.assertEquals(BAZ, value);
                foundBaz = true;
            }
            else if (qEntry.equals(BOX_ATTB_QNAME)) {
                Assert.assertEquals(GRAX, value);
                foundGrax = true;
            }
            else if (qEntry.equals(DFL_ATTB_QNAME)) {
                Assert.assertEquals(BAR, value);
                foundBar = true;
            }
            else if (qEntry.equals(SOX_ATTC_QNAME)) {
                Assert.assertEquals(GMB, value);
                foundGumby = true;
            }
            else if (qEntry.equals(DFL_ATTD_QNAME)) {
                Assert.assertEquals(FIG, value);
                foundFigaro = true;
            }
            else {
                Assert.fail("Unknown QName=" + qEntry + " and value " + value);
            }
        }
        
        Assert.assertTrue(foundBaz);
        Assert.assertTrue(foundGrax);
        Assert.assertTrue(foundBar);
        Assert.assertTrue(foundGumby);
        Assert.assertTrue(foundFigaro);
    }
    
    public static void testNamespaceClash(ServiceLocator locator, URI uri) {
        XmlService xmlService = locator.getService(XmlService.class);
        
        XmlRootHandle<DoubleNamespaceTroubleRootBean> rootHandle = xmlService.unmarshal(uri, DoubleNamespaceTroubleRootBean.class);
        DoubleNamespaceTroubleRootBean root = rootHandle.getRoot();
        
        Assert.assertEquals(FOO, root.getAlice());
        Assert.assertEquals(BAR, root.getAliceDefault());
        Assert.assertEquals(BAZ, root.getAliceB());
        Assert.assertEquals(GRAX, root.getAliceC());
    }

}
