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

package org.glassfish.hk2.tests.locator.negative.constructor;

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
public class NegativeConstructorTest {
    private final static String TEST_NAME = "NegativeConstructorTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new NegativeConstructorModule());
    
    /**
     * This constructor has two non-zero arg constructors marked &#64;Inject
     */
    @Test
    public void testTwoBadNonZeroArgConstructor() {
        try {
            locator.reifyDescriptor(locator.getBestDescriptor(BuilderHelper.createContractFilter(
                    BadC.class.getName())));
            Assert.fail("Should have failed, two @Inject constructors");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage().contains("There is more than one constructor on class "));
        }
        
    }
    
    /**
     * This constructor has two non-zero arg constructors marked &#64;Inject
     */
    @Test
    public void testBadNoConstructorAtAll() {
        try {
            locator.reifyDescriptor(locator.getBestDescriptor(BuilderHelper.createContractFilter(
                    NoC.class.getName())));
            Assert.fail("Should have failed, no constructor");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage().contains(" has no constructor marked @Inject and no zero argument constructor"));
        }
        
    }
    
    /**
     * This constructor has an annotation in its constructor
     */
    @Test
    public void testAnnotationInConstructor() {
        try {
            locator.reifyDescriptor(locator.getBestDescriptor(BuilderHelper.createContractFilter(
                    AnnotationC.class.getName())));
            Assert.fail("Constructor with an Annotation parameter should have failed");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage().contains(" may not have an annotation as a parameter"));
        }
        
    }

}
