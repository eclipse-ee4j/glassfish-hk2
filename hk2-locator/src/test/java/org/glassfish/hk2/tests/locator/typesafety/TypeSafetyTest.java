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

package org.glassfish.hk2.tests.locator.typesafety;

import junit.framework.Assert;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class TypeSafetyTest {
    private final static String TEST_NAME = "TypeSafetyTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new TypeSafetyModule());
    
    /** Returned by the String version of the parameterized type */
    public static final String CHECK_STRING = "Go Eagles!";
    /** Returned by the Integer version of the parameterized type */
    public static final int CHECK_INTEGER = 13;
    /** Returned by the Double version of the parameterized type */
    public static final double CHECK_DOUBLE = 0.131313;

    /**
     * RequiredType: Class
     * DescriptorType: Parameterized
     */
    @Test
    public void testRequiredClassDescriptorParameterized() {
        RawPSInjectee rpi = locator.getService(RawPSInjectee.class);
        Assert.assertNotNull(rpi);
        
        rpi.validate();
        
    }
    
    /**
     * RequiredType: Parameterized with raw wildcard
     * DescriptorType: Parameterized
     */
    @Test
    public void testRequiredRawWildcardDescriptorParameterized() {
        WildcardPSInjectee rpi = locator.getService(WildcardPSInjectee.class);
        Assert.assertNotNull(rpi);
        
        rpi.validate();
        
    }
    
    /**
     * RequiredType: Parameterized with upper bound wildcard
     * DescriptorType: Parameterized
     */
    @Test
    public void testRequiredUpperWildcardDescriptorParameterized() {
        WildcardUpperBoundPSInjectee rpi = locator.getService(WildcardUpperBoundPSInjectee.class);
        Assert.assertNotNull(rpi);
        
        rpi.validate();
        
    }
    
    /**
     * RequiredType: Parameterized with lower bound wildcard
     * DescriptorType: Parameterized
     */
    @Test
    public void testRequiredLowerWildcardDescriptorParameterized() {
        WildcardLowerBoundPSInjectee rpi = locator.getService(WildcardLowerBoundPSInjectee.class);
        Assert.assertNotNull(rpi);
        
        rpi.validate();
    }
    
    /**
     * RequiredType: Parameterized with type variable
     * DescriptorType: Parameterized
     */
    @Test
    public void testRequiredTypeVariableDescriptorParameterized() {
        @SuppressWarnings("rawtypes")
        WildcardTVSInjectee rpi = locator.getService(WildcardTVSInjectee.class);
        Assert.assertNotNull(rpi);
        
        rpi.validate();
    }
    
    /**
     * RequiredType: Parameterized with parameterized type with actual type
     * DescriptorType: Parameterized
     */
    @Test
    public void testRequiredPTWithActualDescriptorParameterized() {
        ActualTypeTVSInjectee rpi = locator.getService(ActualTypeTVSInjectee.class);
        Assert.assertNotNull(rpi);
        
        rpi.validate();
    }
    
    /**
     * RequiredType: Parameterized with parameterized type with type variable
     * DescriptorType: Parameterized
     */
    @Test
    public void testRequiredPTWithTVDescriptorParameterized() {
        @SuppressWarnings("rawtypes")
        TypeVariableTVSInjectee rpi = locator.getService(TypeVariableTVSInjectee.class);
        Assert.assertNotNull(rpi);
        
        rpi.validate();
    }

}
