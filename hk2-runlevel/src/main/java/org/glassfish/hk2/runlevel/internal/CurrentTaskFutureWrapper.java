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

package org.glassfish.hk2.runlevel.internal;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.glassfish.hk2.runlevel.RunLevelFuture;

/**
 * This object is used to wrap the internal CurrentTaskFuture which
 * is a ChangeableRunLevelFuture.  This way the users of the
 * RunLevelController API will not get something that can be
 * cast to a ChangeableRunLevelFuture
 * 
 * @author jwells
 */
public class CurrentTaskFutureWrapper implements RunLevelFuture {
    private final CurrentTaskFuture delegate;
    
    /* package */ CurrentTaskFutureWrapper(CurrentTaskFuture delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean isCancelled() {
        return delegate.isCancelled();
    }

    @Override
    public boolean isDone() {
        return delegate.isDone();
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        return delegate.get();
    }

    @Override
    public Object get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException,
            TimeoutException {
        return delegate.get(timeout, unit);
    }

    @Override
    public int getProposedLevel() {
        return delegate.getProposedLevel();
    }

    @Override
    public boolean isUp() {
        return delegate.isUp();
    }

    @Override
    public boolean isDown() {
        return delegate.isDown();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return delegate.cancel(mayInterruptIfRunning);
    }
    
    /* package */ CurrentTaskFuture getDelegate() {
        return delegate;
    }
    
    @Override
    public String toString() {
        return "CurrentTaskFutureWrapper(" + delegate.toString() + "," + System.identityHashCode(this ) + ")";
    }

}
