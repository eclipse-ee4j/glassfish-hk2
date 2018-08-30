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

package org.glassfish.hk2.xml.test.elements;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.xml.test.utilities.Utilities;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class ElementsTest {
    /**
     * The document has one of each type of Element
     * using streaming service
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testXmlElementsDom() throws Exception {
        ServiceLocator domLocator = Utilities.createDomLocator();
        
        ElementsCommon.testReadOneOfEachElement(domLocator, getClass().getClassLoader());
    }
    
    /**
     * The document has one of each type of Element
     * using jaxb service
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testXmlElementsJaxb() throws Exception {
        ServiceLocator domLocator = Utilities.createLocator();
        
        ElementsCommon.testReadOneOfEachElement(domLocator, getClass().getClassLoader());
    }
    
    /**
     * The document has one of each type of Element
     * using streaming service
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testXmlElementsMarshallDom() throws Exception {
        ServiceLocator domLocator = Utilities.createDomLocator();
        
        ElementsCommon.testMarshal(domLocator, getClass().getClassLoader());
    }
    
    /**
     * The document has one of each type of Element
     * using streaming service
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testXmlElementsMarshallJaxb() throws Exception {
        ServiceLocator domLocator = Utilities.createLocator();
        
        ElementsCommon.testMarshal(domLocator, getClass().getClassLoader());
    }
    
    /**
     * The document has one of each type of Element
     * using streaming service
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testXmlElementsScalarDom() throws Exception {
        ServiceLocator domLocator = Utilities.createDomLocator();
        
        ElementsCommon.testScalarElements(domLocator, getClass().getClassLoader(), false);
    }
    
    /**
     * The document has one of each type of Element
     * using streaming service
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testXmlElementsScalarJaxb() throws Exception {
        ServiceLocator domLocator = Utilities.createLocator();
        
        ElementsCommon.testScalarElements(domLocator, getClass().getClassLoader(), true);
    }

}
