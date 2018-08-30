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

package com.sun.enterprise.module.impl;

import com.sun.enterprise.module.ModuleDefinition;
import com.sun.enterprise.module.Repository;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dochez
 */
public abstract class CookedLibRepository implements Repository {
    
    protected final File rootLocation;
    protected String[] fileNames = {} ;
    
    /** Creates a new instance of CookedLibRepository */
    public CookedLibRepository(String installRoot) {
        rootLocation = new File(installRoot);        
    }
    
    
    /**
     * Returns the repository name
     * @return repository name
     */
    public String getName() {
        return "cooked lib";
    }
    
    /**
     * Returns the repository location
     * @return the URL for the repository location
     */
    public URI getLocation() {
        return rootLocation.toURI();

    }
    
    /**
     * Finds and returns a <code>DefaultModuleDefinition</code> instance
     * for a module given the name and version constraints.
     * @param name the requested module name
     * @param version the requestion module version
     * @return a <code>DefaultModuleDefinition</code> or null if not found
     * in this repository.
     */
    public ModuleDefinition find(String name, String version) {
        for (ModuleDefinition moduleDef : findAll()) {
            // so far we ignore the module version...
            if (moduleDef.getName().equals(name)) {
                return moduleDef;
            }
        }
        return null;
    }
    
    /**
     * Returns a list of all modules available in this repository 
     * @return a list of available modules
     */
    public abstract List<ModuleDefinition> findAll();
    
    /**
     * Finds and returns a list of all the available versions of a 
     * module given its name.
     * @param name the requested module name
     */
    public List<ModuleDefinition> findAll(String name) {
        List<ModuleDefinition> matchingDefs = new ArrayList<ModuleDefinition>();
        for (ModuleDefinition moduleDef : findAll()) {
            if (moduleDef.getName().equals(name)) {
                matchingDefs.add(moduleDef);
            }
        }
        return matchingDefs;
    }
    
    /**
     * Initialize the repository for use. This need to be called at least
     * once before any find methods is invoked.  
     * @throws IOException if an error occur accessing the repository
     */
    public abstract void initialize() throws IOException;

    /**
     * Adds a new module
     */
    public void add(ModuleDefinition definition) throws IOException {
        throw new UnsupportedOperationException("add not suppored for cooked lib");
    }
    
    /**
     * Shutdown the repository. After this call return, the find methods cannot 
     * be used until initialize() is called again.
     * @throws IOException if an error occur accessing the repository
     */
    public void shutdown() throws IOException {
        
    }
    
    public String toString() {
        StringBuffer s = new StringBuffer();
        for (ModuleDefinition moduleDef : findAll()) {
            s.append(moduleDef.getName()).append(":");
        }
        return s.toString();
    }

}
