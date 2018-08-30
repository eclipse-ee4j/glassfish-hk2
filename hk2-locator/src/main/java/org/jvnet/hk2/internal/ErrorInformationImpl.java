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

package org.jvnet.hk2.internal;

import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.ErrorInformation;
import org.glassfish.hk2.api.ErrorType;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.MultiException;

/**
 * @author jwells
 *
 */
public class ErrorInformationImpl implements ErrorInformation {
    private final ErrorType errorType;
    private final Descriptor descriptor;
    private final Injectee injectee;
    private final MultiException exception;
    
    /* package */ ErrorInformationImpl(ErrorType errorType,
            Descriptor descriptor,
            Injectee injectee,
            MultiException exception) {
        this.errorType = errorType;
        this.descriptor = descriptor;
        this.injectee = injectee;
        this.exception = exception;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ErrorInformation#getErrorType()
     */
    @Override
    public ErrorType getErrorType() {
        return errorType;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ErrorInformation#getDescriptor()
     */
    @Override
    public Descriptor getDescriptor() {
        return descriptor;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ErrorInformation#getInjectee()
     */
    @Override
    public Injectee getInjectee() {
        return injectee;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ErrorInformation#getAssociatedException()
     */
    @Override
    public MultiException getAssociatedException() {
        return exception;
    }
    
    public String toString() {
        return "ErrorInformation(" + errorType + "," +
            descriptor + "," +
            injectee + "," +
            exception + "," +
            System.identityHashCode(this) + ")";
    }

}
