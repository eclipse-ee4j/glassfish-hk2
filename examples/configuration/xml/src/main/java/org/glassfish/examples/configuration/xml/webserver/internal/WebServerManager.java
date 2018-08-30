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

import javax.inject.Inject;

import org.glassfish.examples.configuration.xml.webserver.WebServerBean;
import org.glassfish.hk2.api.IterableProvider;
import org.jvnet.hk2.annotations.Service;

/**
 * This WebServerManager is meant to illustrate that the WebServerBeans
 * are added as services to the HK2 service registry when unmarshalled
 * from the {@link org.glassfish.hk2.xml.api.XmlService#unmarshall(java.net.URI, Class)}
 * method
 * 
 * @author jwells
 *
 */
@Service
public class WebServerManager {
    @Inject
    private IterableProvider<WebServerBean> allWebServers;
    
    /**
     * Gets the WebServer bean with the given name, or null if
     * none can be found with that name
     * 
     * @param name The non-null name of the web server to find in
     * the HK2 service registry
     * @return the WebServerBean HK2 service with the given name
     */
    public WebServerBean getWebServer(String name) {
        return allWebServers.named(name).get();
    }

}
