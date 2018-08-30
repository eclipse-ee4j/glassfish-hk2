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
 * This filter can be used to limit the set of Descriptors
 * passed to the matches method.
 * 
 * If both methods return non-null then only those fields that
 * are both named with the returned name AND have the given
 * advertised contract will be given to the matches method
 * 
 * @author jwells
 *
 */
public interface IndexedFilter extends Filter {
    /**
     * If this returns non-null then this index will
     * be used to limit the set of Descriptors that
     * will be passed to the matches method.  Only
     * those descriptors that have an AdverisedContract
     * of this value will be passed to the matches method.
     * 
     * @return If non null this will limit the descriptors
     * passed to the matches method to those that have this
     * contract in their set of advertised contracts
     */
    public String getAdvertisedContract();
    
    /**
     * If this returns non-null then this name will
     * be used to limit the set of Descriptors that
     * will be passed to the matches method.  Only
     * those descriptors that has an name
     * of this value will be passed to the matches method.
     * 
     * @return If non null this will limit the descriptors
     * passed to the matches method to those that have name
     */
    public String getName();

}
