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

package org.glassfish.hk2.utilities;

import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.MultiException;

/**
 * This is an implementation of an {@link HK2Loader} that uses
 * a given classloader
 * 
 * @author jwells
 */
public class HK2LoaderImpl implements HK2Loader {
    private final ClassLoader loader;
    
    /**
     * Initializes this HK2Loader with the system classloader
     */
    public HK2LoaderImpl() {
        this(ClassLoader.getSystemClassLoader());
    }
    
    /**
     * Initializes this HK2Loader with the given ClassLoader
     * 
     * @param loader The non-null classloader to use with this
     * HK2Loader
     */
    public HK2LoaderImpl(ClassLoader loader) {
        if (loader == null) throw new IllegalArgumentException();
        
        this.loader = loader;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.HK2Loader#loadClass(java.lang.String)
     */
    @Override
    public Class<?> loadClass(String className) throws MultiException {
        try {
            return loader.loadClass(className);
        }
        catch (Exception e) {
            throw new MultiException(e);
        }
    }
    
    public String toString() {
        return "HK2LoaderImpl(" + loader + ")";
    }

}
