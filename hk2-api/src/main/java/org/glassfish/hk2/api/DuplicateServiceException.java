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

package org.glassfish.hk2.api;

/**
 * This exception is thrown when an idempotent filter of a
 * DynamicConfiguration object matches an existing descriptor
 * in the ServiceLocator
 * 
 * @author jwells
 */
public class DuplicateServiceException extends HK2RuntimeException {
    private static final long serialVersionUID = 7182947621027566487L;
    
    private Descriptor existingDescriptor;
    
    /**
     * For serialization
     */
    public DuplicateServiceException() {
        super();
    }
    
    /**
     * Called by the system to initialize the existing descriptor
     * that matched
     * 
     * @param existingDescriptor The possibly null existing descriptor
     * that matched one of the idempotent filters
     */
    public DuplicateServiceException(Descriptor existingDescriptor) {
        super();
        this.existingDescriptor = existingDescriptor;
    }
    
    /**
     * Gets the descriptor that matched one of the idempotent
     * filters
     * 
     * @return The descriptor that matched one of the idempotent
     * filters or null if the matching descriptor is unknown
     */
    public Descriptor getExistingDescriptor() {
        return existingDescriptor;
    }
    
    @Override
    public String toString() {
        return "DuplicateServiceException(" + existingDescriptor +
                "," + System.identityHashCode(this) + ")";
    }

}
