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

/**
 * Utility interface to capture generic computation of type V from type K.
 * Used in {@link Cache}.
 *
 * @author Jakub Podlesak (jakub.podlesak @ oracle.com)
 */
public interface Computable<K, V> {

    /**
     * Defines an expensive computation to retrieve value V from key K.
     *
     * @param key input data.
     * @return output from the computation.
     * @throws ComputationErrorException if the computation performed should
     * be returned by the cache but should not be kept in the cache associated
     * with the key
     */
    public V compute(K key) throws ComputationErrorException;
}
