/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.locator.negative.method;

import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class NegativeMethodTest {
    private final static String TEST_NAME = "NegativeMethodTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new NegativeMethodModule());
    
    /**
     * Methods injected may not be static
     */
    @Test
    public void testStaticMethod() {
        try {
            locator.reifyDescriptor(locator.getBestDescriptor(BuilderHelper.createContractFilter(
                    StaticMethodService.class.getName())));
            Assert.fail("static method should cause failure");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage().contains("is static, abstract or has a parameter that is an annotation"));
        }
    }
    
    /**
     * Methods injected may not be static
     */
    @Test
    public void testAnnotationMethod() {
        try {
            locator.reifyDescriptor(locator.getBestDescriptor(BuilderHelper.createContractFilter(
                    AnnotationMethodService.class.getName())));
            Assert.fail("method with annotation should cause failure");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage().contains("is static, abstract or has a parameter that is an annotation"));
        }
    }

}
