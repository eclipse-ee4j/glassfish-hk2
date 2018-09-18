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

import org.jvnet.hk2.annotations.Contract;
import org.jvnet.hk2.annotations.Service;

/**
 * Modules that wish to programmatically control their list of imports can 
 * implement this interface. Implementation of this interface will be called 
 * when the module is in {@link ModuleState#PREPARING PREPARING} state. 
 *
 * <p>
 * To define an implementation of this in a module, write a class
 * that implements this interface and puts {@link Service} on it.
 * Maven will take care of the rest.
 * 
 * @author Jerome Dochez
 * @see ManifestConstants#IMPORT_POLICY
 */
@Contract
public interface ImportPolicy {
    
    /**
     * callback from the module loading system when the module enters the 
     * {@link ModuleState#PREPARING PREPARING} phase.
     * @param module the module instance
     */
    public void prepare(HK2Module module);
}
