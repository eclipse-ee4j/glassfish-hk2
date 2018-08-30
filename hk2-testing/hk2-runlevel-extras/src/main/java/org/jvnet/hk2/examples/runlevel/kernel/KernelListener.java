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

package org.jvnet.hk2.examples.runlevel.kernel;

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
public class KernelListener implements RunLevelListener {
    @Override
    public void onProgress(ChangeableRunLevelFuture future, int runLevel) {
        System.out.println("onProgress Actived Level: " + runLevel);

    }

    @Override
    public void onCancelled(RunLevelFuture future, int runLevel) {
        if (future.isDown()) {
            return;
        }

        System.out.println("onCancelled Actived Level: " + runLevel);

    }

    @Override
    public void onError(RunLevelFuture future, ErrorInformation errorInfo) {
        if (future.isDown()) {
            return;
        }

        System.out.println("Error Info: " + errorInfo.getError());
    }
}
