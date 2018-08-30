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

package org.glassfish.hk2.utilities.reflection;

import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.glassfish.hk2.utilities.reflection.Pretty;
import org.junit.Test;
import org.glassfish.hk2.utilities.reflection.ParameterizedTypeImpl;

/**
 * @author jwells
 *
 */
public class PrettyTest {
    private final static String NULL = "null";
    
    /**
     * Tests the pretty printer clazz facility
     */
    @Test
    public void testPrettyClass() {
        Assert.assertEquals("PrettyTest", Pretty.clazz(this.getClass()));
    }
    
    /**
     * Tests the pretty printer clazz facility
     */
    @Test
    public void testPrettyNullClass() {
        Assert.assertEquals(NULL, Pretty.clazz(null));
    }
    
    /**
     * Tests the pretty printer clazz facility
     */
    @Test
    public void testPrettyNullType() {
        Assert.assertEquals(NULL, Pretty.type(null));
    }
    
    /**
     * Tests a parameterized type with one param
     */
    @Test
    public void testPrettyPT() {
        ParameterizedTypeImpl pti = new ParameterizedTypeImpl(List.class, String.class);
        Assert.assertEquals("List<String>", Pretty.type(pti));
    }
    
    /**
     * Tests a parameterized type with one param
     */
    @Test
    public void testPrettyPT2() {
        ParameterizedTypeImpl pti = new ParameterizedTypeImpl(Map.class, String.class, Integer.class);
        Assert.assertEquals("Map<String,Integer>", Pretty.type(pti));
    }

}
