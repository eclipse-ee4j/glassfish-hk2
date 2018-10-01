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

package org.glassfish.hk2.api;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import org.jvnet.hk2.annotations.Contract;

/**
 * When HK2 automatically analyzes a class to find the constructor, fields,
 * initializer methods and postConstruct and preDestroy methods it uses this
 * service to analyze the class.  This analyzer is only used for descriptors
 * that are not pre-reified and which are not provided by factories.
 * <p>
 * HK2 will provide a default implementation of this service (with the name
 * &quot;default&quot;).  However, individual descriptors may choose a different class
 * analyzer should they so choose.  All user supplied implementations of this
 * service must have a name.  Implementations of this service must not be ClassAnalyzers
 * for themselves.
 * <p>
 * The method {@link ServiceLocator#setDefaultClassAnalyzerName(String)} can be used
 * to set the global ClassAnalyzer name that will be the name of the ClassAnalyzer used
 * when the method {@link Descriptor#getClassAnalysisName()} returns null
 * <p>
 * Implementations of ClassAnalyzer will be instantiated as soon as
 * they are added to HK2 in order to avoid deadlocks and circular references.
 * Therefore it is recommended that implementations of ClassAnalyzer
 * make liberal use of {@link javax.inject.Provider} or {@link IterableProvider}
 * when injecting dependent services so that these services are not instantiated
 * when the ClassAnalyzer is created
 * 
 * @author jwells
 *
 */
@Contract
public interface ClassAnalyzer {
    /** The name of the default ClassAnalyzer service */
    public final static String DEFAULT_IMPLEMENTATION_NAME = "default";
    
    /**
     * Will return the constructor that it to be used when constructing this
     * service
     * <p>
     * The default implementation will use the zero-arg constructor if no single
     * constructor with Inject is found.  Also will return any constructor
     * that is covered by an {@link InjectionResolver} and the
     * {@link InjectionResolver#isConstructorParameterIndicator()} is
     * set to true
     * <p>
     * 
     * @param clazz the non-null class to analyze
     * @return The non-null constructor to use for creating this service
     * @throws MultiException on an error when analyzing the class
     * @throws NoSuchMethodException if there was no available constructor
     */
    public <T> Constructor<T> getConstructor(Class<T> clazz) throws MultiException,
        NoSuchMethodException;
    
    /**
     * Will return the set of initializer method to be used when initializing
     * this service
     * <p>
     * The default implementation will return all methods marked with Inject
     * or that have a parameter that is covered by an {@link InjectionResolver}
     * and the {@link InjectionResolver#isMethodParameterIndicator()} is set 
     * to true.  Also, any method that has a parameter marked with
     * {@link org.glassfish.hk2.api.messaging.SubscribeTo} will NOT be returned,
     * as these methods are instead meant to be called when an event is fired
     * 
     * @param clazz the non-null class to analyze
     * @return A non-null but possibly empty set of initialization methods
     * @throws MultiException on an error when analyzing the class
     */
    public <T> Set<Method> getInitializerMethods(Class<T> clazz) throws MultiException;

    /**
     * Will return the set of initializer fields to be used when initializing
     * this service
     * <p>
     * The default implementation will return all fields marked with Inject
     * or that have a parameter that is covered by an {@link InjectionResolver}
     * 
     * @param clazz the non-null class to analyze
     * @return A non-null but possibly empty set of initialization fields
     * @throws MultiException on an error when analyzing the class
     */
    public <T> Set<Field> getFields(Class<T> clazz) throws MultiException;
    
    /**
     * Will return the postConstruct method of the class
     * <p>
     * The default implementation will return the {@link PostConstruct#postConstruct()}
     * method or the method annotated with PostConstruct
     * 
     * @param clazz the non-null class to analyze
     * @return A possibly null method representing the postConstruct method to call
     * @throws MultiException on an error when analyzing the class
     */
    public <T> Method getPostConstructMethod(Class<T> clazz) throws MultiException;
    
    /**
     * Will return the preDestroy method of the class
     * <p>
     * The default implementation will return the {@link PreDestroy#preDestroy()}
     * method or the method annotated with PreDestroy
     * 
     * @param clazz the non-null class to analyze
     * @return A possibly null method representing the preDestroy method to call
     * @throws MultiException on an error when analyzing the class
     */
    public <T> Method getPreDestroyMethod(Class<T> clazz) throws MultiException;
}
