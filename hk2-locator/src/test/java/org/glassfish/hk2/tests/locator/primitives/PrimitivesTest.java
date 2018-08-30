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

package org.glassfish.hk2.tests.locator.primitives;

import junit.framework.Assert;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class PrimitivesTest {
    /** Thirteen as a char.  I guess t will have to do */
    public final static char THIRTEEN_CHAR = 't';
    
    /** Thirteen as a byte */
    public final static byte THIRTEEN_BYTE = 13;
    
    /** Thirteen as a short */
    public final static short THIRTEEN_SHORT = 13;
    
    /** Thirteen as a int */
    public final static int THIRTEEN_INTEGER = 13;
    
    /** Thirteen as a long */
    public final static long THIRTEEN_LONG = 13L;
    
    /** Thirteen as a float */
    public final static float THIRTEEN_FLOAT = 13;
    
    /** Thirteen as a double */
    public final static double THIRTEEN_DOUBLE = 13;
    
    private final static String TEST_NAME = "PrimitivesTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new PrimitivesModule());
    
    /**
     * Tests character 13
     */
    @Test
    public void testThirteenChar() {
        PrimitiveInjectee pi = locator.getService(PrimitiveInjectee.class);
        
        Assert.assertEquals(THIRTEEN_CHAR, pi.getThirteenChar());
    }
    
    /**
     * Tests byte 13
     */
    @Test
    public void testThirteenByte() {
        PrimitiveInjectee pi = locator.getService(PrimitiveInjectee.class);
        
        Assert.assertEquals(THIRTEEN_BYTE, pi.getThirteenByte());
    }
    
    /**
     * Tests short 13
     */
    @Test
    public void testThirteenShort() {
        PrimitiveInjectee pi = locator.getService(PrimitiveInjectee.class);
        
        Assert.assertEquals(THIRTEEN_SHORT, pi.getThirteenShort());
    }
    
    /**
     * Tests int 13
     */
    @Test
    public void testThirteenInteger() {
        PrimitiveInjectee pi = locator.getService(PrimitiveInjectee.class);
        
        Assert.assertEquals(THIRTEEN_INTEGER, pi.getThirteenInt());
    }
    
    /**
     * Tests long 13
     */
    @Test
    public void testThirteenLong() {
        PrimitiveInjectee pi = locator.getService(PrimitiveInjectee.class);
        
        Assert.assertEquals(THIRTEEN_LONG, pi.getThirteenLong());
    }
    
    /**
     * Tests float 13
     */
    @Test
    public void testThirteenFloat() {
        PrimitiveInjectee pi = locator.getService(PrimitiveInjectee.class);
        
        Assert.assertEquals(THIRTEEN_FLOAT, pi.getThirteenFloat());
    }
    
    /**
     * Tests double 13
     */
    @Test
    public void testThirteenDouble() {
        PrimitiveInjectee pi = locator.getService(PrimitiveInjectee.class);
        
        Assert.assertEquals(THIRTEEN_DOUBLE, pi.getThirteenDouble());
    }

}
