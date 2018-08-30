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

package org.glassfish.hk2.api;

/**
 * This is used to describe the values to be given to
 * the method in the {@link ServiceLocator#assistedInject(Object, java.lang.reflect.Method, MethodParameter...)}
 * method
 * 
 * @author jwells
 *
 */
public interface MethodParameter {
    /**
     * Returns the index of the parameter for which
     * the {@link #getParameterValue()} result should
     * go
     * 
     * @return The index of the parameter in the
     * method where the parameter value should go
     */
    public int getParameterPosition();
    
    /**
     * The value that should be given to the
     * method at the parameter position specified
     * by {@link #getParameterPosition()}
     * 
     * @return The possibly null parameter value
     * that should be passed to the method at the
     * given index
     */
    public Object getParameterValue();

}
