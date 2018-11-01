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

package org.glassfish.hk2.tests.locator.classanalysis;

import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the {@link ClassAnalysis} feature
 * 
 * @author jwells
 *
 */
public class ClassAnalysisTest {
    private final static String TEST_NAME = "ClassAnalysisTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new ClassAnalysisModule());
    
    public static final String ALTERNATE_DEFAULT_ANALYZER = "AlternateDefaultAnalyzer";
    
    @Test
    public void testChangeDefaultAnalyzer() {
        AlternateDefaultAnalyzer alternate = null;
        
        locator.setDefaultClassAnalyzerName(ALTERNATE_DEFAULT_ANALYZER);
        try {
            alternate = locator.getService(AlternateDefaultAnalyzer.class);
            Assert.assertNotNull(alternate);
            
            alternate.reset();
            
            // Now lookup a simple service, which should be analyzed with the alternate
            Assert.assertNotNull(locator.getService(SimpleService1.class));
            
            alternate.check();
        }
        finally {
            locator.setDefaultClassAnalyzerName(null);
        }
        
        alternate.reset();
        
        // Now make sure that the other default has taken over
        Assert.assertNotNull(locator.getService(SimpleService2.class));
        
        alternate.unused();
    }
    
    @Test
    public void testCustomClassAnalyzer() {
        ServiceHandle<ServiceWithManyDoubles> handle = locator.getServiceHandle(ServiceWithManyDoubles.class);
        Assert.assertNotNull(handle);
        
        ServiceWithManyDoubles service = handle.getService();
        Assert.assertNotNull(service);
        
        handle.close();
        
        service.checkCalls();
    }
    
    @Test
    public void testCustomCreateStrategy() {
        ServiceWithManyDoubles service = locator.create(ServiceWithManyDoubles.class, DoubleClassAnalyzer.DOUBLE_ANALYZER);
        Assert.assertNotNull(service);
        
        service.checkAfterConstructor();
    }
    
    @Test
    public void testCustomInitializationStrategy() {
        ServiceWithManyDoubles service = new ServiceWithManyDoubles(DoubleFactory.DOUBLE);
        
        locator.inject(service, DoubleClassAnalyzer.DOUBLE_ANALYZER);
        
        service.checkAfterInitializeBeforePostConstruct();
    }
    
    @Test
    public void testCustomPostDestroyStrategy() {
        ServiceWithManyDoubles service = new ServiceWithManyDoubles(DoubleFactory.DOUBLE);
        
        locator.postConstruct(service, DoubleClassAnalyzer.DOUBLE_ANALYZER);
        
        service.checkAfterPostConstructWithNoInitialization();
    }
    
    @Test
    public void testCustomFullCreateAPI() {
        ServiceWithManyDoubles service = locator.createAndInitialize(ServiceWithManyDoubles.class,
                DoubleClassAnalyzer.DOUBLE_ANALYZER);
        
        service.checkFullCreateWithoutDestroy();
    }
    
    @Test
    public void testLongestConstructor() {
        JaxRsService jrs = locator.getService(JaxRsService.class);
        Assert.assertNotNull(jrs);
        
        jrs.checkProperConstructor();
    }
    
    /**
     * This test also ensures that the analyzer field of Service
     * is honored by the automatic analysis in addActiveDescriptor
     */
    @Test
    public void testLongestConstructorWithNoZeroArgConstructor() {
        DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
        DynamicConfiguration config = dcs.createDynamicConfiguration();
        
        config.addActiveDescriptor(ServiceWithNoValidHK2Constructor.class);
        
        config.commit();
        
        ServiceWithNoValidHK2Constructor service = locator.getService(ServiceWithNoValidHK2Constructor.class);
        service.check();
    }
    
    /**
     * This test also ensures that the analyzer field of Service
     * is honored by the automatic analysis in addActiveDescriptor
     */
    @Test
    public void testLongestConstructorWithValidHK2Constructor() {
        DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
        DynamicConfiguration config = dcs.createDynamicConfiguration();
        
        config.addActiveDescriptor(ServiceWithValidHK2NonZeroArgConstructor.class);
        
        config.commit();
        
        ServiceWithValidHK2NonZeroArgConstructor service = locator.getService(ServiceWithValidHK2NonZeroArgConstructor.class);
        service.check();
    }
    
    /**
     * This test also ensures that the analyzer field of Service
     * is honored by the automatic analysis in addActiveDescriptor
     */
    @Test
    public void testLongestConstructorWithValidHK2ZeroArgConstructor() {
        DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
        DynamicConfiguration config = dcs.createDynamicConfiguration();
        
        config.addActiveDescriptor(ServiceWithValidHK2NoArgConstructor.class);
        
        config.commit();
        
        ServiceWithValidHK2NoArgConstructor service = locator.getService(ServiceWithValidHK2NoArgConstructor.class);
        service.check();
    }
}
