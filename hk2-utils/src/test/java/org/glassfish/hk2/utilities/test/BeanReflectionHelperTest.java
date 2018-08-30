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

package org.glassfish.hk2.utilities.test;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;

import org.glassfish.hk2.utilities.reflection.BeanReflectionHelper;
import org.glassfish.hk2.utilities.reflection.ClassReflectionHelper;
import org.glassfish.hk2.utilities.reflection.internal.ClassReflectionHelperImpl;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class BeanReflectionHelperTest {
    private final static String VALUE = "vAlUe";
    private final static int ANOTHER_VALUE = 13;
    private final static long THIRD_VALUE = Long.MIN_VALUE;
    private final static String BEAN2_KEY = "bean2";
    private final static String BEAN3_KEY = "bean3";
    
    private final static ClassReflectionHelper classHelper = new ClassReflectionHelperImpl();
    
    private static GenericJavaBean createStandardBean() {
        GenericJavaBean gjb = new GenericJavaBean();
        
        gjb.setValue(VALUE);
        gjb.setAnotherValue(ANOTHER_VALUE);
        gjb.setThirdValue(THIRD_VALUE);
        gjb.setBean3(new Generic3JavaBean());
        
        return gjb;
    }
    
    /**
     * Converts a javabean to a map
     */
    @Test
    public void testConvertingBeanToMap() {
        GenericJavaBean gjb = createStandardBean();
        
        Map<String, Object> map = BeanReflectionHelper.convertJavaBeanToBeanLikeMap(classHelper, gjb);
        
        Assert.assertEquals(5, map.size());
        
        Assert.assertEquals(VALUE, map.get("value"));
        int mapAnotherValue = (Integer) map.get("anotherValue");
        long mapThirdValue = (Long) map.get("thirdValue");
        Generic2JavaBean bean2 = (Generic2JavaBean) map.get(BEAN2_KEY);
        Generic3JavaBean bean3 = (Generic3JavaBean) map.get(BEAN3_KEY);
        
        Assert.assertEquals(ANOTHER_VALUE, mapAnotherValue);
        Assert.assertEquals(THIRD_VALUE, mapThirdValue);
        Assert.assertNull(bean2);
        Assert.assertNotNull(bean3);
    }
    
    
    
    /**
     * Converts a javabean to a map
     */
    @Test
    public void testGetMapChangeEvents() {
        GenericJavaBean gjb = createStandardBean();
        
        Map<String, Object> oldMap = BeanReflectionHelper.convertJavaBeanToBeanLikeMap(classHelper, gjb);
        oldMap.remove(BEAN2_KEY);
        
        Map<String, Object> newMap = new HashMap<String, Object>(oldMap);
        
        newMap.put(BEAN2_KEY, new Generic2JavaBean());
        newMap.remove(BEAN3_KEY);
        newMap.put("anotherValue", new Integer(ANOTHER_VALUE + 1));
        
        PropertyChangeEvent events[] = BeanReflectionHelper.getChangeEvents(classHelper, oldMap, newMap);
        
        Assert.assertEquals(3, events.length);
        
        boolean modify = false;
        boolean add = false;
        boolean remove = false;
        for (int lcv = 0; lcv < events.length; lcv++) {
            PropertyChangeEvent event = events[lcv];
            
            if ("anotherValue".equals(event.getPropertyName())) {
                Assert.assertEquals(new Integer(13), event.getOldValue());
                Assert.assertEquals(new Integer(14), event.getNewValue());
                modify = true;
            }
            else if (BEAN2_KEY.equals(event.getPropertyName())) {
                Assert.assertNotNull(event.getNewValue());
                Assert.assertNull(event.getOldValue());
                add = true;
            }
            else if(BEAN3_KEY.equals(event.getPropertyName())) {
                Assert.assertNull(event.getNewValue());
                Assert.assertNotNull(event.getOldValue());
                remove = true;
            }
        }
        
        Assert.assertTrue(modify);
        Assert.assertTrue(add);
        Assert.assertTrue(remove);
    }

}
