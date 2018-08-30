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

package org.jvnet.hk2.metadata.tests;

import java.sql.Connection;

import org.glassfish.hk2.api.Rank;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.Stub;
import org.junit.Assert;
import org.junit.Test;
import org.jvnet.hk2.annotations.Contract;
import org.jvnet.hk2.metadata.tests.faux.stub.AbstractService;
import org.jvnet.hk2.metadata.tests.faux.stub.ConnectionStub;
import org.jvnet.hk2.metadata.tests.faux.stub.ExtendsThings;
import org.jvnet.hk2.metadata.tests.faux.stub.FailingLargeInterfaceStub;
import org.jvnet.hk2.metadata.tests.faux.stub.InterfaceWithTypes;
import org.jvnet.hk2.metadata.tests.faux.stub.NamedBean;
import org.jvnet.hk2.metadata.tests.stub.LargeInterface;

/**
 * @author jwells
 *
 */
public class StubTest {
    /**
     * Makes sure that the stubbed interface is used not the one from the main jar
     */
    @Test
    public void testGetsStubImplementationRatherThanOneFromMain() {
        ServiceLocator locator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
        
        LargeInterface li = locator.getService(LargeInterface.class);
        
        Assert.assertEquals(0, li.methodInt(27));
    }
    
    /**
     * Makes sure that the stubbed interface is used not the one from the main jar
     */
    @Test
    public void testInnerClassCanBeStubbed() {
        ServiceLocator locator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
        
        AbstractService li = locator.getService(AbstractService.class);
        
        Assert.assertNotNull(li.getRandomBeanStub());
    }
    
    /**
     * Ensures that {@link javax.inject.Named} works in a stub
     */
    @Test
    public void testNamedWorks() {
        ServiceLocator locator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
        
        NamedBean rawNamedService = locator.getService(NamedBean.class, "NamedBeanStub");
        Assert.assertEquals("NamedBeanStub", rawNamedService.getName());
        
        NamedBean aliceService = locator.getService(NamedBean.class, InhabitantsGeneratorTest.ALICE);
        Assert.assertEquals(InhabitantsGeneratorTest.ALICE, aliceService.getName());
        
        Assert.assertNull(rawNamedService.getAddress());
        Assert.assertNull(aliceService.getAddress());
    }
    
    /**
     * Ensures that the exception version of the stub works properly
     */
    @Test
    public void testExceptionTypeStub() {
        ServiceLocator locator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
        
        FailingLargeInterfaceStub stub = locator.getService(FailingLargeInterfaceStub.class);
        
        Assert.assertTrue(stub.notOverridden(false));
        
        try {
            stub.methodBoolean(true);
            Assert.fail("Should have thrown exception");
        }
        catch (UnsupportedOperationException uoe) {
            // ok
        }
        
        try {
            stub.methodVoids();
            Assert.fail("Should have thrown exception");
        }
        catch (UnsupportedOperationException uoe) {
            // ok
        }
        
        try {
            stub.methodByte((byte) 0);
            Assert.fail("Should have thrown exception");
        }
        catch (UnsupportedOperationException uoe) {
            // ok
        }
            
        try {
            stub.methodChar('a');
            Assert.fail("Should have thrown exception");
        }
        catch (UnsupportedOperationException uoe) {
            // ok
        }
        
        try {
            stub.methodDouble((double) 0.0);
            Assert.fail("Should have thrown exception");
        }
        catch (UnsupportedOperationException uoe) {
            // ok
        }
        
        try {
            stub.methodFloat((float) 0.0);
            Assert.fail("Should have thrown exception");
        }
        catch (UnsupportedOperationException uoe) {
            // ok
        }
        
        try {
          stub.methodInt(0);
          Assert.fail("Should have thrown exception");
        }
        catch (UnsupportedOperationException uoe) {
            // ok
        }
        
        try {
            stub.methodInt(0);
            Assert.fail("Should have thrown exception");
        }
        catch (UnsupportedOperationException uoe) {
            // ok
        }
        
        
        try {
            stub.methodShort((short) 0);
            Assert.fail("Should have thrown exception");
        }
        catch (UnsupportedOperationException uoe) {
            // ok
        }
        
        try {
            stub.methodDeclared(null, null, null);
            Assert.fail("Should have thrown exception");
        }
        catch (UnsupportedOperationException uoe) {
            // ok
        }
        
        try {
            stub.methodBooleanArray(null);
            Assert.fail("Should have thrown exception");
        }
        catch (UnsupportedOperationException uoe) {
            // ok
        }
        
        try {
            stub.methodByteArray(null);
            Assert.fail("Should have thrown exception");
        }
        catch (UnsupportedOperationException uoe) {
            // ok
        }
        
        try {
            stub.methodCharArray(null);
            Assert.fail("Should have thrown exception");
        }
        catch (UnsupportedOperationException uoe) {
            // ok
        }
        
        try {
            stub.methodDoubleArray(null);
            Assert.fail("Should have thrown exception");
        }
        catch (UnsupportedOperationException uoe) {
            // ok
        }
        
        try {
            stub.methodFloatArray(null);
            Assert.fail("Should have thrown exception");
        }
        catch (UnsupportedOperationException uoe) {
            // ok
        }
        
        try {
            stub.methodIntArray((int[]) null);
            Assert.fail("Should have thrown exception");
        }
        catch (UnsupportedOperationException uoe) {
            // ok
        }
        
        try {
            stub.methodIntArray((long[][][][][]) null);
            Assert.fail("Should have thrown exception");
        }
        catch (UnsupportedOperationException uoe) {
            // ok
        }
        
        try {
            stub.methodShortArray(null);
            Assert.fail("Should have thrown exception");
        }
        catch (UnsupportedOperationException uoe) {
            // ok
        }
        
        try {
            stub.methodDeclaredArray(null, null);
            Assert.fail("Should have thrown exception");
        }
        catch (UnsupportedOperationException uoe) {
            // ok
        }
    }
    
    /**
     * Ensures that the exception version of the stub works properly
     */
    @Test
    public void testStubsWithTypeVariables() {
        ServiceLocator locator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
        
        InterfaceWithTypes<?,?> stub = locator.getService(InterfaceWithTypes.class);
        
        Assert.assertNull(stub.get(null));
        Assert.assertNull(stub.reverseGet(null));
    }
    
    /**
     * Tests that we can use ContractsProvided on a stub for stubbing
     * things we have no control over (like SQL stuff)
     */
    @Test
    // @org.junit.Ignore
    public void testStubWithProvidedContracts() {
        ServiceLocator locator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
        
        Connection conn = locator.getService(Connection.class);
        Assert.assertNotNull(conn);
        
        ConnectionStub stub = locator.getService(ConnectionStub.class);
        Assert.assertNotNull(stub);
        
        Assert.assertEquals(stub, conn);
        
    }
    
    @Stub @Contract @Rank(1)
    public abstract static class Extender implements ExtendsThings<String, Integer, Long> {
        
    }

}
