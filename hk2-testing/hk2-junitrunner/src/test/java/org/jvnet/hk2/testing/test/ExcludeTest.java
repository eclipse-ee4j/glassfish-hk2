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

package org.jvnet.hk2.testing.test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvnet.hk2.testing.junit.HK2Runner;

/**
 * @author jwells
 *
 */
public class ExcludeTest extends HK2Runner {
    
    @Before
    public void before() {
        LinkedList<String> packages = new LinkedList<String>();
        packages.add(this.getClass().getPackage().getName());
        
        Set<String> excludes = new HashSet<String>();
        excludes.add(SimpleService2.class.getName());
        
        initialize(this.getClass().getName(), packages, null, excludes);
    }
    
    /**
     * Ensures that the excluded service is in fact excluded
     */
    @Test
    public void testExcludedServiceIsExcluded() {
        // Makes sure we are sane
        Assert.assertNotNull(testLocator.getService(SimpleService0.class));
        
        // The test
        Assert.assertNull(testLocator.getService(SimpleService2.class));
    }

}
