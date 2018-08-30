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

package com.sun.enterprise.module.common_impl;

import java.util.Iterator;

/**
 * {@link Iterator} implementation that works as a filter to another iterator.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class AdapterIterator<T,U> implements Iterator<T> {
    private final Iterator<? extends U> core;
    private T next;

    public AdapterIterator(Iterator<? extends U> core) {
        this.core = core;
    }

    public T next() {
        fetch();
        T r = next;
        next=null;
        return r;
    }

    public boolean hasNext() {
        fetch();
        return next!=null;
    }

    private void fetch() {
        while(next==null && core.hasNext())
            next = adapt(core.next());
    }

    public void remove() {
        core.remove();
    }

    /**
     *
     * @return
     *      null to filter out this object. Non-null object will
     *      be returned from the iterator to the caller.
     */
    protected abstract T adapt(U u);
}
