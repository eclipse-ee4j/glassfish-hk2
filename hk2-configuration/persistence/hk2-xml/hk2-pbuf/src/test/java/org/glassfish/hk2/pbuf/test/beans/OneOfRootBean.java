/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.pbuf.test.beans;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.glassfish.hk2.pbuf.api.annotations.OneOf;
import org.jvnet.hk2.annotations.Contract;

/**
 * @author jwells
 *
 */
@Contract
@XmlRootElement(name="onOf")
@XmlType(propOrder={
        "mr"
        , "mrs"
        , "miss"
        , "between"
        , "CEO"
        , "CFO"
        , "CTO"
        , "employee"
        , "country"
        })
public interface OneOfRootBean {
    @XmlElement @OneOf("title")
    public void setMr(String mr);
    public String getMr();
    
    @XmlElement @OneOf("title")
    public String getMiss();
    public void setMiss(String name);
    
    @XmlElement @OneOf("job")
    public int getCEO();
    public void setCEO(int ceo);
    
    @XmlElement @OneOf("job")
    public void setCFO(CustomerBean customers);
    public CustomerBean getCFO();
    
    @XmlElement @OneOf("title")
    public String getMrs();
    public void setMrs(String mrs);
    
    @XmlElement @OneOf("job")
    public double getCTO();
    public void setCTO(double cto);
    
    @XmlElement
    public String getCountry();
    public void setCountry(String country);
    
    @XmlElement
    public String getBetween();
    public void setBetween(String between);
    
    @XmlElement @OneOf("job")
    public FooBean getEmployee();
    public void setEmployee(FooBean foo);
}
