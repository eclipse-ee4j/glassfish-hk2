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

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.glassfish.hk2.api.ClassAnalyzer;
import org.glassfish.hk2.api.MultiException;

/**
 * @author jwells
 *
 */
@Singleton @Named(ConfigurablyBadClassAnalyzer.BAD_ANALYZER_NAME)
public class ConfigurablyBadClassAnalyzer implements ClassAnalyzer {
    public static final String BAD_ANALYZER_NAME = "BadAnalyzer";
    
    private boolean throwFromConstructor = false;
    private boolean throwFromMethods = false;
    private boolean throwFromFields = false;
    private boolean throwFromPostConstruct = false;
    private boolean throwFromPreDestroy = false;
    
    private boolean nullFromConstructor = false;
    private boolean nullFromMethods = false;
    private boolean nullFromFields = false;
    
    @Inject @Named(ClassAnalyzer.DEFAULT_IMPLEMENTATION_NAME)
    private ClassAnalyzer delegate;
    
    public void resetToGood() {
        throwFromConstructor = false;
        throwFromMethods = false;
        throwFromFields = false;
        throwFromPostConstruct = false;
        throwFromPreDestroy = false;
        
        nullFromConstructor = false;
        nullFromMethods = false;
        nullFromFields = false;
    }
    
    public void setThrowFromConstructor(boolean throwFromConstructor) {
        this.throwFromConstructor = throwFromConstructor;
    }

    public void setThrowFromMethods(boolean throwFromMethods) {
        this.throwFromMethods = throwFromMethods;
    }

    public void setThrowFromFields(boolean throwFromFields) {
        this.throwFromFields = throwFromFields;
    }

    public void setThrowFromPostConstruct(boolean throwFromPostConstruct) {
        this.throwFromPostConstruct = throwFromPostConstruct;
    }

    public void setThrowFromPreDestroy(boolean throwFromPreDestroy) {
        this.throwFromPreDestroy = throwFromPreDestroy;
    }

    public void setNullFromConstructor(boolean nullFromConstructor) {
        this.nullFromConstructor = nullFromConstructor;
    }

    public void setNullFromMethods(boolean nullFromMethods) {
        this.nullFromMethods = nullFromMethods;
    }

    public void setNullFromFields(boolean nullFromFields) {
        this.nullFromFields = nullFromFields;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ClassAnalyzer#getConstructor(java.lang.Class)
     */
    @Override
    public <T> Constructor<T> getConstructor(Class<T> clazz)
            throws MultiException, NoSuchMethodException {
        if (throwFromConstructor) {
            throw new AssertionError(NegativeClassAnalysisTest.C_THROW);
        }
        if (nullFromConstructor) {
            return null;
        }
        return delegate.getConstructor(clazz);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ClassAnalyzer#getInitializerMethods(java.lang.Class)
     */
    @Override
    public <T> Set<Method> getInitializerMethods(Class<T> clazz)
            throws MultiException {
        if (throwFromMethods) {
            throw new AssertionError(NegativeClassAnalysisTest.M_THROW);
        }
        if (nullFromMethods) {
            return null;
        }
        return delegate.getInitializerMethods(clazz);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ClassAnalyzer#getFields(java.lang.Class)
     */
    @Override
    public <T> Set<Field> getFields(Class<T> clazz) throws MultiException {
        if (throwFromFields) {
            throw new AssertionError(NegativeClassAnalysisTest.F_THROW);
        }
        if (nullFromFields) {
            return null;
        }
        return delegate.getFields(clazz);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ClassAnalyzer#getPostConstructMethod(java.lang.Class)
     */
    @Override
    public <T> Method getPostConstructMethod(Class<T> clazz)
            throws MultiException {
        if (throwFromPostConstruct) {
            throw new AssertionError(NegativeClassAnalysisTest.PC_THROW);
        }
        
        return delegate.getPostConstructMethod(clazz);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ClassAnalyzer#getPreDestroyMethod(java.lang.Class)
     */
    @Override
    public <T> Method getPreDestroyMethod(Class<T> clazz) throws MultiException {
        if (throwFromPreDestroy) {
            throw new AssertionError(NegativeClassAnalysisTest.PD_THROW);
        }
        
        return delegate.getPostConstructMethod(clazz);
    }

}
