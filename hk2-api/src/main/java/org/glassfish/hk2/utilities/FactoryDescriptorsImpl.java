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

package org.glassfish.hk2.utilities;

import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.FactoryDescriptors;

/**
 * This is an implementation of FactoryDescriptors that can be
 * used by hk2 uses when creating descriptors that describe
 * a {@link Factory}
 * 
 * @author jwells
 *
 */
public class FactoryDescriptorsImpl implements FactoryDescriptors {
    private final Descriptor asService;
    private final Descriptor asProvideMethod;
    
    /**
     * This creates a descriptor pair describing a {@link Factory}
     * and the associated {@link Factory#provide()} method
     * 
     * @param asService A description of the factory itself as an hk2 service.
     * May not be null.  Must have DescriptorType of {@link DescriptorType#CLASS}.  One
     * of the contracts must be Factory
     * @param asProvideMethod A description of the provide method of the factory.  Must have
     * DescriptorType of {@link DescriptorType#PROVIDE_METHOD}.
     * May not be null
     * @throws IllegalArgumentException if the descriptors are not of the proper type
     */
    public FactoryDescriptorsImpl(Descriptor asService, Descriptor asProvideMethod) {
        if (asService == null || asProvideMethod == null) throw new IllegalArgumentException();
        if (!DescriptorType.CLASS.equals(asService.getDescriptorType())) {
            throw new IllegalArgumentException("Creation of FactoryDescriptors must have first argument of type CLASS");
        }
        if (!asService.getAdvertisedContracts().contains(Factory.class.getName())) {
            throw new IllegalArgumentException("Creation of FactoryDescriptors must have Factory as a contract of the first argument");
        }
        if (!DescriptorType.PROVIDE_METHOD.equals(asProvideMethod.getDescriptorType())) {
            throw new IllegalArgumentException("Creation of FactoryDescriptors must have second argument of type PROVIDE_METHOD");
            
        }
        this.asService = asService;
        this.asProvideMethod = asProvideMethod;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.FactoryDescriptors#getFactoryAsService()
     */
    @Override
    public Descriptor getFactoryAsAService() {
        return asService;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.FactoryDescriptors#getFactoryAsAFactory()
     */
    @Override
    public Descriptor getFactoryAsAFactory() {
        return asProvideMethod;
    }
    
    @Override
    public int hashCode() {
        return asService.hashCode() ^ asProvideMethod.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof FactoryDescriptors)) return false;
        
        FactoryDescriptors other = (FactoryDescriptors) o;
        Descriptor otherService = other.getFactoryAsAService();
        Descriptor otherFactory = other.getFactoryAsAFactory();
        
        if (otherService == null || otherFactory == null) return false;
        
        return (asService.equals(otherService) && asProvideMethod.equals(otherFactory));
    }
    
    @Override
    public String toString() {
        return "FactoryDescriptorsImpl(\n" +
          asService + ",\n" + asProvideMethod + ",\n\t" + System.identityHashCode(this) + ")";
    }

}
