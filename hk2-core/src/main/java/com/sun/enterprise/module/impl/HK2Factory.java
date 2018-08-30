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

import com.sun.enterprise.module.common_impl.AbstractFactory;
import com.sun.enterprise.module.common_impl.LogHelper;
import com.sun.enterprise.module.common_impl.ModuleId;
import com.sun.enterprise.module.ModulesRegistry;
import com.sun.enterprise.module.ModuleDefinition;

/**
 * @author Sanjeeb.Sahoo@Sun.COM
 */
public class HK2Factory extends AbstractFactory {

    public synchronized static void initialize() {
        if (Instance != null) {
            LogHelper.getDefaultLogger().fine("Singleton already initialized as " + getInstance());
        }
        Instance = new HK2Factory();
    }

    public ModulesRegistry createModulesRegistry() {
        return new ModulesRegistryImpl(null);
    }

    public ModuleId createModuleId(String name, String version) {
        // In HK2, we don't yet use version to compare modules.
        return new ModuleId(name);
    }

    public ModuleId createModuleId(ModuleDefinition md) {
        // In HK2, we don't yet use version to compare modules.
        return new ModuleId(md.getName());
    }
}
