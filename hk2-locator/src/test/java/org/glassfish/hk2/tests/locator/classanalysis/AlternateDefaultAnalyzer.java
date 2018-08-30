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

package org.glassfish.hk2.tests.locator.classanalysis;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.glassfish.hk2.api.ClassAnalyzer;
import org.glassfish.hk2.api.MultiException;
import org.junit.Assert;
import org.jvnet.hk2.annotations.Service;

/**
 * @author jwells
 *
 */
@Service(analyzer=ClassAnalyzer.DEFAULT_IMPLEMENTATION_NAME)
@Singleton
@Named(ClassAnalysisTest.ALTERNATE_DEFAULT_ANALYZER)
public class AlternateDefaultAnalyzer implements ClassAnalyzer {
    @Inject @Named(ClassAnalyzer.DEFAULT_IMPLEMENTATION_NAME)
    private ClassAnalyzer dAnalyzer;
    
    private boolean getConstructorCalled = false;
    private boolean getMethodsCalled = false;
    private boolean getFieldsCalled = false;
    private boolean getPostCalled = false;
    private boolean getPreCalled = false;
    
    /* package */ void reset() {
        getConstructorCalled = false;
        getMethodsCalled = false;
        getFieldsCalled = false;
        getPostCalled = false;
        getPreCalled = false;
    }
    
    /* package */ void check() {
        Assert.assertTrue(getConstructorCalled);
        Assert.assertTrue(getMethodsCalled);
        Assert.assertTrue(getFieldsCalled);
        Assert.assertTrue(getPostCalled);
        Assert.assertTrue(getPreCalled);
    }
    
    /* package */ void unused() {
        Assert.assertFalse(getConstructorCalled);
        Assert.assertFalse(getMethodsCalled);
        Assert.assertFalse(getFieldsCalled);
        Assert.assertFalse(getPostCalled);
        Assert.assertFalse(getPreCalled);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ClassAnalyzer#getConstructor(java.lang.Class)
     */
    @Override
    public <T> Constructor<T> getConstructor(Class<T> clazz)
            throws MultiException, NoSuchMethodException {
        getConstructorCalled = true;
        return dAnalyzer.getConstructor(clazz);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ClassAnalyzer#getInitializerMethods(java.lang.Class)
     */
    @Override
    public <T> Set<Method> getInitializerMethods(Class<T> clazz)
            throws MultiException {
        getMethodsCalled = true;
        return dAnalyzer.getInitializerMethods(clazz);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ClassAnalyzer#getFields(java.lang.Class)
     */
    @Override
    public <T> Set<Field> getFields(Class<T> clazz) throws MultiException {
        getFieldsCalled = true;
        return dAnalyzer.getFields(clazz);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ClassAnalyzer#getPostConstructMethod(java.lang.Class)
     */
    @Override
    public <T> Method getPostConstructMethod(Class<T> clazz)
            throws MultiException {
        getPostCalled = true;
        return dAnalyzer.getPostConstructMethod(clazz);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ClassAnalyzer#getPreDestroyMethod(java.lang.Class)
     */
    @Override
    public <T> Method getPreDestroyMethod(Class<T> clazz) throws MultiException {
        getPreCalled = true;
        return dAnalyzer.getPreDestroyMethod(clazz);
    }

}
