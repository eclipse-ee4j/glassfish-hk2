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

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.xml.test.utilities.Utilities;
import org.junit.Test;

/**
 * Tests that files with unordered children can be read
 * @author jwells
 *
 */
public class UnorderedTest {
    /**
     * Shows we can read an unordered xml file streaming
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testStreamingUnordered() throws Exception {
        ServiceLocator locator = Utilities.createDomLocator();
        
        UnorderedCommons.testUnorderedUnmarshal(locator, getClass().getClassLoader());
    }
    
    /**
     * Shows we can read an unordered xml file jaxb
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testStreamingJaxb() throws Exception {
        ServiceLocator locator = Utilities.createLocator();
        
        UnorderedCommons.testUnorderedUnmarshal(locator, getClass().getClassLoader());
    }

}
