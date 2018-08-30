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

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Registry for RepositoryFactory instances
 * 
 * @author Jerome Dochez
 */
public class RepositoryFactories {
    
    private static RepositoryFactories _singleton = new RepositoryFactories();
    
    private ArrayList<RepositoryFactory> factories = new ArrayList();
    
    /**
     * Return the instance holding registered repository factories
     * @return the instance holding factories
     */
    public static RepositoryFactories getInstance() {
        return _singleton;        
    }
    
    /** Creates a new instance of RepositoriesFactory */
    private RepositoryFactories() {
    }
    
    /**
     * Add a new <code> RepositoryFactory </code> to the list of 
     * repository factories. 
     * @param factory the new factory to add
     */
    public void addRepositoryFactory(RepositoryFactory factory) {        
        factories.add(factory);
    }
    
    /**
     * Returns an interator of registered <code>ReposistoryFactory</code>
     * @return an iterator or registered factories
     */
    public Iterator<RepositoryFactory> getFactories() {
        return factories.iterator();
    }
    
    /**
     * Returns a <code>RespositoryFactory</code> factory instance 
     * capable of creating <code>Repository</code> repositories of 
     * the provided type
     * @param type type of the repository we request the RepositoryFactory
     */
    public RepositoryFactory getFactoryFor(String type) {
        return null;
    }
}
