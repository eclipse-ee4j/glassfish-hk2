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

package org.glassfish.hk2.tests.locator.negative.validation;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.ErrorInformation;
import org.glassfish.hk2.api.ErrorType;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class ValidateThrowsTest {
    public final static String EXPECTED_EXCEPTION = "Expected Exception";
    
    /**
     * Tests that an exception during lookup operation does not throw
     * an exception, but instead invisibles the service
     */
    @Test // @org.junit.Ignore
    public void testExceptionInValidateDuringLookupDoesNotThrow() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(ValidationServiceImpl.class,
                SimpleService.class);
        
        ValidationServiceImpl vsi = locator.getService(ValidationServiceImpl.class);
        SimpleService ss = locator.getService(SimpleService.class);
        Assert.assertNotNull(ss);
        
        vsi.setThrowFromValidate(true);
        
        ss = locator.getService(SimpleService.class);
        Assert.assertNull(ss);
        
        vsi.setThrowFromValidate(false);
        
        ss = locator.getService(SimpleService.class);
        Assert.assertNotNull(ss);
    }
    
    /**
     * Tests that an exception during bind operation causes MultiException
     * to be thrown
     */
    @Test // @org.junit.Ignore
    public void testExceptionInValidateDuringBindDoesNotThrow() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(ValidationServiceImpl.class);
        
        ValidationServiceImpl vsi = locator.getService(ValidationServiceImpl.class);
        
        vsi.setThrowFromValidate(true);
        
        try {
            ServiceLocatorUtilities.addClasses(locator, SimpleService.class);
            Assert.fail("Should have failed with MultiException");
        }
        catch (MultiException me) {
            // This is ok
        }
        catch (Throwable th) {
            // Any other exception is a fail
            if (th instanceof RuntimeException) {
                throw (RuntimeException) th;
            }
            
            throw new RuntimeException(th);
        }
        
        // Make sure service was not actually added
        SimpleService ss = locator.getService(SimpleService.class);
        Assert.assertNull(ss);
    }
    
    /**
     * Tests that an exception during unbind operation causes MultiException
     * to be thrown
     */
    @Test // @org.junit.Ignore
    public void testExceptionInValidateDuringUnBindDoesNotThrow() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(ValidationServiceImpl.class);
        
        ValidationServiceImpl vsi = locator.getService(ValidationServiceImpl.class);
        
        ActiveDescriptor<?> disposeMe = ServiceLocatorUtilities.addClasses(locator, SimpleService.class).get(0);
        
        vsi.setThrowFromValidate(true);
        
        try {
            ServiceLocatorUtilities.removeOneDescriptor(locator, disposeMe);
            Assert.fail("Should have failed with MultiException");
        }
        catch (MultiException me) {
            // Correct
        }
        catch (Throwable th) {
            // Any other exception is a fail
            if (th instanceof RuntimeException) {
                throw (RuntimeException) th;
            }
            
            throw new RuntimeException(th);
        }
        
        vsi.setThrowFromValidate(false);
        
        // Make sure service was not actually unbound
        SimpleService ss = locator.getService(SimpleService.class);
        Assert.assertNotNull(ss);
    }
    
    private void checkErrorInformation(ErrorInformation ei) {
        Assert.assertEquals(ErrorType.VALIDATE_FAILURE, ei.getErrorType());
        Assert.assertNull(ei.getInjectee());
        Assert.assertTrue(ei.getAssociatedException().toString().contains(EXPECTED_EXCEPTION));
        
        Descriptor d = ei.getDescriptor();
        Assert.assertEquals(d.getImplementation(), SimpleService.class.getName());
    }
    
    /**
     * Tests that error handler is invoked properly when looking
     * up a service where the validator throws an exception
     */
    @Test // @org.junit.Ignore
    public void testErrorHandlerInvokedWhenValidateThrowsInLookup() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(ValidationServiceImpl.class,
                SimpleService.class,
                ErrorServiceImpl.class);
        
        ValidationServiceImpl vsi = locator.getService(ValidationServiceImpl.class);
        
        vsi.setThrowFromValidate(true);
        
        SimpleService ss = locator.getService(SimpleService.class);
        Assert.assertNull(ss);
        
        vsi.setThrowFromValidate(false);
        
        ErrorServiceImpl esi = locator.getService(ErrorServiceImpl.class);
        ErrorInformation ei = esi.getLastError();
        
        checkErrorInformation(ei);
    }
    
    /**
     * Tests that error handler is invoked properly when binding
     * a service where the validator throws an exception
     */
    @Test // @org.junit.Ignore
    public void testErrorHandlerInvokedWhenValidateThrowsInBind() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(ValidationServiceImpl.class,
                ErrorServiceImpl.class);
        
        ValidationServiceImpl vsi = locator.getService(ValidationServiceImpl.class);
        ErrorServiceImpl esi = locator.getService(ErrorServiceImpl.class);
        
        vsi.setThrowFromValidate(true);
        
        try {
            ServiceLocatorUtilities.addClasses(locator, SimpleService.class);
            Assert.fail("Should have failed with MultiException");
        }
        catch (MultiException me) {
            // Correct
        }
        catch (Throwable th) {
            // Any other exception is a fail
            if (th instanceof RuntimeException) {
                throw (RuntimeException) th;
            }
            
            throw new RuntimeException(th);
        }
        
        ErrorInformation ei = esi.getLastError();
        
        checkErrorInformation(ei);
    }
    
    /**
     * Tests that error handler is invoked properly when binding
     * a service where the validator throws an exception
     */
    @Test // @org.junit.Ignore
    public void testErrorHandlerInvokedWhenValidateThrowsInUnBind() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(ValidationServiceImpl.class,
                ErrorServiceImpl.class);
        
        ValidationServiceImpl vsi = locator.getService(ValidationServiceImpl.class);
        ErrorServiceImpl esi = locator.getService(ErrorServiceImpl.class);
        ActiveDescriptor<?> disposeMe = ServiceLocatorUtilities.addClasses(locator, SimpleService.class).get(0);
        
        vsi.setThrowFromValidate(true);
        
        try {
            ServiceLocatorUtilities.removeOneDescriptor(locator, disposeMe);
            Assert.fail("Should have failed with MultiException");
        }
        catch (MultiException me) {
            // Correct
        }
        catch (Throwable th) {
            // Any other exception is a fail
            if (th instanceof RuntimeException) {
                throw (RuntimeException) th;
            }
            
            throw new RuntimeException(th);
        }
        
        ErrorInformation ei = esi.getLastError();
        checkErrorInformation(ei);
    }
    
    /**
     * Tests that an exception during lookup operation AND from the
     * error service does not throw an exception, but instead invisibles the service
     */
    @Test // @org.junit.Ignore
    public void testExceptionInValidateAndOnFailureDuringLookupDoesNotThrow() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(ValidationServiceImpl.class,
                SimpleService.class,
                ErrorServiceImpl.class);
        
        ValidationServiceImpl vsi = locator.getService(ValidationServiceImpl.class);
        ErrorServiceImpl esi = locator.getService(ErrorServiceImpl.class);
        
        SimpleService ss = locator.getService(SimpleService.class);
        Assert.assertNotNull(ss);
        
        vsi.setThrowFromValidate(true);
        esi.setThrowInOnFailure(true);
        
        ss = locator.getService(SimpleService.class);
        Assert.assertNull(ss);
        
        vsi.setThrowFromValidate(false);
        
        ss = locator.getService(SimpleService.class);
        Assert.assertNotNull(ss);
    }
    
    /**
     * Tests that an exception during bind operation causes MultiException
     * to be thrown
     */
    @Test // @org.junit.Ignore
    public void testExceptionInValidateDuringBindAndOnFailureDoesNotThrow() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(ValidationServiceImpl.class,
                ErrorServiceImpl.class);
        
        ValidationServiceImpl vsi = locator.getService(ValidationServiceImpl.class);
        ErrorServiceImpl esi = locator.getService(ErrorServiceImpl.class);
        
        vsi.setThrowFromValidate(true);
        esi.setThrowInOnFailure(true);
        
        try {
            ServiceLocatorUtilities.addClasses(locator, SimpleService.class);
            Assert.fail("Should have failed with MultiException");
        }
        catch (MultiException me) {
            // Correct
        }
        catch (Throwable th) {
            // Any other exception is a fail
            if (th instanceof RuntimeException) {
                throw (RuntimeException) th;
            }
            
            throw new RuntimeException(th);
        }
        
        // Make sure service was not actually added
        SimpleService ss = locator.getService(SimpleService.class);
        Assert.assertNull(ss);
    }
    
    /**
     * Tests that an exception during unbind operation causes MultiException
     * to be thrown
     */
    @Test // @org.junit.Ignore
    public void testExceptionInValidateDuringUnBindAndOnErrorDoesNotThrow() {
        ServiceLocator locator = LocatorHelper.getServiceLocator(ValidationServiceImpl.class, ErrorServiceImpl.class);
        
        ValidationServiceImpl vsi = locator.getService(ValidationServiceImpl.class);
        ErrorServiceImpl esi = locator.getService(ErrorServiceImpl.class);
        
        ActiveDescriptor<?> disposeMe = ServiceLocatorUtilities.addClasses(locator, SimpleService.class).get(0);
        
        vsi.setThrowFromValidate(true);
        esi.setThrowInOnFailure(true);
        
        try {
            ServiceLocatorUtilities.removeOneDescriptor(locator, disposeMe);
            Assert.fail("Should have failed with MultiException");
        }
        catch (MultiException me) {
            // Correct
        }
        catch (Throwable th) {
            // Any other exception is a fail
            if (th instanceof RuntimeException) {
                throw (RuntimeException) th;
            }
            
            throw new RuntimeException(th);
        }
        
        vsi.setThrowFromValidate(false);
        
        // Make sure service was not actually unbound
        SimpleService ss = locator.getService(SimpleService.class);
        Assert.assertNotNull(ss);
    }

}
