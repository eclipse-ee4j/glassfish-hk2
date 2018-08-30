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

package org.glassfish.hk2.tests.locator.inheritance;

import java.lang.annotation.Annotation;
import java.util.Set;

import junit.framework.Assert;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class InheritanceTest {
    private final static String TEST_NAME = "InheritanceTest";
    private final static ServiceLocator locator = LocatorHelper.create(TEST_NAME, new InheritanceModule());
    
    /**
     * Tests ensures that all proper qualifiers are added to a class
     */
    @Test
    public void testQualifiersInAHierarchy() {
        DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
        DynamicConfiguration config = dcs.createDynamicConfiguration();
        
        ActiveDescriptor<?> football = config.addActiveDescriptor(AmericanFootball.class);
        
        // Check the qualifiers
        Set<Annotation> qualifiers = football.getQualifierAnnotations();
        Assert.assertEquals(3, qualifiers.size());
        
        boolean foundSuperbowl = false;
        boolean foundOutdoors = false;
        boolean foundHasWinner = false;
        
        for (Annotation qualifier : qualifiers) {
            if (Superbowl.class.equals(qualifier.annotationType())) {
                foundSuperbowl = true;
            }
            else if (Outdoors.class.equals(qualifier.annotationType())) {
                foundOutdoors = true;
            }
            else if (HasWinner.class.equals(qualifier.annotationType())) {
                foundHasWinner = true;
            }
            else {
                Assert.fail("Unexpected qualifier found: " + qualifier);
            }
        }
        
        Assert.assertTrue(foundSuperbowl);
        Assert.assertTrue(foundOutdoors);
        Assert.assertTrue(foundHasWinner);
    }
    
    /**
     * Tests ensures that the non-inherited scope of Sports wipes out
     * the inherited scope of Games, leaving AmericanFootball with its
     * own declared scope of PerLookup
     */
    @Test
    public void testNonInheritedScopeWipesOutInheritedScope() {
        DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
        DynamicConfiguration config = dcs.createDynamicConfiguration();
        
        ActiveDescriptor<?> football = config.addActiveDescriptor(AmericanFootball.class);
        
        Assert.assertEquals(PerLookup.class, football.getScopeAnnotation());
    }
    
    /**
     * Tests ensures that the non-inherited scope of Sports wipes out
     * the inherited scope of Games, leaving AmericanFootball with its
     * own declared scope of PerLookup
     */
    @Test
    public void testInheritedScope() {
        DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
        DynamicConfiguration config = dcs.createDynamicConfiguration();
        
        ActiveDescriptor<?> chess = config.addActiveDescriptor(Chess.class);
        
        Assert.assertEquals(InheritedScope.class, chess.getScopeAnnotation());
    }
    
    /**
     * Tests ensures that the non-inherited scope of Sports wipes out
     * the inherited scope of Games, leaving AmericanFootball with its
     * own declared scope of PerLookup
     */
    @Test
    public void testFactoryInheritanceOfScope() {
        InheritedScopeContext context = locator.getService(InheritedScopeContext.class);
        Assert.assertTrue(context.getSeen().isEmpty());
        
        SimpleService ss = locator.getService(SimpleService.class);
        Assert.assertNotNull(ss);
        
        Assert.assertEquals(1, context.getSeen().size());
        
        Assert.assertEquals(ss, context.getSeen().get(0));
    }

}
