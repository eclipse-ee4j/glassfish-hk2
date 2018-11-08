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

package com.sun.enterprise.module;

/**
 * Listener interface that can be registered on the registry to listen to 
 * notification of module startup and shutdown. Modules which are interdependent
 * could use this approach to limit the interdependencies of code. 
 *
 * @author Jerome Dochez
 */
public interface ModuleLifecycleListener {
 

    /**
     * Callback after a module is installed
     * @param module the module instance
     */
    public void moduleInstalled(HK2Module module);

    /**
     * Callback after a module is resolved
     * @param module the module instance
     */
    public void moduleResolved(HK2Module module);
    
    /**
     * Callback after a module is started. 
     * @param module the module instance
     */
    public void moduleStarted(HK2Module module);
    
    /** 
     * Callback after a module is stopped
     * @param module the module instance
     */
    public void moduleStopped(HK2Module module);

    /**
     * Callback after a module is updated.
     * This is useful in OSGi environment which allows a module to be updated.
     * @param module the module instance
     */
    public void moduleUpdated(HK2Module module);
}
