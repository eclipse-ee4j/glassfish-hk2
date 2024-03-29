/*
 * Copyright (c) 2014, 2024 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.configuration.hub.internal;

import java.util.concurrent.locks.ReentrantLock;

import org.glassfish.hk2.configuration.hub.api.Instance;

/**
 * @author jwells
 *
 */
public class InstanceImpl implements Instance {
    private final ReentrantLock lock = new ReentrantLock();
    private final Object bean;
    private Object metadata;
    
    /* package */ InstanceImpl(Object bean, Object metadata) {
        this.bean = bean;
        this.metadata = metadata;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.Instance#getBean()
     */
    @Override
    public Object getBean() {
        return bean;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.Instance#getMetadata()
     */
    @Override
    public Object getMetadata() {
        lock.lock();
        try {
            return metadata;
        } finally {
            lock.unlock();
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.Instance#setMetadata(java.lang.Object)
     */
    @Override
    public void setMetadata(Object metadata) {
        this.metadata = metadata;

    }

    @Override
    public String toString() {
        return "InstanceImpl(" + bean + "," + metadata + "," + System.identityHashCode(this) + ")";
    }
}
