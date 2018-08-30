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

package org.jvnet.hk2.osgiadapter;

import static org.jvnet.hk2.osgiadapter.Logger.logger;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;

import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.MultiException;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

/**
 * An Hk2Loader that uses a Bundle to load classes 
 * 
 * @author mason.taube@oracle.com
 *
 */
public class OsgiHk2Loader implements HK2Loader {
	private final Bundle bundle;

	OsgiHk2Loader(Bundle bundle) {
		this.bundle = bundle;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Class<?> loadClass(final String className) throws MultiException {
		try {
			bundle.start();
		} catch (BundleException e1) {
			throw new MultiException(e1);
		}
		return (Class<?>) AccessController
				.doPrivileged(new PrivilegedAction() {
					public java.lang.Object run() {
						try {
							return bundle.loadClass(className);
						} catch (Throwable e) {
							logger.logp(
									Level.SEVERE,
									"OSGiModuleImpl",
									"loadClass",
									"Exception in module "
											+ bundle.toString() + " : "
											+ e.toString());
							throw new MultiException(e);
						}
					}
				});

	}
}
