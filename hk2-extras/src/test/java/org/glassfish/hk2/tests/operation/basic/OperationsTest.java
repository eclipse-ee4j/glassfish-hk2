/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2018 Payara Foundation
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

package org.glassfish.hk2.tests.operation.basic;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import org.glassfish.hk2.api.ProxyCtl;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.extras.ExtrasUtilities;
import org.glassfish.hk2.extras.operation.OperationHandle;
import org.glassfish.hk2.extras.operation.OperationManager;
import org.glassfish.hk2.extras.operation.OperationState;
import org.glassfish.hk2.tests.extras.internal.Utilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class OperationsTest {
    public final static BasicOperationScope BASIC_OPERATION_ANNOTATION = new BasicOperationScopeImpl();
    private final static SecondaryOperationScope SECONDARY_OPERATION_ANNOTATION = new SecondaryOperationScopeImpl();
    
    private final static String ALICE_NM = "Alice";
    private final static byte[] ALICE_PW = { 1, 2 };
    private final static String BOB_NM = "Bob";
    private final static byte[] BOB_PW = { 3, 4 };
    
    private final static OperationUser ALICE = new OperationUser() {

        @Override
        public String getName() {
            return ALICE_NM;
        }

        @Override
        public byte[] getPassword() {
            return ALICE_PW;
        }
        
    };
    
    private final static OperationUser BOB = new OperationUser() {

        @Override
        public String getName() {
            return BOB_NM;
        }

        @Override
        public byte[] getPassword() {
            return BOB_PW;
        }
        
    };
    
    private final static long FIRST_ID = 1;
    private final static long SECOND_ID = 2;
    
    public static ServiceLocator createLocator(Class<?>... clazzes) {
        ServiceLocator locator = Utilities.getUniqueLocator(clazzes);
        ExtrasUtilities.enableOperations(locator);
        
        return locator;
    }
    
    /**
     * Tests that operations can be properly swapped on a single thread
     */
    @Test // @org.junit.Ignore
    public void testChangeOperationOnSameThread() {
        ServiceLocator locator = createLocator(BasicOperationScopeContext.class,
                OperationUserFactory.class, SingletonThatUsesOperationService.class);
        
        OperationManager operationManager = locator.getService(OperationManager.class);
        
        OperationHandle<BasicOperationScope> aliceOperation = operationManager.createOperation(BASIC_OPERATION_ANNOTATION);
        aliceOperation.setOperationData(ALICE);
        
        OperationHandle<BasicOperationScope> bobOperation = operationManager.createOperation(BASIC_OPERATION_ANNOTATION);
        bobOperation.setOperationData(BOB);
        
        SingletonThatUsesOperationService singleton = locator.getService(SingletonThatUsesOperationService.class);
        
        // Start ALICE operation
        aliceOperation.resume();
        
        Assert.assertEquals(ALICE_NM, singleton.getCurrentUserName());
        
        // suspend ALICE and start BOB
        aliceOperation.suspend();
        bobOperation.resume();
        
        Assert.assertEquals(BOB_NM, singleton.getCurrentUserName());
        
        // Clean up
        aliceOperation.close();
        bobOperation.close();
    }
    
    /**
     * Tests that operations can be properly swapped on a single thread
     * @throws InterruptedException 
     */
    @Test // @org.junit.Ignore
    public void testOperationsActiveOnTwoThreads() throws InterruptedException {
        ServiceLocator locator = createLocator(BasicOperationScopeContext.class,
                OperationUserFactory.class, SingletonThatUsesOperationService.class);
        
        OperationManager operationManager = locator.getService(OperationManager.class);
        
        OperationHandle<BasicOperationScope> aliceOperation = operationManager.createOperation(BASIC_OPERATION_ANNOTATION);
        aliceOperation.setOperationData(ALICE);
        
        OperationHandle<BasicOperationScope> bobOperation = operationManager.createOperation(BASIC_OPERATION_ANNOTATION);
        bobOperation.setOperationData(BOB);
        
        SingletonThatUsesOperationService singleton = locator.getService(SingletonThatUsesOperationService.class);
        
        SimpleThreadedFetcher<BasicOperationScope> aliceFetcher = new SimpleThreadedFetcher<BasicOperationScope>(aliceOperation, singleton);
        Thread aliceThread = new Thread(aliceFetcher);
        
        SimpleThreadedFetcher<BasicOperationScope> bobFetcher = new SimpleThreadedFetcher<BasicOperationScope>(bobOperation, singleton);
        Thread bobThread = new Thread(bobFetcher);
        
        aliceThread.start();
        bobThread.start();
        
        // Gives both threads time for both to get inside operation
        Thread.sleep(100);
        
        aliceFetcher.go();
        bobFetcher.go();
        
        Assert.assertEquals(ALICE_NM, aliceFetcher.waitForResult());
        Assert.assertEquals(BOB_NM, bobFetcher.waitForResult());
        
        // Clean up
        aliceOperation.close();
        bobOperation.close();
    }
    
    /**
     * Tests that operations can be properly swapped on a single thread
     * @throws InterruptedException 
     */
    @Test // @org.junit.Ignore
    public void testCheckState() throws InterruptedException {
        ServiceLocator locator = createLocator(BasicOperationScopeContext.class,
                OperationUserFactory.class, SingletonThatUsesOperationService.class);
        
        OperationManager operationManager = locator.getService(OperationManager.class);
        
        OperationHandle<BasicOperationScope> operation = operationManager.createOperation(BASIC_OPERATION_ANNOTATION);
        
        Assert.assertEquals(OperationState.SUSPENDED, operation.getState());
        
        Thread t1 = new Thread();
        Thread t2 = new Thread();
        
        operation.resume(t1.getId());
        
        Assert.assertEquals(OperationState.ACTIVE, operation.getState());
        
        operation.resume(t2.getId());
        
        Assert.assertEquals(OperationState.ACTIVE, operation.getState());
        
        Set<Long> activeIds = operation.getActiveThreads();
        Assert.assertEquals(2, activeIds.size());
        
        Assert.assertTrue(activeIds.contains(t1.getId()));
        Assert.assertTrue(activeIds.contains(t2.getId()));
        
        operation.suspend(t1.getId());
        
        Assert.assertEquals(OperationState.ACTIVE, operation.getState());
        
        activeIds = operation.getActiveThreads();
        Assert.assertEquals(1, activeIds.size());
        
        Assert.assertTrue(activeIds.contains(t2.getId()));
        
        // suspend t1 again, make sure nothing bad happens
        operation.suspend(t1.getId());
        
        Assert.assertEquals(OperationState.ACTIVE, operation.getState());
        
        activeIds = operation.getActiveThreads();
        Assert.assertEquals(1, activeIds.size());
        
        Assert.assertTrue(activeIds.contains(t2.getId()));
        
        // Now suspend t2, make sure we go to SUSPENDED
        operation.suspend(t2.getId());
        
        Assert.assertEquals(OperationState.SUSPENDED, operation.getState());
        
        activeIds = operation.getActiveThreads();
        Assert.assertEquals(0, activeIds.size());
        
        operation.close();
        
        Assert.assertEquals(OperationState.CLOSED, operation.getState());
        
        try {
            operation.resume(t1.getId());
            Assert.fail("Should not have been able to resume a closed operation");
        }
        catch (IllegalStateException ise) {
        }
        
        // Should do nothing
        operation.suspend(t1.getId());
        
        Assert.assertEquals(OperationState.CLOSED, operation.getState());
        
        activeIds = operation.getActiveThreads();
        Assert.assertEquals(0, activeIds.size());
    }
    
    /**
     * Tests that operations can be properly swapped on a single thread
     * @throws InterruptedException 
     */
    @Test // @org.junit.Ignore
    public void testDoubleResume() throws InterruptedException {
        ServiceLocator locator = createLocator(BasicOperationScopeContext.class,
                OperationUserFactory.class, SingletonThatUsesOperationService.class);
        
        OperationManager operationManager = locator.getService(OperationManager.class);
        
        OperationHandle<BasicOperationScope> operation = operationManager.createOperation(BASIC_OPERATION_ANNOTATION);
        
        Assert.assertEquals(OperationState.SUSPENDED, operation.getState());
        
        operation.resume(Thread.currentThread().getId());
        
        Assert.assertEquals(OperationState.ACTIVE, operation.getState());
        
        operation.resume(Thread.currentThread().getId());
        
        Assert.assertEquals(OperationState.ACTIVE, operation.getState());
        
        operation.close();
        
        Assert.assertEquals(OperationState.CLOSED, operation.getState());
    }
    
    /**
     * Tests that operations can be properly swapped on a single thread
     * @throws InterruptedException 
     */
    @Test // @org.junit.Ignore
    public void testResumeOfSecondOperationSameThreadFails() throws InterruptedException {
        ServiceLocator locator = createLocator(BasicOperationScopeContext.class,
                OperationUserFactory.class, SingletonThatUsesOperationService.class);
        
        OperationManager operationManager = locator.getService(OperationManager.class);
        
        OperationHandle<BasicOperationScope> operation1 = operationManager.createOperation(BASIC_OPERATION_ANNOTATION);
        OperationHandle<BasicOperationScope> operation2 = operationManager.createOperation(BASIC_OPERATION_ANNOTATION);
        
        operation1.resume();
        
        try {
            operation2.resume();
            Assert.fail("Should not have been able to resume second operation on thread first operation already had");
        }
        catch (IllegalStateException ise) {
            // expected
        }
        
        operation1.close();
        
        // Make sure we can do it later after the first operation has gone away
        operation2.resume();
        
        operation2.close();
    }
    
    /**
     * Tests that operations can be properly swapped on a single thread
     * @throws InterruptedException 
     */
    @Test // @org.junit.Ignore
    public void testUseOperationsInHashSet() throws InterruptedException {
        ServiceLocator locator = createLocator(BasicOperationScopeContext.class,
                OperationUserFactory.class, SingletonThatUsesOperationService.class);
        
        OperationManager operationManager = locator.getService(OperationManager.class);
        
        OperationHandle<BasicOperationScope> operation1 = operationManager.createOperation(BASIC_OPERATION_ANNOTATION);
        OperationHandle<BasicOperationScope> operation2 = operationManager.createOperation(BASIC_OPERATION_ANNOTATION);
        
        HashSet<OperationHandle<BasicOperationScope>> storage = new HashSet<OperationHandle<BasicOperationScope>>();
        
        storage.add(operation1);
        storage.add(operation2);
        
        Assert.assertTrue(storage.contains(operation2));
        Assert.assertTrue(storage.contains(operation1));
        
        Assert.assertFalse(operation1.equals(null));
        Assert.assertFalse(operation1.equals(operationManager));
        Assert.assertFalse(operation1.equals(operation2));
        Assert.assertTrue(operation1.equals(operation1));
    }
    
    /**
     * Tests that a service in operation scope where there is
     * no operation scope on the thread fails, and that we
     * can put an operation on the thread and then it works ok
     */
    @Test // @org.junit.Ignore
    public void testNoOperationOnThread() {
        ServiceLocator locator = createLocator(BasicOperationScopeContext.class,
                OperationUserFactory.class, SingletonThatUsesOperationService.class);
        
        OperationManager operationManager = locator.getService(OperationManager.class);
        
        try (OperationHandle<BasicOperationScope> aliceOperation = operationManager.createAndStartOperation(BASIC_OPERATION_ANNOTATION)) {
            aliceOperation.setOperationData(ALICE);
            SingletonThatUsesOperationService singleton = locator.getService(SingletonThatUsesOperationService.class);
            Assert.assertEquals(ALICE_NM, singleton.getCurrentUserName());
            // suspend ALICE and start BOB
            aliceOperation.suspend();
            try {
                singleton.getCurrentUserName();
                Assert.fail("Should not have been able to call method as there is no operation on the thread");
            }
            catch (IllegalStateException ise) {
                // Expected
            }
            aliceOperation.resume();
            Assert.assertEquals(ALICE_NM, singleton.getCurrentUserName());
        }
    }
    
    /**
     * Tests that two operations of different types can be
     * on the same thread
     */
    @Test // @org.junit.Ignore
    public void testTwoOperationsDifferentTypesOnSameThread() {
        ServiceLocator locator = createLocator(BasicOperationScopeContext.class,
                OperationUserFactory.class,
                SecondaryOperationScopeContext.class,
                SingletonThatUsesBothOperationTypes.class,
                SecondaryData.class);
        
        OperationManager operationManager = locator.getService(OperationManager.class);
        
        OperationHandle<BasicOperationScope> aliceOperation = operationManager.createAndStartOperation(BASIC_OPERATION_ANNOTATION);
        aliceOperation.setOperationData(ALICE);
        
        OperationHandle<BasicOperationScope> bobOperation = operationManager.createOperation(BASIC_OPERATION_ANNOTATION);
        bobOperation.setOperationData(BOB);
        
        OperationHandle<SecondaryOperationScope> firstOperation = operationManager.createOperation(SECONDARY_OPERATION_ANNOTATION);
        OperationHandle<SecondaryOperationScope> secondOperation = operationManager.createOperation(SECONDARY_OPERATION_ANNOTATION);
        
        firstOperation.resume();
        
        SecondaryData firstData = locator.getService(SecondaryData.class);
        firstData.setId(FIRST_ID);
        
        firstOperation.suspend();
        secondOperation.resume();
        
        SecondaryData secondData = locator.getService(SecondaryData.class);
        secondData.setId(SECOND_ID);
        
        SingletonThatUsesBothOperationTypes singleton = locator.getService(SingletonThatUsesBothOperationTypes.class);
        
        Assert.assertEquals(ALICE_NM, singleton.getCurrentUserName());
        
        secondOperation.suspend();
        firstOperation.resume();
        
        Assert.assertEquals(FIRST_ID, singleton.getCurrentSecondaryId());
        
        aliceOperation.suspend();
        bobOperation.resume();
        
        Assert.assertEquals(BOB_NM, singleton.getCurrentUserName());
        
        firstOperation.suspend();
        secondOperation.resume();
        
        Assert.assertEquals(SECOND_ID, singleton.getCurrentSecondaryId());
        
        // Clean up
        aliceOperation.close();
        bobOperation.close();
        firstOperation.close();
        secondOperation.close();
        
    }
    
    /**
     * Tests that we can differentiate injections of OperationHandle via
     * parameterized type
     */
    @Test // @org.junit.Ignore
    public void testDifferentOperationHandleTypes() {
        ServiceLocator locator = createLocator(BasicOperationScopeContext.class,
                SecondaryOperationScopeContext.class,
                InjectsTwoOperationHandlesOfDifferentTypes.class);
        
        OperationManager operationManager = locator.getService(OperationManager.class);
        
        OperationHandle<BasicOperationScope> aliceOperation = operationManager.createAndStartOperation(BASIC_OPERATION_ANNOTATION);
        aliceOperation.setOperationData(ALICE);
        
        OperationHandle<SecondaryOperationScope> firstOperation = operationManager.createOperation(SECONDARY_OPERATION_ANNOTATION);
        firstOperation.setOperationData(BOB);
        
        aliceOperation.resume();
        firstOperation.resume();
        
        InjectsTwoOperationHandlesOfDifferentTypes tdt = locator.getService(InjectsTwoOperationHandlesOfDifferentTypes.class);
        
        Assert.assertEquals(ALICE, tdt.getBasicHandle().getOperationData());
        Assert.assertEquals(BOB, tdt.getSecondaryHandle().getOperationData());
        
        // Clean up
        aliceOperation.close();
        firstOperation.close();
    }
    
    /**
     * SecondaryOperationScope allows null returns, so lets test it
     */
    @Test // @org.junit.Ignore
    public void testOperationWhichAllowsNullAllowsNull() {
        ServiceLocator locator = createLocator(
                SecondaryOperationScopeContext.class,
                PerLookupThatUsesNullMeService.class,
                NullMeFactory.class);
        
        OperationManager operationManager = locator.getService(OperationManager.class);
        
        try (OperationHandle<SecondaryOperationScope> firstOperation = operationManager.createAndStartOperation(SECONDARY_OPERATION_ANNOTATION)) {
            firstOperation.resume();
            PerLookupThatUsesNullMeService usesNullMe = locator.getService(PerLookupThatUsesNullMeService.class);
            Assert.assertTrue(usesNullMe.isNullMeNull());
        }
    }
    
    /**
     * Tests the getAllOperations method on OperationManager
     */
    @Test // @org.junit.Ignore
    public void testGetAllOperations() {
        ServiceLocator locator = createLocator(
                BasicOperationScopeContext.class,
                SecondaryOperationScopeContext.class);
        
        OperationManager operationManager = locator.getService(OperationManager.class);
        
        OperationHandle<BasicOperationScope> firstOperation = operationManager.createOperation(BASIC_OPERATION_ANNOTATION);
        OperationHandle<BasicOperationScope> secondOperation = operationManager.createOperation(BASIC_OPERATION_ANNOTATION);
        OperationHandle<BasicOperationScope> thirdOperation = operationManager.createOperation(BASIC_OPERATION_ANNOTATION);
        OperationHandle<BasicOperationScope> fourthOperation = operationManager.createOperation(BASIC_OPERATION_ANNOTATION);
        
        {
            Set<OperationHandle<BasicOperationScope>> allHandles = operationManager.getCurrentOperations(BASIC_OPERATION_ANNOTATION);
            
            Assert.assertEquals(4, allHandles.size());
            
            Assert.assertTrue(allHandles.contains(firstOperation));
            Assert.assertTrue(allHandles.contains(secondOperation));
            Assert.assertTrue(allHandles.contains(thirdOperation));
            Assert.assertTrue(allHandles.contains(fourthOperation));
        }
        
        // Lets activate one and destroy one, make sure we still have the proper counts
        secondOperation.resume();
        thirdOperation.close();
        
        {
            Set<OperationHandle<BasicOperationScope>> allHandles = operationManager.getCurrentOperations(BASIC_OPERATION_ANNOTATION);
            
            Assert.assertEquals(3, allHandles.size());
            
            Assert.assertTrue(allHandles.contains(firstOperation));
            Assert.assertTrue(allHandles.contains(secondOperation));
            Assert.assertFalse(allHandles.contains(thirdOperation));
            Assert.assertTrue(allHandles.contains(fourthOperation));
        }
        
        // Now deactive the one, activate another and add a fifth
        secondOperation.suspend();
        fourthOperation.resume();
        OperationHandle<BasicOperationScope> fifthOperation = operationManager.createOperation(BASIC_OPERATION_ANNOTATION);
        
        {
            Set<OperationHandle<BasicOperationScope>> allHandles = operationManager.getCurrentOperations(BASIC_OPERATION_ANNOTATION);
            
            Assert.assertEquals(4, allHandles.size());
            
            Assert.assertTrue(allHandles.contains(firstOperation));
            Assert.assertTrue(allHandles.contains(secondOperation));
            Assert.assertFalse(allHandles.contains(thirdOperation));
            Assert.assertTrue(allHandles.contains(fourthOperation));
            Assert.assertTrue(allHandles.contains(fifthOperation));
        }
        
        {
            Set<OperationHandle<SecondaryOperationScope>> allHandles = operationManager.getCurrentOperations(SECONDARY_OPERATION_ANNOTATION);
            
            Assert.assertEquals(0, allHandles.size());
            
            Assert.assertFalse(allHandles.contains(firstOperation));
            Assert.assertFalse(allHandles.contains(secondOperation));
            Assert.assertFalse(allHandles.contains(thirdOperation));
            Assert.assertFalse(allHandles.contains(fourthOperation));
            Assert.assertFalse(allHandles.contains(fifthOperation));
        }
    }
    
    /**
     * Tests the getCurrentOperation method on OperationManager
     */
    @Test // @org.junit.Ignore
    public void testGetCurrentOperation() {
        ServiceLocator locator = createLocator(
                BasicOperationScopeContext.class,
                SecondaryOperationScopeContext.class);
        
        OperationManager operationManager = locator.getService(OperationManager.class);
        
        OperationHandle<BasicOperationScope> firstOperation = operationManager.createOperation(BASIC_OPERATION_ANNOTATION);
        OperationHandle<BasicOperationScope> secondOperation = operationManager.createOperation(BASIC_OPERATION_ANNOTATION);
        OperationHandle<SecondaryOperationScope> thirdOperation = operationManager.createOperation(SECONDARY_OPERATION_ANNOTATION);
        
        Assert.assertNull(operationManager.getCurrentOperation(BASIC_OPERATION_ANNOTATION));
        Assert.assertNull(operationManager.getCurrentOperation(SECONDARY_OPERATION_ANNOTATION));
        
        secondOperation.resume();
        
        Assert.assertEquals(secondOperation, operationManager.getCurrentOperation(BASIC_OPERATION_ANNOTATION));
        Assert.assertNull(operationManager.getCurrentOperation(SECONDARY_OPERATION_ANNOTATION));
        
        thirdOperation.resume();
        
        Assert.assertEquals(secondOperation, operationManager.getCurrentOperation(BASIC_OPERATION_ANNOTATION));
        Assert.assertEquals(thirdOperation, operationManager.getCurrentOperation(SECONDARY_OPERATION_ANNOTATION));
        
        secondOperation.suspend();
        
        Assert.assertNull(operationManager.getCurrentOperation(BASIC_OPERATION_ANNOTATION));
        Assert.assertEquals(thirdOperation, operationManager.getCurrentOperation(SECONDARY_OPERATION_ANNOTATION));
        
        firstOperation.resume();
        thirdOperation.suspend();
        secondOperation.suspend();
        
        Assert.assertEquals(firstOperation, operationManager.getCurrentOperation(BASIC_OPERATION_ANNOTATION));
        Assert.assertNull(operationManager.getCurrentOperation(SECONDARY_OPERATION_ANNOTATION));
        
        firstOperation.close();
        
        Assert.assertNull(operationManager.getCurrentOperation(BASIC_OPERATION_ANNOTATION));
        Assert.assertNull(operationManager.getCurrentOperation(SECONDARY_OPERATION_ANNOTATION));
        
        secondOperation.close();
        thirdOperation.close();
    }
    
    /**
     * Tests the shutdownAllOperations method on OperationManager
     */
    @Test // @org.junit.Ignore
    public void testShutdownAllOperations() {
        ServiceLocator locator = createLocator(
                BasicOperationScopeContext.class,
                SecondaryOperationScopeContext.class);
        
        OperationManager operationManager = locator.getService(OperationManager.class);
        
        OperationHandle<BasicOperationScope> basic1Operation = operationManager.createOperation(BASIC_OPERATION_ANNOTATION);
        OperationHandle<BasicOperationScope> basic2Operation = operationManager.createOperation(BASIC_OPERATION_ANNOTATION);
        OperationHandle<BasicOperationScope> basic3Operation = operationManager.createOperation(BASIC_OPERATION_ANNOTATION);
        
        OperationHandle<SecondaryOperationScope> second1Operation = operationManager.createOperation(SECONDARY_OPERATION_ANNOTATION);
        OperationHandle<SecondaryOperationScope> second2Operation = operationManager.createOperation(SECONDARY_OPERATION_ANNOTATION);
        OperationHandle<SecondaryOperationScope> second3Operation = operationManager.createOperation(SECONDARY_OPERATION_ANNOTATION);
        
        basic2Operation.resume();
        basic3Operation.close();
        
        second2Operation.resume();
        second3Operation.close();
        
        Assert.assertEquals(OperationState.SUSPENDED, basic1Operation.getState());
        Assert.assertEquals(OperationState.ACTIVE, basic2Operation.getState());
        Assert.assertEquals(OperationState.CLOSED, basic3Operation.getState());
        
        Assert.assertEquals(OperationState.SUSPENDED, second1Operation.getState());
        Assert.assertEquals(OperationState.ACTIVE, second2Operation.getState());
        Assert.assertEquals(OperationState.CLOSED, second3Operation.getState());
        
        operationManager.shutdownAllOperations(BASIC_OPERATION_ANNOTATION);
        
        Assert.assertEquals(OperationState.CLOSED, basic1Operation.getState());
        Assert.assertEquals(OperationState.CLOSED, basic2Operation.getState());
        Assert.assertEquals(OperationState.CLOSED, basic3Operation.getState());
        
        Assert.assertEquals(OperationState.SUSPENDED, second1Operation.getState());
        Assert.assertEquals(OperationState.ACTIVE, second2Operation.getState());
        Assert.assertEquals(OperationState.CLOSED, second3Operation.getState());
        
        operationManager.shutdownAllOperations(SECONDARY_OPERATION_ANNOTATION);
        
        Assert.assertEquals(OperationState.CLOSED, basic1Operation.getState());
        Assert.assertEquals(OperationState.CLOSED, basic2Operation.getState());
        Assert.assertEquals(OperationState.CLOSED, basic3Operation.getState());
        
        Assert.assertEquals(OperationState.CLOSED, second1Operation.getState());
        Assert.assertEquals(OperationState.CLOSED, second2Operation.getState());
        Assert.assertEquals(OperationState.CLOSED, second3Operation.getState());
    }
    
    /**
     * Tests the shutdownAllOperations method on OperationManager
     */
    @Test // @org.junit.Ignore
    public void testCreateShutdownCreateShutdown() {
        ServiceLocator locator = createLocator(
                BasicOperationScopeContext.class,
                BasicOperationSimpleService.class);
        
        OperationManager operationManager = locator.getService(OperationManager.class);
        
        BasicOperationSimpleService proxy = locator.getService(BasicOperationSimpleService.class);
        try {
            ((ProxyCtl) proxy).__make();
            Assert.fail("Should not have worked, there is no operation yet");
        }
        catch (IllegalStateException ise) {
            // expected
        }
        
        OperationHandle<BasicOperationScope> basicOperation = operationManager.createAndStartOperation(BASIC_OPERATION_ANNOTATION);
        
        Assert.assertNotNull(locator.getService(BasicOperationSimpleService.class).callMe());
        
        basicOperation.suspend();
        
        proxy = locator.getService(BasicOperationSimpleService.class);
        try {
            ((ProxyCtl) proxy).__make();
            Assert.fail("Should not have worked, there is no operation yet");
        }
        catch (IllegalStateException ise) {
            // expected
        }
        
        basicOperation.resume();
        
        Assert.assertNotNull(locator.getService(BasicOperationSimpleService.class).callMe());
        
        operationManager.shutdownAllOperations(BASIC_OPERATION_ANNOTATION);
        
        proxy = locator.getService(BasicOperationSimpleService.class);
        try {
            ((ProxyCtl) proxy).__make();
            Assert.fail("Should not have worked, there is no operation yet");
        }
        catch (IllegalStateException ise) {
            // expected
        }
        
    }
    
    /**
     * Tests that operation services are disposed when the operation is closed
     */
    @Test // @org.junit.Ignore
    public void testOperationServiceDisposedWhenOperationIsClosed() {
        ServiceLocator locator = createLocator(BasicOperationScopeContext.class,
                BasicOperationLifecycleMethods.class);
        
        OperationManager operationManager = locator.getService(OperationManager.class);
        
        OperationHandle<BasicOperationScope> operation1 = operationManager.createOperation(BASIC_OPERATION_ANNOTATION);
        OperationHandle<BasicOperationScope> operation2 = operationManager.createOperation(BASIC_OPERATION_ANNOTATION);
        
        // Start operation1
        operation1.resume();
        
        BasicOperationLifecycleMethods bolm = locator.getService(BasicOperationLifecycleMethods.class);
        Object id1 = bolm.getId();
        Assert.assertNotNull(id1);
        Assert.assertFalse(BasicOperationLifecycleMethods.isClosed(id1));
        
        operation1.suspend();
        
        // Do the same with operation2, ensure it has switched and created a second
        operation2.resume();
        
        Object id2 = bolm.getId();
        Assert.assertNotNull(id2);
        Assert.assertFalse(BasicOperationLifecycleMethods.isClosed(id2));
        
        Assert.assertNotEquals(id1, id2);
        
        // Now lets close operation 2, make sure preDestroy is called
        operation2.close();
        
        Assert.assertTrue(BasicOperationLifecycleMethods.isClosed(id2));
        Assert.assertFalse(BasicOperationLifecycleMethods.isClosed(id1));
        
        // Now close first operation (which is not currently active)
        operation1.close();
        
        Assert.assertTrue(BasicOperationLifecycleMethods.isClosed(id1));
    }
    
    /**
     * Tests that operation services are disposed when the operation is closed even from a different thread
     * @throws InterruptedException 
     */
    @Test // @org.junit.Ignore
    public void testOperationServiceDisposedWhenOperationIsClosedFromDifferentThread() throws InterruptedException {
        ServiceLocator locator = createLocator(BasicOperationScopeContext.class,
                BasicOperationLifecycleMethods.class);
        
        OperationManager operationManager = locator.getService(OperationManager.class);
        
        OperationHandle<BasicOperationScope> operation1 = operationManager.createAndStartOperation(BASIC_OPERATION_ANNOTATION);
        
        BasicOperationLifecycleMethods bolm = locator.getService(BasicOperationLifecycleMethods.class);
        Object id1 = bolm.getId();
        Assert.assertNotNull(id1);
        Assert.assertFalse(BasicOperationLifecycleMethods.isClosed(id1));
        
        // Now close operation from a separate thread
        Object lock = new Object();
        Closer closer = new Closer(operation1, lock);
        
        Thread t = new Thread(closer);
        t.start();
        
        synchronized (lock) {
            while (!OperationState.CLOSED.equals(operation1.getState())) {
                lock.wait();
            }
        }
        
        Assert.assertTrue(BasicOperationLifecycleMethods.isClosed(id1));
    }
    
    /**
     * Tests that a service used in the preDestroy of another service in the
     * same operation scope where the used service is created FIRST
     */
    @Test // @org.junit.Ignore
    public void testServiceUsedInPreDestroyWorks() throws InterruptedException {
        ServiceLocator locator = createLocator(BasicOperationScopeContext.class,
                BasicOperationSimpleService.class,
                BasicOperationUsesServiceInPreDispose.class);
        
        OperationManager operationManager = locator.getService(OperationManager.class);
        
        OperationHandle<BasicOperationScope> operation1 = operationManager.createAndStartOperation(BASIC_OPERATION_ANNOTATION);
        
        // Create the used service FIRST
        BasicOperationSimpleService bass = locator.getService(BasicOperationSimpleService.class);
        bass.callMe();  // Ensures it is truly created
        
        BasicOperationUsesServiceInPreDispose.clean();
        
        BasicOperationUsesServiceInPreDispose bousipd = locator.getService(BasicOperationUsesServiceInPreDispose.class);
        bousipd.instantiateMe();
        
        Assert.assertFalse(BasicOperationUsesServiceInPreDispose.getDisposeSuccess());
        
        // Now close operation from a separate thread
        Object lock = new Object();
        Closer closer = new Closer(operation1, lock);
        
        Thread t = new Thread(closer);
        t.start();
        
        synchronized (lock) {
            while (!OperationState.CLOSED.equals(operation1.getState())) {
                lock.wait();
            }
        }
        
        Assert.assertTrue(BasicOperationUsesServiceInPreDispose.getDisposeSuccess());
    }
    
    /**
     * Tests that a service used in the preDestroy of another service in the
     * same operation scope where the used service is created SECOND
     */
    @Test // @org.junit.Ignore
    public void testServiceUsedInPreDestroyWorksSecond() throws InterruptedException {
        ServiceLocator locator = createLocator(BasicOperationScopeContext.class,
                BasicOperationSimpleService.class,
                BasicOperationUsesServiceInPreDispose.class);
        
        OperationManager operationManager = locator.getService(OperationManager.class);
        
        OperationHandle<BasicOperationScope> operation1 = operationManager.createAndStartOperation(BASIC_OPERATION_ANNOTATION);
        
        BasicOperationUsesServiceInPreDispose.clean();
        
        BasicOperationUsesServiceInPreDispose bousipd = locator.getService(BasicOperationUsesServiceInPreDispose.class);
        bousipd.instantiateMe();
        
        // Create the used service SECOND
        BasicOperationSimpleService bass = locator.getService(BasicOperationSimpleService.class);
        bass.callMe();  // Ensures it is truly created
        
        Assert.assertFalse(BasicOperationUsesServiceInPreDispose.getDisposeSuccess());
        
        // Now close operation from a separate thread
        Object lock = new Object();
        Closer closer = new Closer(operation1, lock);
        
        Thread t = new Thread(closer);
        t.start();
        
        synchronized (lock) {
            while (!OperationState.CLOSED.equals(operation1.getState())) {
                lock.wait();
            }
        }
        
        Assert.assertTrue(BasicOperationUsesServiceInPreDispose.getDisposeSuccess());
    }
    
    /**
     * Tests that a service used in the preDestroy of another service in the
     * same operation scope where the used service is created in the
     * preDestroy should fail since the operation is closgin
     */
    @Test // @org.junit.Ignore
    public void testNewServiceCannotBeCreatedInClosingService() throws InterruptedException {
        ServiceLocator locator = createLocator(BasicOperationScopeContext.class,
                BasicOperationSimpleService.class,
                BasicOperationUsesServiceInPreDispose.class);
        
        OperationManager operationManager = locator.getService(OperationManager.class);
        
        OperationHandle<BasicOperationScope> operation1 = operationManager.createAndStartOperation(BASIC_OPERATION_ANNOTATION);
        
        BasicOperationUsesServiceInPreDispose.clean();
        
        BasicOperationUsesServiceInPreDispose bousipd = locator.getService(BasicOperationUsesServiceInPreDispose.class);
        bousipd.instantiateMe();
        
        Assert.assertFalse(BasicOperationUsesServiceInPreDispose.getDisposeSuccess());
        
        // Now close operation from a separate thread
        Object lock = new Object();
        Closer closer = new Closer(operation1, lock);
        
        Thread t = new Thread(closer);
        t.start();
        
        synchronized (lock) {
            while (!OperationState.CLOSED.equals(operation1.getState())) {
                lock.wait();
            }
        }
        
        Assert.assertFalse(BasicOperationUsesServiceInPreDispose.getDisposeSuccess());
    }
    
    /**
     * Tests that a service used in the preDestroy of another service in the
     * same operation scope where the used service is created in the
     * preDestroy should fail since the operation is closing with the close
     * occuring on the same thread
     */
    @Test // @org.junit.Ignore
    public void testNewServiceCannotBeCreatedInClosingServiceCloseOnSameThread() throws InterruptedException {
        ServiceLocator locator = createLocator(BasicOperationScopeContext.class,
                BasicOperationSimpleService.class,
                BasicOperationUsesServiceInPreDispose.class);
        
        OperationManager operationManager = locator.getService(OperationManager.class);
        
        try (OperationHandle<BasicOperationScope> operation1 = operationManager.createAndStartOperation(BASIC_OPERATION_ANNOTATION)) {
            BasicOperationUsesServiceInPreDispose.clean();
            
            BasicOperationUsesServiceInPreDispose bousipd = locator.getService(BasicOperationUsesServiceInPreDispose.class);
            bousipd.instantiateMe();
            
            Assert.assertFalse(BasicOperationUsesServiceInPreDispose.getDisposeSuccess());
        }
        
        Assert.assertFalse(BasicOperationUsesServiceInPreDispose.getDisposeSuccess());
    }
    
    /**
     * Tests that a service used in the preDestroy of another service in the
     * same operation scope where the used service is created in the
     * preDestroy should fail since the operation is closing with the close
     * occuring on the same thread.  In this case the close is due to the
     * locator shutting down
     */
    @Test // @org.junit.Ignore
    public void testNewServiceCannotBeCreatedInClosingServiceWithShutdown() throws InterruptedException {
        ServiceLocator locator = createLocator(BasicOperationScopeContext.class,
                BasicOperationSimpleService.class,
                BasicOperationUsesServiceInPreDispose.class);
        
        OperationManager operationManager = locator.getService(OperationManager.class);
        
        operationManager.createAndStartOperation(BASIC_OPERATION_ANNOTATION);
        
        BasicOperationUsesServiceInPreDispose.clean();
        
        BasicOperationUsesServiceInPreDispose bousipd = locator.getService(BasicOperationUsesServiceInPreDispose.class);
        bousipd.instantiateMe();
        
        Assert.assertFalse(BasicOperationUsesServiceInPreDispose.getDisposeSuccess());
        
        locator.shutdown();
        
        Assert.assertFalse(BasicOperationUsesServiceInPreDispose.getDisposeSuccess());
    }
    
    /**
     * Tests that a service used in the preDestroy of another service in the
     * same operation scope where the used service is created in the
     * preDestroy should fail since the operation is closing with shutdown on
     * separate thread
     */
    @Test // @org.junit.Ignore
    public void testNewServiceCannotBeCreatedInClosingServiceShutdownOnSeparateThread() throws InterruptedException {
        ServiceLocator locator = createLocator(BasicOperationScopeContext.class,
                BasicOperationSimpleService.class,
                BasicOperationUsesServiceInPreDispose.class);
        
        OperationManager operationManager = locator.getService(OperationManager.class);
        
        OperationHandle<BasicOperationScope> operation1 = operationManager.createAndStartOperation(BASIC_OPERATION_ANNOTATION);
        
        BasicOperationUsesServiceInPreDispose.clean();
        
        BasicOperationUsesServiceInPreDispose bousipd = locator.getService(BasicOperationUsesServiceInPreDispose.class);
        bousipd.instantiateMe();
        
        Assert.assertFalse(BasicOperationUsesServiceInPreDispose.getDisposeSuccess());
        
        // Now close operation from a separate thread
        Object lock = new Object();
        Downer closer = new Downer(locator, lock);
        
        Thread t = new Thread(closer);
        t.start();
        
        synchronized (lock) {
            while (!OperationState.CLOSED.equals(operation1.getState())) {
                lock.wait();
            }
        }
        
        Assert.assertFalse(BasicOperationUsesServiceInPreDispose.getDisposeSuccess());
    }
    
    /**
     * The Factory produces a class proxy (not an interface) which is legal.
     * This test ensures though that the actual method called is called on
     * the "real" service as created by the Factory
     */
    @Test // @org.junit.Ignore
    public void testFactoryProducedOperationScopeIsProxiedProperly() {
        ServiceLocator locator = createLocator(BasicOperationScopeContext.class,
                ProxyDetectorFactory.class,
                SingletonToDetermineProxyService.class);
        
        OperationManager operationManager = locator.getService(OperationManager.class);
        
        operationManager.createAndStartOperation(BASIC_OPERATION_ANNOTATION);
        
        SingletonToDetermineProxyService stdps = locator.getService(SingletonToDetermineProxyService.class);
        Assert.assertFalse(stdps.isCalledOnProxy());
        
    }
    
    /**
     * Tests that services that come from proxies from the same scope
     * and from a Factory honor the not-in-same-scope attribute
     */
    @Test // @org.junit.Ignore
    public void testFactoriesProxiesAndHandlesOhMy() {
        ServiceLocator locator = createLocator(BasicOperationScopeContext.class,
                SimpleSingleton.class,
                Frobnicator.class,
                CaturgiatorFactory.class);
        
        SimpleSingleton root = locator.getService(SimpleSingleton.class);
        
        root.reticulateSplines();
    }
    
    /**
     * Tests that equals will call through to the proxies
     */
    @Test // @org.junit.Ignore
    public void testEquals() {
        ServiceLocator locator = createLocator(BasicOperationScopeContext.class,
                BasicOperationSimpleService.class);
        
        OperationManager operationManager = locator.getService(OperationManager.class);
        OperationHandle<BasicOperationScope> operation1 = operationManager.createAndStartOperation(BASIC_OPERATION_ANNOTATION);
        
        BasicOperationSimpleService service1 = locator.getService(BasicOperationSimpleService.class);
        Assert.assertTrue(service1 instanceof ProxyCtl);
        
        BasicOperationSimpleService service2 = locator.getService(BasicOperationSimpleService.class);
        Assert.assertTrue(service2 instanceof ProxyCtl);
        
        Assert.assertTrue(service1.equals(service2));
        Assert.assertTrue(service2.equals(service1));
        
        operation1.close();
    }
    
    private static class Closer implements Runnable {
        private final Object notifier;
        private final OperationHandle<BasicOperationScope> closeMe;
        
        private Closer(OperationHandle<BasicOperationScope> closeMe, Object notifier) {
            this.notifier = notifier;
            this.closeMe = closeMe;
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            closeMe.close();
            
            synchronized (notifier) {
                notifier.notifyAll();
            }
            
        }
        
    }
    
    private static class Downer implements Runnable {
        private final Object notifier;
        private final ServiceLocator closeMe;
        
        private Downer(ServiceLocator closeMe, Object notifier) {
            this.notifier = notifier;
            this.closeMe = closeMe;
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            closeMe.shutdown();
            
            synchronized (notifier) {
                notifier.notifyAll();
            }
            
        }
        
    }
    
    private static class SimpleThreadedFetcher<T extends Annotation> implements Runnable {
        private final OperationHandle<T> operation;
        private final SingletonThatUsesOperationService singleton;
        private boolean go = false;
        private String retVal;
        
        private SimpleThreadedFetcher(OperationHandle<T> operation, SingletonThatUsesOperationService singleton) {
            this.operation = operation;
            this.singleton = singleton;
        }
        
        private void go() {
            synchronized (this) {
                go = true;
                this.notifyAll();
            }
        }
        
        private void waitForGo() {
            synchronized (this) {
                long waitTime = 20 * 1000;
                while (waitTime > 0L && !go) {
                    long elapsedTime = System.currentTimeMillis();
                    try {
                        this.wait(20 * 1000);
                    }
                    catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    elapsedTime = System.currentTimeMillis() - elapsedTime;
                    waitTime -= elapsedTime;
                }
                
                if (!go) {
                    Assert.fail("Did not get go signal within 20 seconds");
                }
            }
        }
        
        private String waitForResult() {
            synchronized (this) {
                long waitTime = 20 * 1000;
                while (waitTime > 0L && retVal == null) {
                    long elapsedTime = System.currentTimeMillis();
                    try {
                        this.wait(20 * 1000);
                    }
                    catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    elapsedTime = System.currentTimeMillis() - elapsedTime;
                    waitTime -= elapsedTime;
                }
                
                if (retVal == null) {
                    Assert.fail("Did not get result signal within 20 seconds");
                }
                
                return retVal;
            }
        }
        
        private void setResult(String result) {
            synchronized (this) {
                retVal = result;
                this.notifyAll();
            }
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            operation.resume();
            try {
                waitForGo();
                
                setResult(singleton.getCurrentUserName());
            }
            finally {
                operation.suspend();
            }
            
        }
        
    }

}
