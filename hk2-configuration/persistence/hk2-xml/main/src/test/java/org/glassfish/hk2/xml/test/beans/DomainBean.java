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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.glassfish.hk2.xml.api.annotations.PluralOf;
import org.jvnet.hk2.annotations.Contract;

/**
 * @author jwells
 *
 */
@XmlRootElement(name="domain") @Contract
@XmlType(propOrder={
        "name"
        , "securityManager"
        , "machines"
        , "JMSServers"
        , "HTTPFactories"
        , "HTTPSFactories"
        , "subnetwork"
        , "taxonomy"
        , "diagnostics"})
public interface DomainBean extends NamedBean {
    @XmlElement(name="security-manager")
    public SecurityManagerBean getSecurityManager();
    public void setSecurityManager(SecurityManagerBean secBean);
    public boolean removeSecurityManager();
    
    @XmlElement(name="machine")
    public List<MachineBean> getMachines();
    public void setMachines(List<MachineBean> machines);
    public void addMachine(MachineBean machine);
    public MachineBean removeMachine(String machine);
    public MachineBean lookupMachine(String machine);
    
    @XmlElement(name="jms-server")
    public JMSServerBean[] getJMSServers();
    public void setJMSServers(JMSServerBean[] jmsServers);
    public void addJMSServer(JMSServerBean jmsServer);
    public JMSServerBean removeJMSServer(String jmsServer);
    public JMSServerBean lookupJMSServer(String name);
    
    @XmlElement(name="http-factory") @PluralOf("HTTPFactory")
    public List<HttpFactoryBean> getHTTPFactories();
    public void setHTTPFactories(List<HttpFactoryBean> httpFactories);
    public HttpFactoryBean addHTTPFactory(HttpFactoryBean factory);
    public HttpFactoryBean removeHTTPFactory(HttpFactoryBean factory);
    
    @XmlElement(name="https-factory") @PluralOf("HTTPSFactory")
    public HttpsFactoryBean[] getHTTPSFactories();
    public void setHTTPSFactories(HttpsFactoryBean[] httpsFactories);
    public HttpsFactoryBean addHTTPSFactory(HttpFactoryBean factory);
    public void removeHTTPSFactory(HttpsFactoryBean factory);
    public void removeHTTPSFactory(int index);
    
    @XmlElement(name="subnetwork", defaultValue="0.0.0.255")
    public String getSubnetwork();
    public void setSubnetwork(String subnetwork);
    
    @XmlElement(name="taxonomy")
    public String getTaxonomy();
    public void setTaxonomy(String taxonomy);
    
    @XmlElement(name="diagnostics")
    public DiagnosticsBean getDiagnostics();
    public void setDiagnostics(DiagnosticsBean above);
}
