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

package com.sun.enterprise.module.common_impl;

import com.sun.enterprise.module.ModuleDefinition;
import com.sun.enterprise.module.ModulesRegistry;
import com.sun.enterprise.module.HK2Module;

/**
 * @author Sanjeeb.Sahoo@Sun.COM
 */
public abstract class AbstractFactory {

    // Initialized by appropriate subclass depending on runtime
    protected static AbstractFactory Instance;

    public abstract ModulesRegistry createModulesRegistry();

    public abstract ModuleId createModuleId(String name, String version);

    public abstract ModuleId createModuleId(ModuleDefinition md);

    public static AbstractFactory getInstance() {
        assert(Instance != null);
        return Instance;
    }

}
