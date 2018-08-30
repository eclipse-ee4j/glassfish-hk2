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
 * ModuleState define the state of a Module instance. 
 *
 * @author Jerome Dochez
 */
public enum ModuleState {
    
    /**
     * a Module is in NEW state when the module object is constructed but not 
     * initialized
     */
    NEW,
    /**
     * a Module is in PREPARING state when the module looks for its import
     * policy class if any or use the default import policy to construct the
     * network of dependency modules.
     */
    PREPARING,
    /**
     * a Module is in RESOLVED state when the validation is finished and 
     * successful and before the module is started
     */
    RESOLVED,
    /**
     * the Module has been started as all its dependencies were satisfied
     */
    READY,
    /**
     * a Module is in ERROR state when its class loader cannot be constructed 
     * or when any of its dependent module is in ERROR state.
     */
    ERROR
    
}
