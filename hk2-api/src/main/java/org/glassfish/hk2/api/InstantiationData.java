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

package org.glassfish.hk2.api;

/**
 * Contains information about the caller of a
 * {@link Factory#provide()} method
 * 
 * @author jwells
 *
 */
public interface InstantiationData {
    /**
     * Returns the {@link Injectee} of the service that
     * is being instantiated with this {@link Factory#provide()}
     * method
     * 
     * @return the {@link Injectee} of the service that
     * is being instantiated with this {@link Factory#provide()}
     * method, or null if the {@link Injectee} is unknown or
     * this is from a lookup operation
     */
    public Injectee getParentInjectee();

}
