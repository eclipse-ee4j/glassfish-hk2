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

package org.glassfish.hk2.xml.test.dynamic.marshall;

import java.net.URL;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author jwells
 *
 */
@XmlRootElement(name="ordering-root-bean")
@XmlType(propOrder={"propertyF", "propertyG", "propertyE", "propertyA", "propertyC", "propertyB", "propertyD"})
public interface OrderingRootBean {
    @XmlElement(name="a")
    public List<KeyedLeafBean> getPropertyA();
    public KeyedLeafBean addPropertyA(String propH);
    
    @XmlElement(name="b")
    public List<UnkeyedLeafBean> getPropertyB();
    public UnkeyedLeafBean addPropertyB();
    
    @XmlElement(name="c")
    public UnkeyedLeafBean[] getPropertyC();
    public UnkeyedLeafBean addPropertyC();
    
    @XmlElement(name="d")
    public KeyedLeafBean[] getPropertyD();
    public KeyedLeafBean addPropertyD(String propH);
    
    @XmlElement(name="e")
    public KeyedLeafBean getPropertyE();
    public void setPropertyE(KeyedLeafBean addMe);
    
    @XmlElement(name="f")
    public UnkeyedLeafBean getPropertyF();
    public void setPropertyF(UnkeyedLeafBean addMe);
    
    @XmlElement(name="g")
    public String getPropertyG();
    public void setPropertyG(String prop);
    
    public void aCustomizerMethod(double foo, List<URL> bar);
}
