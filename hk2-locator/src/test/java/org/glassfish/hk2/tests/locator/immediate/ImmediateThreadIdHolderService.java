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

package org.glassfish.hk2.tests.locator.immediate;

import javax.annotation.PostConstruct;

import org.glassfish.hk2.api.Immediate;

/**
 * @author jwells
 *
 */
@Immediate
public class ImmediateThreadIdHolderService {
    private long tid;
    private boolean tidSet = false;
    
    @PostConstruct
    private synchronized void postConstruct() {
        tidSet = true;
        tid = Thread.currentThread().getId();
        
        notifyAll();
    }
    
    public long getTid(long waitTime) throws InterruptedException {
        synchronized (this) {
            while (!tidSet && waitTime > 0) {
                long elapsedTime = System.currentTimeMillis();
                
                this.wait(waitTime);
                
                elapsedTime = System.currentTimeMillis() - elapsedTime;
                waitTime -= elapsedTime;
            }
            
            if (!tidSet) return -1L;
            return tid;
        }
        
    }

}
