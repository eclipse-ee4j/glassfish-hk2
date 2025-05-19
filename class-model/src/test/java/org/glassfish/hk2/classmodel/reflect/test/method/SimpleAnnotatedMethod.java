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

import java.util.List;

/**
 * a simple class with annotated method.
 */
public class SimpleAnnotatedMethod {

    @SomeAnnotation(
            aLong = 10,
            aClass = Void.class,
            aClassArr = {Integer.class, String.class},
            aEnum = SomeEnum.ENUM1,
            childAnnotation = @ChildAnnotation("child_value")
    )
    @Color(name = "red")
    @Color(name = "green")
    @GradientColor({
        @Color(name = "white"),
        @Color(name = "black")
    })
    @GradientColor({
        @Color(name = "yellow"),
        @Color(name = "orange")
    })
    public SampleType<Integer, Character, Boolean> setFoo(
            @Color(name = "brown") String color,
            List<String> input,
            SampleType<Double, String, SampleType<Short, Float, Long>> sampleType,
            int count,
            Object value) throws IllegalArgumentException {
        return null;
    }
}

class SampleType<P, Q, R> {

}
