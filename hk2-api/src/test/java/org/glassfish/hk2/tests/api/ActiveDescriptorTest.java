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

package org.glassfish.hk2.tests.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

import junit.framework.Assert;

import org.glassfish.hk2.utilities.AbstractActiveDescriptor;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class ActiveDescriptorTest {
    /**
     * Tests the caching API of an active descriptor
     */
    @Test
    public void testCache() {
        AbstractActiveDescriptor<Object> testMe = new MyActiveDescriptor<Object>();
        
        Object addMe = new Object();
        
        Assert.assertFalse(testMe.isCacheSet());
        
        testMe.setCache(addMe);
        
        Assert.assertTrue(testMe.isCacheSet());
        
        Object fromCache = testMe.getCache();
        
        Assert.assertEquals(addMe, fromCache);
        
        testMe.releaseCache();
        
        Assert.assertFalse(testMe.isCacheSet());
        
        Assert.assertNull(testMe.getCache());
    }
    
    /**
     * Tests adding and removing contract types
     */
    @Test
    public void testAddAndRemoveContracts() {
        AbstractActiveDescriptor<Object> testMe = new MyActiveDescriptor<Object>();
        
        Set<Type> currentContractTypes = testMe.getContractTypes();
        Assert.assertTrue(currentContractTypes.isEmpty());
        
        Set<String> currentContractNames = testMe.getAdvertisedContracts();
        Assert.assertTrue(currentContractNames.isEmpty());
        
        testMe.addContractType(Object.class);
        
        currentContractTypes = testMe.getContractTypes();
        Assert.assertEquals(1, currentContractTypes.size());
        Assert.assertTrue(currentContractTypes.contains(Object.class));
        
        currentContractNames = testMe.getAdvertisedContracts();
        Assert.assertEquals(1, currentContractNames.size());
        Assert.assertTrue(currentContractNames.contains(Object.class.getName()));
        
        testMe.addContractType(String.class);
        
        currentContractTypes = testMe.getContractTypes();
        Assert.assertEquals(2, currentContractTypes.size());
        Assert.assertTrue(currentContractTypes.contains(Object.class));
        Assert.assertTrue(currentContractTypes.contains(String.class));
        
        currentContractNames = testMe.getAdvertisedContracts();
        Assert.assertEquals(2, currentContractNames.size());
        Assert.assertTrue(currentContractNames.contains(Object.class.getName()));
        Assert.assertTrue(currentContractNames.contains(String.class.getName()));
        
        Assert.assertTrue(testMe.removeContractType(Object.class));
        Assert.assertFalse(testMe.removeContractType(Object.class));
        
        currentContractTypes = testMe.getContractTypes();
        Assert.assertEquals(1, currentContractTypes.size());
        Assert.assertTrue(currentContractTypes.contains(String.class));
        
        currentContractNames = testMe.getAdvertisedContracts();
        Assert.assertEquals(1, currentContractNames.size());
        Assert.assertTrue(currentContractNames.contains(String.class.getName()));
        
        Assert.assertTrue(testMe.removeContractType(String.class));
        Assert.assertFalse(testMe.removeContractType(String.class));
        
        currentContractTypes = testMe.getContractTypes();
        Assert.assertEquals(0, currentContractTypes.size());
        
        currentContractNames = testMe.getAdvertisedContracts();
        Assert.assertEquals(0, currentContractNames.size());
    }
    
    /**
     * Tests adding and removing contract types
     */
    @Test
    public void testAddAndRemoveQualifiers() {
        AbstractActiveDescriptor<Object> testMe = new MyActiveDescriptor<Object>();
        
        Blue blue = new BlueImpl();
        Green green = new GreenImpl();
        
        Set<Annotation> currentQualifierAnnotations = testMe.getQualifierAnnotations();
        Assert.assertTrue(currentQualifierAnnotations.isEmpty());
        
        Set<String> currentQualifierNames = testMe.getQualifiers();
        Assert.assertTrue(currentQualifierNames.isEmpty());
        
        testMe.addQualifierAnnotation(green);
        
        currentQualifierAnnotations = testMe.getQualifierAnnotations();
        Assert.assertEquals(1, currentQualifierAnnotations.size());
        Assert.assertTrue(currentQualifierAnnotations.contains(green));
        
        currentQualifierNames = testMe.getQualifiers();
        Assert.assertEquals(1, currentQualifierNames.size());
        Assert.assertTrue(currentQualifierNames.contains(Green.class.getName()));
        
        testMe.addQualifierAnnotation(blue);
        
        currentQualifierAnnotations = testMe.getQualifierAnnotations();
        Assert.assertEquals(2, currentQualifierAnnotations.size());
        Assert.assertTrue(currentQualifierAnnotations.contains(blue));
        Assert.assertTrue(currentQualifierAnnotations.contains(green));
        
        currentQualifierNames = testMe.getQualifiers();
        Assert.assertEquals(2, currentQualifierNames.size());
        Assert.assertTrue(currentQualifierNames.contains(Blue.class.getName()));
        Assert.assertTrue(currentQualifierNames.contains(Green.class.getName()));
        
        Assert.assertTrue(testMe.removeQualifierAnnotation(green));
        Assert.assertFalse(testMe.removeQualifierAnnotation(green));
        
        currentQualifierAnnotations = testMe.getQualifierAnnotations();
        Assert.assertEquals(1, currentQualifierAnnotations.size());
        Assert.assertTrue(currentQualifierAnnotations.contains(blue));
        
        currentQualifierNames = testMe.getQualifiers();
        Assert.assertEquals(1, currentQualifierNames.size());
        Assert.assertTrue(currentQualifierNames.contains(blue.annotationType().getName()));
        
        Assert.assertTrue(testMe.removeQualifierAnnotation(blue));
        Assert.assertFalse(testMe.removeQualifierAnnotation(blue));
        
        currentQualifierAnnotations = testMe.getQualifierAnnotations();
        Assert.assertEquals(0, currentQualifierAnnotations.size());
        
        currentQualifierNames = testMe.getQualifiers();
        Assert.assertEquals(0, currentQualifierNames.size());
    }
    
    /**
     * Tests adding and removing contract types
     */
    @Test
    public void testCreateParameterizedConstant() {
        ParameterizedObject po = new ParameterizedObject();
        
        AbstractActiveDescriptor<ParameterizedObject> desc = BuilderHelper.createConstantDescriptor(po);
        
        {
            Set<String> contracts = desc.getAdvertisedContracts();
            Assert.assertEquals(2, contracts.size());
            Assert.assertTrue(contracts.contains(ParameterizedObject.class.getName()));
            Assert.assertTrue(contracts.contains(ParameterizedInterface.class.getName()));
        }
        
        {
            Set<Type> contractTypes = desc.getContractTypes();
            Assert.assertEquals(2, contractTypes.size());
            
            for (Type type : contractTypes) {
                if (type instanceof Class) {
                    Assert.assertEquals(ParameterizedObject.class, type);
                }
                else if (type instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) type;
                    
                    Assert.assertEquals(ParameterizedInterface.class, pt.getRawType());
                    Assert.assertEquals(String.class, pt.getActualTypeArguments()[0]);
                }
                else {
                    Assert.fail("Uknown type: " + type);
                }
            }
        }
    }

}
