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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * {@link Iterator} implementation that works like Lisp "flatten" function.
 * @author Kohsuke Kawaguchi
 */
public class FlattenIterator<T> implements Iterator<T> {
    private Iterator<? extends T> current;
    private Iterator<? extends Iterator<? extends T>> source;

    public FlattenIterator(Iterator<? extends Iterator<? extends T>> source) {
        this.source = source;
        if (source.hasNext()) {
            current = source.next();
        } else {
            current = ((List<T>) Collections.emptyList()).iterator();
        }
    }

    public boolean hasNext() {
        while(!current.hasNext() && source.hasNext()) {
            current = source.next();
        }
        return current.hasNext();
    }

    public T next() {
        if(hasNext())
            return current.next();
        else
            throw new NoSuchElementException();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
