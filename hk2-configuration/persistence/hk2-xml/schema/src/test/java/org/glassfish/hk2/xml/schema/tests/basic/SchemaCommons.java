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

import java.net.URI;
import java.net.URL;
import java.util.List;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.xml.api.XmlRootHandle;
import org.glassfish.hk2.xml.api.XmlService;
import org.glassfish.hk2.xml.schema.beans.OpenAttrs;
import org.glassfish.hk2.xml.schema.beans.Schema;
import org.junit.Assert;

/**
 * @author jwells
 *
 */
public class SchemaCommons {
    private final static String BENCHMARK_SCHEMA = "progress-tracker.xsd";

    public static void testReadBasicSchema(ServiceLocator locator, ClassLoader cl) throws Exception {
        XmlService xmlService = locator.getService(XmlService.class);
        
        URL url = cl.getResource(BENCHMARK_SCHEMA);
        URI uri = url.toURI();
        
        XmlRootHandle<Schema> schemaHandle = xmlService.unmarshal(uri, Schema.class);
        Schema schema = schemaHandle.getRoot();
        
        Assert.assertNotNull(schema);
        
        List<OpenAttrs> elements = schema.getSimpleTypeOrComplexTypeOrGroup();
        Assert.assertEquals(3, elements.size());
    }

}
