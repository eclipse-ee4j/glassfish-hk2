/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.jvnet.hk2.external.generator;

import javax.inject.Singleton;

import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.extension.ServiceLocatorGenerator;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.jvnet.hk2.internal.DefaultClassAnalyzer;
import org.jvnet.hk2.internal.DynamicConfigurationImpl;
import org.jvnet.hk2.internal.DynamicConfigurationServiceImpl;
import org.jvnet.hk2.internal.InstantiationServiceImpl;
import org.jvnet.hk2.internal.ServiceLocatorImpl;
import org.jvnet.hk2.internal.ServiceLocatorRuntimeImpl;
import org.jvnet.hk2.internal.Utilities;

/**
 * @author jwells
 *
 */
public class ServiceLocatorGeneratorImpl implements ServiceLocatorGenerator {
    private ServiceLocatorImpl initialize(String name, ServiceLocator parent) {
        if (parent != null && !(parent instanceof ServiceLocatorImpl)) {
            throw new AssertionError("parent must be a " + ServiceLocatorImpl.class.getName() +
                    " instead it is a " + parent.getClass().getName());
        }
        
        ServiceLocatorImpl sli = new ServiceLocatorImpl(name, (ServiceLocatorImpl) parent);
        
        DynamicConfigurationImpl dci = new DynamicConfigurationImpl(sli);
        
        // The service locator itself
        dci.bind(Utilities.getLocatorDescriptor(sli));
        
        // The injection resolver for three thirty
        dci.addActiveDescriptor(Utilities.getThreeThirtyDescriptor(sli));
        
        // The dynamic configuration utility
        dci.bind(BuilderHelper.link(DynamicConfigurationServiceImpl.class, false).
                to(DynamicConfigurationService.class).
                in(Singleton.class.getName()).
                localOnly().
                build());
        
        dci.bind(BuilderHelper.createConstantDescriptor(
                new DefaultClassAnalyzer(sli)));
        
        dci.bind(BuilderHelper.createDescriptorFromClass(ServiceLocatorRuntimeImpl.class));
        
        dci.bind(BuilderHelper.createConstantDescriptor(
                new InstantiationServiceImpl()));
        
        dci.commit();
        
        return sli;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.extension.ServiceLocatorGenerator#create(java.lang.String, org.glassfish.hk2.api.Module)
     */
    @Override
    public ServiceLocator create(String name, ServiceLocator parent) {
        ServiceLocatorImpl retVal = initialize(name, parent);
        
        return retVal;
    }
    
    @Override
    public String toString() {
        return "ServiceLocatorGeneratorImpl(hk2-locator, " + System.identityHashCode(this) + ")";
    }
}
