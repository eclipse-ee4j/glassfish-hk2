/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.locator.negative.dynamicconfig;

import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationListener;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.ErrorService;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ValidationService;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class NegativeConfigTest {
    /**
     * An injection resolver must be in Singleton scope
     */
    @Test
    public void testPerLookupInjectionResolver() {
        ServiceLocator locator = LocatorHelper.create();
        ServiceLocatorUtilities.addClasses(locator, DynamicConfigErrorService.class);
        
        DynamicConfigErrorService errorService = locator.getService(DynamicConfigErrorService.class);
        Assert.assertNull(errorService.getConfigException());
        
        DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
        DynamicConfiguration dc = dcs.createDynamicConfiguration();
        
        dc.bind(BuilderHelper.link(BadInjectionResolver.class).
                to(InjectionResolver.class).build());
        
        try {
            dc.commit();
            Assert.fail("Commit should have failed with PerLookup InjectionResolver");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage(), me.getMessage().contains(
                    " must be in the Singleton scope"));
            
            Assert.assertEquals(errorService.getConfigException(), me);
        }
        
    }
    
    /**
     * A context must be in Singleton scope
     */
    @Test
    public void testPerLookupContext() {
        ServiceLocator locator = LocatorHelper.create();
        ServiceLocatorUtilities.addClasses(locator, DynamicConfigErrorService.class);
        
        DynamicConfigErrorService errorService = locator.getService(DynamicConfigErrorService.class);
        Assert.assertNull(errorService.getConfigException());
        
        DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
        DynamicConfiguration dc = dcs.createDynamicConfiguration();
        
        dc.bind(BuilderHelper.link(BadContext.class).
                to(Context.class).
                in(PerLookup.class.getName()).
                build());
        
        try {
            dc.commit();
            Assert.fail("Commit should have failed with PerLookup Context");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage(), me.getMessage().contains(
                    " must be in the Singleton scope"));
            
            Assert.assertEquals(errorService.getConfigException(), me);
        }
        
    }
    
    /**
     * A validation service must be in Singleton scope
     */
    @Test
    public void testPerLookupValidationService() {
        ServiceLocator locator = LocatorHelper.create();
        ServiceLocatorUtilities.addClasses(locator, DynamicConfigErrorService.class);
        
        DynamicConfigErrorService errorService = locator.getService(DynamicConfigErrorService.class);
        Assert.assertNull(errorService.getConfigException());
        
        DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
        DynamicConfiguration dc = dcs.createDynamicConfiguration();
        
        dc.bind(BuilderHelper.link(BadValidationService.class).
                to(ValidationService.class).
                in(PerLookup.class.getName()).
                build());
        
        try {
            dc.commit();
            Assert.fail("Commit should have failed with PerLookup ValidationService");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage(), me.getMessage().contains(
                    " must be in the Singleton scope"));
            
            Assert.assertEquals(errorService.getConfigException(), me);
        }
        
    }
    
    /**
     * An error service must be in Singleton scope
     */
    @Test
    public void testPerLookupErrorService() {
        ServiceLocator locator = LocatorHelper.create();
        ServiceLocatorUtilities.addClasses(locator, DynamicConfigErrorService.class);
        
        DynamicConfigErrorService errorService = locator.getService(DynamicConfigErrorService.class);
        Assert.assertNull(errorService.getConfigException());
        
        DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
        DynamicConfiguration dc = dcs.createDynamicConfiguration();
        
        dc.bind(BuilderHelper.link(BadErrorService.class).
                to(ErrorService.class).
                build());
        
        try {
            dc.commit();
            Assert.fail("Commit should have failed with PerLookup ErrorService");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage(), me.getMessage().contains(
                    " must be in the Singleton scope"));
            
            Assert.assertEquals(errorService.getConfigException(), me);
        }
        
    }
    
    /**
     * A dynamic configuration listener must be in Singleton scope
     */
    @Test
    public void testPerLookupDynamicConfigurationListener() {
        ServiceLocator locator = LocatorHelper.create();
        ServiceLocatorUtilities.addClasses(locator, DynamicConfigErrorService.class);
        
        DynamicConfigErrorService errorService = locator.getService(DynamicConfigErrorService.class);
        Assert.assertNull(errorService.getConfigException());
        
        DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
        DynamicConfiguration dc = dcs.createDynamicConfiguration();
        
        dc.bind(BuilderHelper.link(BadDynamicConfigurationListener.class).
                to(DynamicConfigurationListener.class).
                build());
        
        try {
            dc.commit();
            Assert.fail("Commit should have failed with PerLookup DynamicConfigurationListener");
        }
        catch (MultiException me) {
            Assert.assertTrue(me.getMessage(), me.getMessage().contains(
                    " must be in the Singleton scope"));
            
            Assert.assertEquals(errorService.getConfigException(), me);
        }
        
    }

}
