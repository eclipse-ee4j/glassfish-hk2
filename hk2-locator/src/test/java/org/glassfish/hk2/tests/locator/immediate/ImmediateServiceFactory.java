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

import javax.inject.Singleton;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.Immediate;

/**
 * @author jwells
 *
 */
@Singleton
public class ImmediateServiceFactory implements
        Factory<GenericImmediateService> {
    private boolean createdOne;
    private boolean destroyedOne;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Factory#provide()
     */
    @Override @Immediate
    public GenericImmediateService provide() {
        synchronized (this) {
            createdOne = true;
            this.notify();
        }
        return new GenericImmediateService();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Factory#dispose(java.lang.Object)
     */
    @Override
    public void dispose(GenericImmediateService instance) {
        synchronized (this) {
            destroyedOne = true;
            this.notify();
        }

    }
    
    public boolean waitToCreate(long waitTime) throws InterruptedException {
        synchronized (this) {
            while (!createdOne && waitTime > 0) {
                long elapsedTime = System.currentTimeMillis();
                this.wait(waitTime);
                elapsedTime = System.currentTimeMillis() - elapsedTime;
                waitTime -= elapsedTime;
            }
            
            return createdOne;
        }
    }
    
    public boolean waitToDestroy(long waitTime) throws InterruptedException {
        synchronized (this) {
            while (!destroyedOne && waitTime > 0) {
                long elapsedTime = System.currentTimeMillis();
                this.wait(waitTime);
                elapsedTime = System.currentTimeMillis() - elapsedTime;
                waitTime -= elapsedTime;
            }
            
            return destroyedOne;
        }
    }

}
