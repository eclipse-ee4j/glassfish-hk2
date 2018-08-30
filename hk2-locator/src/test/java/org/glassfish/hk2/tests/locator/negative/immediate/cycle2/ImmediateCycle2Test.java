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

package org.glassfish.hk2.tests.locator.negative.immediate.cycle2;

import javax.inject.Singleton;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.ImmediateErrorHandler;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class ImmediateCycle2Test {
    @Test
    public void testRawTripleCycle() throws Throwable {
        ServiceLocator locator = LocatorHelper.getServiceLocator(ImmediateErrorHandlerImpl.class);
        
        ServiceLocatorUtilities.enableImmediateScope(locator);
        
        ServiceLocatorUtilities.bind(locator, new AbstractBinder() {

            @Override
            protected void configure() {
                bind(ServiceClientImpl.class).to(ServiceClient.class).in(ServiceLocatorUtilities.getImmediateAnnotation());
                bind(MessageHandlerImpl.class).to(MessageHandler.class).in(ServiceLocatorUtilities.getImmediateAnnotation());
                bind(RepositoryClientImpl.class).to(RepositoryClient.class).in(ServiceLocatorUtilities.getImmediateAnnotation());
            }
        });
        
        Assert.assertTrue(locator.getService(ImmediateErrorHandlerImpl.class).gotException(20 * 1000));
    }
    
    @Singleton
    private static class ImmediateErrorHandlerImpl implements ImmediateErrorHandler {
        private boolean gotException = false;

        /* (non-Javadoc)
         * @see org.glassfish.hk2.utilities.ImmediateErrorHandler#postConstructFailed(org.glassfish.hk2.api.ActiveDescriptor, java.lang.Throwable)
         */
        @Override
        public synchronized void postConstructFailed(ActiveDescriptor<?> immediateService,
                Throwable exception) {
            gotException = true;
            notifyAll();
        }

        /* (non-Javadoc)
         * @see org.glassfish.hk2.utilities.ImmediateErrorHandler#preDestroyFailed(org.glassfish.hk2.api.ActiveDescriptor, java.lang.Throwable)
         */
        @Override
        public void preDestroyFailed(ActiveDescriptor<?> immediateService,
                Throwable exception) {
            
        }
        
        private boolean gotException(long timeout) throws InterruptedException {
            synchronized (this) {
                while (!gotException && timeout > 0) {
                    long elapsedTime = System.currentTimeMillis();
                    
                    this.wait(timeout);
                    
                    elapsedTime = System.currentTimeMillis() - elapsedTime;
                    timeout -= elapsedTime;
                }
                
                return gotException;
            }
            
        }
    }

}
