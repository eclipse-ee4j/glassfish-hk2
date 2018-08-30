/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
 * This describes the type of descriptor
 * 
 * @author jwells
 */
public enum DescriptorType {
    /**
     * This is a class based descriptor, and so the associated implementation class must have
     * a proper constructor (either a no-argument constructor or one marked with Inject)
     */
    CLASS,
    
    /**
     * This is a description of the contracts, scope and qualifiers on the {@link Factory#provide()} method.
     * For this descriptor the implementation class is the class of the {@link Factory} implementation, but
     * the rest of the descriptor provides information about the {@link Factory#provide()} method and the
     * services it can produce
     */
    PROVIDE_METHOD
}
