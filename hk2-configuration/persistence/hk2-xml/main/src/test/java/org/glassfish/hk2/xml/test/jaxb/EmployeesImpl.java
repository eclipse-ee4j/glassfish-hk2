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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author jwells
 *
 */
@XmlRootElement(name="employees")
public class EmployeesImpl {
    private String companyName;
    private Financials financials;
    private Employee employees[];
    private OtherData otherData[];
    
    public String getCompanyName() { return companyName; }
    
    @XmlElement(name="company-name")
    public void setCompanyName(String name) {
        companyName = name;
    }
    
    @XmlElement
    public void setFinancials(Financials finances) {
        financials = finances;
    }
    public Financials getFinancials() { return financials; }
    
    @XmlElement(name="employee")
    public void setEmployees(Employee[] employees) {
        this.employees = employees;
    }
    public Employee[] getEmployees() { return employees; }
    
    @XmlElement(name="other-data")
    public void setOtherData(OtherData[] otherData) {
        this.otherData = otherData;
    }
    public OtherData[] getOtherData() { return otherData; }
}
