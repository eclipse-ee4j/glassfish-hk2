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

package org.glassfish.hk2.utilities.cache;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Cache implementation that relies on FutureTask.
 * Desired value will only be computed once and computed value stored in the cache.
 * The implementation is based on an example from the "Java Concurrency in Practice" book
 * authored by Brian Goetz and company.
 *
 * @author Jakub Podlesak (jakub.podlesak at oracle.com)
 * @param <K> The type of the key of the cache
 * @param <V> The type of the values in the cache
 */
public class Cache<K,V> implements Computable<K,V> {

    /**
     * Should a cycle be detected during computation of a value
     * for given key, this interface allows client code to register
     * a callback that would get invoked in such a case.
     *
     * @param <K> Key type.
     */
    public interface CycleHandler<K> {

        /**
         * Handle cycle that was detected while computing a cache value
         * for given key. This method would typically just throw a runtime exception.
         *
         * @param key instance that caused the cycle.
         */
        public void handleCycle(K key);
    }

    private final CycleHandler<K> cycleHandler;

    /**
     * Helper class, that remembers the future task origin thread, so that cycles could be detected.
     * If any thread starts computation for given key and the same thread requests the computed value
     * before the computation stops, a cycle is detected and registered cycle handler is called.
     */
    private class OriginThreadAwareFuture implements Future<V>{
        private volatile long threadId;
        private final FutureTask<V> future;

        OriginThreadAwareFuture(Cache<K, V> cache, final K key) {
            this.threadId = Thread.currentThread().getId();
            Callable<V> eval = new Callable<V>() {
                @Override
                public V call() throws Exception {
                    try {
                        return computable.compute(key);
                    } finally {
                        threadId = -1;
                    }
                }
            };
            this.future = new FutureTask<V>(eval);
        }

        @Override
        public int hashCode() {
            return future.hashCode();
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            
            final OriginThreadAwareFuture other = (OriginThreadAwareFuture) obj;
            if (this.future != other.future && (this.future == null || !this.future.equals(other.future))) {
                return false;
            }
            return true;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return future.cancel(mayInterruptIfRunning);
        }

        @Override
        public boolean isCancelled() {
            return future.isCancelled();
        }

        @Override
        public boolean isDone() {
            return future.isDone();
        }

        @Override
        public V get() throws InterruptedException, ExecutionException {
            return future.get();
        }

        @Override
        public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return future.get(timeout, unit);
        }

        public void run() {
            future.run();
        }
    }
    
    private static final CycleHandler<Object> EMPTY_HANDLER = new CycleHandler<Object>() {
        @Override
        public void handleCycle(Object key) {
        }
    };

    private final ConcurrentHashMap<K, OriginThreadAwareFuture> cache = new ConcurrentHashMap<K, OriginThreadAwareFuture>();
    private final Computable<K, V> computable;

    /**
     * Create new cache with given computable to compute values.
     * Detected cycles will be ignored as there is a no-op cycle handler registered by default.
     *
     * @param computable
     */
    @SuppressWarnings("unchecked")
    public Cache(Computable<K, V> computable) {
        this(computable, (CycleHandler<K>) EMPTY_HANDLER);
    }

    /**
     * Create new cache with given computable and cycle handler.
     *
     * @param computable
     * @param cycleHandler
     */
    public Cache(Computable<K, V> computable, CycleHandler<K> cycleHandler) {
        this.computable = computable;
        this.cycleHandler = cycleHandler;
    }

    @Override
    public V compute(final K key) {
        while (true) {
            OriginThreadAwareFuture f = cache.get(key);
            if (f == null) {
                OriginThreadAwareFuture ft = new OriginThreadAwareFuture(this, key);

                f = cache.putIfAbsent(key, ft);
                if (f == null) {
                    f = ft;
                    ft.run();
                }
            } else {
                final long tid = f.threadId;
                
                if ((tid != -1) && (Thread.currentThread().getId() == f.threadId)) {
                    cycleHandler.handleCycle(key);
                }
            }
            try {
                return f.get();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            } catch (ExecutionException ex) {
                cache.remove(key);  // otherwise the exception would be remembered
                Throwable cause = ex.getCause();
                if (cause == null) {
                    throw new RuntimeException(ex);
                }

                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                }

                throw new RuntimeException(cause);
            }
        }
    }

    /**
     * Empty cache.
     */
    public void clear() {
        cache.clear();
    }

    /**
     * Returns true if the key has already been cached.
     *
     * @param key
     * @return true if given key is present in the cache.
     */
    public boolean containsKey(final K key) {
        return cache.containsKey(key);
    }

    /**
     * Remove item from the cache.
     *
     * @param key item key.
     */
    public void remove(final K key) {
        cache.remove(key);
    }
    
    /**
     * Returns the size of the cache
     * @return The number of elements in the cache
     */
    public int size() {
        return cache.size();
    }
}
