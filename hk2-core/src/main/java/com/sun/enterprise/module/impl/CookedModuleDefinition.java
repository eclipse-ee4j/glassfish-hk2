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

import com.sun.enterprise.module.ModuleDependency;
import com.sun.enterprise.module.common_impl.DefaultModuleDefinition;
import com.sun.enterprise.module.common_impl.DefaultModuleDefinition;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.net.URI;

/**
 *
 * @author dochez
 */
public class CookedModuleDefinition extends DefaultModuleDefinition {
    
    List<String> publicPkgs = new ArrayList<String>();
    List<ModuleDependency> dependencies = new ArrayList<ModuleDependency>();
    Attributes attr;

    /** Creates a new instance of CookedModuleDefinitionefinition */
    public CookedModuleDefinition(File file, Attributes attr) throws IOException {
        super(file, attr);
    }
           
    public void addPublicInterface(String exported) {
        publicPkgs.add(exported);
    }
    
    public String[] getPublicInterfaces() {
        return publicPkgs.toArray(new String[publicPkgs.size()]);
    }
    
    public void addDependency(ModuleDependency dependent) {
        dependencies.add(dependent);
    }
    
    public ModuleDependency[] getDependencies() {
        return dependencies.toArray(new ModuleDependency[dependencies.size()]);
    }

    public void add(List<URI> extraClassPath) {
        classPath.addAll(extraClassPath);
    }
    
}
