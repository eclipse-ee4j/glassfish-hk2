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

package org.glassfish.hk2.tests.locator.types;

import java.lang.reflect.Type;

import javax.inject.Inject;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class BinderTypesTest {
    private static class A<T, X, Y, Z> {

        private final T t;
        
        @Inject
        private X x;
        
        private Y y;
        private Z z;
        
        @Inject
        public A(T t) {
            this.t = t;
        }
        
        @Inject
        private void init(Z z, Y y) {
            this.z = z;
            this.y = y;
        }

    }

    private static class B {

    }
    
    private static class C {

    }
    
    private static class D {

    }
    
    private static class E {

    }

    /**
     * Tests that we can create a solidified typed class if we tell the descriptor
     * about the solidified type
     */
    @Test
    // @org.junit.Ignore
    public void testBindAsContract() {
        ServiceLocator serviceLocator = ServiceLocatorFactory.getInstance().create(null);
        ServiceLocatorUtilities.bind(serviceLocator, new AbstractBinder() {
            @Override
            protected void configure() {
                bind(B.class).to(B.class);
                bind(C.class).to(C.class);
                bind(D.class).to(D.class);
                bind(E.class).to(E.class);
                
                bindAsContract(new TypeLiteral<A<B, C, D, E>>() {});
            }
        });

        Type type = new TypeLiteral<A<B, C, D, E>>() {}.getType();
        A<B, C, D, E> ab = serviceLocator.getService(type);
        Assert.assertNotNull(ab);
        
        Assert.assertNotNull(ab.t);
        Assert.assertEquals(B.class, ab.t.getClass());
        
        Assert.assertNotNull(ab.x);
        Assert.assertEquals(C.class, ab.x.getClass());
        
        Assert.assertNotNull(ab.y);
        Assert.assertEquals(D.class, ab.y.getClass());
        
        Assert.assertNotNull(ab.z);
        Assert.assertEquals(E.class, ab.z.getClass());
    }
    
    /**
     * Tests using activeLink.asType
     */
    @Test
    // @org.junit.Ignore
    public void testActiveLinkAsType() {
        ServiceLocator serviceLocator = ServiceLocatorFactory.getInstance().create(null);
        
        Type type = new TypeLiteral<A<B, C, D, E>>() {}.getType();
        
        ActiveDescriptor<?> ad = BuilderHelper.activeLink(A.class).
                to(type).
                asType(type).build();
        
        ServiceLocatorUtilities.addClasses(serviceLocator,
                B.class, C.class, D.class, E.class);
        
        ActiveDescriptor<?> added = ServiceLocatorUtilities.addOneDescriptor(serviceLocator, ad);
        Assert.assertNotNull(added);
        
        A<B, C, D, E> ab = serviceLocator.getService(type);
        Assert.assertNotNull(ab);
        
        Assert.assertNotNull(ab.t);
        Assert.assertEquals(B.class, ab.t.getClass());
        
        Assert.assertNotNull(ab.x);
        Assert.assertEquals(C.class, ab.x.getClass());
        
        Assert.assertNotNull(ab.y);
        Assert.assertEquals(D.class, ab.y.getClass());
        
        Assert.assertNotNull(ab.z);
        Assert.assertEquals(E.class, ab.z.getClass());
    }
    
    /**
     * Tests using AbstractActiveDescriptor.setImplementationType
     */
    @Test
    // @org.junit.Ignore
    public void testAbstractActiveDescriptorSetType() {
        ServiceLocator serviceLocator = ServiceLocatorFactory.getInstance().create(null);
        
        Type type = new TypeLiteral<A<B, C, D, E>>() {}.getType();
        
        AbstractActiveDescriptor<?> ad = BuilderHelper.activeLink(A.class).
                to(type).build();
        
        ad.setImplementationType(type);
        
        ServiceLocatorUtilities.addClasses(serviceLocator,
                B.class, C.class, D.class, E.class);
        
        ActiveDescriptor<?> added = ServiceLocatorUtilities.addOneDescriptor(serviceLocator, ad);
        Assert.assertNotNull(added);
        
        A<B, C, D, E> ab = serviceLocator.getService(type);
        Assert.assertNotNull(ab);
        
        Assert.assertNotNull(ab.t);
        Assert.assertEquals(B.class, ab.t.getClass());
        
        Assert.assertNotNull(ab.x);
        Assert.assertEquals(C.class, ab.x.getClass());
        
        Assert.assertNotNull(ab.y);
        Assert.assertEquals(D.class, ab.y.getClass());
        
        Assert.assertNotNull(ab.z);
        Assert.assertEquals(E.class, ab.z.getClass());
    }
    
    /**
     * Tests that pre-reifying a non-reified active descriptor works
     */
    @Test
    // @org.junit.Ignore
    public void testPreReificationWorks() {
        ServiceLocator serviceLocator = ServiceLocatorFactory.getInstance().create(null);
        
        Type type = new TypeLiteral<A<B, C, D, E>>() {}.getType();
        
        AbstractActiveDescriptor<?> ad = BuilderHelper.activeLink(A.class).
                to(type).build();
        
        ad.setImplementationType(type);
        
        ServiceLocatorUtilities.addClasses(serviceLocator,
                B.class, C.class, D.class, E.class);
        
        ActiveDescriptor<?> added = ServiceLocatorUtilities.addOneDescriptor(serviceLocator, ad);
        Assert.assertFalse(added.isReified());
        
        serviceLocator.reifyDescriptor(added);
        
        Assert.assertTrue(added.isReified());
        
        Assert.assertEquals(added.getImplementationType(), type);
        
        A<B, C, D, E> ab = serviceLocator.getService(type);
        Assert.assertNotNull(ab);
        
        Assert.assertNotNull(ab.t);
        Assert.assertEquals(B.class, ab.t.getClass());
        
        Assert.assertNotNull(ab.x);
        Assert.assertEquals(C.class, ab.x.getClass());
        
        Assert.assertNotNull(ab.y);
        Assert.assertEquals(D.class, ab.y.getClass());
        
        Assert.assertNotNull(ab.z);
        Assert.assertEquals(E.class, ab.z.getClass());
    }

}
