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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.glassfish.hk2.xml.api.annotations.PluralOf;
import org.glassfish.hk2.xml.test.basic.beans.Employee;
import org.glassfish.hk2.xml.test.basic.beans.Financials;
import org.glassfish.hk2.xml.test.basic.beans.OtherData;
import org.jvnet.hk2.annotations.Contract;

/**
 * @author jwells
 *
 */
@XmlRootElement @Contract
public interface Employees {
    public String getCompanyName();
    
    @XmlElement(name="company-name")
    public void setCompanyName(String name);
    
    @XmlElement @PluralOf("Financials")
    public void setFinancials(Financials finances);
    public Financials getFinancials();
    public void addFinancials();
    public Financials removeFinancials();
    
    @XmlElement(name="employee")
    public void setEmployees(Employee[] employees);
    public Employee[] getEmployees();
    public Employee lookupEmployee(String employeeName);
    public void addEmployee(String employeeName);
    public void addEmployee(String employeeName, int index);
    public void addEmployee(Employee employee);
    public void addEmployee(Employee employee, int index);
    public Employee removeEmployee(String employeeName);
    
    @XmlElement(name="other-data")
    public void setOtherData(OtherData[] otherData);
    public OtherData[] getOtherData();
    public void addOtherData(int position);
    public void addOtherData(OtherData otherData);
    public void addOtherData(OtherData otherData, int position);
    public OtherData removeOtherData(int position);
}
