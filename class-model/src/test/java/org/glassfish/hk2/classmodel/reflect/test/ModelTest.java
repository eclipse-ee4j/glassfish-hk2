/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.classmodel.reflect.test;

import org.glassfish.hk2.classmodel.reflect.*;

import org.glassfish.hk2.classmodel.reflect.test.ordering.MethodDeclarationOrderTest;
import org.glassfish.hk2.classmodel.reflect.test.parameterized.PathRouteBuilder;
import org.junit.Test;
import org.junit.Assert;
import java.io.IOException;


/**
 * Model related tests
 */
public class ModelTest {

    @Test
    public void orderTest() throws IOException, InterruptedException {

        Types types = ClassModelTestsUtils.getTypes();
        Type order = types.getBy(MethodDeclarationOrderTest.class.getName());
        Assert.assertNotNull(order);
        Assert.assertTrue(order.getMethods().size()==4);
        int i=1;
        for (MethodModel mm : order.getMethods()) {
            if (mm.getName().equals("<init>"))
                continue;

            Assert.assertEquals("method"+i, mm.getName());
            i++;
        }
    }

    @Test
    public void parameterizedInterfacesTest() throws IOException, InterruptedException {
        Types types = ClassModelTestsUtils.getTypes();
        ExtensibleType<?> pathRouteBuilder = (ExtensibleType<?>) types.getBy(PathRouteBuilder.class.getName());
        Assert.assertEquals(pathRouteBuilder.getParameterizedInterfaces().size(), 1);
    }
}
