/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.locator.negative.classanalysis;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import javax.inject.Named;

import org.glassfish.hk2.api.ClassAnalyzer;
import org.glassfish.hk2.api.MultiException;

/**
 * This analyzer will be its own analyzer!
 * 
 * @author jwells
 *
 */
@Named(NegativeClassAnalysisTest.SELF_ANALYZER)
public class SelfAnalyzer implements ClassAnalyzer {

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ClassAnalyzer#getConstructor(java.lang.Class)
     */
    @Override
    public <T> Constructor<T> getConstructor(Class<T> clazz)
            throws MultiException, NoSuchMethodException {
        throw new AssertionError("not called");
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ClassAnalyzer#getInitializerMethods(java.lang.Class)
     */
    @Override
    public <T> Set<Method> getInitializerMethods(Class<T> clazz)
            throws MultiException {
        throw new AssertionError("not called");
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ClassAnalyzer#getFields(java.lang.Class)
     */
    @Override
    public <T> Set<Field> getFields(Class<T> clazz) throws MultiException {
        throw new AssertionError("not called");
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ClassAnalyzer#getPostConstructMethod(java.lang.Class)
     */
    @Override
    public <T> Method getPostConstructMethod(Class<T> clazz)
            throws MultiException {
        throw new AssertionError("not called");
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ClassAnalyzer#getPreDestroyMethod(java.lang.Class)
     */
    @Override
    public <T> Method getPreDestroyMethod(Class<T> clazz) throws MultiException {
        throw new AssertionError("not called");
    }

}
