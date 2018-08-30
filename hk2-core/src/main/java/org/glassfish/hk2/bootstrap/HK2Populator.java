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

package org.glassfish.hk2.bootstrap;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.DescriptorFileFinder;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.Populator;
import org.glassfish.hk2.api.PopulatorPostProcessor;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ClasspathDescriptorFileFinder;
import org.glassfish.hk2.utilities.DescriptorImpl;

import com.sun.enterprise.module.bootstrap.BootException;

/**
 * 
 * @author jwells, mason.taube@oracle.com
 *
 */
public class HK2Populator {

    /**
     * This method can be used to populate the service locator with files that
     * have been written out using the {@link DescriptorImpl} writeObject method.
     * 
     * @param serviceLocator The service locator to populate.  May not be null.
     * @param fileFinder An object that finds files in the environment.  May not be null.
     * @param postProcessors A post-processor that allows the environment to modify the set
     * of descriptors that are added to the system.  May be null, in which case the descriptors
     * read in are those that are used to populate the serviceLocator
     * @throws IOException In case of an error
     */
	public static List<ActiveDescriptor> populate(final ServiceLocator serviceLocator,
			DescriptorFileFinder fileFinder,
			List <? extends PopulatorPostProcessor> postProcessors) throws IOException {
	    if (postProcessors == null) postProcessors = new LinkedList<PopulatorPostProcessor>();
	    
	    DynamicConfigurationService dcs = serviceLocator.getService(DynamicConfigurationService.class);
	    Populator populator = dcs.getPopulator();
	    
	    List<ActiveDescriptor<?>> retVal = populator.populate(fileFinder,
	            postProcessors.toArray(new PopulatorPostProcessor[postProcessors.size()]));
	    
	    return (List<ActiveDescriptor>) ((List) retVal);
	}

	/**
	 * This method can be used to populate the service locator with files that
     * have been written out using the {@link DescriptorImpl} writeObject method,
     * looking in the classpath to locate these files
     * 
	 * @param serviceLocator The service locator to populate.  May not be null
	 * @throws IOException In case of an error
	 */
	public static void populate(final ServiceLocator serviceLocator)
			throws IOException {
		populate(serviceLocator, new ClasspathDescriptorFileFinder(), null);
	}

    public static void populateConfig(ServiceLocator serviceLocator) throws BootException {
        //Populate this serviceLocator with config data
        for (ConfigPopulator populator : serviceLocator.<ConfigPopulator>getAllServices(ConfigPopulator.class)) {
            populator.populateConfig(serviceLocator);
        }
    }
}
