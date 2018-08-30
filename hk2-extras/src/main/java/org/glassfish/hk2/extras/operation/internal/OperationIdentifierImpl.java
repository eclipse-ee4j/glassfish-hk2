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

package org.glassfish.hk2.extras.operation.internal;

import java.lang.annotation.Annotation;

import org.glassfish.hk2.extras.operation.OperationIdentifier;

/**
 * @author jwells
 *
 */
public class OperationIdentifierImpl<T extends Annotation> implements OperationIdentifier<T> {
    private final String identifier;
    private final T scope;
    private final int hashCode;
    
    /* package */ OperationIdentifierImpl(String identifier, T scope) {
        this.identifier = identifier;
        this.scope = scope;
        this.hashCode = identifier.hashCode();
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.extras.operation.OperationIdentifier#getOperationIdentifier()
     */
    @Override
    public String getOperationIdentifier() {
        return identifier;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.extras.operation.OperationIdentifier#getOperationScope()
     */
    @Override
    public T getOperationScope() {
        return scope;
    }
    
    @Override
    public int hashCode() {
        return hashCode;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof OperationIdentifierImpl)) return false;
        
        return identifier.equals(((OperationIdentifierImpl<T>) o).identifier);
    }

    @Override
    public String toString() {
        return identifier;
    }
}
