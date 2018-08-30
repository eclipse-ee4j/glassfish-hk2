/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.utilities;

import org.glassfish.hk2.api.MethodParameter;

/**
 * An implementation of {@link MethodParameter} that
 * has immutable position and value
 * 
 * @author jwells
 */
public class MethodParameterImpl implements MethodParameter {
    private final int index;
    private final Object value;
    
    public MethodParameterImpl(int index, Object value) {
        this.index = index;
        this.value = value;
    }

    @Override
    public int getParameterPosition() {
        return index;
    }

    @Override
    public Object getParameterValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return "MethodParamterImpl(" + index + "," + value + "," + System.identityHashCode(this) + ")";
    }
}
