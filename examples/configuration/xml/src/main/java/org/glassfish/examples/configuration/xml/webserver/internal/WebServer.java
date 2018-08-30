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

package org.glassfish.examples.configuration.xml.webserver.internal;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.glassfish.hk2.api.IterableProvider;
import org.glassfish.hk2.configuration.api.Configured;
import org.glassfish.hk2.configuration.api.ConfiguredBy;
import org.glassfish.hk2.configuration.api.Dynamicity;
import org.jvnet.hk2.annotations.Service;

/**
 * @author jwells
 *
 */
@Service
// The name in @ConfiguredBy is the xpath to the XML that configures this service
@ConfiguredBy("/application/web-server")
public class WebServer {
    @Configured
    private String name;
    
    @Configured
    private int adminPort;
    
    @Configured(dynamicity=Dynamicity.FULLY_DYNAMIC)
    private String address;
    
    private int sslPort;
    private int port;
    
    private boolean opened = false;
    
    /**
     * These are configured services that can be used to get other
     * variable information about the WebServer.  In this case
     * it is getting information about the certificates that
     * this server can use for SSL
     *
    @Inject
    private IterableProvider<SSLCertificateService> certificates;
     */
    
    /**
     * This method is called to set the port and sshPort.  It is guaranteed that
     * the server will not have these ports open at the time this method is called.
     * That is guaranteed since the ports are not open until the postConstruct method
     * is called on boot, and it is only called between the startDynamicConfiguration
     * and finishDynamicConfiguration methods when a dynamic configuration change is
     * made
     * 
     * @param sshPort The sshPort to use
     * @param port The port to use
     */
    @SuppressWarnings("unused")
    private void setUserPorts(
            @Configured(value="SSLPort", dynamicity=Dynamicity.FULLY_DYNAMIC) int sslPort,
            @Configured(value="port", dynamicity=Dynamicity.FULLY_DYNAMIC) int port) {
        this.sslPort = sslPort;
        this.port = port;
    }
    
    @PostConstruct
    private void postConstruct() {
        opened = true;
    }
    
    @PreDestroy
    private void preDestroy() {
        opened = false;
    }

    /* (non-Javadoc)
     * @see org.glassfish.examples.configuration.webserver.WebServer#getCertificates()
     *
    @Override
    public List<File> getCertificates() {
        LinkedList<File> retVal = new LinkedList<File>();
        
        for (SSLCertificateService certService : certificates) {
            retVal.add(certService.getCertificate());
        }
        
        return retVal;
    }
    */
    
    @Override
    public String toString() {
        return "WebServer(name=" + name + ",adminPort=" + adminPort + ",port=" + port + ",sslPort=" + sslPort + ",opened=" + opened + ")";
    }

}
