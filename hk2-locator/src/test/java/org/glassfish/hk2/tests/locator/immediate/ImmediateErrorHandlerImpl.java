/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.locator.immediate;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Singleton;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.utilities.ImmediateErrorHandler;

/**
 * @author jwells
 *
 */
@Singleton
public class ImmediateErrorHandlerImpl implements ImmediateErrorHandler {
    private final List<ErrorData> constructionErrors = new LinkedList<ErrorData>();
    private final List<ErrorData> destructionErrors = new LinkedList<ErrorData>();

    @Override
    public void postConstructFailed(ActiveDescriptor<?> immediateService,
            Throwable exception) {
        synchronized (this) {
            constructionErrors.add(new ErrorData(immediateService, exception));
            this.notifyAll();
        }
        
    }

    @Override
    public void preDestroyFailed(ActiveDescriptor<?> immediateService,
            Throwable exception) {
        synchronized (this) {
            destructionErrors.add(new ErrorData(immediateService, exception));
            this.notifyAll();
        }
        
    }
    
    /* package */ List<ErrorData> waitForAtLeastOneConstructionError(long waitTime) throws InterruptedException {
        synchronized (this) {
            while (constructionErrors.size() <= 0 && waitTime > 0) {
                long currentTime = System.currentTimeMillis();
                wait(waitTime);
                long elapsedTime = System.currentTimeMillis() - currentTime;
                waitTime -= elapsedTime;
            }
            
            return new LinkedList<ErrorData>(constructionErrors);
        }
    }
    
    /* package */ List<ErrorData> waitForAtLeastOneDestructionError(long waitTime) throws InterruptedException {
        synchronized (this) {
            while (destructionErrors.size() <= 0 && waitTime > 0) {
                long currentTime = System.currentTimeMillis();
                wait(waitTime);
                long elapsedTime = System.currentTimeMillis() - currentTime;
                waitTime -= elapsedTime;
            }
            
            return new LinkedList<ErrorData>(destructionErrors);
        }
    }
}
