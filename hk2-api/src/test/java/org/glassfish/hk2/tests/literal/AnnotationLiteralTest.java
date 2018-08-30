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

package org.glassfish.hk2.tests.literal;

import java.lang.reflect.Field;

import junit.framework.Assert;

import org.glassfish.hk2.api.AnnotationLiteral;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class AnnotationLiteralTest {
    public final static String VALUE1 = "value1";
    public final static String VALUE2 = "value2";
    
    /**
     * Tests that the equals and hashCode works between AnnotationLiterals
     */
    @Test
    public void testEqualsAndHashCodeOfLiteral() {
        AnnoWithString aws1_1 = new AnnoWithStringLiteral(VALUE1);
        AnnoWithString aws1_2 = new AnnoWithStringLiteral(VALUE1);
        AnnoWithString aws2_1 = new AnnoWithStringLiteral(VALUE2);
        
        Assert.assertEquals(aws1_1.hashCode(), aws1_2.hashCode());
        Assert.assertTrue(aws1_1.equals(aws1_2));
        Assert.assertTrue(aws1_2.equals(aws1_1));
        
        Assert.assertFalse(aws1_1.equals(aws2_1));
        Assert.assertFalse(aws1_1.hashCode() == aws2_1.hashCode());
        
    }
    
    /**
     * Tests that the Jdk version of equals works the same as the literal version
     */
    @Test
    public void testEqualsAndHashCodeWithJdk() {
        AnnoWithString aws1 = new AnnoWithStringLiteral(VALUE1);
        AnnoWithString aws2 = new AnnoWithStringLiteral(VALUE2);
        
        AnnoWithString awsJdk = ClassWithAnnoWithString.class.getAnnotation(AnnoWithString.class);
        
        Assert.assertEquals(aws1.hashCode(), awsJdk.hashCode());
        Assert.assertFalse(aws2.hashCode() == awsJdk.hashCode());
        
        Assert.assertTrue(aws1.equals(awsJdk));
        Assert.assertFalse(aws2.equals(awsJdk));
        
        Assert.assertTrue(awsJdk.equals(aws1));
        Assert.assertFalse(awsJdk.equals(aws2));
    }
    
    private Q getQField() {
        Class<?> c = ClassWithQField.class;
        
        Field field;
        try {
            field = c.getField("qField");
        }
        catch (NoSuchFieldException nsfe) {
            return null;
        }
        
        return field.getAnnotation(Q.class);
    }
    
    /**
     * Tests JDK version of equals works with an empty qualifier
     */
    @Test
    public void testEqualsOfEmptyAnnotation() {
        Q qJdk = ClassWithQ.class.getAnnotation(Q.class);
        Q qJdkField = getQField();
        
        Assert.assertNotNull(qJdk);
        Assert.assertNotNull(qJdkField);
        
        Assert.assertEquals(qJdk, qJdkField);
        
        Assert.assertEquals(new QImpl().hashCode(), qJdk.hashCode());
        Assert.assertEquals(qJdkField.hashCode(), qJdk.hashCode());
        
        Assert.assertTrue(new QImpl().equals(qJdk));
        
        Assert.assertTrue(qJdk.equals(new QImpl()));
    }
    
    /**
     * An AnnotationLiteral MUST implement an AnnotationType
     */
    @Test(expected=IllegalStateException.class)
    public void testInvalidAnnotationLiteral() {
        new AnnotationLiteral<Q>() {
            /**
             * 
             */
            private static final long serialVersionUID = 8047528061664493726L;};
    }
    
    public class QImpl extends AnnotationLiteral<Q> implements Q {

        /**
         * 
         */
        private static final long serialVersionUID = 4372411188097605709L;
        
    }

}
