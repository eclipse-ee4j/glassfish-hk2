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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.utilities.InjecteeImpl;
import org.glassfish.hk2.api.IterableProvider;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.Unqualified;
import org.glassfish.hk2.utilities.NamedImpl;
import org.glassfish.hk2.utilities.reflection.Pretty;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;

/**
 * @author jwells
 * 
 * @param <T> The type for this provider
 */
public class IterableProviderImpl<T> implements IterableProvider<T> {
    private final ServiceLocatorImpl locator;
    private final Type requiredType;
    private final Set<Annotation> requiredQualifiers;
    private final Unqualified unqualified;
    private final Injectee originalInjectee;
    private final boolean isIterable;
    
    /* package */ IterableProviderImpl(
            ServiceLocatorImpl locator,
            Type requiredType,
            Set<Annotation> requiredQualifiers,
            Unqualified unqualified,
            Injectee originalInjectee,
            boolean isIterable) {
        this.locator = locator;
        this.requiredType = requiredType;
        this.requiredQualifiers = Collections.unmodifiableSet(requiredQualifiers);
        this.unqualified = unqualified;
        this.originalInjectee = originalInjectee;
        this.isIterable = isIterable;
    }
    
    private void justInTime() {
        InjecteeImpl injectee = new InjecteeImpl(originalInjectee);
        injectee.setRequiredType(requiredType);
        injectee.setRequiredQualifiers(requiredQualifiers);
        if (unqualified != null) {
            injectee.setUnqualified(unqualified);
        }
        
        // This does nothing more than run the JIT resolvers
        locator.getInjecteeDescriptor(injectee);
    }

    /* (non-Javadoc)
     * @see javax.inject.Provider#get()
     */
    @SuppressWarnings("unchecked")
    @Override
    public T get() {
        justInTime();
        
        // Must do this in this way to ensure that the generated item is properly associated with the root
        return (T) locator.getUnqualifiedService(requiredType, unqualified,
                isIterable, requiredQualifiers.toArray(new Annotation[requiredQualifiers.size()]));
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.IterableProvider#getHandle()
     */
    @SuppressWarnings("unchecked")
    @Override
    public ServiceHandle<T> getHandle() {
        justInTime();
        
        return (ServiceHandle<T>) locator.getUnqualifiedServiceHandle(requiredType, unqualified,
                isIterable, requiredQualifiers.toArray(new Annotation[requiredQualifiers.size()]));
    }
    

    /* (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<T> iterator() {
        justInTime();
        
        List<ServiceHandle<T>> handles;
        handles = ReflectionHelper.<List<ServiceHandle<T>>>cast(locator.getAllUnqualifiedServiceHandles(requiredType,
                    unqualified, isIterable, requiredQualifiers.toArray(new Annotation[requiredQualifiers.size()])));
        
        return new MyIterator<T>(handles);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.IterableProvider#getSize()
     */
    @Override
    public int getSize() {
        justInTime();
        
        return locator.getAllUnqualifiedServiceHandles(requiredType, unqualified, isIterable,
                requiredQualifiers.toArray(new Annotation[requiredQualifiers.size()])).size();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.IterableProvider#named(java.lang.String)
     */
    @Override
    public IterableProvider<T> named(String name) {
        return qualifiedWith(new NamedImpl(name));
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.IterableProvider#ofType(java.lang.reflect.Type)
     */
    @Override
    public <U> IterableProvider<U> ofType(Type type) {
        return new IterableProviderImpl<U>(locator, type, requiredQualifiers, unqualified, originalInjectee, isIterable);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.IterableProvider#qualifiedWith(java.lang.annotation.Annotation[])
     */
    @Override
    public IterableProvider<T> qualifiedWith(Annotation... qualifiers) {
        HashSet<Annotation> moreAnnotations = new HashSet<Annotation>(requiredQualifiers);
        for (Annotation qualifier : qualifiers) {
            moreAnnotations.add(qualifier);
        }
        
        return new IterableProviderImpl<T>(locator, requiredType, moreAnnotations, unqualified, originalInjectee, isIterable);
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.IterableProvider#handleIterator()
     */
    @Override
    public Iterable<ServiceHandle<T>> handleIterator() {
        justInTime();
        
        List<ServiceHandle<T>> handles = ReflectionHelper.<List<ServiceHandle<T>>>cast(locator.getAllServiceHandles(requiredType,
                requiredQualifiers.toArray(new Annotation[requiredQualifiers.size()])));
        
        return new HandleIterable<T>(handles);
    }
    
    private static class MyIterator<U> implements Iterator<U> {
        private final LinkedList<ServiceHandle<U>> handles;
        
        private MyIterator(List<ServiceHandle<U>> handles) {
            this.handles = new LinkedList<ServiceHandle<U>>(handles);
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        @Override
        public boolean hasNext() {
            return !handles.isEmpty();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        @Override
        public U next() {
            if (handles.isEmpty()) throw new NoSuchElementException();
            
            ServiceHandle<U> nextHandle = handles.removeFirst();
            
            return nextHandle.getService();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
            
        }
        
    }
    
    private static class HandleIterable<U> implements Iterable<ServiceHandle<U>> {
        private final List<ServiceHandle<U>> handles;
        
        private HandleIterable(List<ServiceHandle<U>> handles) {
            this.handles = new LinkedList<ServiceHandle<U>>(handles);
        }

        /* (non-Javadoc)
         * @see java.lang.Iterable#iterator()
         */
        @Override
        public Iterator<ServiceHandle<U>> iterator() {
            return new MyHandleIterator<U>(handles);
        }
        
    }
    
    private static class MyHandleIterator<U> implements Iterator<ServiceHandle<U>> {
        private final LinkedList<ServiceHandle<U>> handles;
        
        private MyHandleIterator(List<ServiceHandle<U>> handles) {
            this.handles = new LinkedList<ServiceHandle<U>>(handles);
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        @Override
        public boolean hasNext() {
            return !handles.isEmpty();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        @Override
        public ServiceHandle<U> next() {
            return handles.removeFirst();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
            
        }
        
    }

    public String toString() {
        return "IterableProviderImpl(" + Pretty.type(requiredType) + "," + Pretty.collection(requiredQualifiers) + "," +
            System.identityHashCode(this) + ")";
    }

    

    

}
