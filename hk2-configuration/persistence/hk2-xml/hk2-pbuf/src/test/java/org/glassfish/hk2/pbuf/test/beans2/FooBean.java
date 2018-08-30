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

package org.glassfish.hk2.pbuf.test.beans2;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlType;

import org.glassfish.hk2.xml.api.annotations.Hk2XmlPreGenerate;
import org.jvnet.hk2.annotations.Contract;

/**
 * This bean intentionally has the same name
 * but different package as the FooBean in
 * beans3
 * 
 * @author jwells
 *
 */
@Contract
@XmlType(propOrder={ "name" })
@Hk2XmlPreGenerate
public interface FooBean {
    @XmlElement(name="name", required=true)
    @XmlID
    public String getName();
    public void setName(String name);
}
