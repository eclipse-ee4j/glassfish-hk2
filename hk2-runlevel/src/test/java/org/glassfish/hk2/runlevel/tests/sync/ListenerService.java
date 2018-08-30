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

package org.glassfish.hk2.runlevel.tests.sync;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.glassfish.hk2.runlevel.ChangeableRunLevelFuture;
import org.glassfish.hk2.runlevel.ErrorInformation;
import org.glassfish.hk2.runlevel.RunLevelFuture;
import org.glassfish.hk2.runlevel.RunLevelListener;

/**
 * @author jwells
 *
 */
@Singleton
public class ListenerService implements RunLevelListener {
    @Inject
    private ServiceWithThreadLocal threadService;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.runlevel.RunLevelListener#onProgress(org.glassfish.hk2.runlevel.ChangeableRunLevelFuture, int)
     */
    @Override
    public void onProgress(ChangeableRunLevelFuture currentJob,
            int levelAchieved) {
        if (currentJob.isUp()) return;
        if (levelAchieved != 0) return;
        
        threadService.toggleDown();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.runlevel.RunLevelListener#onCancelled(org.glassfish.hk2.runlevel.RunLevelFuture, int)
     */
    @Override
    public void onCancelled(RunLevelFuture currentJob, int levelAchieved) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.runlevel.RunLevelListener#onError(org.glassfish.hk2.runlevel.RunLevelFuture, org.glassfish.hk2.runlevel.ErrorInformation)
     */
    @Override
    public void onError(RunLevelFuture currentJob,
            ErrorInformation errorInformation) {
        // TODO Auto-generated method stub

    }

}
