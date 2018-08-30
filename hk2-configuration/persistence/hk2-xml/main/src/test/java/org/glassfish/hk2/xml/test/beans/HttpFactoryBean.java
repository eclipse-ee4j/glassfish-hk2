/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.xml.test.beans;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.jvnet.hk2.annotations.Contract;

/**
 * This bean has children with names but does
 * not have a key itself
 * 
 * @author jwells
 *
 */
@Contract
public interface HttpFactoryBean {
    @XmlAttribute(name="non-key-identifier")
    public String getNonKeyIdentifier();
    
    @XmlElement(name="http-server")
    public List<HttpServerBean> getHttpServers();
    public void addHttpServer(String name);
    public void removeHttpServer(String name);

}
