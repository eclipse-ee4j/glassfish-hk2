/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.jvnet.hk2.generator.internal;

import java.util.HashSet;

/**
 * @author  jwells
 */
public class GenerateMethodAnnotationData {
    private final String implementation;
    
    private final HashSet<String> contracts = new HashSet<String>();
    
    private final String scope;
    
    private String nameMethodName;
    
    private String name;

    public GenerateMethodAnnotationData(String implementation,
            HashSet<String> contracts,
            String scope) {
        this.implementation = implementation;
        this.contracts.addAll(contracts);
        this.scope = scope;
    }
    
    public GenerateMethodAnnotationData(GenerateMethodAnnotationData copyMe) {
        this(copyMe.getImplementation(),
                new HashSet<String>(copyMe.getContracts()),
                copyMe.getScope());
        nameMethodName = copyMe.getNameMethodName();
    }

    /**
     * @return the implementation
     */
    public String getImplementation() {
        return implementation;
    }

    /**
     * @return the contracts
     */
    public HashSet<String> getContracts() {
        return contracts;
    }

    /**
     * @return the scope
     */
    public String getScope() {
        return scope;
    }
    
    /**
     * @return the nameMethodName
     */
    public String getNameMethodName() {
        return nameMethodName;
    }

    /**
     * @param nameMethodName the nameMethodName to set
     */
    public void setNameMethodName(String nameMethodName) {
        this.nameMethodName = nameMethodName;
    }

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

    public String toString() {
        return "GenerateMethodAnnotationData(" +
            implementation + "," +
            contracts + "," +
            scope + "," +
            nameMethodName + "," +
            name + "," +
            System.identityHashCode(this) + ")";
    }
}
