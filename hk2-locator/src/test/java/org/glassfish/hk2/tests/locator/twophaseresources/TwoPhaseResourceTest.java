/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.locator.twophaseresources;

import java.util.List;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.TwoPhaseTransactionData;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class TwoPhaseResourceTest {
    /**
     * Tests that all two phase resource prepares
     * and commits are called and that the added
     * and removed lists are accurate
     */
    @Test
    // @org.junit.Ignore
    public void testAllResourcesSuccess() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(SimpleService2.class);
        DynamicConfiguration dc = getDC(locator);
        
        ServiceHandle<SimpleService2> ss2Handle = locator.getServiceHandle(SimpleService2.class);
        ActiveDescriptor<SimpleService2> removed = ss2Handle.getActiveDescriptor();
        
        Assert.assertNotNull(locator.getService(SimpleService2.class));
        Assert.assertNotNull(removed);
        
        ActiveDescriptor<SimpleService> added = dc.addActiveDescriptor(SimpleService.class);
        dc.addUnbindFilter(BuilderHelper.createContractFilter(SimpleService2.class.getName()));
        
        RecordingResource rr1 = new RecordingResource(false, false, false);
        RecordingResource rr2 = new RecordingResource(false, false, false);
        
        dc.registerTwoPhaseResources(rr1);
        dc.registerTwoPhaseResources(rr2);
        
        dc.commit();
        
        {
            List<TwoPhaseTransactionData> rr1p = rr1.getPrepares();
            Assert.assertEquals(1, rr1p.size());
            
            checkAddsList(rr1p.get(0), added);
            checkRemovedList(rr1p.get(0), removed);
        }
        
        {
            List<TwoPhaseTransactionData> rr2p = rr2.getPrepares();
            Assert.assertEquals(1, rr2p.size());
            
            checkAddsList(rr2p.get(0), added);
            checkRemovedList(rr2p.get(0), removed);
        }
        
        {
            List<TwoPhaseTransactionData> rr1c = rr1.getCommits();
            Assert.assertEquals(1, rr1c.size());
            
            checkAddsList(rr1c.get(0), added);
            checkRemovedList(rr1c.get(0), removed);
        }
        
        {
            List<TwoPhaseTransactionData> rr2c = rr2.getCommits();
            Assert.assertEquals(1, rr2c.size());
            
            checkAddsList(rr2c.get(0), added);
            checkRemovedList(rr2c.get(0), removed);
        }
        
        {
            List<TwoPhaseTransactionData> rr1r = rr1.getRollbacks();
            Assert.assertEquals(0, rr1r.size());
        }
        
        {
            List<TwoPhaseTransactionData> rr2r = rr2.getRollbacks();
            Assert.assertEquals(0, rr2r.size());
        }
        
        {
            Assert.assertNotNull(locator.getService(SimpleService.class));
            Assert.assertNull(locator.getService(SimpleService2.class));
        }
    }
    
    /**
     * Tests that when the first resource fails the second is not called
     */
    @Test
    // @org.junit.Ignore
    public void testFirstResourcePrepareFail() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(SimpleService2.class);
        DynamicConfiguration dc = getDC(locator);
        
        ServiceHandle<SimpleService2> ss2Handle = locator.getServiceHandle(SimpleService2.class);
        ActiveDescriptor<SimpleService2> removed = ss2Handle.getActiveDescriptor();
        
        Assert.assertNotNull(locator.getService(SimpleService2.class));
        Assert.assertNotNull(removed);
        
        ActiveDescriptor<SimpleService> added = dc.addActiveDescriptor(SimpleService.class);
        dc.addUnbindFilter(BuilderHelper.createContractFilter(SimpleService2.class.getName()));
        
        RecordingResource rr1 = new RecordingResource(true, false, false);
        RecordingResource rr2 = new RecordingResource(false, false, false);
        
        dc.registerTwoPhaseResources(rr1);
        dc.registerTwoPhaseResources(rr2);
        
        try {
            dc.commit();
            Assert.fail("Should have failed due to prepare failure");
        }
        catch (MultiException me) {
            // Expected
        }
        
        {
            List<TwoPhaseTransactionData> rr1p = rr1.getPrepares();
            Assert.assertEquals(1, rr1p.size());
            
            checkAddsList(rr1p.get(0), added);
            checkRemovedList(rr1p.get(0), removed);
        }
        
        {
            List<TwoPhaseTransactionData> rr2p = rr2.getPrepares();
            Assert.assertEquals(0, rr2p.size());
        }
        
        {
            List<TwoPhaseTransactionData> rr1c = rr1.getCommits();
            Assert.assertEquals(0, rr1c.size());
        }
        
        {
            List<TwoPhaseTransactionData> rr2c = rr2.getCommits();
            Assert.assertEquals(0, rr2c.size());
        }
        
        {
            List<TwoPhaseTransactionData> rr1r = rr1.getRollbacks();
            Assert.assertEquals(0, rr1r.size());
        }
        
        {
            List<TwoPhaseTransactionData> rr2r = rr2.getRollbacks();
            Assert.assertEquals(0, rr2r.size());
        }
        
        {
            Assert.assertNull(locator.getService(SimpleService.class));
            Assert.assertNotNull(locator.getService(SimpleService2.class));
        }
    }
    
    /**
     * Tests that when the second resource fails the rollback
     * is called on the first
     */
    @Test
    // @org.junit.Ignore
    public void testSecondResourcePrepareFail() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(SimpleService2.class);
        DynamicConfiguration dc = getDC(locator);
        
        ServiceHandle<SimpleService2> ss2Handle = locator.getServiceHandle(SimpleService2.class);
        ActiveDescriptor<SimpleService2> removed = ss2Handle.getActiveDescriptor();
        
        Assert.assertNotNull(locator.getService(SimpleService2.class));
        Assert.assertNotNull(removed);
        
        ActiveDescriptor<SimpleService> added = dc.addActiveDescriptor(SimpleService.class);
        dc.addUnbindFilter(BuilderHelper.createContractFilter(SimpleService2.class.getName()));
        
        RecordingResource rr1 = new RecordingResource(false, false, false);
        RecordingResource rr2 = new RecordingResource(true, false, false);
        
        dc.registerTwoPhaseResources(rr1);
        dc.registerTwoPhaseResources(rr2);
        
        try {
            dc.commit();
            Assert.fail("Should have failed due to prepare failure");
        }
        catch (MultiException me) {
            // Expected
        }
        
        {
            List<TwoPhaseTransactionData> rr1p = rr1.getPrepares();
            Assert.assertEquals(1, rr1p.size());
            
            checkAddsList(rr1p.get(0), added);
            checkRemovedList(rr1p.get(0), removed);
        }
        
        {
            List<TwoPhaseTransactionData> rr2p = rr2.getPrepares();
            Assert.assertEquals(1, rr2p.size());
            
            checkAddsList(rr2p.get(0), added);
            checkRemovedList(rr2p.get(0), removed);
        }
        
        {
            List<TwoPhaseTransactionData> rr1c = rr1.getCommits();
            Assert.assertEquals(0, rr1c.size());
        }
        
        {
            List<TwoPhaseTransactionData> rr2c = rr2.getCommits();
            Assert.assertEquals(0, rr2c.size());
        }
        
        {
            List<TwoPhaseTransactionData> rr1r = rr1.getRollbacks();
            Assert.assertEquals(1, rr1r.size());
            
            checkAddsList(rr1r.get(0), added);
            checkRemovedList(rr1r.get(0), removed);
        }
        
        {
            List<TwoPhaseTransactionData> rr2r = rr2.getRollbacks();
            Assert.assertEquals(0, rr2r.size());
        }
        
        {
            Assert.assertNull(locator.getService(SimpleService.class));
            Assert.assertNotNull(locator.getService(SimpleService2.class));
        }
    }
    
    /**
     * Tests that when the resources fail in activate the
     * transaction is successful
     */
    @Test
    // @org.junit.Ignore
    public void testBothResourcesFailInActivateOk() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(SimpleService2.class);
        DynamicConfiguration dc = getDC(locator);
        
        ServiceHandle<SimpleService2> ss2Handle = locator.getServiceHandle(SimpleService2.class);
        ActiveDescriptor<SimpleService2> removed = ss2Handle.getActiveDescriptor();
        
        Assert.assertNotNull(locator.getService(SimpleService2.class));
        Assert.assertNotNull(removed);
        
        ActiveDescriptor<SimpleService> added = dc.addActiveDescriptor(SimpleService.class);
        dc.addUnbindFilter(BuilderHelper.createContractFilter(SimpleService2.class.getName()));
        
        RecordingResource rr1 = new RecordingResource(false, true, false);
        RecordingResource rr2 = new RecordingResource(false, true, false);
        
        dc.registerTwoPhaseResources(rr1);
        dc.registerTwoPhaseResources(rr2);
        
        dc.commit();
        
        {
            List<TwoPhaseTransactionData> rr1p = rr1.getPrepares();
            Assert.assertEquals(1, rr1p.size());
            
            checkAddsList(rr1p.get(0), added);
            checkRemovedList(rr1p.get(0), removed);
        }
        
        {
            List<TwoPhaseTransactionData> rr2p = rr2.getPrepares();
            Assert.assertEquals(1, rr2p.size());
            
            checkAddsList(rr2p.get(0), added);
            checkRemovedList(rr2p.get(0), removed);
        }
        
        {
            List<TwoPhaseTransactionData> rr1c = rr1.getCommits();
            Assert.assertEquals(1, rr1c.size());
            
            checkAddsList(rr1c.get(0), added);
            checkRemovedList(rr1c.get(0), removed);
        }
        
        {
            List<TwoPhaseTransactionData> rr2c = rr2.getCommits();
            Assert.assertEquals(1, rr2c.size());
            
            checkAddsList(rr2c.get(0), added);
            checkRemovedList(rr2c.get(0), removed);
        }
        
        {
            List<TwoPhaseTransactionData> rr1r = rr1.getRollbacks();
            Assert.assertEquals(0, rr1r.size());
        }
        
        {
            List<TwoPhaseTransactionData> rr2r = rr2.getRollbacks();
            Assert.assertEquals(0, rr2r.size());
        }
        
        {
            Assert.assertNotNull(locator.getService(SimpleService.class));
            Assert.assertNull(locator.getService(SimpleService2.class));
        }
    }
    
    /**
     * Tests that when the resources fail in rollback the
     * transaction is successful
     */
    @Test
    // @org.junit.Ignore
    public void testResourceFailsInRollbackOk() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(SimpleService2.class);
        DynamicConfiguration dc = getDC(locator);
        
        ServiceHandle<SimpleService2> ss2Handle = locator.getServiceHandle(SimpleService2.class);
        ActiveDescriptor<SimpleService2> removed = ss2Handle.getActiveDescriptor();
        
        Assert.assertNotNull(locator.getService(SimpleService2.class));
        Assert.assertNotNull(removed);
        
        ActiveDescriptor<SimpleService> added = dc.addActiveDescriptor(SimpleService.class);
        dc.addUnbindFilter(BuilderHelper.createContractFilter(SimpleService2.class.getName()));
        
        RecordingResource rr1 = new RecordingResource(false, false, true);
        RecordingResource rr2 = new RecordingResource(false, false, true);
        RecordingResource rr3 = new RecordingResource(true, false, false);
        
        dc.registerTwoPhaseResources(rr1);
        dc.registerTwoPhaseResources(rr2, rr3);
        
        try {
            dc.commit();
            Assert.fail("Should have failed");
        }
        catch (MultiException me) {
            // Expected
        }
        
        {
            List<TwoPhaseTransactionData> rr1p = rr1.getPrepares();
            Assert.assertEquals(1, rr1p.size());
            
            checkAddsList(rr1p.get(0), added);
            checkRemovedList(rr1p.get(0), removed);
        }
        
        {
            List<TwoPhaseTransactionData> rr2p = rr2.getPrepares();
            Assert.assertEquals(1, rr2p.size());
            
            checkAddsList(rr2p.get(0), added);
            checkRemovedList(rr2p.get(0), removed);
        }
        
        {
            List<TwoPhaseTransactionData> rr3p = rr3.getPrepares();
            Assert.assertEquals(1, rr3p.size());
            
            checkAddsList(rr3p.get(0), added);
            checkRemovedList(rr3p.get(0), removed);
        }
        
        {
            List<TwoPhaseTransactionData> rr1c = rr1.getCommits();
            Assert.assertEquals(0, rr1c.size());
        }
        
        {
            List<TwoPhaseTransactionData> rr2c = rr2.getCommits();
            Assert.assertEquals(0, rr2c.size());
        }
        
        {
            List<TwoPhaseTransactionData> rr3c = rr3.getCommits();
            Assert.assertEquals(0, rr3c.size());
        }
        
        {
            List<TwoPhaseTransactionData> rr1r = rr1.getRollbacks();
            Assert.assertEquals(1, rr1r.size());
            
            checkAddsList(rr1r.get(0), added);
            checkRemovedList(rr1r.get(0), removed);
        }
        
        {
            List<TwoPhaseTransactionData> rr2r = rr2.getRollbacks();
            Assert.assertEquals(1, rr2r.size());
            
            checkAddsList(rr2r.get(0), added);
            checkRemovedList(rr2r.get(0), removed);
        }
        
        {
            List<TwoPhaseTransactionData> rr3r = rr3.getRollbacks();
            Assert.assertEquals(0, rr3r.size());
        }
        
        {
            Assert.assertNull(locator.getService(SimpleService.class));
            Assert.assertNotNull(locator.getService(SimpleService2.class));
        }
    }
    
    /**
     * Tests that all two phase resource prepares
     * and commits are called and that the added
     * and removed lists are accurate
     */
    @Test
    // @org.junit.Ignore
    public void testOnlyAddSuccess() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(SimpleService2.class);
        DynamicConfiguration dc = getDC(locator);
        
        ServiceHandle<SimpleService2> ss2Handle = locator.getServiceHandle(SimpleService2.class);
        ActiveDescriptor<SimpleService2> removed = ss2Handle.getActiveDescriptor();
        
        Assert.assertNotNull(locator.getService(SimpleService2.class));
        Assert.assertNotNull(removed);
        
        ActiveDescriptor<SimpleService> added = dc.addActiveDescriptor(SimpleService.class);
        
        RecordingResource rr1 = new RecordingResource(false, false, false);
        RecordingResource rr2 = new RecordingResource(false, false, false);
        
        dc.registerTwoPhaseResources(rr1);
        dc.registerTwoPhaseResources(rr2);
        
        dc.commit();
        
        {
            List<TwoPhaseTransactionData> rr1p = rr1.getPrepares();
            Assert.assertEquals(1, rr1p.size());
            
            checkAddsList(rr1p.get(0), added);
            checkRemovedList(rr1p.get(0));
        }
        
        {
            List<TwoPhaseTransactionData> rr2p = rr2.getPrepares();
            Assert.assertEquals(1, rr2p.size());
            
            checkAddsList(rr2p.get(0), added);
            checkRemovedList(rr2p.get(0));
        }
        
        {
            List<TwoPhaseTransactionData> rr1c = rr1.getCommits();
            Assert.assertEquals(1, rr1c.size());
            
            checkAddsList(rr1c.get(0), added);
            checkRemovedList(rr1c.get(0));
        }
        
        {
            List<TwoPhaseTransactionData> rr2c = rr2.getCommits();
            Assert.assertEquals(1, rr2c.size());
            
            checkAddsList(rr2c.get(0), added);
            checkRemovedList(rr2c.get(0));
        }
        
        {
            List<TwoPhaseTransactionData> rr1r = rr1.getRollbacks();
            Assert.assertEquals(0, rr1r.size());
        }
        
        {
            List<TwoPhaseTransactionData> rr2r = rr2.getRollbacks();
            Assert.assertEquals(0, rr2r.size());
        }
        
        {
            Assert.assertNotNull(locator.getService(SimpleService.class));
            Assert.assertNotNull(locator.getService(SimpleService2.class));
        }
    }
    
    /**
     * Tests that all two phase resource prepares
     * and commits are called and that the added
     * and removed lists are accurate
     */
    @Test
    // @org.junit.Ignore
    public void testOnlyRemoveSuccess() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(SimpleService2.class);
        DynamicConfiguration dc = getDC(locator);
        
        ServiceHandle<SimpleService2> ss2Handle = locator.getServiceHandle(SimpleService2.class);
        ActiveDescriptor<SimpleService2> removed = ss2Handle.getActiveDescriptor();
        
        Assert.assertNotNull(locator.getService(SimpleService2.class));
        Assert.assertNotNull(removed);
        
        dc.addUnbindFilter(BuilderHelper.createContractFilter(SimpleService2.class.getName()));
        
        RecordingResource rr1 = new RecordingResource(false, false, false);
        RecordingResource rr2 = new RecordingResource(false, false, false);
        
        dc.registerTwoPhaseResources(rr1);
        dc.registerTwoPhaseResources(rr2);
        
        dc.commit();
        
        {
            List<TwoPhaseTransactionData> rr1p = rr1.getPrepares();
            Assert.assertEquals(1, rr1p.size());
            
            checkAddsList(rr1p.get(0));
            checkRemovedList(rr1p.get(0), removed);
        }
        
        {
            List<TwoPhaseTransactionData> rr2p = rr2.getPrepares();
            Assert.assertEquals(1, rr2p.size());
            
            checkAddsList(rr2p.get(0));
            checkRemovedList(rr2p.get(0), removed);
        }
        
        {
            List<TwoPhaseTransactionData> rr1c = rr1.getCommits();
            Assert.assertEquals(1, rr1c.size());
            
            checkAddsList(rr1c.get(0));
            checkRemovedList(rr1c.get(0), removed);
        }
        
        {
            List<TwoPhaseTransactionData> rr2c = rr2.getCommits();
            Assert.assertEquals(1, rr2c.size());
            
            checkAddsList(rr2c.get(0));
            checkRemovedList(rr2c.get(0), removed);
        }
        
        {
            List<TwoPhaseTransactionData> rr1r = rr1.getRollbacks();
            Assert.assertEquals(0, rr1r.size());
        }
        
        {
            List<TwoPhaseTransactionData> rr2r = rr2.getRollbacks();
            Assert.assertEquals(0, rr2r.size());
        }
        
        {
            Assert.assertNull(locator.getService(SimpleService.class));
            Assert.assertNull(locator.getService(SimpleService2.class));
        }
    }
    
    private static DynamicConfiguration getDC(ServiceLocator locator) {
        DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
        Assert.assertNotNull(dcs);
        
        DynamicConfiguration cd = dcs.createDynamicConfiguration();
        Assert.assertNotNull(cd);
        return cd;
    }
    
    private static void checkAddsList(TwoPhaseTransactionData transactionData, ActiveDescriptor<?>... addedDescriptors) {
        Assert.assertEquals(transactionData.getAllAddedDescriptors().size(), addedDescriptors.length);
        
        for (int lcv = 0; lcv < addedDescriptors.length; lcv++) {
            ActiveDescriptor<?> transactionDataAdd = transactionData.getAllAddedDescriptors().get(lcv);
            
            Assert.assertEquals(addedDescriptors[lcv], transactionDataAdd);
        }
    }
    
    private static void checkRemovedList(TwoPhaseTransactionData transactionData, ActiveDescriptor<?>... removedDescriptors) {
        Assert.assertEquals(transactionData.getAllRemovedDescriptors().size(), removedDescriptors.length);
        
        for (int lcv = 0; lcv < removedDescriptors.length; lcv++) {
            ActiveDescriptor<?> transactionDataAdd = transactionData.getAllRemovedDescriptors().get(lcv);
            
            Assert.assertEquals(removedDescriptors[lcv], transactionDataAdd);
        }
    }

}
