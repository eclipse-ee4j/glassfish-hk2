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

package org.glassfish.hk2.tests.locator.locatorutilities;

import java.util.List;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Assert;
import org.junit.Test;
import org.jvnet.hk2.external.runtime.Hk2LocatorUtilities;

/**
 * @author jwells
 *
 */
public class Hk2LocatorUtilitiesTest {
    /**
     * Ensures filter is empty if no user services were added
     */
    @Test // @org.junit.Ignore
    public void testSingleLocatorNoUserServices() {
        ServiceLocator locator = LocatorHelper.create();
        
        List<ActiveDescriptor<?>> descriptors = locator.getDescriptors(
                Hk2LocatorUtilities.getNoInitialServicesFilter());
        Assert.assertTrue(descriptors.isEmpty());
        
    }
    
    /**
     * Ensures filter is empty if a user services is added
     */
    @Test // @org.junit.Ignore
    public void testSingleLocatorWithUserServices() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(SimpleService.class);
        
        List<ActiveDescriptor<?>> descriptors = locator.getDescriptors(
                Hk2LocatorUtilities.getNoInitialServicesFilter());
        Assert.assertFalse(descriptors.isEmpty());
        Assert.assertEquals(1, descriptors.size());
        
        ActiveDescriptor<?> descriptor = descriptors.get(0);
        
        Assert.assertEquals(descriptor.getImplementationClass(), SimpleService.class);
        
    }
    
    /**
     * Ensures filter is empty in a child if no services in parent or child
     */
    @Test // @org.junit.Ignore
    public void testChildLocatorNoUserServices() {
        ServiceLocator locator = LocatorHelper.getServiceLocator();
        ServiceLocator child = LocatorHelper.create(locator);
        
        List<ActiveDescriptor<?>> descriptors = child.getDescriptors(
                Hk2LocatorUtilities.getNoInitialServicesFilter());
        Assert.assertTrue(descriptors.isEmpty());
    }
    
    /**
     * Ensures filter is empty in a child if there is a service in the parent
     */
    @Test // @org.junit.Ignore
    public void testChildLocatorUserServiceInParent() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(SimpleService.class);
        ServiceLocator child = LocatorHelper.create(locator);
        
        List<ActiveDescriptor<?>> descriptors = child.getDescriptors(
                Hk2LocatorUtilities.getNoInitialServicesFilter());
        Assert.assertFalse(descriptors.isEmpty());
        Assert.assertEquals(1, descriptors.size());
        
        ActiveDescriptor<?> descriptor = descriptors.get(0);
        
        Assert.assertEquals(descriptor.getImplementationClass(), SimpleService.class);
    }
    
    /**
     * Ensures filter is not empty if there is a service in the child
     */
    @Test // @org.junit.Ignore
    public void testChildLocatorUserServiceInChild() {
        ServiceLocator locator = LocatorHelper.getServiceLocator();
        ServiceLocator child = LocatorHelper.create(locator);
        ServiceLocatorUtilities.addClasses(child, SimpleService.class);
        
        List<ActiveDescriptor<?>> parentDescriptors = locator.getDescriptors(
                Hk2LocatorUtilities.getNoInitialServicesFilter());
        
        Assert.assertTrue(parentDescriptors.isEmpty());
        
        List<ActiveDescriptor<?>> descriptors = child.getDescriptors(
                Hk2LocatorUtilities.getNoInitialServicesFilter());
        Assert.assertFalse(descriptors.isEmpty());
        Assert.assertEquals(1, descriptors.size());
        
        ActiveDescriptor<?> descriptor = descriptors.get(0);
        
        Assert.assertEquals(descriptor.getImplementationClass(), SimpleService.class);
    }
    
    /**
     * Ensures filter is not empty if there is a service in the child
     */
    @Test // @org.junit.Ignore
    public void testChildLocatorUserServicesInBoth() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(SimpleService.class);
        ServiceLocator child = LocatorHelper.create(locator);
        ServiceLocatorUtilities.addClasses(child, SimpleService.class);
        
        List<ActiveDescriptor<?>> parentDescriptors = locator.getDescriptors(
                Hk2LocatorUtilities.getNoInitialServicesFilter());
        Assert.assertFalse(parentDescriptors.isEmpty());
        Assert.assertEquals(1, parentDescriptors.size());
        
        ActiveDescriptor<?> descriptorP0 = parentDescriptors.get(0);
        
        Assert.assertEquals(descriptorP0.getImplementationClass(), SimpleService.class);
        
        List<ActiveDescriptor<?>> descriptors = child.getDescriptors(
                Hk2LocatorUtilities.getNoInitialServicesFilter());
        Assert.assertFalse(descriptors.isEmpty());
        Assert.assertEquals(2, descriptors.size());
        
        ActiveDescriptor<?> descriptorC0 = descriptors.get(0);
        ActiveDescriptor<?> descriptorC1 = descriptors.get(1);
        
        Assert.assertEquals(descriptorC0.getImplementationClass(), SimpleService.class);
        Assert.assertEquals(descriptorC1.getImplementationClass(), SimpleService.class);
    }
    
    /**
     * Tests a basic greedy scenario
     */
    @Test
    public void testGreedy() {
        // Do NOT put in SimpleService
        ServiceLocator locator = LocatorHelper.getServiceLocator(ForgotSimpleService.class);
        
        try {
            locator.getService(ForgotSimpleService.class);
            Assert.fail("Should have failed with no SimpleService to inject");
        }
        catch (MultiException me) {
            
        }
        
        ServiceLocatorUtilities.enableGreedyResolution(locator);
        
        // Twice to check idempotence
        ServiceLocatorUtilities.enableGreedyResolution(locator);
        
        ForgotSimpleService forgot = locator.getService(ForgotSimpleService.class);
        Assert.assertNotNull(forgot);
        Assert.assertNotNull(forgot.getInjectedService());
    }
    
    /**
     * Tests greedy with a parameterized type injection point
     */
    @Test
    public void testGreedyParameterizedType() {
     // Do NOT put in SimpleService
        ServiceLocator locator = LocatorHelper.getServiceLocator(ForgotParameterizedService.class);
        
        try {
            locator.getService(ForgotParameterizedService.class);
            Assert.fail("Should have failed with no SimpleService to inject");
        }
        catch (MultiException me) {
            
        }
        
        ServiceLocatorUtilities.enableGreedyResolution(locator);
        
        ForgotParameterizedService forgot = locator.getService(ForgotParameterizedService.class);
        Assert.assertNotNull(forgot);
        Assert.assertNotNull(forgot.getPS());
    }
    
    /**
     * Tests greedy with an interface with GreedyDefaultImplementation
     */
    @Test
    public void testGreedyInterface() {
        // Do NOT put in SimpleService
        ServiceLocator locator = LocatorHelper.getServiceLocator(ForgotSimpleInterface.class);
        
        try {
            locator.getService(ForgotSimpleInterface.class);
            Assert.fail("Should have failed with no SimpleService to inject");
        }
        catch (MultiException me) {
            
        }
        
        ServiceLocatorUtilities.enableGreedyResolution(locator);
        
        ForgotSimpleInterface forgot = locator.getService(ForgotSimpleInterface.class);
        Assert.assertNotNull(forgot);
        Assert.assertNotNull(forgot.getSI());
    }

}
