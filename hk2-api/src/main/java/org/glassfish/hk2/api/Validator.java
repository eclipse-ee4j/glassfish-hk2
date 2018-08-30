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
 * This method is called when it determined that a type that is
 * annotated with a Validating annotation is to be injected into
 * any other class.
 * 
 * @author jwells
 *
 */
public interface Validator {
    /**
     * This method is called whenever it has been determined that a validating
     * class is to be injected into an injection point, or when a descriptor
     * is being looked up explicitly with the API, or a descriptor is being
     * bound or unbound into the registry.
     * <p>
     * The candidate descriptor being passed in may not have yet been reified.  If
     * possible, this method should do its work without reifying the descriptor.
     * However, if it is necessary to reify the descriptor, it should be done with
     * the ServiceLocator.reifyDescriptor method.
     * <p>
     * The operation will determine what operation is being performed.  In the
     * BIND or UNBIND cases the Injectee will be null.  In the LOOKUP case
     * the Injectee will be non-null if this is being done as part of an
     * injection point.  In the LOOKUP case the Injectee will be null if this
     * is being looked up directly from the {@link ServiceLocator} API, in which
     * case the caller of the lookup method will be on the call frame.
     * 
     * @param info Information about the operation being performed
     * @return true if this injection should succeed, false if this candidate should not
     * be returned
     * @throws RuntimeException This method should not throw an exception.  If it
     * does the {@link ErrorService} will be called with the {@link ErrorType#VALIDATE_FAILURE}.
     * Even if the {@link ErrorService#onFailure(ErrorInformation)} rethrows the exception
     * that exception will not be thrown up the stack, instead the system will always
     * behave as if false had been returned from this method
     */
    public boolean validate(ValidationInformation info);
}
