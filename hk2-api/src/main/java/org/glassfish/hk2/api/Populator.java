/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.api;

import java.io.IOException;
import java.util.List;

import org.glassfish.hk2.utilities.DescriptorImpl;

/**
 * Implementations of this interface are used to populate HK2 
 * service locators from inhabitants files
 * 
 * @see DynamicConfigurationService#getPopulator
 * 
 * @author jwells
 *
 */
public interface Populator {
    /**
     * This method can be used to populate the service locator with files that
     * have been written out using the {@link DescriptorImpl} writeObject method.
     * 
     * @param fileFinder An object that finds files in the environment.  If this is null
     * then the system will look in the service locator for an implementation of
     * DescriptorFileFinder.  If one is still not find this service will return an empty list
     * @param postProcessors post-processors that allows the environment to modify the set
     * of descriptors that are added to the system.
     * @return The list of descriptors added to the system.  Will not return null, but may return
     * an empty list
     * @throws IOException In case of an error reading the input streams
     * @throws MultiException if the user code throws an error, in which case none of the descriptors
     * will be added to the system
     */
    public List<ActiveDescriptor<?>> populate(
            DescriptorFileFinder fileFinder,
            PopulatorPostProcessor... postProcessors) throws IOException, MultiException;
    
    /**
     * This method will populate the service locator using the system classloader to
     * find the hk2-locator files from the default location of META-INF/hk2-locator/default.
     * No post processing will be done on the descriptors added to the system
     * 
     * @return The list of descriptors added to the system.  Will not return null, but may return
     * an empty list
     * @throws IOException if there was an error reading any of the descriptors
     * @throws MultiException if the user code throws an error, in which case none of the descriptors
     * will be added to the system
     */
    public List<ActiveDescriptor<?>> populate() throws IOException, MultiException; 

}
