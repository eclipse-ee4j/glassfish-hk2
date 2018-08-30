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

import org.jvnet.hk2.annotations.Service;

/**
 * @author jwells
 *
 */
@Service
public class ServiceWithThreadLocal {
    private static final ThreadLocal < Boolean > upBoolean = 
        new ThreadLocal < Boolean > () {
            @Override protected Boolean initialValue() {
                return new Boolean(false);
        }
    };
    
    private static final ThreadLocal < Boolean > downBoolean = 
        new ThreadLocal < Boolean > () {
            @Override protected Boolean initialValue() {
                return new Boolean(false);
        }
    };

    public boolean wasUpToggled() {
        return upBoolean.get();
    }
    
    public void toggleUp() {
        upBoolean.set(true);
    }
    
    public boolean wasDownToggled() {
        return downBoolean.get();
    }
    
    public void toggleDown() {
        downBoolean.set(true);
    }
    
    
}
