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

/**
 * A ModuleDependency instance holds all information necessary to identify 
 * a dependency between modules. Modules can declare their dependency on a 
 * separate module using the name, the version and whether they accept the 
 * sub module implementation to be shared. They can also specify whether or
 * not they want to re-export the sub module public interfaces. Re-exporting
 * means that the sub-module's public interfaces will also be published as 
 * a public interface of the enclosing module.
 *
 * @author Jerome Dochez
 */
public class ModuleDependency {
    
    final private String name;
    final private String version;
    final private boolean shared;
    final private boolean reexport;
    
    /**
     * Create a new instance of ModuleDependency, where the sub module is 
     * idenfied by its name and version. The sub module implementation should 
     * be shared among users of that module
     * @param name the module name
     * @param version the module version
     */
    public ModuleDependency(String name, String version) {
        this.name = name;
        this.version = version;
        this.shared = true;
        this.reexport = false;
    }

    /**
     * Create a new instance of ModuleDependency, where the sub module is 
     * idenfied by its name and version and wheter the containing module 
     * requires a private copy or not
     * @param name the module name
     * @param version the module version
     * @param shared true if the containing module accept a shared copy 
     */
    public ModuleDependency(String name, String version, 
            boolean shared, boolean reexport) {
        this.name = name;
        this.version = version;
        this.shared = shared;
        this.reexport = reexport;
    }
    
    
    /**
     * Returns the module name
     * @return the module name 
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns the module version
     * @return the module version
     */
    public String getVersion() {
        return version;
    }
    
    /**
     * Returns true if the containing module accept a shared implementation
     * of the sub module
     * @return true if shared implementation is acceptable
     */
    public boolean isShared() {
        return shared;
    }
    
    /**
     * Returns true if the containing module is reexporting the public 
     * interfaces of the sub module
     * @return true if reexporting the sub module public interface
     */
    public boolean isReexporting() {
        return reexport;
    }
    
    /**
     * Returns a string representation
     * @return a printable string about myself
     */
    public String toString() {
       return "Module Dependency : " + getName() + ":" + getVersion();
    }
}
