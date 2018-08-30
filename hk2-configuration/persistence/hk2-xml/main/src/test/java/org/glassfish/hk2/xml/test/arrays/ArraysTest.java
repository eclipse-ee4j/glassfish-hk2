/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.xml.test.arrays;

import java.net.URL;
import java.util.Map;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.xml.api.XmlHk2ConfigurationBean;
import org.glassfish.hk2.xml.api.XmlRootHandle;
import org.glassfish.hk2.xml.api.XmlService;
import org.glassfish.hk2.xml.test.basic.beans.Commons;
import org.glassfish.hk2.xml.test.basic.beans.Employee;
import org.glassfish.hk2.xml.test.utilities.Utilities;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests unmarshalling with an array rather than a List
 * @author jwells
 *
 */
public class ArraysTest {
    /**
     * Tests that we can use arrays rather than Lists for
     * children
     * 
     * @throws Exception
     */
    @Test // @org.junit.Ignore
    public void testBasicReadWithArray() throws Exception {
        ServiceLocator locator = Utilities.createLocator();
        XmlService xmlService = locator.getService(XmlService.class);
        
        URL url = getClass().getClassLoader().getResource(Commons.ACME1_FILE);
        
        XmlRootHandle<Employees> rootHandle = xmlService.unmarshal(url.toURI(), Employees.class);
        Employees employees = rootHandle.getRoot();
        
        Assert.assertTrue(employees instanceof XmlHk2ConfigurationBean);
        XmlHk2ConfigurationBean hk2Configuration = (XmlHk2ConfigurationBean) employees;
        
        Map<String, Object> beanLikeMap = hk2Configuration._getBeanLikeMap();
        Assert.assertEquals(Commons.ACME, beanLikeMap.get(Commons.COMPANY_NAME_TAG));
        
        Employee employeeChildList[] = (Employee[]) beanLikeMap.get(Commons.EMPLOYEE_TAG);
        Assert.assertNotNull(employeeChildList);
        Assert.assertEquals(2, employeeChildList.length);
        
        boolean first = true;
        for (Employee employee : employeeChildList) {
            Assert.assertTrue(employee instanceof XmlHk2ConfigurationBean);
            XmlHk2ConfigurationBean employeeConfiguration = (XmlHk2ConfigurationBean) employee;
            
            Map<String, Object> employeeBeanLikeMap = employeeConfiguration._getBeanLikeMap();
            
            if (first) {
                first = false;
                
                Assert.assertEquals(Commons.HUNDRED_LONG, employeeBeanLikeMap.get(Commons.ID_TAG));
                Assert.assertEquals(Commons.BOB, employeeBeanLikeMap.get(Commons.NAME_TAG));
            }
            else {
                Assert.assertEquals(Commons.HUNDRED_ONE_LONG, employeeBeanLikeMap.get(Commons.ID_TAG));
                Assert.assertEquals(Commons.CAROL, employeeBeanLikeMap.get(Commons.NAME_TAG));
            }
        }
        
        Assert.assertNotNull(locator.getService(Employees.class));
        
        Assert.assertNotNull(locator.getService(Employee.class, Commons.BOB));
        Assert.assertNotNull(locator.getService(Employee.class, Commons.CAROL));
        
    }

}
