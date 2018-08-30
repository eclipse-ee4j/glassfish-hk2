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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jvnet.hk2.annotations.Contract;

/**
 * @author jwells
 *
 */
@Contract
@XmlRootElement(name="root-only")
@XmlType(propOrder={ "name"
        , "address"
        , "notSet"
        , "notSetLong"
        , "enumeration"})
public interface RootOnlyBean {
    @XmlElement(name="name", defaultValue="bob")
    public String getName();
    public void setName(String name);
    
    @XmlElement(name="address")
    public void setAddress(String address);
    public String getAddress();
    
    @XmlElement(name="notSet")
    public String getNotSet();
    public void setNotSet(String notSet);
    
    @XmlElement(name="notSetLong")
    public long getNotSetLong();
    public void setNotSetLong(long notSet);
    
    @XmlElement(name="enumeration")
    public NFCEast getEnumeration();
    public void setEnumeration(NFCEast east);

}
