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

import org.glassfish.hk2.utilities.BuilderHelper;
import org.junit.Assert;
import org.junit.Test;
import org.jvnet.hk2.testing.junit.HK2Runner;
import org.jvnet.hk2.testing.junit.annotations.Packages;
import org.jvnet.hk2.testing.test.alt.AnotherAltService;

/**
 * @author jwells
 *
 */
@Packages("org.jvnet.hk2.testing.test.alt")
public class PackageAnnotationTest extends HK2Runner {
    /**
     * Point of this test, no @Before
     */
    @Test
    public void testPackageAnnotation() {
        Assert.assertNull(testLocator.getBestDescriptor(BuilderHelper.createContractFilter(SimpleService0.class.getName())));
        Assert.assertNotNull(testLocator.getBestDescriptor(BuilderHelper.createContractFilter(AnotherAltService.class.getName())));
    }

}
