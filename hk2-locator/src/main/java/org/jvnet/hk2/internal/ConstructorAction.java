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

package org.jvnet.hk2.internal;

import java.lang.reflect.Constructor;

/**
 * This represents the action used in order to
 * create an object.
 * 
 * It currently has two uses, one for raw creation
 * and one for proxied creation (if there are method
 * interceptors)
 * 
 * @author jwells
 *
 */
public interface ConstructorAction {
    /**
     * Creates the raw object
     * @param c The constructor to call
     * @param args The parameters to give to the argument
     * @param neutralCCL Whether or not the CCL should remain neutral
     * 
     * @return The raw object return
     * @throws Throwable 
     */
    public Object makeMe(Constructor<?> c, Object args[], boolean neutralCCL) throws Throwable;
}
