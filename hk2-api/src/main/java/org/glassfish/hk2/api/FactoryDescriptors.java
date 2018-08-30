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
 * This is a convenience class that links together the factory descriptor as a factory
 * for another type and the factory as a service itself.  It is not required to use
 * this helper to register a factory, as the individual descriptors can be registered
 * with the system independently.
 * 
 * @author jwells
 *
 */
public interface FactoryDescriptors {
    /**
     * This returns the factory as a service itself.  The advertised
     * contracts must contain the implementation class of the factory and
     * the {@link Factory}.  The descriptor type must be {@link DescriptorType#CLASS}
     * since this descriptor is describing the factory itself.
     * 
     * @return The factory as a service itself
     */
    public Descriptor getFactoryAsAService();
    
    /**
     * This returns the factory as a factory for some other type.  The
     * implementation class should contain the implementation class
     * of the factory service.  If the implementation class returned from
     * this does not match the implementation class returned from getFactoryAsAService
     * an error will occur.  The contracts, name and qualifiers should represent
     * the type returned from the provide method of the factory.  The descriptor
     * type must be {@link DescriptorType#PROVIDE_METHOD} since this descriptor is
     * describing the factory as a factory, not as a service.
     * 
     * @return The factory descriptor as a factory
     */
    public Descriptor getFactoryAsAFactory();

}
