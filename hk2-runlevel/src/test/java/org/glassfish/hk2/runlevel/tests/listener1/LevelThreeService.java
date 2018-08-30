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

package org.glassfish.hk2.runlevel.tests.listener1;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.glassfish.hk2.runlevel.RunLevel;

/**
 * @author jwells
 *
 */
@RunLevel(3)
public class LevelThreeService implements TrackingService {
    @Inject
    private UpListener upListener;
    
    private boolean up;
    private boolean down;

    @PostConstruct
    private void postConstruct() {
        up = upListener.isUp();
        down = upListener.isDown();
    }
    
    @PreDestroy
    private void preDestroy() {
        up = upListener.isUp();
        down = upListener.isDown();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.runlevel.tests.listener1.TrackingService#isUp()
     */
    @Override
    public boolean isUp() {
        return up;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.runlevel.tests.listener1.TrackingService#isDown()
     */
    @Override
    public boolean isDown() {
        return down;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.runlevel.tests.listener1.TrackingService#done()
     */
    @Override
    public void done() {
        up = false;
        down = false;
    }

}
