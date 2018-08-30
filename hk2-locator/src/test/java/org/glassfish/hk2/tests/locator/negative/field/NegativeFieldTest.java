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

package org.glassfish.hk2.tests.locator.negative.field;

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
public class NegativeFieldTest {
    private final static String TEST_NAME = "NegativeFieldTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new NegativeFieldModule());
    
    /**
     * Fields injected may not be static
     */
    @Test
    public void testStaticField() {
        try {
            locator.reifyDescriptor(locator.getBestDescriptor(BuilderHelper.createContractFilter(
                    StaticFieldService.class.getName())));
            Assert.fail("static field should cause failure");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage().contains(" may not be static, final or have an Annotation type"));
        }
    }
    
    /**
     * Fields injected may not be final
     */
    @Test
    public void testFinalField() {
        try {
            locator.reifyDescriptor(locator.getBestDescriptor(BuilderHelper.createContractFilter(
                    FinalFieldService.class.getName())));
            Assert.fail("final field should cause failure");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage().contains(" may not be static, final or have an Annotation type"));
        }
    }
    
    /**
     * Fields injected may not be annotations
     */
    @Test
    public void testAnnotationField() {
        try {
            locator.reifyDescriptor(locator.getBestDescriptor(BuilderHelper.createContractFilter(
                    AnnotationFieldService.class.getName())));
            Assert.fail("annotated field should cause failure");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage().contains(" may not be static, final or have an Annotation type"));
        }
    }

}
