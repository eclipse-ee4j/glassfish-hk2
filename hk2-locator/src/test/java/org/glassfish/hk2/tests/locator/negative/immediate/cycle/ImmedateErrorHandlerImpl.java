/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.locator.negative.immediate.cycle;

import javax.inject.Singleton;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.utilities.ImmediateErrorHandler;

/**
 * @author jwells
 *
 */
@Singleton
public class ImmedateErrorHandlerImpl implements ImmediateErrorHandler {
    private ActiveDescriptor<?> postFailure;
    private Throwable postException;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.ImmediateErrorHandler#postConstructFailed(org.glassfish.hk2.api.ActiveDescriptor, java.lang.Throwable)
     */
    @Override
    public void postConstructFailed(ActiveDescriptor<?> immediateService,
            Throwable exception) {
        synchronized (this) {
            postFailure = immediateService;
            postException = exception;
            this.notifyAll();
        }
    }
    
    public ActiveDescriptor<?> waitForPostFailure(long waitTimeMillis) throws InterruptedException {
        synchronized (this) {
            while (postFailure == null && waitTimeMillis > 0) {
                long elapsedTime = System.currentTimeMillis();
                this.wait(waitTimeMillis);
                elapsedTime = System.currentTimeMillis() - elapsedTime;
                
                waitTimeMillis -= elapsedTime;
            }
            
            if (postFailure == null) {
                throw new AssertionError("Did not detect error after waiting for a while");
            }
            
            return postFailure;
        }
    }
    
    public Throwable getPostException() {
        synchronized (this) {
            return postException;
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.utilities.ImmediateErrorHandler#preDestroyFailed(org.glassfish.hk2.api.ActiveDescriptor, java.lang.Throwable)
     */
    @Override
    public void preDestroyFailed(ActiveDescriptor<?> immediateService,
            Throwable exception) {
        // Do nothing

    }

}
