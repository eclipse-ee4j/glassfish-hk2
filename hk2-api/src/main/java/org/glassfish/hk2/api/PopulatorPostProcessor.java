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

package org.glassfish.hk2.api;

import org.glassfish.hk2.utilities.DescriptorImpl;

/**
 * This interface allows the customization of services read in from
 * an external source.  For example, specific classloaders can be added,
 * or duplicate descriptors can be removed.
 * 
 * @author jwells
 *
 */
public interface PopulatorPostProcessor {
    /**
     * This method can be used to alter the descriptor read in.  It can also
     * add descriptors, or remove the descriptor (by returning null).
     * Any alterations made to the descriptor passed in will remain in effect.
     *
     * @param serviceLocator the ServiceLocator being populated.  Will not be null
     * @param descriptorImpl The descriptorImpl read from some external source.  This
     * processor can modify this descriptor fully
     * 
     * @return The descriptor to be added to the system.  If this returns null
     * then the descriptorImpl passed in will NOT be added to the system.  Implementations
     * may return the descriptor passed in, but do not have to.  The descriptor added to
     * the system will be the one returned from this method
     */
     public DescriptorImpl process(ServiceLocator serviceLocator, DescriptorImpl descriptorImpl);

}
