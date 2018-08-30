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

package org.glassfish.hk2.configuration.internal;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.configuration.api.ChildIterable;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;

/**
 * @author jwells
 *
 */
public class ChildIterableImpl<T> implements ChildIterable<T> {
    private final ServiceLocator locator;
    private final Type childType;
    private final String prefix;
    private final String separator;
    
    private final ChildFilter baseFilter;
    
    /* package */ ChildIterableImpl(ServiceLocator locator, Type childType, String prefix, String separator) {
        this.locator = locator;
        this.childType = childType;
        this.prefix = prefix;
        this.separator = separator;
        
        baseFilter = new ChildFilter(childType, prefix);
    }

    /* (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Iterator<T> iterator() {
        List<?> matches = locator.getAllServices(baseFilter);
        List<T> tMatches = (List<T>) matches;
        
        return tMatches.iterator();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.api.ChildIterable#byKey(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public T byKey(String key) {
        if (key == null) throw new IllegalArgumentException();
        
        ChildFilter filter = new ChildFilter(childType, prefix, separator + key);
        
        ActiveDescriptor<?> result = locator.getBestDescriptor(filter);
        if (result == null) return null;
        
        ServiceHandle<?> handle = locator.getServiceHandle(result);
        return (T) handle.getService();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.api.ChildIterable#handleIterator()
     */
    @Override
    public Iterable<ServiceHandle<T>> handleIterator() {
        List<ServiceHandle<?>> matches = locator.getAllServiceHandles(baseFilter);
        final List<ServiceHandle<T>> tMatches = ReflectionHelper.<List<ServiceHandle<T>>>cast(matches);
        
        return new Iterable<ServiceHandle<T>>() {

            @Override
            public Iterator<ServiceHandle<T>> iterator() {
                return tMatches.iterator();
            }
            
        };
    }

}
