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

package org.glassfish.hk2.runlevel.tests.deadlock1;

import org.glassfish.hk2.runlevel.ChangeableRunLevelFuture;
import org.glassfish.hk2.runlevel.ErrorInformation;
import org.glassfish.hk2.runlevel.RunLevelFuture;
import org.glassfish.hk2.runlevel.RunLevelListener;
import org.jvnet.hk2.annotations.Service;

/**
 * @author jwells
 *
 */
@Service
public class DeadLock1Listener implements RunLevelListener {
    private boolean go = false;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.runlevel.RunLevelListener#onCancelled(org.glassfish.hk2.runlevel.RunLevelFuture, int)
     */
    @Override
    public void onCancelled(RunLevelFuture controller, int levelAchieved) {

    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.runlevel.RunLevelListener#onError(org.glassfish.hk2.runlevel.RunLevelFuture, java.lang.Throwable)
     */
    @Override
    public void onError(RunLevelFuture currentJob, ErrorInformation info) {

    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.runlevel.RunLevelListener#onProgress(org.glassfish.hk2.runlevel.RunLevelFuture, int)
     */
    @Override
    public void onProgress(ChangeableRunLevelFuture currentJob, int levelAchieved) {
        if (levelAchieved != 1) return;
        
        OtherThreadCanceller otc = new OtherThreadCanceller(currentJob, this);
        
        Thread myThread = new Thread(otc);
        myThread.start();
        
        synchronized(this) {
            while (!go) {
                try {
                    this.wait();
                }
                catch (InterruptedException e) {
                    throw new AssertionError(e);
                }
            }
        }
    }
    
    public class OtherThreadCanceller implements Runnable {
        private final RunLevelFuture job;
        private final Object lock;
        
        private OtherThreadCanceller(RunLevelFuture job, Object lock) {
            this.job = job;
            this.lock = lock;
            
        }

        @Override
        public void run() {
            // If locks are held in onProgress this will block forever
            job.cancel(false);
            
            synchronized (lock) {
                go = true;
                lock.notifyAll();
            }
            
            
        }
        
    }

}
