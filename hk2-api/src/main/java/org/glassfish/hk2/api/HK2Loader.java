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

package org.glassfish.hk2.api;

/**
 * This class is responsible for loading classes, and different
 * implementations can be used for different descriptors.
 * 
 * @author jwells
 *
 */
public interface HK2Loader {
    /**
     * Loads a class given the class name to instantiate
     * 
     * @param className The descriptor to convert into an ActiveDescriptor
     * @return The class to be loaded.  May not return null
     * @throws MultiException If this loader had some problem loading the class
     */
    public Class<?> loadClass(String className) throws MultiException;

}
