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

package org.glassfish.hk2.configuration.api;

import org.glassfish.hk2.api.ServiceHandle;

/**
 * This interface can be used in conjunction with the
 * {@link ChildInject} annotation to get the current
 * list of children for this injection point
 * 
 * @author jwells
 *
 */
public interface ChildIterable<T> extends Iterable<T> {
    /**
     * Gets the child with the given key.  The separator
     * used to determine the full suffix to look for in
     * the child is given by the {@link ChildInject#separator()}
     * field
     * 
     * @param key The non-null key of the child to get
     * @return The child who has the given key
     */
    T byKey(String key);
    
    /**
     * Returns an iterator of the children's Service
     * Handle, rather than their services
     * 
     * @return the iterator
     */
    Iterable<ServiceHandle<T>> handleIterator();
    
    

}
