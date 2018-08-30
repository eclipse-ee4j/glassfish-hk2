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

import org.jvnet.hk2.annotations.Contract;

/**
 * This is a sub-bean that is a single-stanza sub-bean of DomainBean
 * which has both list children and non-list children
 * 
 * @author jwells
 *
 */
@Contract
public interface SecurityManagerBean {
    @XmlElement(name="authorization-provider")
    public List<AuthorizationProviderBean> getAuthorizationProviders();
    public void setAuthorizationProviders(List<AuthorizationProviderBean> providers);
    public void addAuthorizationProvider(AuthorizationProviderBean provider);
    public void removeAuthorizationProvider(String name);
    
    @XmlElement(name="ssl-manager")
    public SSLManagerBean getSSLManager();
    public void setSSLManager(SSLManagerBean sslManager);

}
