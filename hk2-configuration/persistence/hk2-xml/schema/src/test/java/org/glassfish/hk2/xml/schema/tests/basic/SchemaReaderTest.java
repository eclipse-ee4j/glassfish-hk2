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

package org.glassfish.hk2.xml.schema.tests.basic;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.xml.schema.tests.utilities.SchemaUtilities;
import org.junit.Test;

/**
 * Tests for reading schema files
 * 
 * @author jwells
 *
 */
public class SchemaReaderTest {
    /**
     * Reads a very basic schema using Dom
     */
    @Test
    @org.junit.Ignore
    public void testReadBasicSchemaDom() throws Exception {
        ServiceLocator locator = SchemaUtilities.createDomLocator();
        
        SchemaCommons.testReadBasicSchema(locator, getClass().getClassLoader());
    }
    
    /**
     * Reads a very basic schema using JAXB
     */
    @Test
    @org.junit.Ignore
    public void testReadBasicSchema() throws Exception {
        ServiceLocator locator = SchemaUtilities.createJAXBLocator();
        
        SchemaCommons.testReadBasicSchema(locator, getClass().getClassLoader());
    }
}
