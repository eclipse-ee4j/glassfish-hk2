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

package org.jvnet.hk2.internal;

import java.util.List;

import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceHandle;

/**
 * An internal interface that allows us to have the
 * factory and class implementations
 * 
 * @author jwells
 *
 */
public interface Creator<T> {
    /**
     * Returns all the injectees needed prior
     * to creating this object
     * 
     * @return a List of all the injectees
     */
    public List<Injectee> getInjectees();
    
    /**
     * Creates an instance of the given type
     * 
     * @return an instance of the given type
     * @throws MultiException if the creator threw an exception during construction
     */
    public T create(ServiceHandle<?> root, SystemDescriptor<?> eventThrower) throws MultiException;
    
    /**
     * Disposes the given instance
     * 
     * @param instance removes the given instance
     * @throws MultiException if the underlying creator threw an exception during destruction
     */
    public void dispose(T instance) throws MultiException;
}
