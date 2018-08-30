/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.api;

/**
 * This exception will be found in a {@link MultiException} when a class
 * has a dependency that should be satisfied but cannot be.  The specific
 * InjectionPoint that could not be satisfied can be found in the exception.
 * @author jwells
 *
 */
public class UnsatisfiedDependencyException extends HK2RuntimeException {
    /**
     * For serialization
     */
    private static final long serialVersionUID = 1191047707346290567L;
    
    private final transient Injectee injectionPoint;

    /**
     * Use this if the injectee is unknown
     */
    public UnsatisfiedDependencyException() {
        this(null);
    }

    /**
     * Constructs the exception with the given injectee
     * 
     * @param injectee The injectee that is Unsatisfied, or null if the injectee is unknown
     */
    public UnsatisfiedDependencyException(Injectee injectee) {
        super("There was no object available for injection at " + ((injectee == null) ? "<null>" : injectee.toString()));
        
        this.injectionPoint = injectee;
    }
    
    /**
     * Returns the injectee that is unsatisfied
     * 
     * @return The injectee that is unsatisfied
     */
    public Injectee getInjectee() {
        return injectionPoint;
    }
}
