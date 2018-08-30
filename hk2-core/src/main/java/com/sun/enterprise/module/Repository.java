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

import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * A Repository instance is an abstraction of a set of accessible
 * modules. Repository can be local or remote and are used to
 * procure modules implementation based on constraints like name or
 * version.
 *
 * @author Jerome Dochez
 */
public interface Repository {
    
    /**
     * Returns the repository name
     * @return repository name
     */
    public String getName();
    
    /**
     * Returns the repository location
     * @return the URI for the repository location
     */
    public URI getLocation();
    
    /**
     * Finds and returns a <code>DefaultModuleDefinition</code> instance
     * for a module given the name and version constraints.
     * @param name the requested module name
     * @param version
     *      the module version. Can be null if the caller doesn't care about the version.
     * @return a <code>DefaultModuleDefinition</code> or null if not found
     * in this repository.
     */
    public ModuleDefinition find(String name, String version);
    
    /**
     * Returns a list of all modules available in this repository 
     * @return a list of available modules
     */
    public List<ModuleDefinition> findAll();
    
    /**
     * Finds and returns a list of all the available versions of a 
     * module given its name.
     * @param name the requested module name
     */
    public List<ModuleDefinition> findAll(String name);
    
    /**
     * Initialize the repository for use. This need to be called at least
     * once before any find methods is invoked.  
     * @throws IOException if an error occur accessing the repository
     */
    public void initialize() throws IOException;
    
    /**
     * Shutdown the repository. After this call return, the find methods cannot 
     * be used until initialize() is called again.
     * @throws IOException if an error occur accessing the repository
     */
    public void shutdown() throws IOException;

    /**
     * Returns the plain jar files installed in this repository. Plain jar files
     * are not modules, they do not have the module's metadata and can only be used
     * when referenced from a module dependency list or when added to a class
     * loader directly
     *
     * @return jar files location stored in this repository.
     */
    public List<URI> getJarLocations();

    /**
     * Add a listener to changes happening to this repository. Repository can
     * change during the lifetime of an execution (files added/removed/changed)
     *
     * @param listener implementation listening to this repository changes
     * @return true if the listener was added successfully
     */
    public boolean addListener(RepositoryChangeListener listener);

    /**
     * Removes a previously registered listener
     *
     * @param listener the previously registered listener
     * @return true if the listener was successfully unregistered
     */
    public boolean removeListener(RepositoryChangeListener listener);

}

