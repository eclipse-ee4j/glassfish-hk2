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

import java.lang.annotation.Annotation;

import org.jvnet.hk2.annotations.Contract;

/**
 * An implementation of this must be put into the system in order to
 * create contextual instances of services.  If there is more than
 * one active implementation available for the same scope on the same
 * thread a runtime exception will be thrown when the scope is accessed.
 * <p>
 * An implementation of Context must be in the Singleton scope
 * 
 * @author jwells
 * @param <T> This must be the type for which this is a context.  For example,
 * if your scope is SecureScope, then your context must implement Context&lt;SecureScope&gt;
 *
 */
@Contract
public interface Context<T> {
    /**
     * The scope for which this is the context
     * 
     * @return may not return null, must return the
     * scope for which this is a context
     */
    public Class<? extends Annotation> getScope();
    
    /**
     * Creates a contextual instance of this ActiveDescriptor by calling its
     * create method if there is no other matching contextual instance.  If there
     * is already a contextual instance it is returned.  If parent is null then this
     * must work like the find call
     * 
     * @param activeDescriptor The descriptor to use when creating instances
     * @param root The extended provider for the outermost parent being created
     * 
     * @return A context instance.  This value may NOT be null
     */
    public <U> U findOrCreate(ActiveDescriptor<U> activeDescriptor, ServiceHandle<?> root);
    
    /**
     * Determines if this context has a value for the given key
     * 
     * @param descriptor The descriptor to look for in this context
     * @return true if this context has a value associated with this descriptor
     */
    public boolean containsKey(ActiveDescriptor<?> descriptor);
    
    /**
     * This method is called when {@link ServiceHandle#destroy()} method is called.
     * It is up to the context implementation whether or not to honor this destruction
     * request based on the lifecycle requirements of the context
     * 
     * @param descriptor A non-null descriptor upon which {@link ServiceHandle#destroy()}
     * has been called
     */
    public void destroyOne(ActiveDescriptor<?> descriptor);
    
    /**
     * Returns true if the findOrCreate method can return null
     * 
     * @return true if null is a legal value from the findOrCreate method
     */
    public boolean supportsNullCreation();
    
    /**
     * True if this context is active, false otherwise
     * 
     * @return true if this context is active, false otherwise
     */
    public boolean isActive();

    /**
     * Shut down this context.
     */
    public void shutdown();
}
