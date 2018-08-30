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

package org.glassfish.hk2.configuration.hub.test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.configuration.hub.api.PrepareFailedException;
import org.glassfish.hk2.configuration.hub.api.RollbackFailedException;
import org.glassfish.hk2.configuration.hub.api.Type;
import org.glassfish.hk2.configuration.hub.api.WriteableBeanDatabase;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class HubResourceTest extends HubTestBase {
    private final static String EMPTY_TYPE = "EmptyType";
    private final static String TYPE_TWELVE = "TypeTwelve";
    
    private final static String NAME_PROPERTY = "name";
    
    private final static String ALICE = "Alice";
    private final static String DAVE = "Dave";
    
    public final static String PREPARE_FAIL_MESSAGE = "Expected prepare exception";
    public final static String COMMIT_FAIL_MESSAGE = "Expected commit exception";
    
    private Map<String, Object> oneFieldBeanLikeMap = new HashMap<String, Object>();
    
    @Before
    public void before() {
        super.before();
        
        oneFieldBeanLikeMap.put(NAME_PROPERTY, ALICE);
    }
    
    /**
     * Tests we can add an empty type to the database
     */
    @Test
    // @org.junit.Ignore
    public void testAddEmptyType() {
        Assert.assertNull(hub.getCurrentDatabase().getType(EMPTY_TYPE));
        
        WriteableBeanDatabase wbd = hub.getWriteableDatabaseCopy();
        wbd.addType(EMPTY_TYPE);
        
        DynamicConfiguration cd = dcs.createDynamicConfiguration();
        
        cd.registerTwoPhaseResources(wbd.getTwoPhaseResource());
        
        cd.commit();
        
        try {
            Type emptyType = hub.getCurrentDatabase().getType(EMPTY_TYPE);
            
            Assert.assertNotNull(emptyType);
            Assert.assertEquals(0, emptyType.getInstances().size());
        }
        finally {
            // Cleanup
            wbd = hub.getWriteableDatabaseCopy();
            wbd.removeType(EMPTY_TYPE);
            wbd.commit();
        }
        
    }
    
    /**
     * Tests that all listeners are called, sunny day scenario
     */
    @Test
    // @org.junit.Ignore
    public void testAllListenersPrepareAndCommitInvoked() {
        AbstractCountingListener listener1 = new AbstractCountingListener();
        AbstractCountingListener listener2 = new AbstractCountingListener();
        AbstractCountingListener listener3 = new AbstractCountingListener();
        
        LinkedList<ActiveDescriptor<?>> added = new LinkedList<ActiveDescriptor<?>>();
        added.add(ServiceLocatorUtilities.addOneConstant(testLocator, listener1));
        added.add(ServiceLocatorUtilities.addOneConstant(testLocator, listener2));
        added.add(ServiceLocatorUtilities.addOneConstant(testLocator, listener3));
        
        try {
            GenericJavaBean newBean = new GenericJavaBean();
            
            addTypeAndInstance(TYPE_TWELVE, DAVE, newBean, true);
            
            Assert.assertEquals(1, listener1.getNumPreparesCalled());
            Assert.assertEquals(1, listener1.getNumCommitsCalled());
            Assert.assertEquals(0, listener1.getNumRollbackCalled());
            
            Assert.assertEquals(1, listener2.getNumPreparesCalled());
            Assert.assertEquals(1, listener2.getNumCommitsCalled());
            Assert.assertEquals(0, listener2.getNumRollbackCalled());
            
            Assert.assertEquals(1, listener3.getNumPreparesCalled());
            Assert.assertEquals(1, listener3.getNumCommitsCalled());
            Assert.assertEquals(0, listener3.getNumRollbackCalled());
        }
        finally {
            for (ActiveDescriptor<?> removeMe : added) {
                ServiceLocatorUtilities.removeOneDescriptor(testLocator, removeMe);
            }
            
            removeType(TYPE_TWELVE, true);
        }
        
    }
    
    /**
     * Tests that all listeners are called, sunny day scenario
     */
    @Test
    // @org.junit.Ignore
    public void testMiddleListenerThrowsExceptionInPrepare() {
        AbstractCountingListener listener1 = new AbstractCountingListener();
        PrepareFailListener listener2 = new PrepareFailListener();
        AbstractCountingListener listener3 = new AbstractCountingListener();
        
        LinkedList<ActiveDescriptor<?>> added = new LinkedList<ActiveDescriptor<?>>();
        added.add(ServiceLocatorUtilities.addOneConstant(testLocator, listener1));
        added.add(ServiceLocatorUtilities.addOneConstant(testLocator, listener2));
        added.add(ServiceLocatorUtilities.addOneConstant(testLocator, listener3));
        
        try {
            GenericJavaBean newBean = new GenericJavaBean();
            
            try {
                addTypeAndInstance(TYPE_TWELVE, DAVE, newBean, true);
                Assert.fail("Prepare threw exception, but commit succeeded");
            }
            catch (MultiException me) {
                Assert.assertTrue(me.toString().contains(PREPARE_FAIL_MESSAGE));
                
                boolean found = false;
                for (Throwable inner : me.getErrors()) {
                    if (inner instanceof PrepareFailedException) {
                        Assert.assertFalse("Should only be ONE instance of PrepareFailedException, but there is at least two in " + me, found);
                        found = true;
                    }
                }
                
                Assert.assertTrue(found);
            }
            
            Assert.assertEquals(1, listener1.getNumPreparesCalled());
            Assert.assertEquals(0, listener1.getNumCommitsCalled());
            Assert.assertEquals(1, listener1.getNumRollbackCalled());
            
            Assert.assertEquals(1, listener2.getNumPreparesCalled());
            Assert.assertEquals(0, listener2.getNumCommitsCalled());
            Assert.assertEquals(0, listener2.getNumRollbackCalled());
            
            Assert.assertEquals(0, listener3.getNumPreparesCalled());
            Assert.assertEquals(0, listener3.getNumCommitsCalled());
            Assert.assertEquals(0, listener3.getNumRollbackCalled());
        }
        finally {
            for (ActiveDescriptor<?> removeMe : added) {
                ServiceLocatorUtilities.removeOneDescriptor(testLocator, removeMe);
            }
            
            removeType(TYPE_TWELVE, true);
        }
    }
    
    /**
     * Tests that all listeners are called when one fails in commit
     */
    @Test
    // @org.junit.Ignore
    public void testMiddleListenerThrowsExceptionInCommit() {
        AbstractCountingListener listener1 = new AbstractCountingListener();
        CommitFailListener listener2 = new CommitFailListener();
        AbstractCountingListener listener3 = new AbstractCountingListener();
        
        LinkedList<ActiveDescriptor<?>> added = new LinkedList<ActiveDescriptor<?>>();
        added.add(ServiceLocatorUtilities.addOneConstant(testLocator, listener1));
        added.add(ServiceLocatorUtilities.addOneConstant(testLocator, listener2));
        added.add(ServiceLocatorUtilities.addOneConstant(testLocator, listener3));
        
        try {
            GenericJavaBean newBean = new GenericJavaBean();
            
            // Activate failures do NOT get thrown up by DynamicConfiguration.commit
            addTypeAndInstance(TYPE_TWELVE, DAVE, newBean, true);
            
            Assert.assertEquals(1, listener1.getNumPreparesCalled());
            Assert.assertEquals(1, listener1.getNumCommitsCalled());
            Assert.assertEquals(0, listener1.getNumRollbackCalled());
            
            Assert.assertEquals(1, listener2.getNumPreparesCalled());
            Assert.assertEquals(1, listener2.getNumCommitsCalled());
            Assert.assertEquals(0, listener2.getNumRollbackCalled());
            
            Assert.assertEquals(1, listener3.getNumPreparesCalled());
            Assert.assertEquals(1, listener3.getNumCommitsCalled());
            Assert.assertEquals(0, listener3.getNumRollbackCalled());
        }
        finally {
            for (ActiveDescriptor<?> removeMe : added) {
                ServiceLocatorUtilities.removeOneDescriptor(testLocator, removeMe);
            }
            
            removeType(TYPE_TWELVE, true);
        }
    }
    
    /**
     * Tests that all listeners are called when one fails in prepare and
     * several others fail in rollback
     */
    @Test
    // @org.junit.Ignore
    public void testAnExceptionInPrepareAndSeveralRollbacksAllGetReported() {
        RollbackFailListener listener1 = new RollbackFailListener();
        AbstractCountingListener listener2 = new AbstractCountingListener();
        RollbackFailListener listener3 = new RollbackFailListener();
        PrepareFailListener listener4 = new PrepareFailListener();
        
        LinkedList<ActiveDescriptor<?>> added = new LinkedList<ActiveDescriptor<?>>();
        added.add(ServiceLocatorUtilities.addOneConstant(testLocator, listener1));
        added.add(ServiceLocatorUtilities.addOneConstant(testLocator, listener2));
        added.add(ServiceLocatorUtilities.addOneConstant(testLocator, listener3));
        added.add(ServiceLocatorUtilities.addOneConstant(testLocator, listener4));
        
        try {
            GenericJavaBean newBean = new GenericJavaBean();
            
            try {
                addTypeAndInstance(TYPE_TWELVE, DAVE, newBean, true);
                Assert.fail("Prepare threw exception, but commit succeeded");
            }
            catch (MultiException me) {
                Assert.assertTrue(me.toString().contains(PREPARE_FAIL_MESSAGE));
                
                boolean found = false;
                int rollbackErrorsReported = 0;
                for (Throwable inner : me.getErrors()) {
                    if (inner instanceof PrepareFailedException) {
                        Assert.assertFalse("Should only be ONE instance of PrepareFailedException, but there is at least two in " + me, found);
                        found = true;
                    }
                    else if (inner instanceof RollbackFailedException) {
                        rollbackErrorsReported++;
                    }
                }
                
                Assert.assertTrue(found);
                Assert.assertEquals(2, rollbackErrorsReported);
            }
            
            Assert.assertEquals(1, listener1.getNumPreparesCalled());
            Assert.assertEquals(0, listener1.getNumCommitsCalled());
            Assert.assertEquals(1, listener1.getNumRollbackCalled());
            
            Assert.assertEquals(1, listener2.getNumPreparesCalled());
            Assert.assertEquals(0, listener2.getNumCommitsCalled());
            Assert.assertEquals(1, listener2.getNumRollbackCalled());
            
            Assert.assertEquals(1, listener3.getNumPreparesCalled());
            Assert.assertEquals(0, listener3.getNumCommitsCalled());
            Assert.assertEquals(1, listener3.getNumRollbackCalled());
            
            Assert.assertEquals(1, listener4.getNumPreparesCalled());
            Assert.assertEquals(0, listener4.getNumCommitsCalled());
            Assert.assertEquals(0, listener4.getNumRollbackCalled());
        }
        finally {
            for (ActiveDescriptor<?> removeMe : added) {
                ServiceLocatorUtilities.removeOneDescriptor(testLocator, removeMe);
            }
            
            removeType(TYPE_TWELVE, true);
        }
    }
    
    /**
     * Tests that all commit errors are reported
     */
    @Test
    // @org.junit.Ignore
    public void testMultipleCommitErrorsAllGetReported() {
        CommitFailListener listener1 = new CommitFailListener();
        CommitFailListener listener2 = new CommitFailListener();
        
        LinkedList<ActiveDescriptor<?>> added = new LinkedList<ActiveDescriptor<?>>();
        added.add(ServiceLocatorUtilities.addOneConstant(testLocator, listener1));
        added.add(ServiceLocatorUtilities.addOneConstant(testLocator, listener2));
        
        try {
            GenericJavaBean newBean = new GenericJavaBean();
            
            addTypeAndInstance(TYPE_TWELVE, DAVE, newBean, true);
            
            Assert.assertEquals(1, listener1.getNumPreparesCalled());
            Assert.assertEquals(1, listener1.getNumCommitsCalled());
            Assert.assertEquals(0, listener1.getNumRollbackCalled());
            
            Assert.assertEquals(1, listener2.getNumPreparesCalled());
            Assert.assertEquals(1, listener2.getNumCommitsCalled());
            Assert.assertEquals(0, listener2.getNumRollbackCalled());
        }
        finally {
            for (ActiveDescriptor<?> removeMe : added) {
                ServiceLocatorUtilities.removeOneDescriptor(testLocator, removeMe);
            }
            
            removeType(TYPE_TWELVE, true);
        }
    }

}
