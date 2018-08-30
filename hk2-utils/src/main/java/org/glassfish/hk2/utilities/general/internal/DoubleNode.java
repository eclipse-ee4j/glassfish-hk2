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

package org.glassfish.hk2.utilities.general.internal;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * Used for doubly linked lists with weak keys
 * 
 * @author jwells
 *
 * @param <K> key
 * @param <V> value
 */
public class DoubleNode<K, V> {
    private final WeakReference<K> weakKey;
    private final V value;
    private DoubleNode<K, V> previous;
    private DoubleNode<K, V> next;
    private K hardenedKey;
    
    public DoubleNode(K key, V value, ReferenceQueue<? super K> queue) {
        weakKey = new WeakReference<K>(key, queue);
        this.value = value;
    }

    /**
     * @return the previous
     */
    public DoubleNode<K, V> getPrevious() {
        return previous;
    }

    /**
     * @param previous the previous to set
     */
    public void setPrevious(DoubleNode<K, V> previous) {
        this.previous = previous;
    }

    /**
     * @return the next
     */
    public DoubleNode<K, V> getNext() {
        return next;
    }

    /**
     * @param next the next to set
     */
    public void setNext(DoubleNode<K, V> next) {
        this.next = next;
    }

    /**
     * @return the weakKey
     */
    public WeakReference<K> getWeakKey() {
        return weakKey;
    }

    /**
     * @return the value
     */
    public V getValue() {
        return value;
    }

    /**
     * @return the hardenedKey
     */
    public K getHardenedKey() {
        return hardenedKey;
    }

    /**
     * @param hardenedKey the hardenedKey to set
     */
    public void setHardenedKey(K hardenedKey) {
        this.hardenedKey = hardenedKey;
    }
    
    
    
    
}
