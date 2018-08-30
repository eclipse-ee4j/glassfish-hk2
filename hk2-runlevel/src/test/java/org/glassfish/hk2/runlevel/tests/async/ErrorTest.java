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

package org.glassfish.hk2.runlevel.tests.async;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.runlevel.RunLevel;
import org.glassfish.hk2.runlevel.RunLevelController;
import org.glassfish.hk2.runlevel.RunLevelFuture;
import org.glassfish.hk2.runlevel.tests.utilities.Utilities;
import org.junit.Assert;
import org.junit.Test;
import org.jvnet.hk2.annotations.Service;

/**
 * @author jwells
 *
 */
public class ErrorTest {
    private final static int ZERO = 0;
    
    private final static int ONE = 1;
    private final static String SERVICE_ONE = "Service1";
    private final static String SERVICE_ONE_DOWN = "Service1_Down";
    
    private final static int TWO = 2;
    private final static String SERVICE_TWO_ONE = "Service2_1";
    private final static String SERVICE_TWO_TWO = "Service2_2";
    private final static String SERVICE_TWO_THREE = "Service2_3";
    
    private final static String SERVICE_TWO_ONE_DOWN = "Service2_1_Down";
    private final static String SERVICE_TWO_TWO_DOWN = "Service2_2_Down";
    private final static String SERVICE_TWO_THREE_DOWN = "Service2_3_Down";
    
    private final static int THREE = 3;
    private final static String SERVICE_THREE = "Service3";
    private final static String SERVICE_THREE_DOWN = "Service3_Down";
    
    private final static String ERROR_MESSAGE = "Expected exception from postConstruct of Service2_2";
    private final static String ERROR_MESSAGE_2 = "Expected exception from predDestroy of Service1_Down";
    private final static String ERROR_MESSAGE_3 = "Expected exception from predDestroy of Service2_2_Down";
    
    private static boolean errorOutOfTwoTwo = true;
    
    /**
     * Test basic error processing when going going up
     * 
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    @Test
    public void testErrorUp() throws InterruptedException, TimeoutException, ExecutionException {
        errorOutOfTwoTwo = true;
        
        ServiceLocator basicLocator = Utilities.getServiceLocator(
                UpRecorder.class,
                DownRecorder.class,
                Service1.class,
                Service2_1.class,
                Service2_2.class,
                Service2_3.class,
                Service3.class);
        
        UpRecorder upRecorder = basicLocator.getService(UpRecorder.class);
        Assert.assertNotNull(upRecorder);
        upRecorder.getRecordsAndPurge();
        
        DownRecorder downRecorder = basicLocator.getService(DownRecorder.class);
        Assert.assertNotNull(downRecorder);
        downRecorder.getRecordsAndPurge();
        
        RunLevelController controller = basicLocator.getService(RunLevelController.class);
        
        // With only two threads Service2_3 will not come up prior to be cancelled
        controller.setMaximumUseableThreads(1);
        
        RunLevelFuture future = controller.proceedToAsync(THREE);
        
        try {
            future.get(20, TimeUnit.SECONDS);
            Assert.fail("Should have failed due to exception in Service2_2");
        }
        catch (ExecutionException ee) {
            Throwable cause = ee.getCause();
            
            Assert.assertTrue(cause instanceof MultiException);
            MultiException me = (MultiException) cause;
            
            List<Throwable> errors = me.getErrors();
            Assert.assertEquals(1, errors.size());
            
            Throwable cause1 = errors.get(0);
            
            Assert.assertTrue(cause1 instanceof MultiException);
            MultiException me1 = (MultiException) cause1;
            
            List<Throwable> errors1 = me1.getErrors();
            Assert.assertTrue(errors1.size() > 0);
            
            Throwable cause2 = errors1.get(0);
            Assert.assertEquals(cause2.getMessage(), ERROR_MESSAGE); 
        }
        
        Assert.assertEquals(1, controller.getCurrentRunLevel());
        Assert.assertTrue(future.isDone());
        Assert.assertFalse(future.isCancelled());
        
        // Now lets check the services that came up
        List<String> upRecords = upRecorder.getRecordsAndPurge();
        List<String> downRecords = downRecorder.getRecordsAndPurge();
        
        Assert.assertEquals(2, upRecords.size());
        
        Assert.assertEquals(SERVICE_ONE, upRecords.get(0));
        Assert.assertEquals(SERVICE_TWO_ONE, upRecords.get(1));
        
        Assert.assertEquals(1, downRecords.size());
        
        Assert.assertEquals(SERVICE_TWO_ONE, downRecords.get(0));
        
        errorOutOfTwoTwo = false;
        
        future = controller.proceedToAsync(THREE);
        future.get(20, TimeUnit.SECONDS);
        
        upRecords = upRecorder.getRecordsAndPurge();
        
        Assert.assertEquals(4, upRecords.size());
        
        Assert.assertEquals(SERVICE_TWO_ONE, upRecords.get(0));
        Assert.assertEquals(SERVICE_TWO_TWO, upRecords.get(1));
        Assert.assertEquals(SERVICE_TWO_THREE, upRecords.get(2));
        Assert.assertEquals(SERVICE_THREE, upRecords.get(3));
        
    }
    
    /**
     * Test basic error processing when going going down.  In the down case we
     * keep going even when there are errors (but we do alert the listener)
     * 
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    @Test
    public void testErrorDown() throws InterruptedException, TimeoutException, ExecutionException {
        ServiceLocator basicLocator = Utilities.getServiceLocator(
                UpRecorder.class,
                DownRecorder.class,
                Service1_Down.class,
                Service2_1_Down.class,
                Service2_2_Down.class,
                Service2_3_Down.class,
                Service3_Down.class,
                ErrorListener.class);
        
        ErrorListener cancelledListener = basicLocator.getService(ErrorListener.class);
        Assert.assertNotNull(cancelledListener);
        cancelledListener.getAndPurgeReportedErrors();
        
        UpRecorder upRecorder = basicLocator.getService(UpRecorder.class);
        Assert.assertNotNull(upRecorder);
        upRecorder.getRecordsAndPurge();
        
        DownRecorder downRecorder = basicLocator.getService(DownRecorder.class);
        Assert.assertNotNull(downRecorder);
        downRecorder.getRecordsAndPurge();
        
        RunLevelController controller = basicLocator.getService(RunLevelController.class);
        
        RunLevelFuture future = controller.proceedToAsync(THREE);
        future.get();
        
        Assert.assertTrue(future.isDone());
        
        future = controller.proceedToAsync(ONE);
        future.get(20, TimeUnit.SECONDS);
        
        Assert.assertEquals(1, controller.getCurrentRunLevel());
        Assert.assertTrue(future.isDone());
        Assert.assertFalse(future.isCancelled());
        
        // Now lets check the services that came up
        List<String> upRecords = upRecorder.getRecordsAndPurge();
        List<String> downRecords = downRecorder.getRecordsAndPurge();
        
        Assert.assertEquals(5, upRecords.size());
        
        Assert.assertEquals(SERVICE_ONE_DOWN, upRecords.get(0));
        Assert.assertEquals(SERVICE_TWO_ONE_DOWN, upRecords.get(1));
        Assert.assertEquals(SERVICE_TWO_TWO_DOWN, upRecords.get(2));
        Assert.assertEquals(SERVICE_TWO_THREE_DOWN, upRecords.get(3));
        Assert.assertEquals(SERVICE_THREE_DOWN, upRecords.get(4));
        
        Assert.assertEquals(4, downRecords.size());
        
        Assert.assertEquals(SERVICE_THREE_DOWN, downRecords.get(0));
        Assert.assertEquals(SERVICE_TWO_THREE_DOWN, downRecords.get(1));
        Assert.assertEquals(SERVICE_TWO_TWO_DOWN, downRecords.get(2));
        Assert.assertEquals(SERVICE_TWO_ONE_DOWN, downRecords.get(3));
        
        List<Throwable> errorsReported = cancelledListener.getAndPurgeReportedErrors();
        Assert.assertNotNull(errorsReported);
        Assert.assertEquals(1, errorsReported.size());
        
        Throwable s22_error = errorsReported.get(0);
        
        Assert.assertTrue(s22_error instanceof MultiException);
        MultiException s22_me = (MultiException) s22_error;
        Assert.assertTrue(s22_me.getErrors().get(0).getMessage().contains(ERROR_MESSAGE_3));
        
        // Make sure we can keep going down
        future = controller.proceedToAsync(ZERO);
        future.get(20, TimeUnit.SECONDS);
        
        downRecords = downRecorder.getRecordsAndPurge();
        
        Assert.assertEquals(1, downRecords.size());
        
        Assert.assertEquals(SERVICE_ONE_DOWN, downRecords.get(0));
        
        errorsReported = cancelledListener.getAndPurgeReportedErrors();
        Assert.assertNotNull(errorsReported);
        Assert.assertEquals(1, errorsReported.size());
        
        Throwable s1_error = errorsReported.get(0);
        
        Assert.assertTrue(s1_error instanceof MultiException);
        MultiException s1_me = (MultiException) s1_error;
        Assert.assertTrue(s1_me.getErrors().get(0).getMessage().contains(ERROR_MESSAGE_2));
    }
    
    /**
     * Test basic error reporting to the listener when going up
     * 
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    @Test
    public void testErrorUpListener() throws InterruptedException, ExecutionException, TimeoutException {
        errorOutOfTwoTwo = true;
        
        ServiceLocator basicLocator = Utilities.getServiceLocator(
                UpRecorder.class,
                DownRecorder.class,
                Service1.class,
                Service2_1.class,
                Service2_2.class,
                Service2_3.class,
                Service3.class,
                ErrorListener.class);
        
        ErrorListener cancelledListener = basicLocator.getService(ErrorListener.class);
        Assert.assertNotNull(cancelledListener);
        cancelledListener.getAndPurgeReportedErrors();
        
        RunLevelController controller = basicLocator.getService(RunLevelController.class);
        
        RunLevelFuture future = controller.proceedToAsync(THREE);
        
        try {
            future.get(20, TimeUnit.SECONDS);
            Assert.fail("Should have failed due to error from Service2_2");
        }
        catch (ExecutionException ee) {
            // The other test inspects this return, so not doing it here
        }
        
        Assert.assertEquals(1, controller.getCurrentRunLevel());
        Assert.assertTrue(future.isDone());
        Assert.assertFalse(future.isCancelled());
        
        List<Throwable> errorsReported = cancelledListener.getAndPurgeReportedErrors();
        Assert.assertNotNull(errorsReported);
        Assert.assertTrue(errorsReported.size() <=3 && errorsReported.size() >= 1);
        
        for (Throwable s22_error : errorsReported) {
            Assert.assertTrue(s22_error instanceof MultiException);
            MultiException s22_me = (MultiException) s22_error;
        
            Assert.assertTrue(s22_me.getErrors().get(0).getMessage().contains(ERROR_MESSAGE));
        }
    }
    
    @RunLevel(ONE) @Service
    public static class Service1 extends AbstractRunLevelService {

        @Override
        public String getServiceName() {
            return SERVICE_ONE;
        }
        
    }
    
    @RunLevel(TWO) @Service
    public static class Service2_1 extends AbstractRunLevelService {

        @Override
        public String getServiceName() {
            return SERVICE_TWO_ONE;
        }
        
    }
    
    @RunLevel(TWO) @Service
    public static class Service2_2 extends AbstractRunLevelService {
        @SuppressWarnings("unused")
        @Inject
        private Service2_1 dependency;
        
        @PostConstruct
        public void postConstruct() {
            if (errorOutOfTwoTwo) {
                throw new AssertionError(ERROR_MESSAGE);
            }
            
            super.postConstruct();
        }

        @Override
        public String getServiceName() {
            return SERVICE_TWO_TWO;
        }
        
    }
    
    @RunLevel(TWO) @Service
    public static class Service2_3 extends AbstractRunLevelService {
        @SuppressWarnings("unused")
        @Inject
        private Service2_2 dependency;

        @Override
        public String getServiceName() {
            return SERVICE_TWO_THREE;
        }
        
    }
    
    @RunLevel(THREE) @Service
    public static class Service3 extends AbstractRunLevelService {

        @Override
        public String getServiceName() {
            return SERVICE_THREE;
        }
        
    }
    
    @Service
    public static class ErrorListener extends AbstractRunLevelListener {
        
    }
    
    @RunLevel(ONE) @Service
    public static class Service1_Down extends AbstractRunLevelService {

        @Override
        public String getServiceName() {
            return SERVICE_ONE_DOWN;
        }
        
        @PreDestroy
        public void preDestroy() {
            super.preDestroy();
            
            throw new AssertionError(ERROR_MESSAGE_2);
        }
        
    }
    
    @RunLevel(TWO) @Service
    public static class Service2_1_Down extends AbstractRunLevelService {

        @Override
        public String getServiceName() {
            return SERVICE_TWO_ONE_DOWN;
        }
        
    }
    
    @RunLevel(TWO) @Service
    public static class Service2_2_Down extends AbstractRunLevelService {
        @SuppressWarnings("unused")
        @Inject
        private Service2_1_Down dependency;
        
        @PreDestroy
        public void preDestroy() {
            super.preDestroy();
            
            throw new AssertionError(ERROR_MESSAGE_3);
        }

        @Override
        public String getServiceName() {
            return SERVICE_TWO_TWO_DOWN;
        }
        
    }
    
    @RunLevel(TWO) @Service
    public static class Service2_3_Down extends AbstractRunLevelService {
        @SuppressWarnings("unused")
        @Inject
        private Service2_2_Down dependency;

        @Override
        public String getServiceName() {
            return SERVICE_TWO_THREE_DOWN;
        }
        
    }
    
    @RunLevel(THREE) @Service
    public static class Service3_Down extends AbstractRunLevelService {

        @Override
        public String getServiceName() {
            return SERVICE_THREE_DOWN;
        }
        
    }

}
