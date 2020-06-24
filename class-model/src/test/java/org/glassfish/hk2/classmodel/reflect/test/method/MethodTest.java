/*
 * Copyright (c) 2010, 2020 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.hk2.classmodel.reflect.test.method;

import org.glassfish.hk2.classmodel.reflect.*;
import org.glassfish.hk2.classmodel.reflect.test.ClassModelTestsUtils;
import org.junit.Assert;
import org.junit.Test;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * method related tests
 */
public class MethodTest {

    @Test
    public void simpleTest() throws IOException, InterruptedException {
        Types types = ClassModelTestsUtils.getTypes();
        Type type = types.getBy(SomeAnnotation.class.getName());
        Assert.assertTrue(type instanceof AnnotationType);
        AnnotationType annotation = (AnnotationType) type;
        Collection<AnnotatedElement> aes = annotation.allAnnotatedTypes();
        // we must find our SimpleAnnotatedMethod.setFoo method
        Assert.assertNotNull(aes);
        Assert.assertTrue(aes.size() > 0);
        for (AnnotatedElement ae : aes) {
            if (ae instanceof MethodModel) {
                MethodModel mm = (MethodModel) ae;
                if ("setFoo".equals(mm.getName())) {
                    if (mm.getDeclaringType().getName().equals(SimpleAnnotatedMethod.class.getName())) {
                        // success

                        Assert.assertEquals(mm.getAnnotations().toString(), 3, mm.getAnnotations().size());
                        Iterator<AnnotationModel> itr = mm.getAnnotations().iterator();
                        AnnotationModel someAnnotation = itr.next();
                        Assert.assertEquals("values", 5, someAnnotation.getValues().size());
                        Assert.assertEquals("aLong value", 10L, someAnnotation.getValues().get("aLong"));

                        Assert.assertEquals("aEnum value", "ENUM1", someAnnotation.getValues().get("aEnum").toString());

                        AnnotationModel childAnnotation = (AnnotationModel) someAnnotation.getValues().get("childAnnotation");
                        Assert.assertEquals("childAnnotation value", "child_value", childAnnotation.getValues().get("value"));

                        Assert.assertEquals("aClass value", "java.lang.Void", someAnnotation.getValues().get("aClass"));

                        List aClassArr = (List) someAnnotation.getValues().get("aClassArr");
                        Assert.assertEquals("aClassArr value 1", "java.lang.Integer", aClassArr.get(0));
                        Assert.assertEquals("aClassArr value 2", "java.lang.String", aClassArr.get(1));

                        Assert.assertEquals("default values", 3, someAnnotation.getType().getDefaultValues().size());
                        Assert.assertEquals("java.lang.Void", someAnnotation.getType().getDefaultValues().get("environment"));

                        AnnotationModel colorsRepeatable = itr.next();
                        List<AnnotationModel> colors = (List<AnnotationModel>) colorsRepeatable.getValues().get("value");
                        Assert.assertEquals("red", colors.get(0).getValues().get("name"));
                        Assert.assertEquals("green", colors.get(1).getValues().get("name"));

                        AnnotationModel gradientColorsRepeatable = itr.next();
                        List<AnnotationModel> gradientColors = (List<AnnotationModel>) gradientColorsRepeatable.getValues().get("value");

                        List<AnnotationModel> gradientColor1 = (List<AnnotationModel>) gradientColors.get(0).getValues().get("value");
                        Assert.assertEquals("white", gradientColor1.get(0).getValues().get("name"));
                        Assert.assertEquals("black", gradientColor1.get(1).getValues().get("name"));

                        List<AnnotationModel> gradientColor2 = (List<AnnotationModel>) gradientColors.get(1).getValues().get("value");
                        Assert.assertEquals("yellow", gradientColor2.get(0).getValues().get("name"));
                        Assert.assertEquals("orange", gradientColor2.get(1).getValues().get("name"));

                        // Parameter annotations, type and generic types check
                        Assert.assertEquals(5, mm.getParameters().size());

                        Parameter param1 = mm.getParameter(0);
                        Assert.assertEquals(1, param1.getAnnotations().size());
                        AnnotationModel param1AnnotationModel = param1.getAnnotations().iterator().next();
                        Assert.assertEquals("brown", param1AnnotationModel.getValues().get("name"));
                        Assert.assertEquals("java.lang.String", param1.getTypeName());
                        Assert.assertEquals(0, param1.getGenericTypes().size());

                        Parameter param2 = mm.getParameter(1);
                        Assert.assertEquals(0, param2.getAnnotations().size());
                        Assert.assertEquals("java.util.List", param2.getTypeName());
                        Assert.assertEquals(1, param2.getGenericTypes().size());
                        Assert.assertEquals("java.lang.String", param2.getGenericTypes().get(0).getTypeName());

                        Parameter param3 = mm.getParameter(2);
                        Assert.assertEquals(0, param3.getAnnotations().size());
                        Assert.assertEquals("org.glassfish.hk2.classmodel.reflect.test.method.SampleType", param3.getTypeName());

                        List<ParameterizedType> param3Generics = param3.getGenericTypes();
                        Assert.assertEquals(3, param3Generics.size());
                        Assert.assertEquals("java.lang.Double", param3Generics.get(0).getTypeName());
                        Assert.assertEquals("java.lang.String", param3Generics.get(1).getTypeName());
                        Assert.assertEquals("org.glassfish.hk2.classmodel.reflect.test.method.SampleType", param3Generics.get(2).getTypeName());

                        List<ParameterizedType> param3NestedGenericTypes = param3Generics.get(2).getGenericTypes();
                        Assert.assertEquals(3, param3NestedGenericTypes.size());
                        Assert.assertEquals("java.lang.Short", param3NestedGenericTypes.get(0).getTypeName());
                        Assert.assertEquals("java.lang.Float", param3NestedGenericTypes.get(1).getTypeName());
                        Assert.assertEquals("java.lang.Long", param3NestedGenericTypes.get(2).getTypeName());

                        Parameter param4 = mm.getParameter(3);
                        Assert.assertEquals("int", param4.getTypeName());

                        Parameter param5 = mm.getParameter(4);
                        Assert.assertEquals("java.lang.Object", param5.getTypeName());

                        // Method's return type check
                        ParameterizedType returnType = mm.getReturnType();
                        Assert.assertEquals("org.glassfish.hk2.classmodel.reflect.test.method.SampleType", returnType.getTypeName());
                        List<ParameterizedType> returnTypeGenerics = returnType.getGenericTypes();
                        Assert.assertEquals(3, returnTypeGenerics.size());
                        Assert.assertEquals("java.lang.Integer", returnTypeGenerics.get(0).getTypeName());
                        Assert.assertEquals("java.lang.Character", returnTypeGenerics.get(1).getTypeName());
                        Assert.assertEquals("java.lang.Boolean", returnTypeGenerics.get(2).getTypeName());

                        return;
                    }
                }
            }
        }
        Assert.fail("Did not find a SimpleAnnotatedMethod.setFoo annotated method with SomeAnnotation");
    }
}
