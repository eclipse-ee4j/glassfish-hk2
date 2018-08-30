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

import com.sun.enterprise.module.bootstrap.StartupContext;

import java.util.List;
import java.util.Properties;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.BuilderHelper;

/**
 * Implementation of the modules registry that use a single class loader to load
 * all available classes. There is one virtual module available in the modules
 * registry and that module's class loader is the single class loader used to
 * load all artifacts.
 *
 * @author Jerome Dochez
 */
public class StaticModulesRegistry extends SingleModulesRegistry {
              
    final private StartupContext startupContext; 

    public StaticModulesRegistry(ClassLoader singleCL) {
        super(singleCL);
        startupContext = null;
    }

    public StaticModulesRegistry(ClassLoader singleCL, StartupContext startupContext) {
        super(singleCL);
        this.startupContext = startupContext;
    }

    public StaticModulesRegistry(ClassLoader singleCL, List<ManifestProxy.SeparatorMappings> mappings, StartupContext startupContext) {
        super(singleCL, mappings);
        this.startupContext = startupContext;
    }

    @Override
    public void populateConfig(ServiceLocator serviceLocator) {
        // do nothing...
    }

    @Override
    public ServiceLocator createServiceLocator(String name) throws MultiException {
        ServiceLocator serviceLocator = super.createServiceLocator(name);

        StartupContext sc = startupContext;

        if (startupContext==null) {
            sc = new StartupContext(new Properties());
        }

        DynamicConfigurationService dcs = serviceLocator.getService(DynamicConfigurationService.class);
        DynamicConfiguration config = dcs.createDynamicConfiguration();
        config.bind(BuilderHelper.createConstantDescriptor(sc));
        config.commit();
        
        return serviceLocator;
    }

}
