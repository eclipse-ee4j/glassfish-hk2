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

package org.glassfish.hk2.tests.locator.negative.classanalysis;

import junit.framework.Assert;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ClassAnalyzer;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.junit.Test;

/**
 * Negative tests for class analysis
 * 
 * @author jwells
 *
 */
public class NegativeClassAnalysisTest {
    private final static String TEST_NAME = "NegativeClassAnalysisTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new NegativeClassAnalysisModule());
    
    public final static String C_THROW = "Expected throw from constructor";
    public final static String M_THROW = "Expected throw from method";
    public final static String F_THROW = "Expected throw from field";
    public final static String PC_THROW = "Expected throw from pc";
    public final static String PD_THROW = "Expected throw from pd";
    
    public final static String NULL_RETURN = "null return";
    public final static String SELF_ANALYZER = "Narcissus";
    
    @Test
    public void testBadConstructorThrow() {
        ConfigurablyBadClassAnalyzer cbca = locator.getService(ConfigurablyBadClassAnalyzer.class);
        cbca.resetToGood();
        
        cbca.setThrowFromConstructor(true);
        try {
            locator.create(SimpleService.class, ConfigurablyBadClassAnalyzer.BAD_ANALYZER_NAME);
            Assert.fail("Should have failed due to bad constructor throw");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.toString().contains(C_THROW));
        }
        
    }
    
    @Test
    public void testBadMethodThrow() {
        ConfigurablyBadClassAnalyzer cbca = locator.getService(ConfigurablyBadClassAnalyzer.class);
        cbca.resetToGood();
        
        cbca.setThrowFromMethods(true);
        try {
            SimpleService ss = new SimpleService();
            locator.inject(ss, ConfigurablyBadClassAnalyzer.BAD_ANALYZER_NAME);
            Assert.fail("Should have failed due to bad method throw");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.toString().contains(M_THROW));
        }
        
    }
    
    @Test
    public void testBadFieldThrow() {
        ConfigurablyBadClassAnalyzer cbca = locator.getService(ConfigurablyBadClassAnalyzer.class);
        cbca.resetToGood();
        
        cbca.setThrowFromFields(true);
        try {
            SimpleService ss = new SimpleService();
            locator.inject(ss, ConfigurablyBadClassAnalyzer.BAD_ANALYZER_NAME);
            Assert.fail("Should have failed due to bad field throw");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.toString().contains(F_THROW));
        }
        
    }
    
    @Test
    public void testBadPCThrow() {
        ConfigurablyBadClassAnalyzer cbca = locator.getService(ConfigurablyBadClassAnalyzer.class);
        cbca.resetToGood();
        
        cbca.setThrowFromPostConstruct(true);
        try {
            SimpleService ss = new SimpleService();
            locator.postConstruct(ss, ConfigurablyBadClassAnalyzer.BAD_ANALYZER_NAME);
            Assert.fail("Should have failed due to bad pc throw");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.toString().contains(PC_THROW));
        }
        
    }
    
    @Test
    public void testBadPDThrow() {
        ConfigurablyBadClassAnalyzer cbca = locator.getService(ConfigurablyBadClassAnalyzer.class);
        cbca.resetToGood();
        
        cbca.setThrowFromPreDestroy(true);
        try {
            SimpleService ss = new SimpleService();
            locator.preDestroy(ss, ConfigurablyBadClassAnalyzer.BAD_ANALYZER_NAME);
            Assert.fail("Should have failed due to bad pd throw");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.toString(), me.toString().contains(PD_THROW));
        }
        
    }
    
    @Test
    public void testBadConstructorNull() {
        ConfigurablyBadClassAnalyzer cbca = locator.getService(ConfigurablyBadClassAnalyzer.class);
        cbca.resetToGood();
        
        cbca.setNullFromConstructor(true);
        try {
            locator.create(SimpleService.class, ConfigurablyBadClassAnalyzer.BAD_ANALYZER_NAME);
            Assert.fail("Should have failed due to null constructor return");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.toString().contains("null return"));
        }
        
    }
    
    @Test
    public void testBadMethodNull() {
        ConfigurablyBadClassAnalyzer cbca = locator.getService(ConfigurablyBadClassAnalyzer.class);
        cbca.resetToGood();
        
        cbca.setNullFromMethods(true);
        try {
            SimpleService ss = new SimpleService();
            locator.inject(ss, ConfigurablyBadClassAnalyzer.BAD_ANALYZER_NAME);
            Assert.fail("Should have failed due to null method return");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.toString().contains("null return"));
        }
        
    }
    
    @Test
    public void testBadFieldNull() {
        ConfigurablyBadClassAnalyzer cbca = locator.getService(ConfigurablyBadClassAnalyzer.class);
        cbca.resetToGood();
        
        cbca.setNullFromFields(true);
        try {
            SimpleService ss = new SimpleService();
            locator.inject(ss, ConfigurablyBadClassAnalyzer.BAD_ANALYZER_NAME);
            Assert.fail("Should have failed due to null method return");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.toString().contains("null return"));
        }
        
    }
    
    /**
     * This test makes sure a class analyzer is not its own analyzer
     */
    @Test
    public void testSelfAnalyzer() {
        ActiveDescriptor<?> selfDescriptor =
                locator.getBestDescriptor(BuilderHelper.createNameAndContractFilter(
                        ClassAnalyzer.class.getName(),
                        SELF_ANALYZER));
        Assert.assertNotNull(selfDescriptor);
        
        try {
            locator.reifyDescriptor(selfDescriptor);
            Assert.fail("Should have failed, a class may not analyze itself");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.toString().contains("is its own ClassAnalyzer"));
        }
    }

}
