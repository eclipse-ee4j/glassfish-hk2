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
import org.glassfish.hk2.classmodel.reflect.test.parameterized.RouteBuilder;
import org.junit.Test;
import org.junit.Assert;
import java.io.IOException;
import org.glassfish.hk2.classmodel.reflect.test.parameterized.GenericRouteBuilder;
import org.glassfish.hk2.classmodel.reflect.test.parameterized.PathPattern;
import org.glassfish.hk2.classmodel.reflect.test.parameterized.Pattern;

/**
 * Model related tests
 */
public class ModelTest {

    @Test
    public void orderTest() throws IOException, InterruptedException {

        Types types = ClassModelTestsUtils.getTypes();
        Type order = types.getBy(MethodDeclarationOrderTest.class.getName());
        Assert.assertNotNull(order);
        Assert.assertTrue(order.getMethods().size() == 4);
        int i = 1;
        for (MethodModel mm : order.getMethods()) {
            if (mm.getName().equals("<init>")) {
                continue;
            }

            Assert.assertEquals("method" + i, mm.getName());
            i++;
        }
    }

    @Test
    public void parameterizedInterfaceTest() throws IOException, InterruptedException {
        Types types = ClassModelTestsUtils.getTypes();
        ExtensibleType<?> routeBuilder = (ExtensibleType<?>) types.getBy(RouteBuilder.class.getName());
        Assert.assertEquals(2, routeBuilder.getMethods().size());

        MethodModel passMethod = routeBuilder.getMethods().stream()
                .filter(m -> "passPattern".equals(m.getName()))
                .findFirst()
                .get();

        Assert.assertEquals("void", passMethod.getReturnType().getTypeName());
        Assert.assertNull(passMethod.getReturnType().getType());  //Even though it's a constant, the actual type is null
        Assert.assertEquals(1, passMethod.getParameters().size());
        Assert.assertEquals(Pattern.class.getName(), passMethod.getParameters().get(0).getTypeName()); //We know the constraint
        Assert.assertNull(passMethod.getParameters().get(0).getType());  //Even though it's constrained, the actual type is null

        MethodModel patternMethod = routeBuilder.getMethods().stream()
                .filter(m -> "pattern".equals(m.getName()))
                .findFirst()
                .get();

        Assert.assertEquals(Pattern.class.getName(), patternMethod.getReturnType().getTypeName()); //We know the constraint
        Assert.assertNull(patternMethod.getReturnType().getType());  //Even though it's constrained, the actual type is null
        Assert.assertEquals(0, patternMethod.getParameters().size());
    }

    @Test
    public void parameterizedGenericInterfaceTest() throws IOException, InterruptedException {
        Types types = ClassModelTestsUtils.getTypes();
        ExtensibleType<?> genericRouteBuilder = (ExtensibleType<?>) types.getBy(GenericRouteBuilder.class.getName());
        Assert.assertEquals(3, genericRouteBuilder.getMethods().size());

        MethodModel passMethod = genericRouteBuilder.getMethods().stream()
                .filter(m -> "passPattern".equals(m.getName()))
                .findFirst()
                .get();

        Assert.assertEquals("void", passMethod.getReturnType().getTypeName());
        Assert.assertNull(passMethod.getReturnType().getType());  //Even though it's a constant, the actual type is null
        Assert.assertEquals(1, passMethod.getParameters().size());
        Assert.assertEquals(Pattern.class.getName(), passMethod.getParameters().get(0).getTypeName()); //We know the constraint
        Assert.assertNull(passMethod.getParameters().get(0).getType());  //Even though it's constrained, the actual type is null

        MethodModel patternMethod = genericRouteBuilder.getMethods().stream()
                .filter(m -> "pattern".equals(m.getName()))
                .findFirst()
                .get();

        Assert.assertEquals(Pattern.class.getName(), patternMethod.getReturnType().getTypeName()); //We know the constraint
        Assert.assertNull(patternMethod.getReturnType().getType());  //Even though it's constrained, the actual type is null
        Assert.assertEquals(0, patternMethod.getParameters().size());

        FieldModel pathField = ((ClassModel) genericRouteBuilder).getFields().stream()
                .filter(f -> "path".equals(f.getName()))
                .findFirst()
                .get();

        Assert.assertNotNull(pathField);
        Assert.assertEquals(Pattern.class.getName(), pathField.getTypeName());
        Assert.assertEquals(Pattern.class.getName(), pathField.getDeclaringTypeName());
        Assert.assertNotNull(pathField.getType());

    }

    @Test
    public void parameterizedInterfaceImplementerTest() throws IOException, InterruptedException {
        Types types = ClassModelTestsUtils.getTypes();
        ExtensibleType<?> pathRouteBuilder = (ExtensibleType<?>) types.getBy(PathRouteBuilder.class.getName());
        Assert.assertEquals(1, pathRouteBuilder.getParameterizedInterfaces().size());

        Assert.assertEquals(5, pathRouteBuilder.getMethods().size());

        MethodModel passMethod = pathRouteBuilder.getMethods().stream()
                .filter(m -> "passPattern".equals(m.getName()))
                .findFirst()
                .get();

        Assert.assertEquals("void", passMethod.getReturnType().getTypeName());
        Assert.assertNull(passMethod.getReturnType().getType());  //Even though it's a constant, the actual type is null
        Assert.assertEquals(1, passMethod.getParameters().size());
        Assert.assertEquals(PathPattern.class.getName(), passMethod.getParameters().get(0).getTypeName()); //We know the value that the generic takes
        Assert.assertNotNull(passMethod.getParameters().get(0).getType());  //Type is filled in so it exists now

        MethodModel patternMethod = pathRouteBuilder.getMethods().stream()
                .filter(m -> "pattern".equals(m.getName()))
                .findFirst()
                .get();

        Assert.assertEquals(PathPattern.class.getName(), patternMethod.getReturnType().getTypeName());//We know the value that the generic takes
        Assert.assertNotNull(patternMethod.getReturnType().getType()); //Type is filled in so it exists now
        Assert.assertEquals(0, patternMethod.getParameters().size());

        FieldModel pathField = ((ClassModel) pathRouteBuilder).getFields().stream()
                .filter(f -> "path".equals(f.getName()))
                .findFirst()
                .get();

        Assert.assertNotNull(pathField);
        Assert.assertEquals(PathPattern.class.getName(), pathField.getTypeName());
        Assert.assertEquals(PathPattern.class.getName(), pathField.getDeclaringTypeName());
        Assert.assertNotNull(pathField.getType());

    }
}
