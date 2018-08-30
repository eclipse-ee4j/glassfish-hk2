/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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

import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * This is a poor mans version of a {@link java.lang.ThreadLocal} with
 * the one major upside of a {@link #removeAll()} method that
 * can be used to remove ALL instances of all thread locals on
 * ALL threads from any other thread.
 *
 * @author jwells
 *
 */
public class Hk2ThreadLocal<T> {
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final WriteLock wLock = readWriteLock.writeLock();
    private final ReadLock rLock = readWriteLock.readLock();
    
    private final WeakHashMap<Thread, T> locals = new WeakHashMap<Thread, T>();
    
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
    protected T initialValue() {
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
    public T get() {
        Thread id = Thread.currentThread();
        
        rLock.lock();
        try {
            if (locals.containsKey(id)) {
                return locals.get(id);
            }
        }
        finally {
            rLock.unlock();
        }
        
        // Did not previously get a value, so get it now
        // under write lock
        wLock.lock();
        try {
            if (locals.containsKey(id)) {
                return locals.get(id);
            }
            
            T initialValue = initialValue();
            locals.put(id, initialValue);
            
            return initialValue;
        }
        finally {
            wLock.unlock();
        }
        
    }
    
    /**
     * Sets the current thread's copy of this thread-local variable
     * to the specified value.  Most subclasses will have no need to
     * override this method, relying solely on the {@link #initialValue}
     * method to set the values of thread-locals.
     *
     * @param value the value to be stored in the current thread's copy of
     *        this thread-local.
     */
    public void set(T value) {
        Thread id = Thread.currentThread();
        
        wLock.lock();
        try {
            locals.put(id, value);
        }
        finally {
            wLock.unlock();
        }
        
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
         Thread id = Thread.currentThread();
         
         wLock.lock();
         try {
             locals.remove(id);
         }
         finally {
             wLock.unlock();
         }
         
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
          wLock.lock();
          try {
              locals.clear();
          }
          finally {
              wLock.unlock();
          }
          
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
          rLock.lock();
          try {
              return locals.size();
          }
          finally {
              rLock.unlock();
          }
      }
}
