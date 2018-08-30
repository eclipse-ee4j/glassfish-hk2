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

package org.glassfish.hk2.utilities;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ServiceHandle;

/**
 * Used by several contexts for keeping the inputs of
 * {@link org.glassfish.hk2.api.Context#findOrCreate(ActiveDescriptor, ServiceHandle)}.
 * May be used as the key in a HashMap, where the criteria for equality
 * is the equality of the Descriptor
 * 
 * @author jwells
 */
public class ContextualInput<T> {
    private final ActiveDescriptor<T> descriptor;
    private final ServiceHandle<?> root;
    
    /**
     * The inputs from the {@link org.glassfish.hk2.api.Context#findOrCreate(ActiveDescriptor, ServiceHandle)}
     * method
     * 
     * @param descriptor The non-null descriptor associated with a contextual creation
     * @param root The possibly null root associated with a contextual creation
     */
    public ContextualInput(ActiveDescriptor<T> descriptor, ServiceHandle<?> root) {
        this.descriptor = descriptor;
        this.root = root;
    }
    
    /**
     * Returns the descriptor associated with this contextual creation
     * @return The non-null descriptor associated with this creation
     */
    public ActiveDescriptor<T> getDescriptor() {
        return descriptor;
    }
    
    /**
     * Returns the {@link ServiceHandle} root associated with this
     * contextual creation
     * 
     * @return The possibly null root associated with this creation
     */
    public ServiceHandle<?> getRoot() {
        return root;
    }
    
    @Override
    public int hashCode() {
        return descriptor.hashCode();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof ContextualInput)) return false;
        
        ContextualInput<T> other = (ContextualInput<T>) o;
        
        return descriptor.equals(other.descriptor);
    }
    
    @Override
    public String toString() {
        return "ContextualInput(" + descriptor.getImplementation() + "," + root + "," + System.identityHashCode(this) + ")";
    }

}
