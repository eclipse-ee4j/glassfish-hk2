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

package org.glassfish.hk2.xml.test.basic.beans;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.glassfish.hk2.xml.api.annotations.PluralOf;
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
    public void setEmployees(List<Employee> employees);
    @NotNull
    public List<Employee> getEmployees();
    public Employee lookupEmployee(String employeeName);
    public void addEmployee(String employeeName);
    public void addEmployee(String employeeName, int index);
    public void addEmployee(Employee employee);
    public void addEmployee(Employee employee, int index);
    public Employee removeEmployee(String employeeName);
    
    @XmlElement(name="other-data")
    public void setOtherData(List<OtherData> otherData);
    public List<OtherData> getOtherData();
    public void addOtherData(int position);
    public void addOtherData(OtherData otherData);
    public void addOtherData(OtherData otherData, int position);
    public boolean removeOtherData(int position);
    
    @XmlElement(name="company-names")
    public void setNames(NamedBean[] named);
    public NamedBean[] getNames();
    public NamedBean addName(String name);
    public NamedBean addName(String name, int index);
    public NamedBean addName(NamedBean namedBean);
    public NamedBean addName(NamedBean namedBean, int index);
    
    @XmlElement(name="bagel-type")
    @EverythingBagel(byteValue = 13,
        booleanValue=true,
        charValue = 'e',
        shortValue = 13,
        intValue = 13,
        longValue = 13L,
        floatValue = (float) 13.00,
        doubleValue = 13.00,
        enumValue = GreekEnum.BETA,
        stringValue = "13",
        classValue = Employees.class,
    
        byteArrayValue = { 13, 14 },
        booleanArrayValue = { true, false },
        charArrayValue = { 'e', 'E' },
        shortArrayValue = { 13, 14 },
        intArrayValue = { 13, 14 },
        longArrayValue = { 13L, 14L },
        floatArrayValue = { (float) 13.00, (float) 14,00 },
        doubleArrayValue = { 13.00, 14.00 },
        enumArrayValue = { GreekEnum.GAMMA, GreekEnum.ALPHA },
        stringArrayValue = { "13", "14" },
        classArrayValue = { String.class, double.class })
    public void setBagelPreference(int bagelType);
    public int getBagelPreference();
    
    @XmlElement(name="no-child-list")
    public void setNoChildList(List<OtherData> noChildren);
    public List<OtherData> getNoChildList();
    
    @XmlElement(name="no-child-array")
    public void setNoChildArray(OtherData noChildren[]);
    public OtherData[] getNoChildArray();
    
    @XmlElement(name="set-to-null-string")
    public void setAStringThatWillBeSetToNull(String setMe);
    public String getAStringThatWillBeSetToNull();
    
    @XmlElement(name="encrypted-credentials")
    public byte[] getEncryptedCredentials();
    public void setEncryptedCredentials(byte[] creds);
}
