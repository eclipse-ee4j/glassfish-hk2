/*
 * Copyright (c) 2007, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.jvnet.hk2.annotations;

import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;

import org.glassfish.hk2.api.ClassAnalyzer;

/**
 * Annotation placed on classes that are to be automatically added
 * to an hk2 {@link org.glassfish.hk2.api.ServiceLocator}.  A service
 * marked with this annotation has the default scope of {@link javax.inject.Singleton},
 * but any other scope annotation placed on the class will override that default.
 * <p>
 * This annotation is read at build time using the hk2-inhabitant-generator
 * and information about the service is placed into a file in the
 * associated jar.  The usual way to get these services into
 * a {@link org.glassfish.hk2.api.ServiceLocator} is to use a
 * {@link org.glassfish.hk2.api.Populator} as provided by the
 * {@link org.glassfish.hk2.api.DynamicConfigurationService#getPopulator()}
 * method.  An easier way to do that is with the
 * {@link org.glassfish.hk2.utilities.ServiceLocatorUtilities#createAndPopulateServiceLocator()}
 * utility.
 *
 * @author Jerome Dochez
 * @author Kohsuke Kawaguchi
 * @see org.glassfish.hk2.api.Factory org.glassfish.hk2.api.ClassAnalyzer
 */
@Retention(RUNTIME)
@Target(TYPE)
@Documented
@InhabitantAnnotation("default")
public @interface Service {

    /**
     * Name of the service.
     *
     * <p>
     * {@link org.glassfish.hk2.api.ServiceLocator#getService(Class, String, java.lang.annotation.Annotation...)} and
     * similar methods can be used to obtain a service with a particular name.
     *
     * <p>
     * The default value "" indicates that the inhabitant has no name.
     */
    String name() default "";

    /**
     * Additional metadata that goes into the inhabitants file.
     * The value is "key={value},key={value1,value2,...},..." format.
     *
     * This information is accessible from {@link org.glassfish.hk2.api.Descriptor#getMetadata()}.
     *
     * <p>
     * While this is limited in expressiveness, metadata has a performance advantage
     * in it that it can be read without even creating a classloader for this class.
     * For example, this feature is used by the configuration module so that
     * the config file can be read without actually loading the classes. 
     */
    String metadata() default "";
    
    /**
     * The name of the {@link ClassAnalyzer} service that should be used
     * to analyze this class
     * 
     * @return The name of the {@link ClassAnalyzer} service that should
     * be used to analyze this class
     */
    String analyzer() default ClassAnalyzer.DEFAULT_IMPLEMENTATION_NAME;
}
