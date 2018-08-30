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

package org.jvnet.hk2.internal;

import java.net.URL;

import org.glassfish.hk2.utilities.reflection.Pretty;

/**
 * A classloader that delegates first to an optional parent and then to a delegate loader
 */
class DelegatingClassLoader extends ClassLoader {
	private final ClassLoader delegates[];

	/**
	 * Constructor for special classloader to give to proxy making code
	 * 
	 * @param parent the java-style classloader parent of this loader
	 * @param classLoaderDelegates other classloaders to delegate to
	 */
	DelegatingClassLoader(ClassLoader parent, ClassLoader... classLoaderDelegates) {
		super(parent);
        delegates=classLoaderDelegates;
    }
	
	@Override
	public Class<?> loadClass(String clazz)
			throws ClassNotFoundException {
		
		if (getParent() != null) {
			try {
				return getParent().loadClass(clazz);
			} catch (ClassNotFoundException cnfe) {}
		}

		ClassNotFoundException firstFail = null;
		for (ClassLoader delegate : delegates) {
		    try {
		      return delegate.loadClass(clazz);
		    }
		    catch (ClassNotFoundException ncfe) {
		        if (firstFail == null) firstFail = ncfe;
		    }
		}
		
		if (firstFail != null) throw firstFail;
		throw new ClassNotFoundException("Could not find " + clazz);
	}

	@Override
	public URL getResource(String resource) {
		if (getParent() != null) {
			URL u = getParent().getResource(resource);
			
			if (u != null) {
				return u;
			}
		}
		
		for (ClassLoader delegate : delegates) {
		    URL u = delegate.getResource(resource);
		    
		    if (u != null) return u;
		}
		
		return null;
	}
	
	@Override
	public String toString() {
	    return "DelegatingClassLoader(" + getParent() + "," +
	        Pretty.array(delegates) + "," + System.identityHashCode(this) + ")";
	}
}
