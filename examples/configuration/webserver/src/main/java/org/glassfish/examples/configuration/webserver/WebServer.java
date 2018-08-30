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

import java.io.File;
import java.util.List;

import org.jvnet.hk2.annotations.Contract;

/**
 * This is a fake WebServer that is designed to show the features of
 * the HK2 configuration system.  This class is intended to have parameters
 * that might be useful for a real web server, some of which are dynamic
 * and some of which are not dynamic.  They will be configured using the
 * HK2 configuration system
 * <p>
 * 
 * @author jwells
 *
 */
@Contract 
public interface WebServer {
    /**
     * Gets the name of this web server
     * 
     * @return The name of this web server
     */
    public String getName();
    
    /**
     * Opens the admin port, and returns the number
     * of the port open
     * 
     * @return The admin port opened
     */
    public int openAdminPort();
    
    /**
     * Opens the SSL port, and returns the number
     * of the port open
     * 
     * @return The SSL port open
     */
    public int openSSLPort();
    
    /**
     * Opens the non-SSL port, and returns the number
     * of the port open
     * 
     * @return The non-SSL port open
     */
    public int openPort();
    
    /**
     * Gets the current admin port, or -1
     * if the port is not open
     * 
     * @return The current admin port, or -1
     */
    public int getAdminPort();
    
    /**
     * Gets the current SSL port, or -1
     * if the port is not open
     * 
     * @return The current SSL port, or -1
     */
    public int getSSLPort();
    
    /**
     * Gets the current HTTP port, or -1
     * if the port is not open
     * 
     * @return The current HTTP port, or -1
     */
    public int getPort();
    
    /**
     * Gets the list of certificates that are
     * used by this web server
     * 
     * @return A non-null but possibly empty set
     * of Files pointing to the public certificates
     * of the web server
     */
    public List<File> getCertificates();
    
    
}
