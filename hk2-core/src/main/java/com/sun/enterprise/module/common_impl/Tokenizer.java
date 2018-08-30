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

import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.Collections;

/**
 * JDK5-friendly string tokenizer.
 * 
 * @author Kohsuke Kawaguchi
 */
public final class Tokenizer implements Iterable<String> {
    private final String text;
    private final String delimiter;

    /**
     * @param data
     *      Text to be tokenized. Can be null, in which case
     *      the iterator will return nothing.
     * @param delimiter
     *      Passed as a delimiter to {@link StringTokenizer#StringTokenizer(String, String)},.
     */
    public Tokenizer(String data, String delimiter) {
        this.text = data;
        this.delimiter = delimiter;
    }


    public Iterator<String> iterator() {
        if(text==null)
            return Collections.<String>emptyList().iterator();

        return new Iterator<String>() {
            private final StringTokenizer tokens = new StringTokenizer(text,delimiter);

            public boolean hasNext() {
                return tokens.hasMoreTokens();
            }

            public String next() {
                return tokens.nextToken().trim();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
