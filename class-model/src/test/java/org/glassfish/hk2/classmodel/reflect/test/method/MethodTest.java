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
import java.util.Collection;
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
                      
                        Assert.assertEquals(mm.getAnnotations().toString(), 1, mm.getAnnotations().size());
                        AnnotationModel ann = (AnnotationModel) mm.getAnnotations().iterator().next();
                        Assert.assertEquals("values", 3, ann.getValues().size());
                        Assert.assertEquals("aLong value", 10L, ann.getValues().get("aLong"));
                        Assert.assertEquals("aClass value", "java.lang.Void", ann.getValues().get("aClass"));
//                        Assert.assertEquals("aClassArr value", "java.lang.Void", ann.getValues().get("aClassArr"));
                        Assert.assertEquals("default values", 3, ann.getType().getDefaultValues().size());
                        Assert.assertEquals("java.lang.Void", ann.getType().getDefaultValues().get("environment"));
                        return;
                    }
                }
            }
        }
        Assert.fail("Did not find a SimpleAnnotatedMethod.setFoo annotated method with SomeAnnotation");
    }
}
