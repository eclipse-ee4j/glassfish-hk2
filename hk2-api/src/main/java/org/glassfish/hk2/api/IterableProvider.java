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
import java.lang.reflect.Type;

import javax.inject.Provider;

/**
 * This object can be injected rather than {@link Provider} when
 * it is desired to iterate over more than one returned instance of the type.
 * This interface also has several methods which allow the user to further
 * narrow down the selected services at runtime.  It can also provide
 * an {@link Iterable} of {@link ServiceHandle} for finer control of the lifecycle
 * of services found
 * <p>
 * The iterator returned will be in ranked order (with DescriptorRank as
 * primary key, largest rank first and ServiceID as secondary key, smallest
 * id first)
 * 
 * @author jwells
 * @param <T> The type of this IterableProvider
 */
public interface IterableProvider<T> extends Provider<T>, Iterable<T> {
    /**
     * Rather than getting the service directly with get (in which
     * case the returned service cannot be disposed of) this method
     * will instead return a service handle for the current best service.
     * 
     * @return A ServiceHandle for the service, or null if there is
     * currently no service definition available
     */
    public ServiceHandle<T> getHandle();
    
    /**
     * Returns the size of the iterator that would be returned
     * 
     * @return the size of the iterator that would be chosen
     */
    public int getSize();
    
    /**
     * Returns an IterableProvider that is further qualified
     * with the given name
     * 
     * @param name The value field of the Named annotation parameter.  Must
     * not be null
     * @return An iterable provider further qualified with the given name
     */
    public IterableProvider<T> named(String name);
    
    /**
     * Returns an IterableProvider that is of the given type.  This type
     * must be one of the type safe contracts of the original iterator
     * 
     * @param type The type to restrict the returned iterator to
     * @return An iterator restricted to only providing the given type
     */
    public <U> IterableProvider<U> ofType(Type type);
    
    /**
     * A set of qualifiers to further restrict this iterator to.
     * 
     * @param qualifiers The qualifiers to further restrict this iterator to
     * @return An iterator restricted with the given qualifiers
     */
    public IterableProvider<T> qualifiedWith(Annotation... qualifiers);
    
    /**
     * This version of iterator returns an iterator of ServiceHandles rather
     * than returning the services (which then have no way to be properly
     * destroyed)
     * 
     * @return An iterator of ServiceHandles for the set of services
     * represtended by this IterableProvider
     */
    public Iterable<ServiceHandle<T>> handleIterator();

}
