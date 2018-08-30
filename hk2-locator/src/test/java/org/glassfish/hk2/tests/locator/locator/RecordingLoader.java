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

package org.glassfish.hk2.tests.locator.locator;

import java.util.HashSet;

import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.MultiException;

/**
 * @author jwells
 *
 */
public class RecordingLoader implements HK2Loader {
    private final static RecordingLoader INSTANCE = new RecordingLoader();
    
    private final HashSet<String> loadedClasses = new HashSet<String>();
    
    private RecordingLoader() {
    }
    
    public static RecordingLoader getInstance() { return INSTANCE; }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.HK2Loader#loadClass(java.lang.String)
     */
    @Override
    public Class<?> loadClass(String className) throws MultiException {
        loadedClasses.add(className);
        
        try {
            return getClass().getClassLoader().loadClass(className);
        }
        catch (ClassNotFoundException e) {
            throw new MultiException(e);
        }
    }
    
    public boolean wasClassLoaded(String className) {
        return loadedClasses.contains(className);
    }
    
    public void clear() {
        loadedClasses.clear();
    }

}
