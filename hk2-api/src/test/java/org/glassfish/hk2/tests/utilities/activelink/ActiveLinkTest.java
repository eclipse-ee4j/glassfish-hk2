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

package org.glassfish.hk2.tests.utilities.activelink;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.inject.Named;
import javax.inject.Singleton;

import junit.framework.Assert;

import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class ActiveLinkTest {
    private final static String NAME = "name";
    
    /**
     * Tests a simple and mostly empty descriptor
     */
    @Test
    public void testOnlyImpl() {
        AbstractActiveDescriptor<?> desc = BuilderHelper.activeLink(ServiceA.class).build();
        
        Assert.assertSame(ServiceA.class, desc.getImplementationClass());
        Assert.assertSame(ServiceA.class.getName(), desc.getImplementation());
        
        Assert.assertNull(desc.getName());
        
        Assert.assertEquals(PerLookup.class, desc.getScopeAnnotation());
        Assert.assertEquals(PerLookup.class.getName(), desc.getScope());
        
        Assert.assertTrue(desc.getAdvertisedContracts().isEmpty());
        Assert.assertTrue(desc.getContractTypes().isEmpty());
        
        Assert.assertTrue(desc.getQualifiers().isEmpty());
        Assert.assertTrue(desc.getQualifierAnnotations().isEmpty());
        
        Assert.assertNull(desc.getLoader());
        Assert.assertSame(DescriptorType.CLASS, desc.getDescriptorType());
        Assert.assertTrue(desc.getInjectees().isEmpty());
        
        Assert.assertFalse(desc.isReified());
    }
    
    @Test
    public void testDescWithFields() {
        SimpleQualifier1 sq1 = new SimpleQualifier1Impl();
        
        AbstractActiveDescriptor<?> desc = BuilderHelper.activeLink(ServiceA.class).
                to(SimpleInterface1.class).
                in(Singleton.class).
                qualifiedBy(sq1).
                named(NAME).
                has(NAME, NAME).
                ofRank(1).
                proxy().
                proxyForSameScope(false).
                localOnly().
                andLoadWith(new HK2Loader() {

                    @Override
                    public Class<?> loadClass(String className)
                            throws MultiException {
                        throw new AssertionError("not called");
                    }
                    
                }).
                build();
                
        
        Assert.assertSame(ServiceA.class, desc.getImplementationClass());
        Assert.assertSame(ServiceA.class.getName(), desc.getImplementation());
        
        Assert.assertSame(NAME, desc.getName());
        
        Assert.assertEquals(Singleton.class, desc.getScopeAnnotation());
        Assert.assertEquals(Singleton.class.getName(), desc.getScope());
        
        Assert.assertEquals(Boolean.TRUE, desc.isProxiable());
        Assert.assertEquals(Boolean.FALSE, desc.isProxyForSameScope());
        Assert.assertEquals(DescriptorVisibility.LOCAL, desc.getDescriptorVisibility());
        
        testSetOfOne(desc.getAdvertisedContracts(), SimpleInterface1.class.getName());
        testSetOfOne(desc.getContractTypes(), SimpleInterface1.class);
        
        boolean foundSQ1 = false;
        boolean foundName = false;
        for (Annotation anno : desc.getQualifierAnnotations()) {
            if (anno.annotationType().equals(SimpleQualifier1.class)) {
                foundSQ1 = true;
            }
            else if (anno.annotationType().equals(Named.class)) {
                String annoName = ((Named) anno).value();
                Assert.assertSame(annoName, NAME);
                foundName = true;
            }
            else {
                Assert.fail("Unknown annotation found " + anno);
            }
        }
        Assert.assertTrue(foundName);
        Assert.assertTrue(foundSQ1);
        
        foundSQ1 = false;
        foundName = false;
        for (String anno : desc.getQualifiers()) {
            if (anno.equals(SimpleQualifier1.class.getName())) {
                foundSQ1 = true;
            }
            else if (anno.equals(Named.class.getName())) {
                foundName = true;
            }
            else {
                Assert.fail("Unknown annotation found " + anno);
            }
        }
        Assert.assertTrue(foundName);
        Assert.assertTrue(foundSQ1);
        
        Assert.assertNotNull(desc.getLoader());
        Assert.assertSame(DescriptorType.CLASS, desc.getDescriptorType());
        Assert.assertTrue(desc.getInjectees().isEmpty());
        
        Assert.assertFalse(desc.isReified());
    }
    
    @SuppressWarnings("deprecation")
    @Test
    public void testFactoryDescWithFields() {
        SimpleQualifier1 sq1 = new SimpleQualifier1Impl();
        
        AbstractActiveDescriptor<?> desc = BuilderHelper.activeLink(ServiceA.class).
                to(SimpleInterface1.class).
                in(Singleton.class).
                qualifiedBy(sq1).
                named(NAME).
                has(NAME, NAME).
                ofRank(1).
                proxy(false).
                visibility(DescriptorVisibility.LOCAL).
                andLoadWith(new HK2Loader() {

                    @Override
                    public Class<?> loadClass(String className)
                            throws MultiException {
                        throw new AssertionError("not called");
                    }
                    
                }).
                buildFactory();
                
        
        Assert.assertSame(ServiceA.class, desc.getImplementationClass());
        Assert.assertSame(ServiceA.class.getName(), desc.getImplementation());
        
        Assert.assertSame(NAME, desc.getName());
        
        Assert.assertEquals(Singleton.class, desc.getScopeAnnotation());
        Assert.assertEquals(Singleton.class.getName(), desc.getScope());
        Assert.assertNull(desc.getScopeAsAnnotation());  // was not explicitly set
        
        Assert.assertEquals(Boolean.FALSE, desc.isProxiable());
        Assert.assertEquals(DescriptorVisibility.LOCAL, desc.getDescriptorVisibility());
        
        testSetOfOne(desc.getAdvertisedContracts(), SimpleInterface1.class.getName());
        testSetOfOne(desc.getContractTypes(), SimpleInterface1.class);
        
        boolean foundSQ1 = false;
        boolean foundName = false;
        for (Annotation anno : desc.getQualifierAnnotations()) {
            if (anno.annotationType().equals(SimpleQualifier1.class)) {
                foundSQ1 = true;
            }
            else if (anno.annotationType().equals(Named.class)) {
                String annoName = ((Named) anno).value();
                Assert.assertSame(annoName, NAME);
                foundName = true;
            }
            else {
                Assert.fail("Unknown annotation found " + anno);
            }
        }
        Assert.assertTrue(foundName);
        Assert.assertTrue(foundSQ1);
        
        foundSQ1 = false;
        foundName = false;
        for (String anno : desc.getQualifiers()) {
            if (anno.equals(SimpleQualifier1.class.getName())) {
                foundSQ1 = true;
            }
            else if (anno.equals(Named.class.getName())) {
                foundName = true;
            }
            else {
                Assert.fail("Unknown annotation found " + anno);
            }
        }
        Assert.assertTrue(foundName);
        Assert.assertTrue(foundSQ1);
        
        Assert.assertNotNull(desc.getLoader());
        Assert.assertSame(DescriptorType.PROVIDE_METHOD, desc.getDescriptorType());
        Assert.assertTrue(desc.getInjectees().isEmpty());
        
        Assert.assertFalse(desc.isReified());
    }
    
    /**
     * Tests that adding the whole annotation adds the class and string
     */
    @Test
    public void testDescWithAnnotationGivenDirectly() {
        AbstractActiveDescriptor<?> desc = BuilderHelper.activeLink(ServiceA.class).
                in(ServiceLocatorUtilities.getSingletonAnnotation()).
                build();
        
        Assert.assertEquals(desc.getScopeAsAnnotation(), ServiceLocatorUtilities.getSingletonAnnotation());
        Assert.assertEquals(desc.getScopeAnnotation(), Singleton.class);
        Assert.assertEquals(desc.getScope(), Singleton.class.getName()); 
    }
    
    private void testSetOfOne(Set<?> set, Object item) {
        Assert.assertNotNull(set);
        Assert.assertNotNull(item);
        
        Assert.assertTrue(set.size() == 1);
        
        Object setItem = null;
        for (Object candidate : set) {
            setItem = candidate;
        }
        
        Assert.assertSame(item, setItem);
        
    }

}
