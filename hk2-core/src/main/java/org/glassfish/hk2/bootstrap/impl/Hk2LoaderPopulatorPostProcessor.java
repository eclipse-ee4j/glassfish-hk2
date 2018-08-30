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

package org.glassfish.hk2.bootstrap.impl;

import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.PopulatorPostProcessor;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.DescriptorImpl;

/**
 * A Simple PopulatorPostProcessor that uses the given classloader to override default
 * HK2Loader behavior 
 * 
 * @author mtaube
 *
 */
public class Hk2LoaderPopulatorPostProcessor implements PopulatorPostProcessor {

	private final HK2Loader hk2Loader;
	
	/**
	 * Creates the post-processor to use the given classloader
	 * 
	 * @param classLoader The classloader to use, may not be null
	 */
	public Hk2LoaderPopulatorPostProcessor(ClassLoader classLoader) {
		if (classLoader == null) {
			classLoader = getClass().getClassLoader();
		}
		
		final ClassLoader fClassLoader = classLoader;
		hk2Loader = new HK2Loader() {

            @Override
            public Class<?> loadClass(String className) throws MultiException {
                try {
                    return fClassLoader.loadClass(className);
                } catch (ClassNotFoundException e) {
                    throw new MultiException(e);
                }
            }
		    
		} ;
    }

	public Hk2LoaderPopulatorPostProcessor() {
		this(null);
	}
	
	/**
	 * Uses the given classloader to load the class from the descriptor
	 */
	@Override
	public DescriptorImpl process(ServiceLocator serviceLocator, DescriptorImpl descriptorImpl) {
		descriptorImpl.setLoader(hk2Loader);
		
		return descriptorImpl;
	}	

}
