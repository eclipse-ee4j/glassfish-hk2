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

import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * We need a compound enumeration so that we can aggregate the results from
 * various delegates.
 *
 * @author Sanjeeb.Sahoo@Sun.COM
 */
public class CompositeEnumeration implements Enumeration<URL> {
    /*
     * TODO(Sahoo): Merge with FlattenIterator.
     */

    Enumeration<URL>[] enumerators;
    int index = 0; // current position, lazily initialized

    public CompositeEnumeration(List<Enumeration<URL>> enumerators) {
        this.enumerators = enumerators.toArray(new Enumeration[enumerators.size()]);
    }

    public boolean hasMoreElements() {
        Enumeration<URL> current = getCurrent();
        return (current!=null) ? true : false;
    }

    public URL nextElement() {
        Enumeration<URL> current = getCurrent();
        if (current != null) {
            return current.nextElement();
        } else {
            throw new NoSuchElementException("No more elements in this enumeration");
        }
    }

    private Enumeration<URL> getCurrent() {
        for (int start = index; start < enumerators.length; start++) {
            Enumeration<URL> e = enumerators[start];
            if (e.hasMoreElements()) {
                index = start;
                return e;
            }
        }
        // no one has any elements, set the index to max and return null
        index = enumerators.length;
        return null;
    }
}
