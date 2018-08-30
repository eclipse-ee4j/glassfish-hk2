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

import static org.jvnet.hk2.osgiadapter.Logger.logger;
import com.sun.enterprise.module.common_impl.AbstractFactory;
import com.sun.enterprise.module.common_impl.ModuleId;
import com.sun.enterprise.module.ModuleDefinition;
import org.osgi.framework.BundleContext;

import java.util.logging.Level;

/**
 * @author Sanjeeb.Sahoo@Sun.COM
 */
public class OSGiFactoryImpl extends AbstractFactory {

    private BundleContext ctx;

    public static synchronized void initialize(BundleContext ctx) {
        if (Instance != null) {
            // TODO : this is somehow invoked twice during gf startup, we need to investigate.
            logger.logp(Level.FINE, "OSGiFactoryImpl", "initialize",
                    "Singleton already initialized as {0}", getInstance());
        }
        Instance = new OSGiFactoryImpl(ctx);
    }

    private OSGiFactoryImpl(BundleContext ctx) {
        this.ctx = ctx;
    }

    public AbstractOSGiModulesRegistryImpl createModulesRegistry() {
        String val = ctx.getProperty(Constants.OBR_ENABLED);
        return (val != null && Boolean.valueOf(val)) ? new OSGiObrModulesRegistryImpl(ctx) : new OSGiModulesRegistryImpl(ctx);
    }

    public ModuleId createModuleId(String name, String version)
    {
        return new OSGiModuleId(name, version);
    }

    public ModuleId createModuleId(ModuleDefinition md)
    {
        return new OSGiModuleId(md);
    }
}
