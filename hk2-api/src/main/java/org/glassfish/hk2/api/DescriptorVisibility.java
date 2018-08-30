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
 * This enumeration determines how visibility of a descriptor
 * 
 * @author jwells
 *
 */
public enum DescriptorVisibility {
    /**
     * This descriptor may be seen by all the children service locators
     * of the service locator in which this descriptor is bound
     */
    NORMAL,
    
    /**
     * This descriptor may only be seen by the service locator in
     * which it is bound, and not by any of that service locators
     * children
     */
    LOCAL

}
