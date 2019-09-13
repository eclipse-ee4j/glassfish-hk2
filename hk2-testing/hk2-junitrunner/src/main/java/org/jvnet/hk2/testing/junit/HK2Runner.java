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

package org.jvnet.hk2.testing.junit;

import java.util.List;
import java.util.Set;

import org.glassfish.hk2.api.ServiceLocator;
import org.junit.Before;
import org.jvnet.hk2.testing.junit.annotations.Classes;
import org.jvnet.hk2.testing.junit.annotations.Excludes;
import org.jvnet.hk2.testing.junit.annotations.Packages;
import org.jvnet.hk2.testing.junit.internal.TestServiceLocator;

/**
 * This class should be extended by test classes in order to get an automatically
 * filled in ServiceLocator.  By default the testLocator will inspect the package
 * of the test to find any classes annotated with &#64;Service.  The locator will
 * also be able to do second-chance advertisement of services that were injected.
 * The default ServiceLocator will also have an error handler that causes any classloading
 * failure to get rethrown up to the lookup call, since this can sometimes cause
 * confusion.
 * <p>
 * The behavior of HK2Runner can be customized by annotating the class extending
 * HK2Runner with {@link org.jvnet.hk2.testing.junit.annotations.Packages},
 * {@link org.jvnet.hk2.testing.junit.annotations.Classes}, {@link org.jvnet.hk2.testing.junit.annotations.Excludes}
 * or {@link org.jvnet.hk2.testing.junit.annotations.InhabitantFiles}.
 * <p>
 * {@link org.jvnet.hk2.testing.junit.annotations.Packages} gives the names of packages
 * that will automatically be scanned for classes that should be added to testLocator
 * as services.  {@link org.jvnet.hk2.testing.junit.annotations.Classes} gives an
 * explicit set of classes that should be added to testLocator as services.
 * {@link org.jvnet.hk2.testing.junit.annotations.Excludes} gives a set of services
 * that should not be automatically added to the testLocator.
 * {@link org.jvnet.hk2.testing.junit.annotations.InhabitantFiles} gives a set of
 * inhabitant files to load in the classpath of the test.
 * <p>
 * This behavior can be customized by overriding the before method of the test and calling one
 * of the {@link #initialize(String, List, List)} methods.  The annotations listed above
 * are overridden by any values passed to the initialize methods
 * 
 * @author jwells
 */
public class HK2Runner {
    private TestServiceLocator testServiceLocator;
    
    /**
     * Test classes can use this service locator as their private test locator
     */
    protected ServiceLocator testLocator;

    public HK2Runner() {
        testServiceLocator = new TestServiceLocator(this);
    }
    
    /**
     * This will generate the default testLocator for this test
     * class, which will search the package of the test itself for
     * classes annotated with &#64;Service.
     */
    @Before
    public void before() {
        testServiceLocator.initializeOnBefore();
        testLocator = testServiceLocator.getServiceLocator();
    }
    
    /**
     * This method initializes the service locator with services.  The name
     * of the locator will be the fully qualified name of the class.  All
     * other values will either be empty or will come from the annotations
     * {@link Packages}, {@link Classes}, {@link Excludes}, @{link InhabitantFiles}
     */
    public void initialize() {
        testServiceLocator.initialize();
        testLocator = testServiceLocator.getServiceLocator();
    }

    /**
     * This method initializes the service locator with services from the given list
     * of packages (in "." format) and with the set of classes given.
     * 
     * @param name The name of the service locator to create.  If there is already a
     * service locator of this name then the remaining fields will be ignored and the existing
     * locator with this name will be returned.  May not be null
     * @param packages The list of packages (in "." format, i.e. "com.acme.test.services") that
     * we should hunt through the classpath for in order to find services.  If null this is considered
     * to be the empty set
     * @param clazzes A set of classes that should be analyzed as services, whether they declare
     * &#64;Service or not.  If null this is considered to be the empty set
     */
    protected void initialize(String name, List<String> packages, List<Class<?>> clazzes) {
        testServiceLocator.initialize(name, packages, clazzes);
        testLocator = testServiceLocator.getServiceLocator();
    }
    
    /**
     * This method initializes the service locator with services from the given list
     * of packages (in "." format) and with the set of classes given.
     * 
     * @param name The name of the service locator to create.  If there is already a
     * service locator of this name then the remaining fields will be ignored and the existing
     * locator with this name will be returned.  May not be null
     * @param packages The list of packages (in "." format, i.e. "com.acme.test.services") that
     * we should hunt through the classpath for in order to find services.  If null this is considered
     * to be the empty set
     * @param clazzes A set of classes that should be analyzed as services, whether they declare
     * &#64;Service or not.  If null this is considered to be the empty set
     * @param excludes A set of implementations that should be excluded from being added.  This list is
     * NOT checked against the clazzes list (the explicit include wins), but instead against the set of
     * things coming from packages or from the hk2-locator/default file
     */
    protected void initialize(String name, List<String> packages, List<Class<?>> clazzes, Set<String> excludes) {
        testServiceLocator.initialize(name, packages, clazzes, excludes);
        testLocator = testServiceLocator.getServiceLocator();
    }
    
    /**
     * This method initializes the service locator with services from the given list
     * of packages (in "." format) and with the set of classes given.
     * 
     * @param name The name of the service locator to create.  If there is already a
     * service locator of this name then the remaining fields will be ignored and the existing
     * locator with this name will be returned.  May not be null
     * @param packages The list of packages (in "." format, i.e. "com.acme.test.services") that
     * we should hunt through the classpath for in order to find services.  If null this is considered
     * to be the empty set
     * @param clazzes A set of classes that should be analyzed as services, whether they declare
     * &#64;Service or not.  If null this is considered to be the empty set
     * @param excludes A set of implementations that should be excluded from being added.  This list is
     * NOT checked against the clazzes list (the explicit include wins), but instead against the set of
     * things coming from packages or from the hk2-locator/default file
     * @param locatorFiles A set of locator inhabitant files to search the classpath for to load.  If
     * this value is null then only META-INF/hk2-locator/default files on the classpath will be searched.
     * If this value is an empty set then no inhabitant files will be loaded.  If this value contains
     * values those will be searched as resources from the jars in the classpath to load the registry with
     */
    protected void initialize(String name, List<String> packages, List<Class<?>> clazzes, Set<String> excludes, Set<String> locatorFiles) {
        testServiceLocator.initialize(name, packages, clazzes, excludes, locatorFiles);
        testLocator = testServiceLocator.getServiceLocator();
    }
    
    protected void setVerbosity(boolean verbose) {
        testServiceLocator.setVerbosity(verbose);
    }
}
