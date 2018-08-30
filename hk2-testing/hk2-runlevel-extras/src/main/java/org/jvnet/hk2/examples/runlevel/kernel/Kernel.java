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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.glassfish.hk2.runlevel.RunLevelController;
import org.jvnet.hk2.annotations.Service;
import org.jvnet.hk2.examples.runlevel.StartupRunLevels;

/**
 * @author jwells
 *
 */
@Service
public class Kernel {
    private final RunLevelController controller;

    @Inject
    public Kernel(RunLevelController controller, KernelListener listener) {
        this.controller = controller;
    }

    @PostConstruct
    public void startup() {
        System.out.println("Kernel starting up. Proceeding to: " + StartupRunLevels.POST_STARTUP);
        controller.proceedTo(StartupRunLevels.POST_STARTUP);
    }

    @PreDestroy
    public void shutdown() throws InterruptedException {
        System.out.println("Kernel shutting down. Proceeding to: " + StartupRunLevels.SHUTDOWN);
        controller.proceedTo(StartupRunLevels.SHUTDOWN);
    }
}
