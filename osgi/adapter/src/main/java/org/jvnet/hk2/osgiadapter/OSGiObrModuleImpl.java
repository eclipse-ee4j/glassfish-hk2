/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.jvnet.hk2.osgiadapter;

import com.sun.enterprise.module.HK2Module;
import com.sun.enterprise.module.ModuleDefinition;
import com.sun.enterprise.module.ModuleState;
import com.sun.enterprise.module.ResolveError;
import com.sun.enterprise.module.bootstrap.BootException;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.PopulatorPostProcessor;
import org.glassfish.hk2.api.ServiceLocator;
import org.osgi.framework.Bundle;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

/**
 * @author Sanjeeb.Sahoo@Sun.COM
 */
public class OSGiObrModuleImpl extends OSGiModuleImpl {

    public OSGiObrModuleImpl(OSGiObrModulesRegistryImpl registry, File file) throws IOException {
        this(registry, new OSGiModuleDefinition(file));
    }

    public OSGiObrModuleImpl(OSGiObrModulesRegistryImpl registry, ModuleDefinition moduleDef) {
        this(registry, null, moduleDef);
    }

    public OSGiObrModuleImpl(OSGiObrModulesRegistryImpl registry, Bundle bundle, ModuleDefinition moduleDef) {
        super(registry, bundle, moduleDef);
    }

    private synchronized boolean isUninitialized() {
        return getBundle() == null;
    }

    private synchronized void init() {
        if (isUninitialized()) {
            final ModuleDefinition moduleDefinition = getModuleDefinition();
            Bundle bundle = getRegistry().getObrHandler().deploy(moduleDefinition.getName(), moduleDefinition.getVersion());
            if (bundle != null) {
                setBundle(bundle);
            } else {
                throw new RuntimeException("Unable to install module [ "
                        + this
                        + "] due to unsatisfied dependencies. See previous log messages.");
            }
        }
    }

    @Override
    public OSGiObrModulesRegistryImpl getRegistry() {
        return (OSGiObrModulesRegistryImpl) super.getRegistry();
    }

    @Override
    public ModuleState getState() {
        if (isUninitialized()) {
            return ModuleState.NEW;
        }
        return super.getState();
    }

    @Override
    public void resolve() throws ResolveError {
        init();
        super.resolve();
    }

    @Override
    public void start() throws ResolveError {
        init();
        super.start();
    }

    @Override
    public boolean stop() {
        if (isUninitialized()) {
            return false;
        }
        return super.stop();
    }

    @Override
    public void detach() {
        if (isUninitialized()) {
            return;
        }
        super.detach();
    }

    @Override
    public void uninstall() {
        if (isUninitialized()) {
            return;
        }
        super.uninstall();
    }

    @Override
    public void refresh() {
        if (isUninitialized()) {
            return;
        }
        super.refresh();
    }

    @Override
    public void dumpState(PrintStream writer) {
        writer.print(toString());
    }

    @Override
    public ClassLoader getClassLoader() {
        init();
        return super.getClassLoader();
    }

    @Override
    public List<HK2Module> getImports() {
        if (isUninitialized()) {
            return Collections.emptyList();
        }
        return super.getImports();
    }

    @Override
    List<ActiveDescriptor> parseInhabitants(String name, ServiceLocator serviceLocator, List<PopulatorPostProcessor> populatorPostProcessors) throws IOException, BootException {
        init();
        return super.parseInhabitants(name, serviceLocator, populatorPostProcessors);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("OSGiObrModuleImpl::");
        if (isUninitialized()) {
            sb.append("Name: [" + getName() + "], State: [" + getState() + "]");
            return sb.toString();
        }
        return sb.append(super.toString()).toString();
    }
}
