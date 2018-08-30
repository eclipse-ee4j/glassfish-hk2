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

package org.glassfish.hk2.tests.locator.context.ghost;

import java.lang.annotation.Annotation;

import javax.inject.Singleton;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.AnnotationLiteral;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.tests.locator.utilities.LocatorHelper;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class ContextGhostTest {
    /**
     * Ensures that you can change from one hard-coded scope on the descriptor
     * to a different scope
     */
    @Test
    public void testSwitchFromExplicitScopeToGhostedScope() {
        ServiceLocator locator = LocatorHelper.create();
        ServiceLocatorUtilities.enableLookupExceptions(locator);
        ServiceLocatorUtilities.addClasses(locator, GhostedContext.class);
        
        Descriptor desc = BuilderHelper.activeLink(SingletonScopedService.class).
                to(SingletonScopedService.class).
                in(new GhostedScopeImpl(0)).
                build();
        
        ServiceLocatorUtilities.addOneDescriptor(locator, desc);
        
        SingletonScopedService one = locator.getService(SingletonScopedService.class);
        SingletonScopedService two = locator.getService(SingletonScopedService.class);
        
        Assert.assertNotNull(one);
        Assert.assertNotSame(one, two);
    }
    
    /**
     * Ensures we can add a scope to a service with no scope at all
     */
    @Test
    public void testGiveClassWithNoScopeAScope() {
        ServiceLocator locator = LocatorHelper.create();
        ServiceLocatorUtilities.enableLookupExceptions(locator);
        
        Descriptor desc = BuilderHelper.activeLink(NoScopeService.class).
                to(NoScopeService.class).
                in(ServiceLocatorUtilities.getSingletonAnnotation()).
                build();
        
        ServiceLocatorUtilities.addOneDescriptor(locator, desc);
        
        NoScopeService one = locator.getService(NoScopeService.class);
        NoScopeService two = locator.getService(NoScopeService.class);
        
        Assert.assertNotNull(one);
        Assert.assertEquals(one, two);
        
    }
    
    /**
     * Tests that we can change the value of a field in a
     * scope with a ghost added annotation
     */
    @Test
    public void testModifyExistingScopeWithDifferentValue() {
        ServiceLocator locator = LocatorHelper.create();
        ServiceLocatorUtilities.addClasses(locator, GhostedContext.class);
        
        Descriptor desc = BuilderHelper.activeLink(GhostedServiceWithValue.class).
                to(GhostedServiceWithValue.class).
                in(new GhostedScopeImpl(0)).
                build();
        
        ServiceLocatorUtilities.addOneDescriptor(locator, desc);
        
        GhostedServiceWithValue ghosted = locator.getService(GhostedServiceWithValue.class);
        Assert.assertNotNull(ghosted);
        
        ActiveDescriptor<?> gDesck = ghosted.getDescriptor();
        
        Annotation anno = gDesck.getScopeAsAnnotation();
        Assert.assertNotNull(anno);
        
        GhostedScope gs = (GhostedScope) anno;
        Assert.assertEquals(0, gs.value());
        
    }
    
    private static class GhostedScopeImpl extends AnnotationLiteral<GhostedScope> implements GhostedScope {
        private final int value;
        
        private GhostedScopeImpl(int value) {
            this.value = value;
        }

        /* (non-Javadoc)
         * @see org.glassfish.hk2.tests.locator.context.ghost.GhostedScope#value()
         */
        @Override
        public int value() {
            return value;
        }
    }

}
