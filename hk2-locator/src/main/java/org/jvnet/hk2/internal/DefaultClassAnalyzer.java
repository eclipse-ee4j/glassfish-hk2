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

package org.jvnet.hk2.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import javax.inject.Named;
import javax.inject.Singleton;

import org.glassfish.hk2.api.ClassAnalyzer;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.Visibility;

/**
 * this is the default implementation of the ClassAnalyzer
 * 
 * @author jwells
 *
 */
@Singleton
@Named(ClassAnalyzer.DEFAULT_IMPLEMENTATION_NAME)
@Visibility(DescriptorVisibility.LOCAL)
public class DefaultClassAnalyzer implements ClassAnalyzer {
    private final ServiceLocatorImpl locator;
    
    /**
     * The DefaultClassAnalyzer is per ServiceLocatorImpl
     * 
     * @param locator The non-null locator associated with this analyzer
     */
    public DefaultClassAnalyzer(ServiceLocatorImpl locator) {
        this.locator = locator;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Constructor<T> getConstructor(Class<T> clazz)
            throws MultiException, NoSuchMethodException {
        Collector collector = new Collector();
        
        Constructor<T> retVal = (Constructor<T>)
                Utilities.findProducerConstructor(clazz, locator, collector);
        
        try {
            collector.throwIfErrors();
        }
        catch (MultiException me) {
            for (Throwable th : me.getErrors()) {
                if (th instanceof NoSuchMethodException) {
                    throw (NoSuchMethodException) th;
                }
            }
            
            throw me;
        }
        
        return retVal;
    }

    @Override
    public <T> Set<Method> getInitializerMethods(Class<T> clazz)
            throws MultiException {
        Collector collector = new Collector();
        
        Set<Method> retVal = Utilities.findInitializerMethods(clazz, locator, collector);
        
        collector.throwIfErrors();
        
        return retVal;
    }

    @Override
    public <T> Set<Field> getFields(Class<T> clazz) throws MultiException {
        Collector collector = new Collector();
        
        Set<Field> retVal = Utilities.findInitializerFields(clazz, locator, collector);
        
        collector.throwIfErrors();
        
        return retVal;
    }

    @Override
    public <T> Method getPostConstructMethod(Class<T> clazz)
            throws MultiException {
        Collector collector = new Collector();
        
        Method retVal = Utilities.findPostConstruct(clazz, locator, collector);
        
        collector.throwIfErrors();
        
        return retVal;
    }

    @Override
    public <T> Method getPreDestroyMethod(Class<T> clazz) throws MultiException {
        Collector collector = new Collector();
        
        Method retVal = Utilities.findPreDestroy(clazz, locator, collector);
        
        collector.throwIfErrors();
        
        return retVal;
    }

    
}
