/*
 * Copyright (c) 2013, 2019 Oracle and/or its affiliates. All rights reserved.
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

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ClassAnalyzer;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.IgnoringErrorService;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Negative tests for class analysis
 *
 * @author jwells
 */
public class NegativeClassAnalysisTest {
    private final static String TEST_NAME = "NegativeClassAnalysisTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME,
        new NegativeClassAnalysisModule(ClassAnalyzer.DEFAULT_IMPLEMENTATION_NAME));

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
            fail("Should have failed due to bad constructor throw");
        }
        catch (MultiException me) {
            assertThat(me.getMessage(), me.getMessage(), containsString(C_THROW));
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
            fail("Should have failed due to bad method throw");
        }
        catch (MultiException me) {
            assertThat(me.getMessage(), me.getMessage(), containsString(M_THROW));
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
            fail("Should have failed due to bad field throw");
        }
        catch (MultiException me) {
            assertThat(me.getMessage(), me.getMessage(), containsString(F_THROW));
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
            fail("Should have failed due to bad pc throw");
        }
        catch (MultiException me) {
            assertThat(me.getMessage(), me.getMessage(), containsString(PC_THROW));
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
            fail("Should have failed due to bad pd throw");
        }
        catch (MultiException me) {
            assertThat(me.toString(), me.toString(), containsString(PD_THROW));
        }

    }

    @Test
    public void testBadConstructorNull() {
        ConfigurablyBadClassAnalyzer cbca = locator.getService(ConfigurablyBadClassAnalyzer.class);
        cbca.resetToGood();

        cbca.setNullFromConstructor(true);
        try {
            locator.create(SimpleService.class, ConfigurablyBadClassAnalyzer.BAD_ANALYZER_NAME);
            fail("Should have failed due to null constructor return");
        }
        catch (MultiException me) {
            assertThat(me.getMessage(), me.getMessage(), containsString("null return"));
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
            fail("Should have failed due to null method return");
        }
        catch (MultiException me) {
            assertThat(me.getMessage(), me.getMessage(), containsString("null return"));
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
            fail("Should have failed due to null method return");
        }
        catch (MultiException me) {
            assertThat(me.getMessage(), me.getMessage(), containsString("null return"));
        }

    }

    /**
     * This test makes sure a class analyzer is not its own analyzer
     */
    @Test
    public void testSelfAnalyzerWithIgnoringExceptions() {
        final ServiceLocator ownLocator = ServiceLocatorFactory.getInstance()
            .create(TEST_NAME + "_IllegalIgnoringExceptions");
        final DynamicConfigurationService dcs = ownLocator.getService(DynamicConfigurationService.class);
        final DynamicConfiguration cfg = dcs.createDynamicConfiguration();
        cfg.bind(BuilderHelper.createDescriptorFromClass(IgnoringErrorService.class));
        cfg.commit();

        final NegativeClassAnalysisModule module = new NegativeClassAnalysisModule(SELF_ANALYZER);
        DynamicConfiguration cfg2 = dcs.createDynamicConfiguration();
        module.configure(cfg2);
        cfg2.commit();

        final ActiveDescriptor<?> selfDescriptor = ownLocator
            .getBestDescriptor(BuilderHelper.createNameAndContractFilter(ClassAnalyzer.class.getName(), SELF_ANALYZER));
        assertNotNull(selfDescriptor);
        try {
            ownLocator.reifyDescriptor(selfDescriptor);
            fail("Should have failed, a class may not analyze itself");
        } catch (MultiException me) {
            assertThat(me.getMessage(), me.getMessage(), containsString("is its own ClassAnalyzer"));
        }
    }
    /**
     * This test makes sure a class analyzer is not its own analyzer
     */
    @Test
    public void testSelfAnalyzer() {
        try {
            LocatorHelper.create(TEST_NAME + "_Illegal", new NegativeClassAnalysisModule(SELF_ANALYZER));
            fail("Should have failed, a class may not analyze itself");
        } catch (MultiException me) {
            assertThat(me.getMessage(), me.getMessage(), containsString("is its own ClassAnalyzer"));
        }
    }
}
