/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.utilities;

import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.PopulatorPostProcessor;
import org.glassfish.hk2.api.ServiceLocator;

/**
 * This is a {@link PopulatorPostProcessor} that adds an {@link HK2Loader}
 * based on a ClassLoader.  This is useful for those writing ClassLoader
 * based plugins that need to ensure their descriptors are loaded with
 * the given ClassLoader
 * 
 * @author jwells
 *
 */
public class ClassLoaderPostProcessor implements PopulatorPostProcessor {
    private final HK2Loader loader;
    private final boolean force;
    
    /**
     * Creates a {@link PopulatorPostProcessor} that will set the HK2Loader
     * of descriptors added with
     * {@link org.glassfish.hk2.api.Populator#populate(org.glassfish.hk2.api.DescriptorFileFinder, PopulatorPostProcessor...)}
     * 
     * @param classloader The classloader to use when classloading the added services
     * @param force If true then this will overwrite any value in the descriptor.  If false then if
     * the descriptor will only be changed if the HK2Loader field of the descriptor is not
     * already set 
     */
    public ClassLoaderPostProcessor(ClassLoader classloader, boolean force) {
        loader = new HK2LoaderImpl(classloader);
        this.force = force;
    }
    
    /**
     * Creates a {@link PopulatorPostProcessor} that will set the HK2Loader
     * of descriptors added with
     * {@link org.glassfish.hk2.api.Populator#populate(org.glassfish.hk2.api.DescriptorFileFinder, PopulatorPostProcessor...)}.
     * The HK2Loader field of services will only be changed if they have not already
     * been set
     * 
     * @param classloader The classloader to use when classloading the added services 
     */
    public ClassLoaderPostProcessor(final ClassLoader classloader) {
        this(classloader, false);
    }

    @Override
    public DescriptorImpl process(ServiceLocator serviceLocator,
            DescriptorImpl descriptorImpl) {
        if (force) {
            // Doesn't matter what the old loader was, replace with the new one
            descriptorImpl.setLoader(loader);
            return descriptorImpl;
        }
        
        if (descriptorImpl.getLoader() != null) {
            // loader already set, force is false, do nothing
            return descriptorImpl;
        }
        
        // loader is null so set to our loader
        descriptorImpl.setLoader(loader);
        return descriptorImpl;
    }

}
