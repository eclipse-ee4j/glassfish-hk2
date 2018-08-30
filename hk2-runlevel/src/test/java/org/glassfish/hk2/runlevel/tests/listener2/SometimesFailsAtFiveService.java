/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.runlevel.tests.listener2;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.glassfish.hk2.runlevel.RunLevel;

/**
 * @author jwells
 *
 */
@RunLevel(5)
public class SometimesFailsAtFiveService {
    private static boolean bombAtFive = false;
    
    public synchronized static void setBombAtFive(boolean doBomb) {
        bombAtFive = doBomb;
    }
    
    private synchronized boolean doBomb() {
        return bombAtFive;
    }
    
    @PostConstruct
    public void postConstruct() {
        if (doBomb()) {
            throw new AssertionError("Was asked to bomb at five so I am bombing at level five on the way up");
        }
    }
    
    @PreDestroy
    public void preDestroy() {
        if (doBomb()) {
            throw new AssertionError("Was asked to bomb at five so I am bombing at level five on the way down");
        }
    }
}
