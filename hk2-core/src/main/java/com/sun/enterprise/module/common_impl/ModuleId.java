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

/**
 * Class representing the primary Key for a {@link com.sun.enterprise.module.Module}.
 * A module is identified by its name and version. This class
 * encapsulates both and implements hashCode and equals method
 * so that it can be used in Sets and Maps.
 *
 * @author Sahoo@Sun.COM
 */
public class ModuleId
{
    protected String name;
    protected String version;

    protected ModuleId() {}

    public ModuleId(String name)
    {
        this.name = name;
    }

    public ModuleId(String name, String version)
    {
        init(name, version);
    }

    public ModuleId(ModuleDefinition md)
    {
        init(md.getName(), md.getVersion());
    }

    protected void init(String name, String version)
    {
        this.name = name;
        this.version = version;
    }

    @Override
    public int hashCode()
    {
        return (name + version).hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean result = false;
        if (obj instanceof ModuleId) {
            ModuleId other = ModuleId.class.cast(obj);
            result = (name == other.name);
            if (!result && (name!=null)) {
                result = name.equals(other.name);
            }
            if (result) {
                result = (version == other.version);
                if (!result && (version!=null)) {
                    result = version.equals(other.version);
                }
            }
        }
        return result;
    }

    @Override
    public String toString()
    {
        return name + ":" + version;
    }
}
