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

package org.glassfish.hk2.classmodel.reflect.test.method;

import org.glassfish.hk2.classmodel.reflect.*;
import org.glassfish.hk2.classmodel.reflect.test.ClassModelTestsUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.lang.annotation.Repeatable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * method related tests
 */
public class MethodTest {

    @Test
    public void simpleTest() throws IOException, InterruptedException{
        Types types = ClassModelTestsUtils.getTypes();
        Type type = types.getBy(SomeAnnotation.class.getName());
        Assert.assertTrue(type instanceof AnnotationType);
        AnnotationType annotation = (AnnotationType) type;
        Collection<AnnotatedElement> aes = annotation.allAnnotatedTypes();
        // we must find our SimpleAnnotatedMethod.setFoo method
        Assert.assertNotNull(aes);
        Assert.assertTrue(aes.size()>0);
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

                        return;
                    }
                }
            }
        }
        Assert.fail("Did not find a SimpleAnnotatedMethod.setFoo annotated method with SomeAnnotation");
    }
}
