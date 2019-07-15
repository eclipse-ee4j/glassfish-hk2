/*
 * Copyright (c) 2015, 2019 Oracle and/or its affiliates. All rights reserved.
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

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is a poor mans version of a {@link java.lang.ThreadLocal} with
 * the one major upside of a {@link #removeAll()} method that
 * can be used to remove ALL instances of all thread locals on
 * ALL threads from any other thread.
 *
 * @author jwells
 * @author Bryan Atsatt
 */
public class Hk2ThreadLocal<V> {
    private static final Object NULL = new Object();

    private final Map<Key, Object> locals = new ConcurrentHashMap<>();
    private final ReferenceQueue<Thread> queue = new ReferenceQueue<>();

    /**
     * Returns the current thread's "initial value" for this
     * thread-local variable.  This method will be invoked the first
     * time a thread accesses the variable with the {@link #get}
     * method, unless the thread previously invoked the {@link #set}
     * method, in which case the <tt>initialValue</tt> method will not
     * be invoked for the thread.  Normally, this method is invoked at
     * most once per thread, but it may be invoked again in case of
     * subsequent invocations of {@link #remove} followed by {@link #get}.
     *
     * <p>This implementation simply returns <tt>null</tt>; if the
     * programmer desires thread-local variables to have an initial
     * value other than <tt>null</tt>, <tt>ThreadLocal</tt> must be
     * subclassed, and this method overridden.  Typically, an
     * anonymous inner class will be used.
     *
     * @return the initial value for this thread-local
     */
    protected V initialValue() {
        return null;
    }

    /**
     * Returns the value in the current thread's copy of this
     * thread-local variable.  If the variable has no value for the
     * current thread, it is first initialized to the value returned
     * by an invocation of the {@link #initialValue} method.
     *
     * @return the current thread's value of this thread-local
     */
    @SuppressWarnings("unchecked")
    public V get() {
        removeStaleEntries();
        final Key key = newLookupKey();
        Object value = locals.get(key);
        if (value == null) {
            value = initialValue();
            locals.put(newStorageKey(queue), maskNull(value));
        } else {
            value = unmaskNull(value);
        }
        return (V) value;
    }

    /**
     * Sets the current thread's copy of this thread-local variable
     * to the specified value.  Most subclasses will have no need to
     * override this method, relying solely on the {@link #initialValue}
     * method to set the values of thread-locals.
     *
     * @param value the value to be stored in the current thread's copy of
     * this thread-local.
     */
    public void set(V value) {
        final Key key = newStorageKey(queue);
        locals.put(key, maskNull(value));
    }

    /**
     * Removes the current thread's value for this thread-local
     * variable.  If this thread-local variable is subsequently
     * {@linkplain #get read} by the current thread, its value will be
     * reinitialized by invoking its {@link #initialValue} method,
     * unless its value is {@linkplain #set set} by the current thread
     * in the interim.  This may result in multiple invocations of the
     * <tt>initialValue</tt> method in the current thread.
     */
    public void remove() {
        final Key key = newLookupKey();
        locals.remove(key);
    }

    /**
     * Removes all threads current thread's value for this thread-local
     * variable.  If this thread-local variable is subsequently
     * {@linkplain #get read} by the current thread, its value will be
     * reinitialized by invoking its {@link #initialValue} method,
     * unless its value is {@linkplain #set set} by the current thread
     * in the interim.  This may result in multiple invocations of the
     * <tt>initialValue</tt> method in the current thread.
     */
    public void removeAll() {
        locals.clear();
    }

    /**
     * Returns the total size of the internal data structure in
     * terms of entries.  This is used for diagnostics purposes
     * only
     *
     * @return The current number of entries across all threads.
     * This is basically the number of threads that currently
     * have data with the Hk2ThreadLocal
     */
    public int getSize() {
        removeStaleEntries();
        return locals.size();
    }

    /**
     * Removes from the map any key that has been enqueued by the garbage
     * collector.
     */
    private void removeStaleEntries() {
        for (Object queued; (queued = queue.poll()) != null; ) {
            final Key key = (Key) queued;
            locals.remove(key);
        }
    }

    /**
     * Replace a {@code null} value with a sentinel that can be stored in the map.
     *
     * @param value The value.
     * @return The value or sentinel.
     */
    private static Object maskNull(Object value) {
        return (value == null) ? NULL : value;
    }

    /**
     * Replace a sentinel with {@code null}.
     *
     * @param value The value or sentinel.
     * @return The value or {@code null}.
     */
    private static Object unmaskNull(Object value) {
        return (value == NULL) ? null : value;
    }

    /**
     * Create a key for the current thread that will be enqueued unless cleared.
     * All keys actually stored in the map must be created by this method and must
     * not be explicitly cleared.
     *
     * @param queue The reference queue.
     * @return The key.
     */
    private static Key newStorageKey(ReferenceQueue<Thread> queue) {
        return new Key(Thread.currentThread(), queue);
    }

    /**
     * Create a key for the current thread that will not be enqueued. Using this
     * method (or clearing a 'storage' key) ensures that no extra work is required
     * in {@link #removeStaleEntries()}.
     *
     * @return The key.
     */
    private static Key newLookupKey() {
        return new Key(Thread.currentThread(), null);
    }

    /**
     * A weakly referenced thread suitable as a map key.
     */
    private static class Key extends WeakReference<Thread> {
        private final long threadId;
        private final int hash;

        private Key(Thread thread, ReferenceQueue<Thread> queue) {
            super(thread, queue);
            this.threadId = thread.getId();
            this.hash = thread.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Key)) {
                return false;
            }
            final Key other = (Key) obj;
            return other.threadId == threadId;
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
