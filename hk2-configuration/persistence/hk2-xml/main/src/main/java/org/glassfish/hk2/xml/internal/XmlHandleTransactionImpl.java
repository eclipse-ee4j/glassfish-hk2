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

package org.glassfish.hk2.xml.internal;

import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.xml.api.XmlHandleTransaction;
import org.glassfish.hk2.xml.api.XmlRootHandle;

/**
 * @author jwells
 *
 */
public class XmlHandleTransactionImpl<T> implements XmlHandleTransaction<T> {
    private final XmlRootHandle<T> root;
    private final DynamicChangeInfo changeInfo;
    
    public XmlHandleTransactionImpl(XmlRootHandle<T> root, DynamicChangeInfo changeInfo) {
        this.root = root;
        this.changeInfo = changeInfo;
        
        changeInfo.getWriteLock().lock();
        changeInfo.startOrContinueChange(null);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.api.XmlHandleTransaction#getRootHandle()
     */
    @Override
    public XmlRootHandle<T> getRootHandle() {
        return root;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.api.XmlHandleTransaction#commit()
     */
    @Override
    public void commit() throws MultiException {
        try {
            changeInfo.endOrDeferChange(true);
        }
        finally {
            changeInfo.getWriteLock().unlock();
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.api.XmlHandleTransaction#abandon()
     */
    @Override
    public void abandon() {
        try {
            changeInfo.endOrDeferChange(false);
        }
        finally {
            changeInfo.getWriteLock().unlock();
        }
    }

    @Override
    public String toString() {
        return "XmlHandleTransactionImpl(" + root + "," + System.identityHashCode(this) + ")";
    }
}
