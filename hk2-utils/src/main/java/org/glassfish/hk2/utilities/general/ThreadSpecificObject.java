/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.utilities.general;

/**
 * This ties the given object with the thread upon
 * which this object is created
 * 
 * This class can be used as the key in a hashSet if the
 * incoming object can be used as the key in a hashSet
 * @author jwells
 *
 */
public class ThreadSpecificObject<T> {
    private final T incoming;
    private final long tid;
    private final int hash;
    
    public ThreadSpecificObject(T incoming) {
        this.incoming = incoming;
        this.tid = Thread.currentThread().getId();
        
        int hash = (incoming == null) ? 0 : incoming.hashCode();
        hash ^= Long.valueOf(tid).hashCode();
        
        this.hash = hash;
    }
    
    /**
     * Gets the thread on which this object was created
     * @return The thread on which this object was created
     */
    public long getThreadIdentifier() {
        return tid;
    }
    
    /**
     * Gets the incoming object bound to the thread id
     * @return The incoming object bound to the thread id
     */
    public T getIncomingObject() {
        return incoming;
    }
    
    @Override
    public int hashCode() {
        return hash;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof ThreadSpecificObject)) return false;
        ThreadSpecificObject other = (ThreadSpecificObject) o;
        
        if (tid != other.tid) return false;
        return GeneralUtilities.safeEquals(incoming, other.incoming);
    }
}
