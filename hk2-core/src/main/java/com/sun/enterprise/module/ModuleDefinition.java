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

package com.sun.enterprise.module;

import java.net.URI;
import java.util.jar.Manifest;

/**
 * A module definition contains all information about a module
 * existence, its dependencies and its exported interfaces.
 *
 * This module meta information can be obtained from different
 * sources and format. For instance OSGi modules use the manifest
 * file and so is the glassfish application server. Others can
 * use api or xml file.
 *
 * @author Jerome Dochez
 */
public interface ModuleDefinition {

    /**
     *  Returns the module name, usually this is the same name as
     *  the jar file name containing the module's implementation.

     * @return module name
     */
    String getName();

    /**
     *  Returns a list of public interfaces for this module.
     *  Public interface can be packages, interfaces, or classes
     *
     * @return a array of public interfaces
     */
    String[] getPublicInterfaces();

    /**
     * Returns the list of this module's dependencies. Each dependency
     * must be satisfied at run time for this module to function
     * properly.
     *
     * @return list of dependencies
     */
    ModuleDependency[] getDependencies();

    /**
     * A Module is implemented by one to many jar files. This method returns
     * the list of jar files implementing the module
     *
     * @return the module's list of implementation jars
     */
    URI[] getLocations();

    /**
     * Returns the version of this module's implementation
     *
     * @return a version number
     */
    String getVersion();

    /**
     * Returns the import policy class name. Although the implementation of
     * this policy does not necessary have to implement the ImportPolicy, but
     * could use another interface, it is the responsibility of the associated
     * Repository to invoke that interface when the module is started.
     *
     * @return
     *      Fully qualified class name that's assignable to {@link ImportPolicy},
     *      or null if no import policy exists.
     */
    String getImportPolicyClassName();

    /**
     * Returns the lifecycle policy class name. Although the implementation of
     * this policy does not necessary have to implement the LifecyclePolicy, but
     * could use another interface, it is the responsibility of the associated
     * Repository to invoke that interface when the module is started.
     *
     * @return
     *      Fully qualified class name that's assignable to {@link LifecyclePolicy},
     *      or null if no import policy exists.
     */
    String getLifecyclePolicyClassName();

    /**
     * Returns the manifest file for the main module jar file
     * 
     * @return the manifest file
     */
    Manifest getManifest();

    /**
     * Gets the metadata that describes various components and services in this module.
     *
     * @return
     *      Always non-null.
     */
    ModuleMetadata getMetadata();
}
