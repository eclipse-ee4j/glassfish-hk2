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

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;

import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.PopulatorPostProcessor;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.DescriptorImpl;

/**
 * A PopulatorPostProcessor that sets the HK2Loader prior to binding a descriptor
 * 
 * @author mason.taube@oracle.com
 *
 */
public class OsgiPopulatorPostProcessor implements
		PopulatorPostProcessor {
	
	public static final String BUNDLE_VERSION = "Bundle-Version";
	public static final String BUNDLE_SYMBOLIC_NAME = "Bundle-SymbolicName";
	private final OSGiModuleImpl osgiModule;
	private final HK2Loader loader;

	OsgiPopulatorPostProcessor(OSGiModuleImpl paramOsgiModule) {
		this.osgiModule = paramOsgiModule;
		
		loader = new HK2Loader() {

            @SuppressWarnings({ "unchecked", "rawtypes" })
            @Override
            public Class<?> loadClass(final String className)
                    throws MultiException {
                osgiModule.start();
                return (Class<?>) AccessController.doPrivileged(new PrivilegedAction() {
                    public java.lang.Object run() {
                        try {
                            return osgiModule.getBundle().loadClass(className);
                        } catch (Throwable e) {
                            logger.logp(Level.SEVERE, "OSGiModuleImpl",
                                    "loadClass",
                                    "Exception in module " + osgiModule.getBundle().toString(), e);

                            throw new MultiException(e);
                        }
                    }
                });

            }
            
            public String toString() {
                return "OsgiPopulatorPostProcessor.HK2Loader(" +
                        osgiModule + "," + System.identityHashCode(this) + ")";
            }
            
        };
	}

	@Override
	public DescriptorImpl process(ServiceLocator serviceLocator, DescriptorImpl descriptorImpl) {
        
		descriptorImpl.setLoader(loader);
		
		descriptorImpl.addMetadata(BUNDLE_SYMBOLIC_NAME,osgiModule.getBundle().getSymbolicName());
		descriptorImpl.addMetadata(BUNDLE_VERSION, osgiModule.getBundle().getVersion().toString());
		return descriptorImpl;
	}	
}
