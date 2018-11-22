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

package org.glassfish.hk2.xml.test.validation;

import java.net.URL;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.xml.api.XmlRootHandle;
import org.glassfish.hk2.xml.api.XmlService;
import org.glassfish.hk2.xml.test.utilities.Utilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class ValidationTest {
    private final static String VALID1_FILE = "valid1.xml";
    private final static String VALID2_FILE = "valid2.xml";
    private final static String INVALID1_FILE = "invalid1.xml";
    private final static String INVALID2_FILE = "invalid2.xml";
    private final static String INVALID3_FILE = "invalid3.xml";
    private final static String INVALID4_FILE = "invalid4.xml";
    
    private final static String E1 = "E1";
    
    private final static String ALICE = "Alice";
    private final static String BOB = "Bob";
    
    /**
     * Tests that validation on a valid file works
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testValidDocument() throws Exception {
        ServiceLocator locator = Utilities.createLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        
        URL url = getClass().getClassLoader().getResource(VALID1_FILE);
        
        XmlRootHandle<ValidationRootBean> rootHandle = xmlService.unmarshal(url.toURI(), ValidationRootBean.class);
        
        Assert.assertFalse(rootHandle.isValidating());
        
        rootHandle.startValidating();
        
        Assert.assertTrue(rootHandle.isValidating());
        
        rootHandle.stopValidating();
        
        Assert.assertFalse(rootHandle.isValidating());
    }
    
    /**
     * Tests that validation on a valid file works
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testInvalidDocument() throws Exception {
        ServiceLocator locator = Utilities.createLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        
        URL url = getClass().getClassLoader().getResource(INVALID1_FILE);
        
        XmlRootHandle<ValidationRootBean> rootHandle = xmlService.unmarshal(url.toURI(), ValidationRootBean.class);
        
        ValidationRootBean validationRoot = rootHandle.getRoot();
        Assert.assertNull(validationRoot.getElementOne());
        
        try {
            rootHandle.startValidating();
            Assert.fail("Should have failed");
        }
        catch (ConstraintViolationException me) {
            // Expected
        }
    }
    
    private static void checkMultiException(MultiException me, String expectedConstraintMessage) {
        ConstraintViolationException found = null;
        for (Throwable th : me.getErrors()) {
            if (th instanceof ConstraintViolationException) {
                found = (ConstraintViolationException) th;
                break;
            }
        }
        
        Assert.assertNotNull(found);
        
        boolean foundMessage = false;
        Set<ConstraintViolation<?>> violations = found.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            if (expectedConstraintMessage.equals(violation.getMessage())) {
                foundMessage = true;
                break;
            }
        }
        
        Assert.assertTrue("Did not find expected exception in " + me + " was looking for " + expectedConstraintMessage,
                foundMessage);
        
    }
    
    /**
     * Tests that validation happens on a bad set
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testBadSet() throws Exception {
        ServiceLocator locator = Utilities.createLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        
        URL url = getClass().getClassLoader().getResource(VALID1_FILE);
        
        XmlRootHandle<ValidationRootBean> rootHandle = xmlService.unmarshal(url.toURI(), ValidationRootBean.class);
        
        rootHandle.startValidating();
        
        ValidationRootBean root = rootHandle.getRoot();
        Assert.assertEquals(E1, root.getElementOne());
        
        try {
            root.setElementOne(null);
            Assert.fail("Should not have worked because validation is on");
        }
        catch (MultiException e) {
            checkMultiException(e, "must not be null");
        }
        
        // Nothing should have changed
        Assert.assertEquals(E1, root.getElementOne());
        
    }
    
    /**
     * Tests that validation on a valid file works
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testValidDocumentWithChildren() throws Exception {
        ServiceLocator locator = Utilities.createLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        
        URL url = getClass().getClassLoader().getResource(VALID2_FILE);
        
        XmlRootHandle<ValidationRootBean> rootHandle = xmlService.unmarshal(url.toURI(), ValidationRootBean.class);
        
        rootHandle.startValidating();
    }
    
    /**
     * Tests that validation on an invalid list child fails
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testValidDocumentWithInvalidListChild() throws Exception {
        ServiceLocator locator = Utilities.createLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        
        URL url = getClass().getClassLoader().getResource(INVALID2_FILE);
        
        XmlRootHandle<ValidationRootBean> rootHandle = xmlService.unmarshal(url.toURI(), ValidationRootBean.class);
        
        try {
            rootHandle.startValidating();
            Assert.fail("Should have failed");
        }
        catch (ConstraintViolationException me) {
            // Expected
        }
    }
    
    /**
     * Tests that validation on an invalid list child fails
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testValidDocumentWithInvalidArrayChild() throws Exception {
        ServiceLocator locator = Utilities.createLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        
        URL url = getClass().getClassLoader().getResource(INVALID3_FILE);
        
        XmlRootHandle<ValidationRootBean> rootHandle = xmlService.unmarshal(url.toURI(), ValidationRootBean.class);
        
        try {
            rootHandle.startValidating();
            Assert.fail("Should have failed");
        }
        catch (ConstraintViolationException me) {
            // Expected
        }
    }
    
    /**
     * Tests that validation on an invalid list child fails
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testValidDocumentWithInvalidDirectChild() throws Exception {
        ServiceLocator locator = Utilities.createLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        
        URL url = getClass().getClassLoader().getResource(INVALID4_FILE);
        
        XmlRootHandle<ValidationRootBean> rootHandle = xmlService.unmarshal(url.toURI(), ValidationRootBean.class);
        
        try {
            rootHandle.startValidating();
            Assert.fail("Should have failed");
        }
        catch (ConstraintViolationException me) {
            // Expected
        }
    }
    
    /**
     * Tests that validation on a valid file works
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testAddInvalidListChild() throws Exception {
        ServiceLocator locator = Utilities.createLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        
        URL url = getClass().getClassLoader().getResource(VALID2_FILE);
        
        XmlRootHandle<ValidationRootBean> rootHandle = xmlService.unmarshal(url.toURI(), ValidationRootBean.class);
        
        rootHandle.startValidating();
        
        ValidationChildBean listChild = xmlService.createBean(ValidationChildBean.class);
        
        // Do NOT fill in ElementTwo
        ValidationRootBean root = rootHandle.getRoot();
        
        try {
            root.addListChild(listChild);
            Assert.fail("Add of invalid bean should have failed");
        }
        catch (MultiException me) {
            checkMultiException(me, "must not be null");
        }
        
        List<ValidationChildBean> listChildren = root.getListChildren();
        Assert.assertEquals(1, listChildren.size());
    }
    
    /**
     * Tests that validation on a valid file works
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testAddInvalidArrayChild() throws Exception {
        ServiceLocator locator = Utilities.createLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        
        URL url = getClass().getClassLoader().getResource(VALID2_FILE);
        
        XmlRootHandle<ValidationRootBean> rootHandle = xmlService.unmarshal(url.toURI(), ValidationRootBean.class);
        
        rootHandle.startValidating();
        
        ValidationChildArrayBean arrayChild = xmlService.createBean(ValidationChildArrayBean.class);
        
        // Do NOT fill in ElementTwo
        ValidationRootBean root = rootHandle.getRoot();
        
        try {
            root.addArrayChild(arrayChild);
            Assert.fail("Add of invalid array child should have failed");
        }
        catch (MultiException me) {
            checkMultiException(me, "must not be null");
        }
        
        // Make sure we didn't actually add it
        ValidationChildArrayBean arrayChildren[] = root.getArrayChildren();
        Assert.assertEquals(2, arrayChildren.length);
    }
    
    /**
     * Tests that validation on a valid file works
     * @throws Exception
     */
    @Test
    // @org.junit.Ignore
    public void testAddInvalidDirectChild() throws Exception {
        ServiceLocator locator = Utilities.createLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        
        URL url = getClass().getClassLoader().getResource(VALID2_FILE);
        
        XmlRootHandle<ValidationRootBean> rootHandle = xmlService.unmarshal(url.toURI(), ValidationRootBean.class);
        
        rootHandle.startValidating();
        
        ValidationChildDirectBean directChild = xmlService.createBean(ValidationChildDirectBean.class);
        
        // Do NOT fill in ElementTwo
        ValidationRootBean root = rootHandle.getRoot();
        
        // First remove the child before setting it to something bad
        root.setDirectChild(null);
        
        try {
            root.setDirectChild(directChild);
            Assert.fail("Add of invalid array child should have failed");
        }
        catch (MultiException me) {
            checkMultiException(me, "must not be null");
        }
        
        Assert.assertNull(root.getDirectChild());
    }
    
    private static void isConstraintViolationException(MultiException me) {
        for (Throwable th : me.getErrors()) {
            if (th instanceof ConstraintViolationException) return;
        }
        
        throw me;
    }
    
    @Test
    public void testBasicConstraint() {
        ServiceLocator locator = Utilities.createDomLocator(CaptureRootChangeListener.class);
        
        XmlService xmlService = locator.getService(XmlService.class);
        
        XmlRootHandle<ConstraintRootBean> handle = xmlService.createEmptyHandle(ConstraintRootBean.class);
        handle.addChangeListener(locator.getService(CaptureRootChangeListener.class));
        HasChildWithNameValidator.setRootListener(locator.getService(CaptureRootChangeListener.class));
        
        handle.startValidating();
        
        handle.addRoot();
        
        ConstraintRootBean crb = handle.getRoot();
        
        NamedBean namedBeanAlice = xmlService.createBean(NamedBean.class);
        namedBeanAlice.setName(ALICE);
        
        namedBeanAlice = crb.addNamed(namedBeanAlice);
        
        BeanToValidate1Bean aliceBean = xmlService.createBean(BeanToValidate1Bean.class);
        aliceBean.setNameReference(ALICE);
        
        crb.addValid1(aliceBean);
        
        BeanToValidate1Bean bobBean = xmlService.createBean(BeanToValidate1Bean.class);
        bobBean.setNameReference(BOB);
        
        try {
            crb.addValid1(bobBean);
            Assert.fail("Should have failed as there is no BOB bean");
        }
        catch (MultiException me) {
            isConstraintViolationException(me);
        }
    }

}
