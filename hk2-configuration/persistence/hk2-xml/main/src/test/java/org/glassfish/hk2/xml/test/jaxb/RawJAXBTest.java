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

package org.glassfish.hk2.xml.test.jaxb;

import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.glassfish.hk2.xml.test.basic.beans.Commons;
import org.junit.Assert;
import org.junit.Test;

/**
 * This test suite is just here to test how JAXB
 * works in some scenarios.  It is only here to
 * clarify how JAXB works in some scenarios
 * 
 * @author jwells
 *
 */
public class RawJAXBTest {
    /**
     * Tests whether or not children can
     * be arrays
     */
    @Test
    public void testArrayChildren() throws Exception {
        URL url = getClass().getClassLoader().getResource(Commons.ACME1_FILE);
        
        JAXBContext context = JAXBContext.newInstance(EmployeesImpl.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        EmployeesImpl employees = (EmployeesImpl) unmarshaller.unmarshal(url);
        
        Employee employee[] = employees.getEmployees();
        Assert.assertEquals(2, employee.length);
        
        Assert.assertEquals(Commons.BOB, employee[0].getName());
        Assert.assertEquals(Commons.CAROL, employee[1].getName());
    }

}
