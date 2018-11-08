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

package com.sun.enterprise.module.single;

import com.sun.enterprise.module.*;

import java.util.List;
import java.util.ArrayList;
import java.io.PrintStream;

/**
 * Creates a HK2Module implementation based on a single class loader
 *
 * @author Jerome Dochez
 */
public class ProxyModule implements HK2Module {

    final ClassLoader classLoader;
    final ModuleDefinition moduleDef;
    final ModulesRegistry modulesRegistry;

    public ProxyModule(ModulesRegistry registry, ModuleDefinition moduleDef, ClassLoader cl) {
        this.classLoader = cl;
        this.moduleDef = moduleDef;
        this.modulesRegistry = registry;
    }

    public ModuleDefinition getModuleDefinition() {
        return moduleDef;
    }

    public String getName() {
        return moduleDef.getName();
    }

    public ModulesRegistry getRegistry() {
        return modulesRegistry;
    }

    public ModuleState getState() {
        return ModuleState.READY;
    }

    public void resolve() throws ResolveError {

    }

    public void start() throws ResolveError {

    }

    public boolean stop() {
        return false;
    }

    public void detach() {
    }

    public void refresh() {
    }

    public ModuleMetadata getMetadata() {
        return moduleDef.getMetadata();
    }

    public void addListener(ModuleChangeListener listener) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void removeListener(ModuleChangeListener listener) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public List<HK2Module> getImports() {
        return new ArrayList<HK2Module>();
    }

    public void addImport(HK2Module module) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public HK2Module addImport(ModuleDependency dependency) {
        throw new UnsupportedOperationException("Inports cannot be added dynamically to static modules");
    }

    public boolean isShared() {
        return false;
    }

    public boolean isSticky() {
        return true;
    }

    public void setSticky(boolean sticky) {
        if (!sticky)
            throw new UnsupportedOperationException("Stickyness cannot be changed for static modules");
    }

    public <T> Iterable<Class<? extends T>> getProvidersClass(Class<T> serviceClass) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Iterable<Class> getProvidersClass(String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean hasProvider(Class serviceClass) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void dumpState(PrintStream writer) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void uninstall() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
