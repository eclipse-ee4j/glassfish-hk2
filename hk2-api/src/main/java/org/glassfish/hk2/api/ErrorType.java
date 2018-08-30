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
 * This enumeration describes the types of errors that might
 * occur
 * 
 * @author jwells
 *
 */
public enum ErrorType {
    /**
     * This type is set if an ActiveDescriptor fails to reify during a lookup operation
     */
    FAILURE_TO_REIFY,
    
    /**
     * This type is set if a dynamic configuration operation fails
     */
    DYNAMIC_CONFIGURATION_FAILURE,
    
    /**
     * A service threw an error upon creation
     */
    SERVICE_CREATION_FAILURE,
    
    /**
     * A service threw an error upon destruction
     */
    SERVICE_DESTRUCTION_FAILURE,
    
    /**
     * The {@link Validator#validate(ValidationInformation)} method failed
     */
    VALIDATE_FAILURE

}
