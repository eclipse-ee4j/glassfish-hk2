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

package org.glassfish.hk2.utilities.reflection;

import java.util.Set;

import org.glassfish.hk2.utilities.reflection.internal.ClassReflectionHelperImpl;
import org.glassfish.hk2.utilities.reflection.types2.ServiceInterface2;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class ClassReflectionHelperTest {
    /**
     * Tests we get all the methods from an interface
     */
    @Test
    public void testInterface() {
        ClassReflectionHelper helper = new ClassReflectionHelperImpl();
        
        Set<MethodWrapper> wrappers = helper.getAllMethods(OverallInterface.class);
        Assert.assertEquals(4, wrappers.size());
        
        boolean foundName = false;
        boolean foundOne = false;
        boolean foundTwo = false;
        boolean foundThree = false;
        for (MethodWrapper wrapper : wrappers) {
            String name = wrapper.getMethod().getName();
            if ("getName".equals(name)) foundName = true;
            else if ("fromBaseInterfaceOne".equals(name)) foundOne = true;
            else if ("fromBaseInterfaceTwo".equals(name)) foundTwo = true;
            else if ("fromBaseInterfaceThree".equals(name)) foundThree = true;
            else {
                Assert.fail("Uknown method name=" + name);
            }
        }
        
        Assert.assertTrue(foundName);
        Assert.assertTrue(foundOne);
        Assert.assertTrue(foundTwo);
        Assert.assertTrue(foundThree);
        
    }
    
    /**
     * Tests that an interface extending another works
     */
    @Test
    public void testInterfaceExtendsAnotherInterface() {
        ClassReflectionHelper helper = new ClassReflectionHelperImpl();
        
        Set<MethodWrapper> wrappers = helper.getAllMethods(ServiceInterface2.class);
        Assert.assertEquals(3, wrappers.size());
        
        boolean foundBase = false;
        boolean foundService = false;
        boolean foundService2 = false;
        for (MethodWrapper wrapper : wrappers) {
            String name = wrapper.getMethod().getName();
            if ("fromServiceInterface".equals(name)) foundService = true;
            else if ("fromBaseInterface".equals(name)) foundBase = true;
            else if ("fromServiceInterface2".equals(name)) foundService2 = true;
            else {
                Assert.fail("Uknown method name=" + name);
            }
        }
        
        Assert.assertTrue(foundBase);
        Assert.assertTrue(foundService);
        Assert.assertTrue(foundService2);
    }
}
