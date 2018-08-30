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
 * This object contains information about the validation
 * point.  The values available may vary depending on
 * the type of operation.
 * 
 * @author jwells
 *
 */
public interface ValidationInformation {
    /**
     * The operation that is to be performed, one of<UL>
     * <LI>BIND - The candidate descriptor is being added to the system</LI>
     * <LI>UNBIND - The candidate descriptor is being removed from the system</LI>
     * <LI>LOOKUP - The candidate descriptor is being looked up</LI>
     * </UL>
     * 
     * @return The operation being performed
     */
    public Operation getOperation();
    
    /**
     * The candidate descriptor for this operation
     * 
     * @return The candidate descriptor for the operation being performed
     */
    public ActiveDescriptor<?> getCandidate();
    
    /**
     * On a LOOKUP operation if the lookup is being performed due to an
     * injection point (as opposed to a lookup via the API) then this
     * method will return a non-null {@link Injectee} that is the injection
     * point that would be injected into
     * 
     * @return The injection point being injected into on a LOOKUP operation
     */
    public Injectee getInjectee();
    
    /**
     * On a LOOKUP operation the {@link Filter} that was used in the
     * lookup operation.  This may give more information about what
     * exactly was being looked up by the caller
     * 
     * @return The filter used in the lookup operation
     */
    public Filter getFilter();
    
    /**
     * This method attempts to return the StackTraceElement
     * of the code calling the HK2 method that caused
     * this validation to occur
     * <p>
     * This method may not work properly if called outside
     * of the call frame of the {@link Validator#validate(ValidationInformation)}
     * method
     * 
     * @return The caller of the HK2 API that caused this
     * validation to occur, or null if the caller could
     * not be determined
     */
    public StackTraceElement getCaller();

}
