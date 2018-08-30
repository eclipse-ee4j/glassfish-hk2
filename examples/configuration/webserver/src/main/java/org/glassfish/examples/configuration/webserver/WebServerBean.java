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

package org.glassfish.examples.configuration.webserver;

/**
 * This bean describes a WebServer
 * 
 * @author jwells
 *
 */
public class WebServerBean {
    private String name;
    private String address;
    private int adminPort;
    private int sslPort;
    private int port;
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }
    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }
    /**
     * @return the adminPort
     */
    public int getAdminPort() {
        return adminPort;
    }
    /**
     * @param adminPort the adminPort to set
     */
    public void setAdminPort(int adminPort) {
        this.adminPort = adminPort;
    }
    /**
     * @return the sslPort
     */
    public int getSSLPort() {
        return sslPort;
    }
    /**
     * @param sslPort the sslPort to set
     */
    public void setSSLPort(int sslPort) {
        this.sslPort = sslPort;
    }
    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }
    /**
     * @param sshPort the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

}
