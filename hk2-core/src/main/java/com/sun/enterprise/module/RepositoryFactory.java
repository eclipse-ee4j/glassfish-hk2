/*
 * Copyright (c) 2007, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.enterprise.module;

import java.net.URI;

/**
 * Factory to create repositories. 
 *
 * @author Jerome Dochez
 */
public abstract class RepositoryFactory {
        
    /**
     * Returns true if this factory can handle this type of repository
     * @param type repository type
     */
    public abstract boolean handleType(String type);
    
    /**
     * Creates a new <code>Repository</code> with a parent (for delegating 
     * module resolutions) and a name. The URI source identifies the repository 
     * location.
     * @param parent the parent <code>Repository</code> to delegate module 
     * resolution
     * @param name the repository name
     * @param source the location of the repository
     */
    public abstract Repository createRepository(Repository parent, String name, URI source);
    
    /**
     * Creates a new <code>Repository</code>. The URI source identifies the 
     * repository location.
     * @param name the repository name
     * @param source the location of the repository
     */    
    public abstract Repository createRepository(String name, URI source);
}
