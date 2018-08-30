/*
 * Copyright (c) 2007, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.jvnet.hk2.osgiadapter;

/**
 * An enum for constants defined in {@link org.osgi.framework.BundleEvent}
 *
 * @author Sanjeeb.Sahoo@Sun.COM
 */
public enum BundleEventType {
    INSTALLED(0x1),
    STARTED(0x2),
    STOPPED(0x4),
    UPDATED(0x8),
    UNINSTALLED(0x10),
    RESOLVED(0x20),
    UNRESOLVED(0x40),
    STARTING(0x80),
    STOPPING(0x100),
    LAZY_ACTIVATION(0x200),
    UNKNOWN_EVENT;

    private int i;
    BundleEventType(int i) {
        this.i = i;
    }

    BundleEventType() {
    }

    public static BundleEventType valueOf(int i) {
        for (BundleEventType m : values()) {
            if(m.i == i) return m;
        }
        return UNKNOWN_EVENT;
    }
}
