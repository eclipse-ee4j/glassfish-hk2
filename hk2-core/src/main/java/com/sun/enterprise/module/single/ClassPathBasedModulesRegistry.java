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

package com.sun.enterprise.module.single;

import com.sun.enterprise.module.ModuleDefinition;
import com.sun.enterprise.module.ResolveError;
import com.sun.enterprise.module.common_impl.DefaultModuleDefinition;
import com.sun.enterprise.module.impl.ModulesRegistryImpl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.PopulatorPostProcessor;
import org.glassfish.hk2.api.ServiceLocator;
import com.sun.enterprise.module.HK2Module;

/**
 * Implements a modules registry based on a class-path style of module
 * description using a single class loader (capable of loading the entire
 * class-path)
 *
 * @author Jerome Dochez
 */
public class ClassPathBasedModulesRegistry extends ModulesRegistryImpl {

    final ClassLoader cLoader;
    final List<ModuleDefinition> moduleDefs = new ArrayList<ModuleDefinition>();
    final List<HK2Module> modules = new ArrayList<HK2Module>();


    public ClassPathBasedModulesRegistry(ClassLoader singleCL, String classPath) throws IOException {

        super(null);
        this.cLoader = singleCL;
        setParentClassLoader(cLoader);

        StringTokenizer st = new StringTokenizer(classPath, File.pathSeparator);
        while (st.hasMoreTokens()) {
            String classPathElement = st.nextToken();
            File f = new File(classPathElement);
            if (f.exists()) {
                ModuleDefinition md = new DefaultModuleDefinition(f);
                moduleDefs.add(md);
                add(md);
            }
        }

        // now create fake modules
        for (ModuleDefinition md : moduleDefs) {
            // they all use the same class loader since they are not really modules
            // and we don't run in a modular environment
            modules.add(new ProxyModule(this, md, cLoader));
        }
    }

    @Override
    public HK2Module find(Class clazz) {
        HK2Module m = super.find(clazz);
        // all modules can load all classes
        if (m == null)
            return modules.get(0);
        return m;
    }

    @Override
    public Collection<HK2Module> getModules(String moduleName) {
        // I could not care less about the modules names
        return getModules();
    }

    @Override
    public Collection<HK2Module> getModules() {
        ArrayList<HK2Module> list = new ArrayList<HK2Module>();
        list.addAll(modules);
        return list;
    }

    @Override
    public HK2Module makeModuleFor(String name, String version, boolean resolve) throws ResolveError {
        for (int i=0;i<moduleDefs.size();i++) {
            ModuleDefinition md = moduleDefs.get(i);
            if (md.getName().equals(name)) {
                return modules.get(i); 
            }
        }
        return null;
    }

    @Override
    protected List<ActiveDescriptor> parseInhabitants(HK2Module module, String name, ServiceLocator serviceLocator, List<PopulatorPostProcessor> postProcessors)
            throws IOException {
        return null;
    }

}
