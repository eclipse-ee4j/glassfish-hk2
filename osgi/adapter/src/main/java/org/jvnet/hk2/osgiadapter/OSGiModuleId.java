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

package org.jvnet.hk2.osgiadapter;

import com.sun.enterprise.module.common_impl.ModuleId;
import com.sun.enterprise.module.ModuleDefinition;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

/**
 * @author Sanjeeb.Sahoo@Sun.COM
 */
/* package */ class OSGiModuleId extends ModuleId
{
    OSGiModuleId(String name, String version)
    {
        super(name, version);
    }

    OSGiModuleId(ModuleDefinition md)
    {
        super(md);
    }

    OSGiModuleId(Bundle b)
    {
        String name = b.getSymbolicName();
        // R3 bundles may not have any name.
        // So, we use location in such cases. We encounter this
        // problem when user has dropped some plain jars or R3 bundles
        // in modules dir. If you choose to use a different name,
        // please also change the code in OSGiModuleDefinition class which makes
        // similar assumption.
        if (name == null) name = b.getLocation();
                
        String version = b.getVersion().toString(); // (String) b.getHeaders().get(Constants.BUNDLE_VERSION);
        
        init(name, version);
    }


}
