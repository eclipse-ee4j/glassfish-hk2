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

package org.glassfish.hk2.tests.locator.qualifiers;

import java.lang.annotation.Annotation;
import java.util.List;

import junit.framework.Assert;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class QualifierTest {
    private final static String TEST_NAME = "QualifierTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new QualifierModule());
    
    /** 49ers */
    public final static String RED = "Red";
    /** Packers */
    public final static String YELLOW = "Yellow";
    /** Giants */
    public final static String BLUE = "Blue";
    /** Broncos */
    public final static String ORANGE = "Orange";
    /** Vikings */
    public final static String PURPLE = "Purple";
    /** Eagles */
    public final static String GREEN = "Green";
    /** Raiders */
    public final static String BLACK = "Black";

    /**
     * Checks the qualifiers
     */
    @Test
    public void testAllColors() {
        ColorWheel wheel = locator.getService(ColorWheel.class);
        Assert.assertNotNull("ColorWheel is null", wheel);
        
        Assert.assertEquals(RED, wheel.getRed().getColorName());
        Assert.assertEquals(GREEN, wheel.getGreen().getColorName());
        Assert.assertEquals(BLUE, wheel.getBlue().getColorName());
        Assert.assertEquals(YELLOW, wheel.getYellow().getColorName());
        Assert.assertEquals(ORANGE, wheel.getOrange().getColorName());
        Assert.assertEquals(PURPLE, wheel.getPurple().getColorName());
        
    }

    @Test
    public void testUnqualifiedClass() {
        BlackInjectee injectee = locator.getService(BlackInjectee.class);
        Assert.assertNotNull("Injectee is null", injectee);

        Assert.assertEquals(BLACK, injectee.getBlack().getColorName());
    }

    /**
     * Tests getting something via a qualifier only
     */
    @Test
    public void testGetByQualifierOnly() {
        List<SpecifiedImplementation> specs =
                locator.getAllServices(new ImplementationQualifierImpl(SpecifiedImplementation.class.getName()));
        
        Assert.assertNotNull(specs);
        Assert.assertEquals(1, specs.size());
        Assert.assertTrue(specs.get(0) instanceof SpecifiedImplementation);
    }
    
    /**
     * Tests getting something via a qualifier only
     */
    @Test
    public void testGetByQualifierOnlyHandles() {
        List<ServiceHandle<?>> specs =
                locator.getAllServiceHandles(new ImplementationQualifierImpl(SpecifiedImplementation.class.getName()));
        
        Assert.assertNotNull(specs);
        Assert.assertEquals(1, specs.size());
        ServiceHandle<?> handle = specs.get(0);
        
        SpecifiedImplementation si = (SpecifiedImplementation) handle.getService();
        Assert.assertNotNull(si);
    }
    
    /**
     * Tests getting something via a qualifier only
     */
    @Test
    public void testFailToGetByQualifierOnly() {
        List<SpecifiedImplementation> specs =
                locator.getAllServices(new ImplementationQualifierImpl(SpecifiedImplementation.class.getName()),
                        new BlueAnnotationImpl());
        
        Assert.assertNotNull(specs);
        Assert.assertEquals(0, specs.size());
    }
    
    /**
     * Tests getting something via a qualifier only
     */
    @Test
    public void testFailToGetByQualifierOnlyHandles() {
        List<ServiceHandle<?>> specs =
                locator.getAllServiceHandles(new ImplementationQualifierImpl(SpecifiedImplementation.class.getName()),
                        new BlueAnnotationImpl());
        
        Assert.assertNotNull(specs);
        Assert.assertEquals(0, specs.size());
    }
    
    /**
     * Tests getting something via a qualifier only
     */
    @Test(expected=IllegalArgumentException.class)
    public void testNullQualifier() {
        locator.getAllServiceHandles((Annotation) null);
    }
    
    /**
     * Tests getting something via a qualifier only
     */
    @Test(expected=IllegalArgumentException.class)
    public void testDoubleQualifier() {
        locator.getAllServiceHandles(new ImplementationQualifierImpl(SpecifiedImplementation.class.getName()),
                new ImplementationQualifierImpl(SpecifiedImplementation.class.getName() + "_another"));
    }
    
    @Test
    public void testLazyReificationWhenLookedUpByQualifierWithGetAllServiceHandles() {
        List<ServiceHandle<Mauve>> handles = locator.getAllServiceHandles(Mauve.class);
        Assert.assertNotNull(handles);
        Assert.assertEquals(1, handles.size());
        
        ServiceHandle<Mauve> mauveHandle = handles.get(0);
        ActiveDescriptor<Mauve> mauveDescriptor = mauveHandle.getActiveDescriptor();
        
        // The true test
        Assert.assertFalse(mauveDescriptor.isReified());
    }
    
    @Test
    public void testLazyReificationWhenLookedUpByQualifierWithGetServiceHandle() {
        ServiceHandle<Mauve> handle = locator.getServiceHandle(Mauve.class);
        Assert.assertNotNull(handle);
        
        ActiveDescriptor<Mauve> mauveDescriptor = handle.getActiveDescriptor();
        
        // The true test
        Assert.assertFalse(mauveDescriptor.isReified());
    }
    
    @Test
    public void testLookupViaQualifierWithGetService() {
        Object maroonQualified = locator.getService(Maroon.class);
        Assert.assertNotNull(maroonQualified);
        
        Assert.assertTrue(maroonQualified instanceof MaroonQualified);
    }
}
