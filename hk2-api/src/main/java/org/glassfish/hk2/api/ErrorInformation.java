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
 * This class has information in it about the error that
 * has occurred
 * 
 * @author jwells
 *
 */
public interface ErrorInformation {
    /**
     * Gets the type of error that has occurred.  Code should be
     * written such that future error types are handled appropriately.
     * 
     * @return <UL>
     * <LI>{@link ErrorType#FAILURE_TO_REIFY}</LI>
     * <LI>{@link ErrorType#DYNAMIC_CONFIGURATION_FAILURE}</LI>
     * <LI>{@link ErrorType#SERVICE_CREATION_FAILURE}</LI>
     * <LI>{@link ErrorType#SERVICE_DESTRUCTION_FAILURE}</LI>
     * <LI>{@link ErrorType#VALIDATE_FAILURE}</LI>
     * </UL>
     */
    public ErrorType getErrorType();
    
    /**
     * This will contain the active descriptor that is associated
     * with this failure.  In the case of FAILURE_TO_REIFY it will
     * contain the descriptor that failed to reify.  In the
     * DYNAMIC_CONFIGURATION_FAILURE case this will return null.
     * In SERVICE_CREATION_FAILURE and SERVICE_DESTRUCTION_FAILURE
     * it will contain the descriptor whose create or destroy methods
     * failed.  In the case of VALIDATE_FAILURE it will contain
     * the descriptor that failed the security check
     * 
     * @return The descriptor associated with this failure
     */
    public Descriptor getDescriptor();
    
    /**
     * This will contain information about the Injectee that was being
     * injected into when the error occurred.
     * <p>
     * In the case of FAILURE_TO_REIFY this will be the injectee that was
     * being looked up to satisfy the injection point, or null if this lookup
     * was due to an API call.
     * <p>
     * In the case of VALIDATE_FAILURE this will contain the injectee that
     * was being looked up when the failure occurred or null if this was a
     * lookup operation or the injectee is unknown for some other reason
     * <p>
     * In the cases of DYNAMIC_CONFIGURATION_FAILURE, SERVICE_CREATION_FAILURE and
     * SERVICE_DESTRUCTION_FAILURE this will return null.
     * 
     * @return The injectee associated with this failure
     */
    public Injectee getInjectee();
    
    /**
     * This will contain the associated exception or exceptions that caused
     * the failure.
     * <p>
     * In the case of FAILURE_TO_REIFY this will contain the exception that caused
     * the reification process to fail
     * <p>
     * In the case of DYNAMIC_CONFIGURATION_FAILURE this will contain the exception
     * that cause the configuration operation to fail
     * <p>
     * In the case of SERVICE_CREATION_FAILURE this will contain the exception
     * that was thrown during service creation
     * <p>
     * In the case of SERVICE_DESTRUCTION_FAILURE this will contain the exception
     * that was thrown during service destruction
     * <p>
     * In the case of VALIDATE_FAILURE this will contain the exception that was
     * thrown from the {@link Validator#validate(ValidationInformation)} method
     * 
     * @return The exception associated with this failure
     */
    public MultiException getAssociatedException();
}
