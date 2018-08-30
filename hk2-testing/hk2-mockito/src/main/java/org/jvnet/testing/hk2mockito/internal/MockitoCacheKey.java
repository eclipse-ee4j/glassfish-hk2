/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.jvnet.testing.hk2mockito.internal;

import java.lang.reflect.Type;

/**
 * Cache key for spies created.
 *
 * @author Sharmarke Aden
 */
public class MockitoCacheKey {

    private final Type type;
    private final Object value;

    public MockitoCacheKey(Type type, Object value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 83 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MockitoCacheKey other = (MockitoCacheKey) obj;
        if (this.type != other.type && (this.type == null || !this.type.equals(other.type))) {
            return false;
        }
        return this.value == other.value || (this.value != null && this.value.equals(other.value));
    }

    @Override
    public String toString() {
        return "SpyCacheKey{" + "type=" + type + ", value=" + value + '}';
    }

}
