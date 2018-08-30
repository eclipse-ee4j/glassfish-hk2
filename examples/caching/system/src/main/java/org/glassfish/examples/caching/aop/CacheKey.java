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

package org.glassfish.examples.caching.aop;

/**
 * This is an immutable object that can be used
 * as a key in our method and constructor caches
 * 
 * @author jwells
 *
 */
public class CacheKey {
    private final String className;
    private final String methodName;
    private final Object input;
    
    /**
     * None of these values can be null
     * 
     * @param className The name of the class
     * @param methodName The name of the method or &lt;init&gt; for a constructor
     * @param input The input parameter of the constructor or method
     */
    public CacheKey(String className, String methodName, Object input) {
        this.className = className;
        this.methodName = methodName;
        this.input = input;
    }
    
    @Override
    public int hashCode() {
        return className.hashCode() ^ methodName.hashCode() ^ input.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof CacheKey)) return false;
        CacheKey other = (CacheKey) o;
        
        if (!other.className.equals(className)) return false;
        if (!other.methodName.equals(methodName)) return false;
        return other.input.equals(input);
    }

}
