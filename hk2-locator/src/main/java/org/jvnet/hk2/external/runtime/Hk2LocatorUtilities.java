/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.jvnet.hk2.external.runtime;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.Filter;
import org.jvnet.hk2.internal.DefaultClassAnalyzer;
import org.jvnet.hk2.internal.DynamicConfigurationServiceImpl;
import org.jvnet.hk2.internal.InstantiationServiceImpl;
import org.jvnet.hk2.internal.ServiceLocatorImpl;
import org.jvnet.hk2.internal.ServiceLocatorRuntimeImpl;
import org.jvnet.hk2.internal.ThreeThirtyResolver;

/**
 * This is a utility class specific to this implementation
 * of the hk2 API
 * 
 * @author jwells
 *
 */
public class Hk2LocatorUtilities {
    private final static Filter NO_INITIAL_SERVICES_FILTER = new Filter() {
        private final List<String> INITIAL_SERVICES = Arrays.asList(new String[] {
            ServiceLocatorImpl.class.getName(),
            ThreeThirtyResolver.class.getName(),
            DynamicConfigurationServiceImpl.class.getName(),
            DefaultClassAnalyzer.class.getName(),
            ServiceLocatorRuntimeImpl.class.getName(),
            InstantiationServiceImpl.class.getName()
        });
        
        private final HashSet<String> INITIAL_SERVICE_SET = new HashSet<String>(INITIAL_SERVICES);

        @Override
        public boolean matches(Descriptor d) {
            return !INITIAL_SERVICE_SET.contains(d.getImplementation());
        }
        
    };
    
    /**
     * Returns a filter that only returns services that are not
     * in the initial set of services offered by all ServiceLocators
     * created by this implementation of hk2.  This filter
     * is guaranteed to work properly for all versions of this
     * implementation of hk2
     * 
     * @return A Filter that only returns services that are not
     * in the initial set of services offered by all ServiceLocators
     * created by this implementation of hk2
     */
    public static Filter getNoInitialServicesFilter() {
        return NO_INITIAL_SERVICES_FILTER;
        
    }

}
