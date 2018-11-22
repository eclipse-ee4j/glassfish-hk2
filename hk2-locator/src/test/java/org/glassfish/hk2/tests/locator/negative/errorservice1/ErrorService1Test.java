/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.locator.negative.errorservice1;

import java.util.List;

import javax.inject.Singleton;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ErrorInformation;
import org.glassfish.hk2.api.ErrorType;
import org.glassfish.hk2.api.FactoryDescriptors;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Assert;;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class ErrorService1Test {
    public static final String ERROR_STRING = "Expected Exception ErrorService1Test";
    
    /**
     * Tests that a service that fails in the constructor has the error passed to the error service
     */
    @Test
    public void testServiceFailsInConstructor() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(RecordingErrorService.class,
                ServiceFailsInConstructor.class);
        
        ActiveDescriptor<?> serviceDescriptor = locator.getBestDescriptor(BuilderHelper.createContractFilter(
                ServiceFailsInConstructor.class.getName()));
        Assert.assertNotNull(serviceDescriptor);
        
        try {
            locator.getService(ServiceFailsInConstructor.class);
            Assert.fail("Should have failed");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage().contains(ERROR_STRING));
        }
        
        List<ErrorInformation> errors = locator.getService(RecordingErrorService.class).getAndClearErrors();
        
        Assert.assertEquals(1, errors.size());
        
        ErrorInformation ei = errors.get(0);
        
        Assert.assertEquals(ErrorType.SERVICE_CREATION_FAILURE, ei.getErrorType());
        Assert.assertEquals(serviceDescriptor, ei.getDescriptor());
        Assert.assertNull(ei.getInjectee());
        
        Throwable associatedException = ei.getAssociatedException();
        Assert.assertTrue(associatedException.getMessage().contains(ERROR_STRING));
    }
    
    /**
     * Tests that a service that fails in an initializer method has the error passed to the error service
     */
    @Test
    public void testServiceFailsInInitializer() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(RecordingErrorService.class,
                SimpleService.class,
                ServiceFailsInInitializerMethod.class);
        
        ActiveDescriptor<?> serviceDescriptor = locator.getBestDescriptor(BuilderHelper.createContractFilter(
                ServiceFailsInInitializerMethod.class.getName()));
        Assert.assertNotNull(serviceDescriptor);
        
        try {
            locator.getService(ServiceFailsInInitializerMethod.class);
            Assert.fail("Should have failed");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage().contains(ERROR_STRING));
        }
        
        List<ErrorInformation> errors = locator.getService(RecordingErrorService.class).getAndClearErrors();
        
        Assert.assertEquals(1, errors.size());
        
        ErrorInformation ei = errors.get(0);
        
        Assert.assertEquals(ErrorType.SERVICE_CREATION_FAILURE, ei.getErrorType());
        Assert.assertEquals(serviceDescriptor, ei.getDescriptor());
        Assert.assertNull(ei.getInjectee());
        
        Throwable associatedException = ei.getAssociatedException();
        Assert.assertTrue(associatedException.getMessage().contains(ERROR_STRING));
    }
    
    /**
     * Tests that a service that fails in an postConstruct method has the error passed to the error service
     */
    @Test
    public void testServiceFailsInPostConstruct() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(RecordingErrorService.class,
                ServiceFailsInPostConstruct.class);
        
        ActiveDescriptor<?> serviceDescriptor = locator.getBestDescriptor(BuilderHelper.createContractFilter(
                ServiceFailsInPostConstruct.class.getName()));
        Assert.assertNotNull(serviceDescriptor);
        
        try {
            locator.getService(ServiceFailsInPostConstruct.class);
            Assert.fail("Should have failed");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage().contains(ERROR_STRING));
        }
        
        List<ErrorInformation> errors = locator.getService(RecordingErrorService.class).getAndClearErrors();
        
        Assert.assertEquals(1, errors.size());
        
        ErrorInformation ei = errors.get(0);
        
        Assert.assertEquals(ErrorType.SERVICE_CREATION_FAILURE, ei.getErrorType());
        Assert.assertEquals(serviceDescriptor, ei.getDescriptor());
        Assert.assertNull(ei.getInjectee());
        
        Throwable associatedException = ei.getAssociatedException();
        Assert.assertTrue(associatedException.getMessage().contains(ERROR_STRING));
    }
    
    /**
     * Tests that a service that comes from a factory that fails in provide
     */
    @Test
    public void testFactorServiceFailsInProvide() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(RecordingErrorService.class);
        
        FactoryDescriptors fds = BuilderHelper.link(FactoryFailsInProvideService.class)
                     .to(SimpleService.class.getName())
                     .in(Singleton.class.getName())
                     .buildFactory(Singleton.class.getName());
        
        ServiceLocatorUtilities.addFactoryDescriptors(locator, fds);
        
        ActiveDescriptor<?> serviceDescriptor = locator.getBestDescriptor(BuilderHelper.createContractFilter(
                SimpleService.class.getName()));
        Assert.assertNotNull(serviceDescriptor);
        
        try {
            locator.getService(SimpleService.class);
            Assert.fail("Should have failed");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage().contains(ERROR_STRING));
        }
        
        List<ErrorInformation> errors = locator.getService(RecordingErrorService.class).getAndClearErrors();
        
        Assert.assertEquals(1, errors.size());
        
        ErrorInformation ei = errors.get(0);
        
        Assert.assertEquals(ErrorType.SERVICE_CREATION_FAILURE, ei.getErrorType());
        Assert.assertEquals(serviceDescriptor, ei.getDescriptor());
        Assert.assertNull(ei.getInjectee());
        
        Throwable associatedException = ei.getAssociatedException();
        Assert.assertTrue(associatedException.getMessage().contains(ERROR_STRING));
    }
    
    /**
     * Tests that a service that fails but tells HK2 to NOT report the failure to the error handler service
     */
    @Test
    public void testSilentFailureInPostConstruct() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(RecordingErrorService.class,
                ServiceDirectsNoErrorService.class);
        
        ActiveDescriptor<?> serviceDescriptor = locator.getBestDescriptor(BuilderHelper.createContractFilter(
                ServiceDirectsNoErrorService.class.getName()));
        Assert.assertNotNull(serviceDescriptor);
        
        try {
            locator.getService(ServiceDirectsNoErrorService.class);
            Assert.fail("Should have failed");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage().contains(ERROR_STRING));
        }
        
        List<ErrorInformation> errors = locator.getService(RecordingErrorService.class).getAndClearErrors();
        
        Assert.assertEquals(0, errors.size());
    }
    
    /**
     * Tests that a third-party service that fails in create
     */
    @Test
    public void testFailingThirdPartyService() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(RecordingErrorService.class);
        
        AlwaysFailActiveDescriptor thirdPartyDescriptor = new AlwaysFailActiveDescriptor();
        
        ServiceLocatorUtilities.addOneDescriptor(locator, thirdPartyDescriptor);
        
        ActiveDescriptor<?> serviceDescriptor = locator.getBestDescriptor(BuilderHelper.createContractFilter(
                SimpleService.class.getName()));
        Assert.assertNotNull(serviceDescriptor);
        
        try {
            locator.getService(SimpleService.class);
            Assert.fail("Should have failed");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage().contains(ERROR_STRING));
        }
        
        List<ErrorInformation> errors = locator.getService(RecordingErrorService.class).getAndClearErrors();
        
        Assert.assertEquals(1, errors.size());
        
        ErrorInformation ei = errors.get(0);
        
        Assert.assertEquals(ErrorType.SERVICE_CREATION_FAILURE, ei.getErrorType());
        Assert.assertEquals(serviceDescriptor, ei.getDescriptor());
        Assert.assertNull(ei.getInjectee());
        
        Throwable associatedException = ei.getAssociatedException();
        Assert.assertTrue(associatedException.getMessage().contains(ERROR_STRING));
    }
    
    /**
     * Tests that a service that fails during destruction is reported to the error service
     */
    @Test
    public void testFailsInDestroy() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(RecordingErrorService.class,
                ServiceFailsInDestructor.class);
        
        ActiveDescriptor<?> serviceDescriptor = locator.getBestDescriptor(BuilderHelper.createContractFilter(
                ServiceFailsInDestructor.class.getName()));
        Assert.assertNotNull(serviceDescriptor);
        
        try (ServiceHandle<ServiceFailsInDestructor> handle = locator.getServiceHandle(ServiceFailsInDestructor.class)) {
            Assert.assertNotNull(handle.getService());
        }
        
        List<ErrorInformation> errors = locator.getService(RecordingErrorService.class).getAndClearErrors();
        
        Assert.assertEquals(1, errors.size());
        
        ErrorInformation ei = errors.get(0);
        
        Assert.assertEquals(ErrorType.SERVICE_DESTRUCTION_FAILURE, ei.getErrorType());
        Assert.assertEquals(serviceDescriptor, ei.getDescriptor());
        Assert.assertNull(ei.getInjectee());
        
        Throwable associatedException = ei.getAssociatedException();
        Assert.assertTrue(associatedException.getMessage().contains(ERROR_STRING));
    }
    
    /**
     * Tests that a service that is created by a factory where the factory dispose method fails
     */
    @Test
    public void testFactorServiceFailsInDispose() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(RecordingErrorService.class);
        
        FactoryDescriptors fds = BuilderHelper.link(FactoryFailsInDisposeService.class)
                     .to(SimpleService.class.getName())
                     .in(Singleton.class.getName())
                     .buildFactory(Singleton.class.getName());
        
        ServiceLocatorUtilities.addFactoryDescriptors(locator, fds);
        
        ActiveDescriptor<?> serviceDescriptor = locator.getBestDescriptor(BuilderHelper.createContractFilter(
                SimpleService.class.getName()));
        Assert.assertNotNull(serviceDescriptor);
        
        try (ServiceHandle<SimpleService> handle = locator.getServiceHandle(SimpleService.class)) {
            Assert.assertNotNull(handle);
            Assert.assertNotNull(handle.getService());
        }
        
        List<ErrorInformation> errors = locator.getService(RecordingErrorService.class).getAndClearErrors();
        
        Assert.assertEquals(1, errors.size());
        
        ErrorInformation ei = errors.get(0);
        
        Assert.assertEquals(ErrorType.SERVICE_DESTRUCTION_FAILURE, ei.getErrorType());
        Assert.assertEquals(serviceDescriptor, ei.getDescriptor());
        Assert.assertNull(ei.getInjectee());
        
        Throwable associatedException = ei.getAssociatedException();
        Assert.assertTrue(associatedException.getMessage().contains(ERROR_STRING));
    }
    
    /**
     * Tests that a third-party service that fails in dispose
     */
    @Test
    public void testFailingInDisposeThirdPartyService() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(RecordingErrorService.class);
        
        AlwaysFailInDisposeActiveDescriptor thirdPartyDescriptor = new AlwaysFailInDisposeActiveDescriptor();
        
        ServiceLocatorUtilities.addOneDescriptor(locator, thirdPartyDescriptor);
        
        ActiveDescriptor<?> serviceDescriptor = locator.getBestDescriptor(BuilderHelper.createContractFilter(
                SimpleService.class.getName()));
        Assert.assertNotNull(serviceDescriptor);
        
        ServiceHandle<SimpleService> handle = locator.getServiceHandle(SimpleService.class);
        Assert.assertNotNull(handle);
        Assert.assertNotNull(handle.getService());
        
        handle.destroy();
        
        List<ErrorInformation> errors = locator.getService(RecordingErrorService.class).getAndClearErrors();
        
        Assert.assertEquals(1, errors.size());
        
        ErrorInformation ei = errors.get(0);
        
        Assert.assertEquals(ErrorType.SERVICE_DESTRUCTION_FAILURE, ei.getErrorType());
        Assert.assertEquals(serviceDescriptor, ei.getDescriptor());
        Assert.assertNull(ei.getInjectee());
        
        Throwable associatedException = ei.getAssociatedException();
        Assert.assertTrue(associatedException.getMessage().contains(ERROR_STRING));
    }

}
