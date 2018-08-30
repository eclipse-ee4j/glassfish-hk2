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

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.xml.test.utilities.Utilities;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class ElementAttributeTest {
    /**
     * The document is fine.  Just a benchmark test
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testGoodDocumentDom() throws Exception {
        ServiceLocator domLocator = Utilities.createDomLocator();
        
        ElementAttributeCommon.testReadGoodFile(domLocator, getClass().getClassLoader());
    }
    
    /**
     * The document is fine.  Just a benchmark test
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testGoodDocumentJaxb() throws Exception {
        ServiceLocator domLocator = Utilities.createLocator();
        
        ElementAttributeCommon.testReadGoodFile(domLocator, getClass().getClassLoader());
    }
    
    /**
     * The document is fine.  Just a benchmark test
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testWhollyMixedDom() throws Exception {
        ServiceLocator domLocator = Utilities.createDomLocator();
        
        ElementAttributeCommon.testReadWhollyMixedFile(domLocator, getClass().getClassLoader());
    }
    
    /**
     * The document is fine.  Just a benchmark test
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testWhollyMixedJaxb() throws Exception {
        ServiceLocator domLocator = Utilities.createLocator();
        
        ElementAttributeCommon.testReadWhollyMixedFile(domLocator, getClass().getClassLoader());
    }
    
    /**
     * The document is fine.  Just a benchmark test
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testBothAttributesDom() throws Exception {
        ServiceLocator domLocator = Utilities.createDomLocator();
        
        ElementAttributeCommon.testReadBothAttributes(domLocator, getClass().getClassLoader());
    }
    
    /**
     * The document is fine.  Just a benchmark test
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testBothAttributesJaxb() throws Exception {
        ServiceLocator domLocator = Utilities.createLocator();
        
        ElementAttributeCommon.testReadBothAttributes(domLocator, getClass().getClassLoader());
    }
    
    /**
     * The document is fine.  Just a benchmark test
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testBothElementsDom() throws Exception {
        ServiceLocator domLocator = Utilities.createDomLocator();
        
        ElementAttributeCommon.testReadBothElements(domLocator, getClass().getClassLoader());
    }
    
    /**
     * The document is fine.  Just a benchmark test
     * 
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testBothElementsJaxb() throws Exception {
        ServiceLocator domLocator = Utilities.createLocator();
        
        ElementAttributeCommon.testReadBothElements(domLocator, getClass().getClassLoader());
    }

}
