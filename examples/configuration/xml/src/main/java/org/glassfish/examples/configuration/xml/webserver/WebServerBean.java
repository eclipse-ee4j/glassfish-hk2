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

package org.glassfish.examples.configuration.xml.webserver;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.glassfish.hk2.xml.api.annotations.Hk2XmlPreGenerate;
import org.glassfish.hk2.xml.api.annotations.XmlIdentifier;
import org.jvnet.hk2.annotations.Contract;

/**
 * This bean defines a WebServer
 * 
 * @author jwells
 *
 */
@Contract
@Hk2XmlPreGenerate
public interface WebServerBean {
    /**
     * @return the name
     */
    @XmlAttribute(required=true)
    @XmlIdentifier
    public String getName();
    
    /**
     * @param name the name to set
     */
    public void setName(String name);
    
    /**
     * @return the address
     */
    @XmlElement
    public String getAddress();
    
    /**
     * @param address the address to set
     */
    public void setAddress(String address);
    
    /**
     * @return the adminPort
     */
    @XmlElement(defaultValue="1007")
    public int getAdminPort();
    
    /**
     * @param adminPort the adminPort to set
     */
    public void setAdminPort(int adminPort);
    
    /**
     * @return the sslPort
     */
    @XmlElement(name="SSLPort", defaultValue="81")
    public int getSSLPort();
    
    /**
     * @param sslPort the sslPort to set
     */
    public void setSSLPort(int sslPort);
    
    /**
     * @return the port
     */
    @XmlElement(defaultValue="80")
    public int getPort();
    
    /**
     * @param sshPort the port to set
     */
    public void setPort(int port);
}
