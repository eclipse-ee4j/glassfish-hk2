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

package org.glassfish.hk2.tests.locator.negative.errorservice;

import javax.inject.Singleton;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ErrorInformation;
import org.glassfish.hk2.api.ErrorService;
import org.glassfish.hk2.api.ErrorType;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.MultiException;

/**
 * @author jwells
 *
 */
@Singleton
public class ErrorServiceImpl implements ErrorService {
    private ActiveDescriptor<?> descriptor;
    private Injectee injectee;
    private MultiException me;
    
    private boolean doThrow = false;
    private boolean reThrow = false;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ErrorService#failureToReify(org.glassfish.hk2.api.ActiveDescriptor, org.glassfish.hk2.api.Injectee, org.glassfish.hk2.api.MultiException)
     */
    @Override
    public void onFailure(ErrorInformation ei) {
        if (ei.getErrorType().equals(ErrorType.SERVICE_CREATION_FAILURE)) return;
        if (ei.getErrorType().equals(ErrorType.SERVICE_DESTRUCTION_FAILURE)) return;
        
        this.descriptor = (ActiveDescriptor<?>) ei.getDescriptor();
        this.injectee = ei.getInjectee();
        this.me = ei.getAssociatedException();

        if (doThrow) {
            if (reThrow) {
                throw me;
            }
            else {
                throw new AssertionError(ErrorServiceTest.EXCEPTION_STRING_DUEX);
            }
        }
    }

    /**
     * @return the descriptor
     */
    public ActiveDescriptor<?> getDescriptor() {
        return descriptor;
    }

    /**
     * @return the injectee
     */
    public Injectee getInjectee() {
        return injectee;
    }

    /**
     * @return the me
     */
    public MultiException getMe() {
        return me;
    }
    
    /**
     * Used by the test to clear the stat of the error service impl
     */
    public void clear() {
        descriptor = null;
        injectee = null;
        me = null;
    }
    
    public void doThrow() {
        doThrow = true;
    }
    
    public void reThrow() {
        reThrow = true;
    }
}
